package com.hub_service.application;

import com.hub_service.domain.model.HubRoute;
import com.hub_service.domain.repository.HubRouteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HubRouteService {

    private final HubRouteRepository hubRouteRepository;
    private static final double RELAY_DISTANCE_THRESHOLD_KM = 200.0;

    @Cacheable(value = "hubRoutes")
    public List<HubRoute> findAllActiveRoutes() {

        return hubRouteRepository.findByDeletedAtIsNull();
    }

    @Transactional
    public HubRoute createRoute(HubRoute hubRoute) {

        return hubRouteRepository.save(hubRoute);
    }

    @Transactional
    public void deleteRouteLogical(UUID routeId, String deletedBy) {
        hubRouteRepository.findById(routeId).ifPresent(hubRoute -> {
            hubRoute.setDeletedAt(java.time.Instant.now());
            hubRoute.setDeletedBy(
                    (deletedBy != null && !deletedBy.isBlank())
                            ? deletedBy
                            : "system" // 기본값
            );
            hubRouteRepository.save(hubRoute);
        });
    }
    /**
     * 출발 허브(origin) → 도착 허브(destination)까지의 최단 경로 계산
     *
     * 1.캐시된 모든 허브 경로를 불러옴
     * 2.출발-도착 직접 경로 존재 여부 확인
     * 3.존재하지 않으면 전체 허브 그래프를 만들고 Dijkstra 실행
     * 4.결과 노드 시퀀스를 실제 HubRoute 리스트로 변환
     */
    public List<HubRoute> findPath(UUID originId, UUID destinationId) {
        // 같은 허브면 이동 없음
        if (originId.equals(destinationId)) return Collections.emptyList();

        // 허브 간 모든 활성 경로 조회
        List<HubRoute> all = findAllActiveRoutes();

        // 출발 → 도착 직통 경로가 있다면 바로 반환
        Optional<HubRoute> direct = all.stream()
                .filter(r -> r.getOriginHubId().equals(originId)
                        && r.getDestinationHubId().equals(destinationId))
                .findFirst();
        if (direct.isPresent()) return List.of(direct.get());

        // 허브 간 그래프 구성 (노드: 허브ID, 간선: 거리)
        Map<UUID, List<Edge>> graph = new HashMap<>();
        for(HubRoute r : all) {
            graph.computeIfAbsent(r.getOriginHubId(), k -> new ArrayList<>())
                    .add(new Edge(r.getDestinationHubId(), r.getDistanceKm(), r.getRouteId()));
            // 양방향 이동 허용 (A→B, B→A)
            graph.computeIfAbsent(r.getDestinationHubId(), k -> new ArrayList<>())
                    .add(new Edge(r.getOriginHubId(), r.getDistanceKm(), r.getRouteId()));
        }

        // Dijkstra 실행
        List<UUID> nodePath = dijkstra(originId, destinationId, graph);
        if(nodePath.isEmpty()) return Collections.emptyList();

        // 노드 시퀀스를 실제 HubRoute 리스트로 변환
        List<HubRoute> routePath = new ArrayList<>();
        for(int i = 0; i < nodePath.size() - 1; i++) {
            UUID a = nodePath.get(i);
            UUID b = nodePath.get(i + 1);
            Optional<HubRoute> route = all.stream()
                    .filter(r -> r.getOriginHubId().equals(a)
                            && r.getDestinationHubId().equals(b))
                    .findFirst();
            route.ifPresent(routePath::add);
        }
        return routePath;
    }

    /**
     * Dijkstra 알고리즘
     *  - 목적: 출발 노드(src)부터 다른 모든 노드까지의 최소 거리 계산
     *  - dist : 현재까지 알려진 최단 거리
     *  - prev : 최단 경로 복원을 위한 이전 노드
     *  - PQ   : 가장 짧은 거리 순으로 노드를 탐색하기 위한 우선순위 큐
     */
    private List<UUID> dijkstra(UUID src, UUID target, Map<UUID, List<Edge>> graph) {
        Map<UUID, Double> dist = new HashMap<>();
        Map<UUID, UUID> prev = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.dist));

        dist.put(src, 0.0);
        pq.add(new Node(src, 0.0));

        while(!pq.isEmpty()) {
            Node cur = pq.poll();

            // 현재 노드까지의 거리가 기존 dist보다 크면 스킵 (더 좋은 경로 있음)
            if(cur.dist > dist.getOrDefault(cur.node, Double.MAX_VALUE)) continue;

            // 도착 노드에 도달 시 종료
            if(cur.node.equals(target)) break;

            // 인접 노드들 탐색
            for(Edge e : graph.getOrDefault(cur.node, List.of())) {
                double nd = cur.dist + e.weight; // 현재 거리 + 간선 거리
                if(nd < dist.getOrDefault(e.to, Double.MAX_VALUE)) {
                    dist.put(e.to, nd);
                    prev.put(e.to, cur.node);
                    pq.add(new Node(e.to, nd));
                }
            }
        }

        // target까지 경로가 없으면 빈 리스트
        if (!dist.containsKey(target)) return Collections.emptyList();

        // prev 맵을 따라 경로 복원
        LinkedList<UUID> path = new LinkedList<>();
        UUID at = target;
        while (at != null) {
            path.addFirst(at);
            at = prev.get(at);
        }
        return path;
    }

    /**
     내부 동작 흐름:
     * 캐시된 모든 허브 간 유효한 경로를 조회 (deleted_at == null)
     * 출발→도착 직통 경로 존재 시 바로 반환
     * 허브 좌표(hubCoordinates) 기반으로 두 허브 사이의 직선 거리 계산
     * 거리가 200km 이상이면, 중간 경유 허브 후보 5개를 선정
     * 후보 허브 중 실제 연결 가능한 허브를 찾아 두 구간을 병합
     * 최종 경유 경로 반환
     * 만약 경유 허브를 찾을 수 없으면 일반 Dijkstra 최단 경로(findPath)로 대체
     */
    public List<HubRoute> findPathWithRelay(UUID originId, UUID destinationId, Map<UUID, Coordinate> hubCoordinates) {
        // 출발지와 도착지가 동일한 경우 → 이동 경로 없음
        if (originId.equals(destinationId)) return Collections.emptyList();

        // 논리삭제(deleted_at IS NULL)된 허브 경로만 조회
        List<HubRoute> all = findAllActiveRoutes();

        // 직통 경로 존재 확인 (출발→도착)
        Optional<HubRoute> direct = all.stream()
                .filter(hubRoute ->
                        hubRoute.getOriginHubId().equals(originId)
                                && hubRoute.getDestinationHubId().equals(destinationId))
                .findFirst();

        // 직통 경로가 있으면 바로 반환
        if (direct.isPresent()) return List.of(direct.get());

        // 허브의 좌표정보 조회
        Coordinate o = hubCoordinates.get(originId);
        Coordinate d = hubCoordinates.get(destinationId);

        // 좌표정보가 누락되면 일반 Dijkstra 경로탐색으로 대체
        if (o == null || d == null) {
            return findPath(originId, destinationId);
        }

        // 두 허브 간의 직선 거리 계산
        double straightDist = Coordinate.haversineDistanceKm(o, d);

        // 200km 미만이면 중간 허브 탐색 생략
        if (straightDist < RELAY_DISTANCE_THRESHOLD_KM) {
            return findPath(originId, destinationId);
        }

        // 출발지와 도착지의 중간점(midpoint) 계산
        Coordinate mid = new Coordinate((o.lat + d.lat) / 2.0, (o.lon + d.lon) / 2.0);

        // 전체 허브 중 중간점 기준으로 가까운 순서대로 후보 5개 선정
        List<Map.Entry<UUID, Coordinate>> candidates = new ArrayList<>();
        for (Map.Entry<UUID, Coordinate> e : hubCoordinates.entrySet()) {
            UUID id = e.getKey();
            if (id.equals(originId) || id.equals(destinationId)) continue; // 출발/도착 제외
            candidates.add(e);
        }

        // 거리 기준 정렬
        candidates.sort(Comparator.comparingDouble(e -> Coordinate.haversineDistanceKm(mid, e.getValue())));
        int maxCandidates = Math.min(5, candidates.size());

        // 후보 허브를 중간 경유지로 시도
        for (int i = 0; i < maxCandidates; i++) {
            UUID candidateId = candidates.get(i).getKey();

            // 1 출발 → 후보 허브 경로 계산
            List<HubRoute> part1 = findPath(originId, candidateId);
            if (part1.isEmpty()) continue;

            // 2 후보 허브 → 도착 허브 경로 계산
            List<HubRoute> part2 = findPath(candidateId, destinationId);
            if (part2.isEmpty()) continue;

            // 3 두 구간 병합
            List<HubRoute> merged = new ArrayList<>();
            merged.addAll(part1);
            merged.addAll(part2);

            return merged; // 첫 번째로 유효한 경유 경로 반환
        }

        // 경유 허브가 없으면 일반 최단경로(Dijkstra)로 대체
        return findPath(originId, destinationId);
    }


    //내부 클래스 정의 (그래프 구조용)
    private static class Edge {
        UUID to;          // 도착 허브 ID
        double weight;    // 이동 거리 (가중치)
        UUID routeId;     // 실제 경로 ID
        Edge(UUID to, double weight, UUID routeId) {
            this.to = to;
            this.weight = weight;
            this.routeId = routeId;
        }
    }

    private static class Node {
        UUID node;  // 허브 ID
        double dist; // 현재까지의 거리
        Node(UUID node, double dist) {
            this.node = node;
            this.dist = dist;
        }
    }

    public static class Coordinate {
        public final double lat;
        public final double lon;

        public Coordinate(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        //두 지점의 위도(lat), 경도(lon)를 이용하여 구면 거리(km)를 계산.
        private static double haversineDistanceKm(Coordinate a, Coordinate b) {
            final int EARTH_RADIUS_KM = 6371; // 지구 반지름 (km)
            double dLat = Math.toRadians(b.lat - a.lat);
            double dLon = Math.toRadians(b.lon - a.lon);
            double lat1 = Math.toRadians(a.lat);
            double lat2 = Math.toRadians(b.lat);

            double h = Math.pow(Math.sin(dLat / 2), 2)
                    + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
            double c = 2 * Math.asin(Math.sqrt(h));

            return EARTH_RADIUS_KM * c;
        }
    }

}

/*

 findAllActiveRoutes()
    Redis 캐시 + deletedAt=null 필터

 createRoute()
    신규 경로 등록

 findPath()
    Dijkstra 기반 최단경로 탐색

 findPathWithRelay()
    거리 200km 이상 → 중간 허브 탐색
    후보 허브 5개 선정
    두 구간 병합 후 반환
    실패 시 Dijkstra로 대체

 deleteRouteLogical()
    deletedBy, deletedAt 기록 후 비활성
*/
