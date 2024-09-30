
# Spring Boot에서 Logback 로그 설정 및 log4jdbc를 사용한 쿼리 로깅

## 1. Logback 기본 설정

Spring Boot는 기본적으로 `Logback`을 포함하고 있어, 별도의 추가 설치 없이 바로 사용할 수 있습니다. `Lombok` 라이브러리를 사용 중인 경우와 그렇지 않은 경우에 따라 로깅을 사용하는 방법을 나누어 설명합니다.

### 1.1 Lombok 사용 시 (`@Slf4j` 어노테이션)

`Lombok`을 사용하면 클래스에 `@Slf4j` 어노테이션을 추가해 자동으로 `Logger` 객체를 생성할 수 있습니다.

#### 사용 예시:

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExampleClass {
    public void logExample(int num) {
        log.info("num check: {}", num);  // 로그 출력
    }
}
```

- **장점**: 간결한 코드로 `log` 객체를 자동 생성
- **의존성 추가**: `Lombok`이 프로젝트에 포함되어 있어야 합니다.

#### Lombok 의존성 추가 (Maven):

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.26</version>  <!-- 최신 버전 사용 -->
    <scope>provided</scope>
</dependency>
```

### 1.2 Lombok 미사용 시 (`LoggerFactory` 수동 사용)

`Lombok`을 사용하지 않는 경우에는 `LoggerFactory`를 이용해 수동으로 `Logger` 객체를 생성해야 합니다.

#### 사용 예시:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleClass {
    private static final Logger log = LoggerFactory.getLogger(ExampleClass.class);

    public void logExample(int num) {
        log.info("num check: {}", num);  // 로그 출력
    }
}
```

- **장점**: Lombok 없이도 로그 사용 가능
- **단점**: `Logger` 객체를 수동으로 정의해야 하는 번거로움

---

## 2. log4jdbc를 사용한 쿼리 로깅

SQL 쿼리 로그를 출력하기 위해 `log4jdbc` 라이브러리를 설정할 수 있습니다.

### 2.1 의존성 추가 (Maven)

쿼리 로그 출력을 위해 `log4jdbc-log4j2` 라이브러리를 추가합니다.

```xml
<dependency>
    <groupId>org.bgee.log4jdbc-log4j2</groupId>
    <artifactId>log4jdbc-log4j2-jdbc4.1</artifactId>
    <version>1.16</version>
</dependency>
```

### 2.2 `application.yml` 수정

데이터소스 URL에 `log4jdbc`를 사용하도록 설정하고, MySQL 드라이버와 로그 설정을 추가합니다.

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

---

## 3. `logback.xml` 설정

다음은 콘솔과 파일에 로그를 출력하고, SQL 쿼리 로그를 포함하는 Logback 설정 예시입니다. 로그 파일의 경로, 패턴, 크기 등을 설정할 수 있습니다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <!-- 로그 패턴 및 경로 설정 -->
    <property name="CONSOLE_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%5level) - %msg%n" />
    <property name="FILE_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} %5level - %msg%n" />
    <property name="LOG_PATH" value="logs" />
    <property name="FILE_NAME" value="${LOG_PATH}/application.log" />

    <!-- 콘솔 로그 설정 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${CONSOLE_PATTERN}</pattern>
        </encoder>
    </appender>

    <!-- 파일 로그 설정 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${FILE_NAME}</file>
        <encoder>
            <pattern>${FILE_PATTERN}</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/application-%d{yyyy-MM-dd}-%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>300MB</totalSizeCap>
        </rollingPolicy>
    </appender>

    <!-- SQL 쿼리 로그 설정 -->
    <logger name="jdbc.sqlonly" level="INFO" additivity="false">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </logger>

    <!-- SQL 결과 로깅 설정 -->
    <logger name="jdbc.resultsettable" level="INFO" additivity="false">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </logger>

    <!-- 기타 데이터베이스 관련 로그 끄기 -->
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

---

### 참고 사항
- **Lombok을 사용하는 경우**: `@Slf4j`를 사용해 로그 객체를 자동으로 생성할 수 있습니다.
- **Lombok을 사용하지 않는 경우**: `LoggerFactory`를 이용해 직접 로그 객체를 정의해야 합니다.
- **SQL 쿼리 로깅**: `log4jdbc`를 통해 SQL 쿼리 로그를 기록할 수 있습니다.
- **로그 파일 관리**: 로그 파일의 크기, 보관 기간, 경로 등을 `logback.xml`에서 설정하여 로그 관리를 효율적으로 할 수 있습니다.
