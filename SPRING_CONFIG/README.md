# Player Evaluation API — Spring Boot

## Yêu cầu môi trường
| Công cụ | Phiên bản |
|---------|-----------|
| Java | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |

---

## Cài đặt nhanh

### 1. Khởi tạo Database
Chạy file `src/main/resources/init.sql` trong MySQL Workbench hoặc CLI:
```bash
mysql -u root -p < src/main/resources/init.sql
```

### 2. Cấu hình kết nối DB
Chỉnh sửa file `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/player_evaluation
spring.datasource.username=root
spring.datasource.password=<your_password>
```

### 3. Chạy ứng dụng
```bash
mvn spring-boot:run
```
Server khởi động tại: `http://localhost:8080`

---

## API Endpoints

### Base URL: `http://localhost:8080/api/players`

| Method | URL | Mô tả |
|--------|-----|-------|
| `GET` | `/api/players` | Lấy danh sách tất cả người chơi |
| `GET` | `/api/players/{id}` | Lấy chi tiết một người chơi |
| `POST` | `/api/players` | Thêm người chơi mới |
| `PUT` | `/api/players/{id}` | Cập nhật thông tin người chơi |
| `DELETE` | `/api/players/{id}` | Xóa người chơi |

---

## Request / Response Examples

### POST `/api/players` — Thêm người chơi mới

**Request Body:**
```json
{
  "name": "ronaldo",
  "fullName": "Cristiano Ronaldo",
  "age": "39",
  "indexId": 1,
  "playerIndexes": [
    { "indexId": 1, "value": 95.5 },
    { "indexId": 2, "value": 8.0 },
    { "indexId": 3, "value": 0.85 }
  ]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Thêm người chơi mới thành công",
  "data": {
    "playerId": 1,
    "name": "ronaldo",
    "fullName": "Cristiano Ronaldo",
    "age": "39",
    "indexer": {
      "indexId": 1,
      "name": "speed",
      "valueMin": 10.0,
      "valueMax": 100.0
    },
    "playerIndexes": [
      { "id": 1, "indexId": 1, "indexName": "speed",    "value": 95.5, "valueMin": 10.0, "valueMax": 100.0 },
      { "id": 2, "indexId": 2, "indexName": "strength", "value": 8.0,  "valueMin": 0.0,  "valueMax": 10.0  },
      { "id": 3, "indexId": 3, "indexName": "accurate", "value": 0.85, "valueMin": 0.0,  "valueMax": 1.0   }
    ]
  }
}
```

### GET `/api/players` — Lấy tất cả người chơi

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Lấy danh sách người chơi thành công",
  "data": [ ... ]
}
```

### PUT `/api/players/1` — Cập nhật người chơi

**Request Body:** (tương tự POST)

### DELETE `/api/players/1` — Xóa người chơi

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Xóa người chơi thành công"
}
```

---

## Validation Rules

| Field | Rule |
|-------|------|
| `name` | Bắt buộc, tối đa 64 ký tự, **không trùng** với người chơi khác |
| `fullName` | Bắt buộc, tối đa 128 ký tự |
| `age` | Bắt buộc, số nguyên dương từ **1–100** |
| `indexId` | Bắt buộc, phải tồn tại trong bảng `indexer` |
| `playerIndexes[].value` | Phải nằm trong khoảng `[valueMin, valueMax]` của indexer tương ứng |

---

## Cấu trúc project
```
SPRING_CONFIG/
├── pom.xml
└── src/main/
    ├── java/com/wcd/playerevaluation/
    │   ├── PlayerEvaluationApplication.java
    │   ├── controller/
    │   │   └── PlayerController.java
    │   ├── service/
    │   │   ├── PlayerService.java          (interface)
    │   │   └── impl/PlayerServiceImpl.java
    │   ├── repository/
    │   │   ├── IndexerRepository.java
    │   │   ├── PlayerRepository.java
    │   │   └── PlayerIndexRepository.java
    │   ├── entity/
    │   │   ├── Indexer.java
    │   │   ├── Player.java
    │   │   └── PlayerIndex.java
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── PlayerRequestDTO.java
    │   │   │   └── PlayerIndexRequestDTO.java
    │   │   └── response/
    │   │       ├── ApiResponseDTO.java
    │   │       ├── PlayerResponseDTO.java
    │   │       └── PlayerIndexResponseDTO.java
    │   └── exception/
    │       ├── GlobalExceptionHandler.java
    │       ├── ResourceNotFoundException.java
    │       └── ValidationException.java
    └── resources/
        ├── application.properties
        └── init.sql
```
