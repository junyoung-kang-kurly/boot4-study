package com.example.springboot4study.jackson3;

import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.StringNode;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalDouble;

/**
 * Jackson 3의 새로운 기능들을 데모하는 클래스.
 *
 * Jackson 3에서 새롭게 추가되거나 변경된 주요 기능들:
 *
 * 1. Immutable ObjectMapper - Builder 패턴으로 생성, 설정 변경 불가
 * 2. Unchecked Exceptions - JacksonException이 RuntimeException 상속
 * 3. 내장 Java 8+ 지원 - Optional, java.time 별도 모듈 없이 지원
 * 4. 새로운 JsonNode 메서드 - asShort(), asFloat(), Optional 변형들
 * 5. 변경된 기본값 - FAIL_ON_UNKNOWN_PROPERTIES 기본 false 등
 * 6. 패키지 변경 - com.fasterxml.jackson -> tools.jackson
 * 7. 클래스명 변경 - TextNode -> StringNode, Module -> JacksonModule
 */
@Component
public class Jackson3Examples {

    private final ObjectMapper objectMapper;

    public Jackson3Examples() {
        // Jackson 3: ObjectMapper는 이제 불변(Immutable)!
        // Builder 패턴으로만 생성 가능
        this.objectMapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
    }

    // ========================================
    // 1. Immutable ObjectMapper 데모
    // ========================================

    /**
     * Jackson 3의 Immutable ObjectMapper 데모.
     *
     * Jackson 2에서는:
     * - ObjectMapper mapper = new ObjectMapper();
     * - mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 런타임에 설정 변경 가능
     *
     * Jackson 3에서는:
     * - ObjectMapper는 불변(Immutable)
     * - Builder 패턴으로만 생성
     * - 설정 변경 시 rebuild()로 새 인스턴스 생성
     */
    public record ImmutableMapperDemo(
            String description,
            boolean isImmutable,
            String builderPattern,
            String rebuildPattern
    ) {}

    public ImmutableMapperDemo demonstrateImmutableMapper() {
        // 기존 설정을 복사하여 새로운 ObjectMapper 생성
        ObjectMapper newMapper = objectMapper.rebuild()
                .disable(SerializationFeature.INDENT_OUTPUT)
                .build();

        return new ImmutableMapperDemo(
                "Jackson 3의 ObjectMapper는 불변(Immutable)입니다. " +
                "생성 후 설정을 변경할 수 없으며, 변경이 필요하면 rebuild()로 새 인스턴스를 생성합니다.",
                true,
                "JsonMapper.builder().enable(...).build()",
                "objectMapper.rebuild().disable(...).build()"
        );
    }

    // ========================================
    // 2. Unchecked Exception 데모
    // ========================================

    /**
     * Jackson 3의 Unchecked Exception 데모.
     *
     * Jackson 2에서는:
     * - JsonProcessingException extends IOException (checked)
     * - 모든 곳에서 try-catch 또는 throws 필요
     *
     * Jackson 3에서는:
     * - JacksonException extends RuntimeException (unchecked)
     * - 더 깔끔한 코드 작성 가능
     * - 예외 계층: JacksonException -> StreamReadException/StreamWriteException/DatabindException
     */
    public record ExceptionDemo(
            String description,
            String jackson2Exception,
            String jackson3Exception,
            String exceptionHierarchy,
            String exampleError
    ) {}

    public ExceptionDemo demonstrateUncheckedException() {
        String errorMessage = "";
        try {
            // 잘못된 JSON 파싱 시도
            objectMapper.readValue("{invalid json}", Map.class);
        } catch (JacksonException e) {
            // Jackson 3: JacksonException은 RuntimeException!
            // try-catch가 선택적 (unchecked)
            errorMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
        }

        return new ExceptionDemo(
                "Jackson 3에서 예외는 RuntimeException을 상속합니다. " +
                "더 이상 모든 곳에서 try-catch가 필요하지 않습니다.",
                "JsonProcessingException extends IOException (checked)",
                "JacksonException extends RuntimeException (unchecked)",
                "JacksonException -> StreamReadException (파싱), StreamWriteException (생성), DatabindException (바인딩)",
                errorMessage
        );
    }

    // ========================================
    // 3. 내장 Java 8+ 지원 데모
    // ========================================

