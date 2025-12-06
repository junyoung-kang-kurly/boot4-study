# Boot4 Study

Spring Boot 4.0과 Java 25 LTS의 최신 기능들을 학습할 수 있는 스터디 프로젝트입니다.

## 기술 스택

| Technology | Version | Release Date |
|------------|---------|--------------|
| Java | 25 (LTS) | September 2025 |
| Spring Boot | 4.0.0 | November 2025 |
| Spring Framework | 7.0 | November 2025 |
| Gradle | 8.x | - |

## 프로젝트 구조

```
src/main/java/com/example/springboot4study/
├── SpringBoot4StudyApplication.java  # 메인 애플리케이션
├── java25/                          # Java 25 새 기능 예제
│   ├── PatternMatchingExample.java  # Primitive Pattern Matching (JEP 507)
│   ├── StructuredConcurrencyExample.java  # Structured Concurrency (JEP 505)
│   ├── VirtualThreadExample.java    # Virtual Threads
│   └── ScopedValueExample.java      # Scoped Values (Finalized)
├── springboot4/                     # Spring Boot 4 새 기능 예제
│   ├── HttpServiceClientExample.java  # HTTP Interface Client
│   ├── HttpClientConfig.java        # HTTP Client Configuration
│   └── ApiVersioningExample.java    # API Versioning
├── controller/
│   └── DemoController.java          # REST API Demo
├── domain/
│   └── Product.java                 # JPA Entity with Records
└── repository/
    └── ProductRepository.java       # Spring Data JPA
```

## Java 25 주요 기능

### 1. Primitive Types in Pattern Matching (JEP 507)

기존에는 참조 타입만 가능했던 패턴 매칭이 이제 `int`, `long`, `double` 등 원시 타입에서도 사용 가능합니다.

```java
public String classifyHttpStatus(int statusCode) {
    return switch (statusCode) {
        case int s when s >= 200 && s < 300 -> "Success";
        case int s when s >= 400 && s < 500 -> "Client Error";
        case int s when s >= 500 && s < 600 -> "Server Error";
        default -> "Unknown";
    };
}
```

### 2. Structured Concurrency (JEP 505)

관련된 작업들을 하나의 단위로 관리하여 오류 처리와 취소를 단순화합니다.

```java
try (var scope = StructuredTaskScope.open(
        StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow())) {

    var profileTask = scope.fork(() -> fetchProfile(userId));
    var ordersTask = scope.fork(() -> fetchOrders(userId));

    scope.join();

    return new Dashboard(profileTask.get(), ordersTask.get());
}
```

### 3. Virtual Threads

수백만 개의 경량 스레드를 생성하여 높은 동시성을 효율적으로 처리합니다.

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    IntStream.range(0, 100_000)
        .mapToObj(i -> executor.submit(() -> processTask(i)))
        .toList();
}
```

### 4. Scoped Values

ThreadLocal의 현대적 대안으로, Virtual Thread 환경에 최적화되어 있습니다.

```java
private static final ScopedValue<String> USER_ID = ScopedValue.newInstance();

ScopedValue.runWhere(USER_ID, "user-123", () -> {
    // 이 스코프 내 어디서든 USER_ID.get() 사용 가능
    processRequest();
});
```

## Spring Boot 4 주요 기능

### 1. HTTP Interface Client

인터페이스에 어노테이션만 붙이면 Spring이 자동으로 HTTP 클라이언트 구현체를 생성합니다.

```java
@HttpExchange("/users")
public interface UserServiceClient {

    @GetExchange
    List<User> getAllUsers();

    @GetExchange("/{id}")
    User getUserById(@PathVariable Long id);

    @PostExchange
    User createUser(@RequestBody CreateUserRequest request);
}
```

### 2. API Versioning

URL, 헤더, 쿼리 파라미터 기반의 API 버전 관리를 지원합니다.

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserControllerV1 { ... }

@RestController
@RequestMapping("/api/v2/users")
public class UserControllerV2 { ... }
```

### 3. Virtual Threads 자동 설정

```yaml
spring:
  threads:
    virtual:
      enabled: true
```

## 실행 방법

### 요구사항
- Java 25 이상
- Gradle 8.x 이상

### 빌드 및 실행

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 또는 JAR 직접 실행
java -jar build/libs/boot4-study-0.0.1-SNAPSHOT.jar
```

### API 테스트

```bash
# 헬스 체크
curl http://localhost:8080/api/demo/health

# Pattern Matching 테스트
curl http://localhost:8080/api/demo/pattern-matching/42
curl http://localhost:8080/api/demo/http-status/200

# Structured Concurrency 테스트
curl http://localhost:8080/api/demo/structured-concurrency/user-123

# Virtual Threads 벤치마크
curl "http://localhost:8080/api/demo/virtual-threads/benchmark?taskCount=10000"

# 상품 API
curl http://localhost:8080/api/demo/products
curl http://localhost:8080/api/demo/products/category/Electronics
curl "http://localhost:8080/api/demo/products/price-range?min=100&max=500"

# API 버전별 사용자 조회
curl http://localhost:8080/api/v1/users
curl http://localhost:8080/api/v2/users
curl http://localhost:8080/api/v3/users
```

### H2 Console

개발용 H2 데이터베이스 콘솔: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (비워두기)

## 참고 자료

- [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- [Java 25 JEP List](https://openjdk.org/projects/jdk/25/)
- [Structured Concurrency (JEP 505)](https://openjdk.org/jeps/505)
- [Pattern Matching for Primitives (JEP 507)](https://openjdk.org/jeps/507)

## 라이선스

MIT License
