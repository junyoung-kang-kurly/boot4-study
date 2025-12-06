package com.example.springboot4study.jackson3;

import org.springframework.web.bind.annotation.*;
import tools.jackson.core.JacksonException;

import java.util.List;
import java.util.Map;

/**
 * Jackson 3의 새로운 기능들을 데모하는 REST API Controller.
 *
 * Spring Boot 4는 Jackson 3를 기본 JSON 라이브러리로 사용합니다.
 * 이 컨트롤러는 Jackson 2에서 Jackson 3로 변경된 주요 기능들을 보여줍니다.
 *
 * 엔드포인트:
 * - GET /api/jackson3 - 모든 데모 목록
 * - GET /api/jackson3/immutable-mapper - Immutable ObjectMapper 데모
 * - GET /api/jackson3/unchecked-exception - Unchecked Exception 데모
 * - GET /api/jackson3/java-time - java.time 내장 지원 데모
 * - GET /api/jackson3/optional - Optional 내장 지원 데모
 * - GET /api/jackson3/json-node - 새로운 JsonNode 메서드 데모
 * - GET /api/jackson3/string-node - StringNode (구 TextNode) 데모
 * - GET /api/jackson3/defaults - 변경된 기본값 데모
 * - GET /api/jackson3/records - Record 네이티브 지원 데모
 * - GET /api/jackson3/package-changes - 패키지/클래스 변경 정보
 * - GET /api/jackson3/comparison - Jackson 2 vs 3 종합 비교
 */
@RestController
@RequestMapping("/api/jackson3")
public class Jackson3Controller {

    private final Jackson3Examples jackson3Examples;

    public Jackson3Controller(Jackson3Examples jackson3Examples) {
        this.jackson3Examples = jackson3Examples;
    }

    /**
     * 사용 가능한 모든 Jackson 3 데모 목록
     */
    @GetMapping
    public Map<String, Object> index() {
        return Map.of(
                "title", "Jackson 3 새로운 기능 데모",
                "description", "Spring Boot 4에 포함된 Jackson 3의 주요 변경사항과 새로운 기능들을 데모합니다.",
                "jackson3Version", "3.0.0",
                "springBootVersion", "4.0.0",
                "endpoints", List.of(
                        Map.of("path", "/api/jackson3/immutable-mapper",
                                "description", "Immutable ObjectMapper - Builder 패턴으로 생성"),
                        Map.of("path", "/api/jackson3/unchecked-exception",
                                "description", "Unchecked Exception - JacksonException이 RuntimeException 상속"),
                        Map.of("path", "/api/jackson3/java-time",
                                "description", "java.time 내장 지원 - 별도 모듈 불필요"),
                        Map.of("path", "/api/jackson3/optional",
                                "description", "Optional 내장 지원 - 별도 모듈 불필요"),
                        Map.of("path", "/api/jackson3/json-node",
                                "description", "새로운 JsonNode 메서드 - asShort(), asFloat(), Optional 변형"),
                        Map.of("path", "/api/jackson3/string-node",
                                "description", "StringNode - TextNode에서 이름 변경"),
                        Map.of("path", "/api/jackson3/defaults",
                                "description", "변경된 기본값 - FAIL_ON_UNKNOWN_PROPERTIES 등"),
                        Map.of("path", "/api/jackson3/records",
                                "description", "Record 네이티브 지원 - 추가 설정 불필요"),
                        Map.of("path", "/api/jackson3/package-changes",
                                "description", "패키지 변경 - com.fasterxml.jackson → tools.jackson"),
                        Map.of("path", "/api/jackson3/comparison",
                                "description", "Jackson 2 vs 3 종합 비교")
                )
        );
    }

    /**
     * 1. Immutable ObjectMapper 데모
     *
     * Jackson 3에서 ObjectMapper는 불변(Immutable)입니다.
     * Builder 패턴으로만 생성할 수 있으며, 생성 후 설정 변경이 불가능합니다.
     */
    @GetMapping("/immutable-mapper")
    public Jackson3Examples.ImmutableMapperDemo immutableMapper() {
        return jackson3Examples.demonstrateImmutableMapper();
    }

