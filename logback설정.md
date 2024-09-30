
# Spring Boot에서 Logback 로그 설정 및 log4jdbc를 사용한 쿼리 로깅

## 1. Logback 기본 설정
Spring Boot는 기본적으로 `logback`이 포함되어 있어, 별도의 추가 설치 없이 바로 사용할 수 있습니다. `lombok` 라이브러리를 사용 중이라면, 클래스 상단에 `@Slf4j` 어노테이션을 추가하여 쉽게 로그를 사용할 수 있습니다.

```java
@Slf4j
public class ExampleClass {
    public void logExample(int num) {
        log.info("num check: {}", num);  // 문자열 결합 시 + 대신 {} 사용
    }
}
```

## 2. log4jdbc를 사용한 쿼리 로깅

### 1) 의존성 추가

쿼리 로그를 출력하려면 `log4jdbc-log4j2` 라이브러리를 추가해야 합니다.

```xml
<dependency>
    <groupId>org.bgee.log4jdbc-log4j2</groupId>
    <artifactId>log4jdbc-log4j2-jdbc4.1</artifactId>
    <version>1.16</version>
</dependency>
```

### 2) `application.yml` 수정

`log4jdbc`를 통해 쿼리 로그를 출력하기 위해 데이터소스와 드라이버 설정을 아래와 같이 추가합니다.

```yaml
spring:
  datasource:
    url: jdbc:log4jdbc:mysql://localhost:3306/dbelespoglog?serverTimezone=UTC&characterEncoding=UTF-8
    username: user1
    password: 1234
    driver-class-name: net.sf.log4jdbc.sql.jdbcapi.DriverSpy

# Logback 설정 파일 위치 지정
logging.config: classpath:logback-local.xml
```

### 3) `log4jdbc.log4j2.properties` 파일 추가

다음과 같은 속성을 `src/main/resources` 경로에 있는 `log4jdbc.log4j2.properties` 파일에 추가합니다. (해당 파일은 삭제해도 큰 문제는 없습니다.)

```properties
log4jdbc.spylogdelegator.name=net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator
log4jdbc.dump.sql.maxlinelength=0
```

## 3. `logback.xml` 설정

다음은 `logback.xml` 파일을 통한 설정 예시입니다. 로그는 콘솔과 파일에 모두 기록되며, 쿼리 로그를 포함해 다양한 로그를 출력할 수 있습니다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <!-- 패턴 및 경로 설정 -->
    <property name="CONSOLE_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%5level) - %msg%n" />
    <property name="FILE_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} %5level - %msg%n" />
    <property name="LOG_PATH" value="logs" />
    <property name="FILE_NAME" value="${LOG_PATH}/application.log" />

    <!-- 콘솔 어펜더 설정 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${CONSOLE_PATTERN}</pattern>
        </encoder>
    </appender>

    <!-- 파일 어펜더 설정 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${FILE_NAME}</file>
        <encoder>
            <pattern>${FILE_PATTERN}</pattern>
        </encoder>

        <!-- 롤링 정책 설정 (크기 및 시간 기반) -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/application-%d{yyyy-MM-dd}-%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>300MB</totalSizeCap>
        </rollingPolicy>
    </appender>

    <!-- SQL 쿼리 로깅 설정 -->
    <logger name="jdbc.sqlonly" level="INFO" additivity="false">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </logger>

    <!-- SQL 결과 로깅 설정 -->
    <logger name="jdbc.resultsettable" level="INFO" additivity="false">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </logger>

    <!-- 기타 데이터베이스 관련 로거 끄기 -->
    <logger name="jdbc" level="OFF" />
    <logger name="jdbc.audit" level="OFF" />
    <logger name="jdbc.resultset" level="OFF" />
    <logger name="jdbc.connection" level="OFF" />

    <!-- 루트 로거 설정 -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>

</configuration>
```

이 설정을 통해 Spring Boot에서 콘솔 및 파일로 로깅을 출력하고, `log4jdbc`를 사용하여 SQL 쿼리 로그를 확인할 수 있습니다.

### 참고 사항
- `log4jdbc.log4j2.properties` 파일은 쿼리 로그 출력을 위한 추가 설정이지만, 삭제해도 기본 동작에는 문제가 없습니다.
- 로그 파일의 경로와 패턴, 크기, 유지 기간 등을 적절히 설정하여 원하는 로그 관리 방식을 구성할 수 있습니다.
