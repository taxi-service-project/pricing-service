# MSA 기반 Taxi 호출 플랫폼 - Pricing Service

Taxi 호출 플랫폼의 **요금 계산 및 정책 관리**를 담당하는 마이크로서비스입니다. 현재 활성화된 요금 정책을 기반으로 운행 거리, 시간, 종료 시각을 고려하여 최종 택시 요금을 계산합니다. 또한, 요금 정책을 생성하고 관리하는 기능을 제공합니다.

## 주요 기능

* **요금 계산:**
    * 현재 활성화된(`isActive=true`) `FarePolicy`를 조회합니다.
    * 기본 요금(`baseFare`)을 기준으로, 기본 거리 초과 시 미터당 요금을 추가합니다.
    * 초당 요금(`ratePerSecond`)을 적용하여 시간 요금을 추가합니다.
    * 운행 종료 시각(`endTimestamp`)이 심야 할증 시간에 해당하면 할증률을 적용합니다.
    * 최종 계산된 요금을 반환합니다 (`FareResponse`).
    * **(API Endpoint):** `GET /api/pricing/calculate?distance={meters}&duration={seconds}&endTime={timestamp}`
* **요금 정책 생성:**
    * 새로운 요금 정책(`FarePolicy`) 정보를 받아 DB에 저장합니다.
    * 새 정책을 활성화(`isActive=true`)하면 기존의 모든 정책을 비활성화합니다.
    * **(API Endpoint):** `POST /api/pricing/policies`
* **요금 정책 목록 조회:**
    * DB에 저장된 모든 요금 정책 목록을 조회하여 반환합니다.
    * **(API Endpoint):** `GET /api/pricing/policies`

## 기술 스택

* **Language & Framework:** Java, Spring Boot
* **Database:** Spring Data JPA, MySQL