    /**
     * Jackson 3의 내장 Java 8+ 지원 데모.
     *
     * Jackson 2에서는 별도 모듈 필요:
     * - jackson-datatype-jdk8 (Optional 지원)
     * - jackson-datatype-jsr310 (java.time 지원)
     * - jackson-module-parameter-names (생성자 파라미터 이름)
     *
     * Jackson 3에서는:
     * - 모두 jackson-databind에 내장!
     * - 추가 의존성 불필요
     */
    public record JavaTimeDemo(
            String description,
            LocalDate localDate,
            LocalDateTime localDateTime,
            ZonedDateTime zonedDateTime,
            String serialized
    ) {}

    public JavaTimeDemo demonstrateJavaTimeSupport() {
        LocalDate date = LocalDate.of(2025, 12, 6);
        LocalDateTime dateTime = LocalDateTime.of(2025, 12, 6, 10, 30, 0);
        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        record TimeData(LocalDate date, LocalDateTime dateTime, ZonedDateTime zonedDateTime) {}
        TimeData data = new TimeData(date, dateTime, zonedDateTime);

        String serialized = objectMapper.writeValueAsString(data);

        return new JavaTimeDemo(
                "Jackson 3에서는 java.time 타입이 기본 지원됩니다. " +
                "별도의 jackson-datatype-jsr310 모듈이 필요 없습니다!",
                date,
                dateTime,
                zonedDateTime,
                serialized
        );
    }

    /**
     * Optional 지원 데모.
     * Jackson 3에서는 Optional도 별도 모듈 없이 기본 지원!
     */
    public record OptionalDemo(
            String description,
            Optional<String> presentValue,
            Optional<String> emptyValue,
            OptionalInt optionalInt,
            OptionalDouble optionalDouble,
            String serialized
    ) {}

    public OptionalDemo demonstrateOptionalSupport() {
        record OptionalData(
                Optional<String> name,
                Optional<String> nickname,
                OptionalInt age,
                OptionalDouble score
        ) {}

        OptionalData data = new OptionalData(
                Optional.of("Spring Boot 4"),
                Optional.empty(),
                OptionalInt.of(25),
                OptionalDouble.of(99.5)
        );

        String serialized = objectMapper.writeValueAsString(data);

        return new OptionalDemo(
                "Jackson 3에서는 Optional 타입이 기본 지원됩니다. " +
                "Optional.empty()는 null로, Optional.of(value)는 value로 직렬화됩니다.",
                Optional.of("Spring Boot 4"),
                Optional.empty(),
                OptionalInt.of(25),
                OptionalDouble.of(99.5),
                serialized
        );
    }

    // ========================================
    // 4. 새로운 JsonNode 메서드 데모
    // ========================================

    /**
     * Jackson 3의 새로운 JsonNode 메서드 데모.
     *
     * 새로 추가된 메서드:
     * - asShort(), asFloat() - 새로운 변환 메서드
     * - intValueOpt(), longValueOpt() 등 - Optional 반환 변형
     * - asShortOpt(), asFloatOpt() 등 - Optional 변환 메서드
     *
     * 이름 변경:
     * - TextNode -> StringNode
     */
    public record JsonNodeDemo(
            String description,
            short shortValue,
            float floatValue,
            String optionalMethods,
            String stringNodeNote
    ) {}

    public JsonNodeDemo demonstrateJsonNodeImprovements() {
        String json = """
                {
                    "shortNum": 123,
                    "floatNum": 45.67,
                    "text": "Hello Jackson 3",
                    "missing": null
                }
                """;

        JsonNode root = objectMapper.readTree(json);

        // 새로운 asShort(), asFloat() 메서드
        short shortValue = root.get("shortNum").asShort();
        float floatValue = root.get("floatNum").asFloat();

        // Optional 변형 메서드들
        Optional<Integer> intOpt = root.get("shortNum").asIntOpt();
        Optional<Float> floatOpt = root.get("floatNum").asFloatOpt();
        Optional<String> missingOpt = root.get("nonexistent") != null
                ? root.get("nonexistent").asStringOpt()
                : Optional.empty();

        return new JsonNodeDemo(
                "Jackson 3에서는 JsonNode에 새로운 메서드들이 추가되었습니다.",
                shortValue,
                floatValue,
                "intValueOpt(), longValueOpt(), asShortOpt(), asFloatOpt() 등 Optional 반환 메서드 추가",
                "TextNode가 StringNode로 이름 변경됨"
        );
    }

