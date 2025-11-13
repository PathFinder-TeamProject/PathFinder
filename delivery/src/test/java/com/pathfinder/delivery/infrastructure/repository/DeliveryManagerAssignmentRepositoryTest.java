package com.pathfinder.delivery.infrastructure.repository;

import com.pathfinder.delivery.domain.repository.DeliveryManagerAssignmentRepository;
import com.pathfinder.delivery.infrastructure.repository.impl.DeliveryManagerAssignmentRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryManagerAssignmentRepositoryTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private DeliveryManagerAssignmentRepository repository;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        repository = new DeliveryManagerAssignmentRepositoryImpl(redisTemplate);
    }

    @Test
    @DisplayName("마지막 배정 순번 조회 시 Redis에 값이 있으면 해당 값을 반환한다")
    void getLastAssignedOrder_shouldReturnOrder_whenValueExists() {
        // given
        UUID hubId = UUID.randomUUID();
        Integer lastOrder = 2;
        when(valueOperations.get(anyString())).thenReturn(lastOrder);

        // when
        Integer result = repository.getLastAssignedOrder(hubId);

        // then
        assertThat(result).isEqualTo(2);
        verify(valueOperations).get("delivery:manager:lastOrder:" + hubId);
    }

    @Test
    @DisplayName("마지막 배정 순번 조회 시 Redis에 값이 없으면 null을 반환한다")
    void getLastAssignedOrder_shouldReturnNull_whenValueNotExists() {
        // given
        UUID hubId = UUID.randomUUID();
        when(valueOperations.get(anyString())).thenReturn(null);

        // when
        Integer result = repository.getLastAssignedOrder(hubId);

        // then
        assertThat(result).isNull();
        verify(valueOperations).get("delivery:manager:lastOrder:" + hubId);
    }

    @Test
    @DisplayName("마지막 배정 순번 저장 시 Redis에 TTL과 함께 저장한다")
    void saveLastAssignedOrder_shouldSaveWithTTL() {
        // given
        UUID hubId = UUID.randomUUID();
        Integer order = 1;

        // when
        repository.saveLastAssignedOrder(hubId, order);

        // then
        verify(valueOperations).set(
                eq("delivery:manager:lastOrder:" + hubId),
                eq(order),
                eq(24 * 7L),
                eq(TimeUnit.HOURS)
        );
    }

    @Test
    @DisplayName("마지막 배정 순번 조회 시 Number 타입이면 Integer로 변환하여 반환한다")
    void getLastAssignedOrder_shouldConvertNumberToInteger() {
        // given
        UUID hubId = UUID.randomUUID();
        Long longValue = 3L;
        when(valueOperations.get(anyString())).thenReturn(longValue);

        // when
        Integer result = repository.getLastAssignedOrder(hubId);

        // then
        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("마지막 배정 순번 조회 시 예상치 못한 타입이면 null을 반환한다")
    void getLastAssignedOrder_shouldReturnNull_whenUnexpectedType() {
        // given
        UUID hubId = UUID.randomUUID();
        String unexpectedValue = "unexpected";
        when(valueOperations.get(anyString())).thenReturn(unexpectedValue);

        // when
        Integer result = repository.getLastAssignedOrder(hubId);

        // then
        assertThat(result).isNull();
    }
}

