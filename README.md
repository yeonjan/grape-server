# Grape

포도알을 하나씩 채워가는 습관/목표 달성 기록 앱

---

## Overview

매일 꾸준히 무언가를 반복할 때 시각적인 동기부여가 필요하다는 생각에서 출발
목표 횟수(30 / 50 / 100)만큼 기록을 채우면 포도송이가 완성되는 경험을 제공

---

## Tech Stack

| 분류        | 기술                    |
|-----------|-----------------------|
| Language  | Java 21               |
| Framework | Spring Boot 4.0.2     |
| Database  | PostgreSQL            |
| Auth      | Spring Security + JWT |
| Docs      | Spring REST Docs      |

---

## Features

**인증**

- 게스트 로그인 (디바이스 ID 기반)
- 소셜 로그인 (카카오, Apple)
- 게스트 → 소셜 계정 마이그레이션

**포도 관리**

- 포도 생성 / 수정 / 조회
- 동시에 진행 중인 포도는 1개로 제한
- 상태 관리: `IN_PROGRESS` → `COMPLETED`

**기록**

- 날짜별 기록 작성 / 조회 / 수정 / 삭제
- 하루에 포도 1개당 기록 1개 제한