    /**
     * StringNode (구 TextNode) 데모.
     */
    public record StringNodeDemo(
            String description,
            String jackson2Name,
            String jackson3Name,
            String createdValue
    ) {}

    public StringNodeDemo demonstrateStringNode() {
        // Jackson 3: TextNode -> StringNode
        StringNode stringNode = StringNode.valueOf("Hello from StringNode!");

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.set("message", stringNode);

        return new StringNodeDemo(
                "Jackson 3에서 TextNode가 StringNode로 이름이 변경되었습니다.",
                "TextNode",
                "StringNode",
                stringNode.asString()
        );
    }

    // ========================================
    // 5. 변경된 기본값 데모
    // ========================================

    /**
     * Jackson 3의 변경된 기본값 데모.
     */
    public record DefaultsDemo(
            String description,
            List<DefaultChange> changes
    ) {}

    public record DefaultChange(
            String feature,
            String jackson2Default,
            String jackson3Default,
            String impact
    ) {}

    public DefaultsDemo demonstrateDefaultChanges() {
        List<DefaultChange> changes = List.of(
                new DefaultChange(
                        "FAIL_ON_UNKNOWN_PROPERTIES",
                        "true (알 수 없는 프로퍼티 시 예외)",
                        "false (알 수 없는 프로퍼티 무시)",
                        "더 유연한 역직렬화, 하위 호환성 향상"
                ),
                new DefaultChange(
                        "WRITE_DATES_AS_TIMESTAMPS",
                        "true (밀리초 타임스탬프)",
                        "false (ISO-8601 문자열)",
                        "더 읽기 쉬운 날짜 형식"
                ),
                new DefaultChange(
                        "FAIL_ON_TRAILING_TOKENS",
                        "false (후행 토큰 무시)",
                        "true (후행 토큰 시 예외)",
                        "더 엄격한 JSON 검증"
                ),
                new DefaultChange(
                        "FAIL_ON_NULL_FOR_PRIMITIVES",
                        "false (null -> 기본값)",
                        "true (null -> 예외)",
                        "더 안전한 타입 처리"
                ),
                new DefaultChange(
                        "Enum 직렬화",
                        "name() 사용",
                        "toString() 사용",
                        "더 유연한 Enum 표현"
                ),
                new DefaultChange(
                        "프로퍼티 순서",
                        "선언 순서",
                        "알파벳 순서",
                        "일관된 JSON 출력"
                )
        );

        return new DefaultsDemo(
                "Jackson 3에서 여러 기본값이 변경되었습니다. " +
                "마이그레이션 시 주의가 필요합니다.",
                changes
        );
    }

    // ========================================
    // 6. Record 클래스 네이티브 지원 데모
    // ========================================

    /**
     * Jackson 3의 Record 지원 데모.
     *
     * Jackson 2에서는:
     * - Record 지원을 위해 추가 설정 필요
     * - 파라미터 이름 모듈 필요
     *
     * Jackson 3에서는:
     * - Record가 완벽히 네이티브 지원
     * - 추가 설정 불필요
     */
    public record RecordDemo(
            String description,
            Person examplePerson,
            String serialized,
            String deserialized
    ) {}

    public record Person(
            String name,
            int age,
            Optional<String> email,
            LocalDate birthDate
    ) {}

    public RecordDemo demonstrateRecordSupport() {
        Person person = new Person(
                "김개발",
                30,
                Optional.of("dev@example.com"),
                LocalDate.of(1995, 3, 15)
        );

        // Record 직렬화 - 추가 설정 없이 작동!
        String serialized = objectMapper.writeValueAsString(person);

        // Record 역직렬화 - 생성자 파라미터 이름 자동 감지!
        Person deserialized = objectMapper.readValue(serialized, Person.class);

        return new RecordDemo(
                "Jackson 3에서는 Java Record가 완벽히 네이티브 지원됩니다. " +
                "별도의 파라미터 이름 모듈 없이도 생성자 파라미터가 자동으로 매핑됩니다.",
                person,
                serialized,
                deserialized.toString()
        );
    }

    // ========================================
    // 7. 패키지 변경 정보
    // ========================================

