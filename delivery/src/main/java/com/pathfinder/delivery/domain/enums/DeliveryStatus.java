package com.pathfinder.delivery.domain.enums;

public enum DeliveryStatus {
READY    ,          // 허브 대기중
IN_TRANSIT   ,      // 허브 이동중 
HUB_ARRIVED    ,    // 목적지 허브 도착 
IN_PROGRESS      ,  // 배송중
OUT_FOR_DELIVERY  , // 업체 이동중 
DONE         ,      // 배송완료
CANCELLED          // 취소
}

