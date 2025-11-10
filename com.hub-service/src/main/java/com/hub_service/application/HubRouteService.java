package com.hub_service.application;

import com.hub_service.domain.model.HubRoute;
import com.hub_service.domain.repository.HubRouteRepository;
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

    public HubRoute createRoute(HubRoute hubRoute) {

        return hubRouteRepository.save(hubRoute);
    }

    public void deleteRouteLogical(UUID routeId, String deletedBy) {
        hubRouteRepository.findById(routeId).ifPresent(hubRoute -> {
                    hubRoute.setDeletedAt(java.time.Instant.now());
                    hubRoute.setDeletedBy(deletedBy);
                    hubRouteRepository.save(hubRoute);

            }
        );
    }
    /**
     * 출발 허브(origin) → 도착 허브(destination)까지의 최단 경로 계산
     *
     * 1.캐시된 모든 허브 경로를 불러옴
     * 2.출발-도착 직접 경로 존재 여부 확인
     * 3.존재하지 않으면 전체 허브 그래프를 만들고 Dijkstra 실행
     * 4.결과 노드 시퀀스를 실제 HubRoute 리스트로 변환
     */
    public List<HubRoute> findPath(UUID origin, UUID destination) {
        // 같은 허브면 이동 없음
        if (origin.equals(destination)) return Collections.emptyList();

        // 허브 간 모든 활성 경로 조회
        List<HubRoute> all = findAllActiveRoutes();

        // 출발 → 도착 직통 경로가 있다면 바로 반환
        Optional<HubRoute> direct = all.stream()
                .filter(r -> r.getOriginHubId().equals(origin)
                        && r.getDestinationHubId().equals(destination))
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
        List<UUID> nodePath = dijkstra(origin, destination, graph);
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

    public List<HubRoute> findPathWithRelay(UUID origin, UUID destination, Map<UUID, Coordinate> hubCoordinates) {
        if(origin.equals(destination)) return Collections.emptyList();
        List<HubRoute> all = findAllActiveRoutes();

        Optional<HubRoute> direct = all.stream()
                .filter(hubRoute -> hubRoute.getOriginHubId().equals(origin) && hubRoute.getDestinationHubId().equals(destination))
                .findFirst();
        if(direct.isPresent()) return List.of(direct.get());

        Coordinate o = hubCoordinates.get(origin);
        Coordinate d = hubCoordinates.get(destination);
        if(o == null || d == null) {
            return findPath(origin, destination);
        }

        double straightDist = Coordinate.haversineDistanceKm(o, d);
        if(straightDist < RELAY_DISTANCE_THRESHOLD_KM){
            return findPath(origin, destination);
        }

        Coordinate mid = new Coordinate((o.lat + d.lat) / 2.0, (o.lon + d.lon) / 2.0);
        List<Map.Entry<UUID, Coordinate>> candidates = new ArrayList<>();
        for (Map.Entry<UUID, Coordinate> e : hubCoordinates.entrySet()) {
            UUID id = e.getKey();
            if (id.equals(origin) || id.equals(destination)) continue;
            candidates.add(e);
        }

        candidates.sort(Comparator.comparingDouble(e -> Coordinate.haversineDistanceKm(mid, e.getValue())));

        int maxCandidates = Math.min(5, candidates.size());
        for (int i = 0; i < maxCandidates; i++) {
            UUID candidateId = candidates.get(i).getKey();

            List<HubRoute> part1 = findPath(origin, candidateId);
            if (part1.isEmpty()) continue;

            List<HubRoute> part2 = findPath(candidateId, destination);
            if (part2.isEmpty()) continue;

            List<HubRoute> merged = new ArrayList<>();
            merged.addAll(part1);
            merged.addAll(part2);

            return merged;
        }
        return findPath(origin, destination);
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
요청: findPath(originHubId, destinationHubId)

[1] 캐시된 전체 HubRoute 조회
[2] 출발-도착 직통 경로 있는지 검사 : O → 바로 반환, X → Dijkstra 실행
[3] 그래프(Map<UUID,List<Edge>>) 생성
[4] PriorityQueue 로 최단 거리 계산
[5] prev 맵으로 경로 복원 (A→C→D→B)
[6] HubRoute 리스트로 변환 후 반환
*/