    public record PackageChangeDemo(
            String description,
            String jackson2Package,
            String jackson3Package,
            List<ClassRename> classRenames
    ) {}

    public record ClassRename(
            String jackson2Name,
            String jackson3Name,
            String reason
    ) {}

    public PackageChangeDemo demonstratePackageChanges() {
        List<ClassRename> renames = List.of(
                new ClassRename(
                        "com.fasterxml.jackson.core.JsonProcessingException",
                        "tools.jackson.core.JacksonException",
                        "RuntimeException으로 변경"
                ),
                new ClassRename(
                        "com.fasterxml.jackson.core.JsonParseException",
                        "tools.jackson.core.exc.StreamReadException",
                        "더 명확한 이름"
                ),
                new ClassRename(
                        "com.fasterxml.jackson.core.JsonGenerationException",
                        "tools.jackson.core.exc.StreamWriteException",
                        "더 명확한 이름"
                ),
                new ClassRename(
                        "com.fasterxml.jackson.databind.JsonMappingException",
                        "tools.jackson.databind.DatabindException",
                        "더 명확한 이름"
                ),
                new ClassRename(
                        "com.fasterxml.jackson.databind.node.TextNode",
                        "tools.jackson.databind.node.StringNode",
                        "더 직관적인 이름"
                ),
                new ClassRename(
                        "com.fasterxml.jackson.databind.Module",
                        "tools.jackson.databind.JacksonModule",
                        "java.lang.Module과 충돌 방지"
                ),
                new ClassRename(
                        "com.fasterxml.jackson.core.JsonFactory",
                        "tools.jackson.core.TokenStreamFactory",
                        "포맷 중립적 이름"
                ),
                new ClassRename(
                        "JsonParser.getText()",
                        "JsonParser.getString()",
                        "더 명확한 메서드 이름"
                )
        );

        return new PackageChangeDemo(
                "Jackson 3에서는 패키지와 클래스 이름이 대폭 변경되었습니다.",
                "com.fasterxml.jackson",
                "tools.jackson",
                renames
        );
    }

    // ========================================
    // 8. 종합 비교 데모
    // ========================================

    public record ComprehensiveComparison(
            String title,
            String jackson2Code,
            String jackson3Code,
            String benefit
    ) {}

    public List<ComprehensiveComparison> getComprehensiveComparison() {
        return List.of(
                new ComprehensiveComparison(
                        "ObjectMapper 생성",
                        """
                        // Jackson 2
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        mapper.registerModule(new Jdk8Module());
                        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
                        """,
                        """
                        // Jackson 3
                        ObjectMapper mapper = JsonMapper.builder()
                                .build();
                        // java.time과 Optional 자동 지원!
                        // FAIL_ON_UNKNOWN_PROPERTIES 기본 false!
                        """,
                        "더 간결한 코드, 기본값 개선"
                ),
                new ComprehensiveComparison(
                        "예외 처리",
                        """
                        // Jackson 2 - Checked Exception
                        try {
                            mapper.readValue(json, MyClass.class);
                        } catch (JsonProcessingException e) {
                            // 반드시 처리해야 함
                        }
                        """,
                        """
                        // Jackson 3 - Unchecked Exception
                        MyClass obj = mapper.readValue(json, MyClass.class);
                        // JacksonException은 RuntimeException!
                        // 필요한 경우에만 catch
                        """,
                        "더 깔끔한 코드"
                ),
                new ComprehensiveComparison(
                        "Record 직렬화",
                        """
                        // Jackson 2 - 추가 설정 필요
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new ParameterNamesModule());
                        // -parameters 컴파일 옵션도 필요
                        """,
                        """
                        // Jackson 3 - 그냥 작동!
                        ObjectMapper mapper = JsonMapper.builder().build();
                        String json = mapper.writeValueAsString(myRecord);
                        MyRecord obj = mapper.readValue(json, MyRecord.class);
                        """,
                        "Record 네이티브 지원"
                ),
                new ComprehensiveComparison(
                        "날짜/시간 처리",
                        """
                        // Jackson 2 - 모듈 등록 필요
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
                        """,
                        """
                        // Jackson 3 - 기본 지원
                        ObjectMapper mapper = JsonMapper.builder().build();
                        // java.time 자동 지원!
                        // ISO-8601 형식이 기본!
                        """,
                        "추가 의존성 불필요"
                )
        );
    }
}
