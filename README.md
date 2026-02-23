# 💵 Pricing Service

> **요금 정책을 관리하고 최종 요금을 계산합니다.**

## 🛠 Tech Stack
| Category | Technology  |
| :--- |:------------|
| **Language** | **Java 17** |
| **Framework** | Spring Boot |
| **Database** | MySQL (JPA) |

## 📡 API Specification

### Internal API (Microservice Communication)
| Method | URI | Description |
| :--- | :--- | :--- |
| `GET` | `/internal/api/pricing/calculate` | **[내부망]** 거리, 시간, 종료 시각을 바탕으로 최종 요금 계산 |
| `POST` | `/internal/api/pricing/policies` | **[내부망]** 새로운 요금 정책 등록 |
| `GET` | `/internal/api/pricing/policies` | **[내부망]** 전체 요금 정책 목록 조회 |





----------

## 아키텍쳐
<img width="2324" height="1686" alt="Image" src="https://github.com/user-attachments/assets/81a25ff9-ee02-4996-80d3-f9217c3b7750" />
