## springboot 에서 logback log 사용하기

1. spring web 의존성 설치시 logback 이 포함되어 있어 사용가능

2. lombok 이 설치되어 있다면 클래스 위에 @Slf4j 어노테이션 붙이고

3. 로그사용

log.info("num check: {}", num);  // + 로 하지말고 {} 로 할것

## 쿼리로그 출력하기 (log4jdbc 사용)

1. 의존성추가

<dependency>
	<groupId>org.bgee.log4jdbc-log4j2</groupId>
	<artifactId>log4jdbc-log4j2-jdbc4.1</artifactId>
	<version>1.16</version>
</dependency>	

2. application.yml 수정

# 1) datasource 에 log4jdbc 및 driver class name 설정

  datasource:
    url: jdbc:log4jdbc:mysql://localhost:3306/dbelespoglog?serverTimezone=UTC&characterEncoding=UTF-8
    username: user1
    password: 1234
    driver-class-name: net.sf.log4jdbc.sql.jdbcapi.DriverSpy

# 2) logback 설정파일 위치 지정

logging.config: classpath:logback-local.xml 

3. src/main/resources 에 log4jdbc.log4j2.properties 추가(근데 삭제해도 되는데? -> ai 확인하기)

log4jdbc.spylogdelegator.name=net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator
log4jdbc.dump.sql.maxlinelength=0

4. logback.xml 설정

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