    /**
     * 2. Unchecked Exception 데모
     *
     * Jackson 3에서 예외가 RuntimeException을 상속합니다.
     * 더 이상 모든 곳에서 try-catch가 필요하지 않습니다.
     */
    @GetMapping("/unchecked-exception")
    public Jackson3Examples.ExceptionDemo uncheckedException() {
        return jackson3Examples.demonstrateUncheckedException();
    }

    /**
     * 3. java.time 내장 지원 데모
     *
     * Jackson 3에서는 java.time 타입이 기본 지원됩니다.
     * 별도의 jackson-datatype-jsr310 모듈이 필요 없습니다.
     */
    @GetMapping("/java-time")
    public Jackson3Examples.JavaTimeDemo javaTime() {
        return jackson3Examples.demonstrateJavaTimeSupport();
    }

    /**
     * 4. Optional 내장 지원 데모
     *
     * Jackson 3에서는 Optional 타입이 기본 지원됩니다.
     * 별도의 jackson-datatype-jdk8 모듈이 필요 없습니다.
     */
    @GetMapping("/optional")
    public Jackson3Examples.OptionalDemo optional() {
        return jackson3Examples.demonstrateOptionalSupport();
    }

    /**
     * 5. 새로운 JsonNode 메서드 데모
     *
     * Jackson 3에서는 JsonNode에 새로운 메서드들이 추가되었습니다.
     * - asShort(), asFloat()
     * - intValueOpt(), asFloatOpt() 등 Optional 변형
     */
    @GetMapping("/json-node")
    public Jackson3Examples.JsonNodeDemo jsonNode() {
        return jackson3Examples.demonstrateJsonNodeImprovements();
    }

    /**
     * 6. StringNode (구 TextNode) 데모
     *
     * Jackson 3에서 TextNode가 StringNode로 이름이 변경되었습니다.
     */
    @GetMapping("/string-node")
    public Jackson3Examples.StringNodeDemo stringNode() {
        return jackson3Examples.demonstrateStringNode();
    }

    /**
     * 7. 변경된 기본값 데모
     *
     * Jackson 3에서 여러 기능의 기본값이 변경되었습니다.
     */
    @GetMapping("/defaults")
    public Jackson3Examples.DefaultsDemo defaults() {
        return jackson3Examples.demonstrateDefaultChanges();
    }

    /**
     * 8. Record 네이티브 지원 데모
     *
     * Jackson 3에서는 Java Record가 완벽히 네이티브 지원됩니다.
     */
    @GetMapping("/records")
    public Jackson3Examples.RecordDemo records() {
        return jackson3Examples.demonstrateRecordSupport();
    }

    /**
     * 9. 패키지/클래스 변경 정보
     *
     * Jackson 3에서는 패키지와 클래스 이름이 대폭 변경되었습니다.
     */
    @GetMapping("/package-changes")
    public Jackson3Examples.PackageChangeDemo packageChanges() {
        return jackson3Examples.demonstratePackageChanges();
    }

    /**
     * 10. Jackson 2 vs 3 종합 비교
     *
     * 주요 사용 사례별 Jackson 2와 3의 코드 비교
     */
    @GetMapping("/comparison")
    public Map<String, Object> comparison() {
        return Map.of(
                "title", "Jackson 2 vs Jackson 3 종합 비교",
                "comparisons", jackson3Examples.getComprehensiveComparison()
        );
    }

    /**
     * 예외 처리 테스트 엔드포인트
     *
     * 잘못된 JSON을 의도적으로 처리하여 Jackson 3의 예외 동작을 테스트합니다.
     */
    @PostMapping("/parse-test")
    public Map<String, Object> parseTest(@RequestBody String json) {
        try {
            // Jackson 3: JacksonException은 RuntimeException!
            var mapper = tools.jackson.databind.json.JsonMapper.builder().build();
            var result = mapper.readTree(json);
            return Map.of(
                    "success", true,
                    "parsed", result.toString()
            );
        } catch (JacksonException e) {
            // Unchecked exception - 이 catch는 선택적!
            return Map.of(
                    "success", false,
                    "error", e.getClass().getSimpleName(),
                    "message", e.getMessage()
            );
        }
    }

    /**
     * Record 직렬화/역직렬화 테스트
     */
    @PostMapping("/record-test")
    public Jackson3Examples.Person recordTest(@RequestBody Jackson3Examples.Person person) {
        // Jackson 3: Record 타입이 그냥 작동합니다!
        // 추가 설정이나 모듈 등록 불필요
        return person;
    }
}
