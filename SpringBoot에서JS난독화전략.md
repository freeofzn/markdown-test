
# JS 난독화 전략

## 1. `static` 폴더를 로컬/운영 환경으로 구분

- `static/local` : 로컬 개발 시 사용
- `static/prod` : 운영 빌드시 사용 (Python으로 JS 난독화 생성)

## 2. `application.yml` 설정: Profile별로 `static` 경로 구분 설정

### 기본 설정 (로컬 profile)
```yaml
spring:
  web:
    resources:
      static-locations: classpath:/static/local/
```

### 운영 환경 설정 (prod profile)
```yaml
spring:
  web:
    resources:
      static-locations: classpath:/static/prod/
```

## 3. 운영 실행 시 `prod` 프로필 지정
운영 환경에서 `/static/prod` 경로를 사용하도록 `prod` 프로필을 지정하여 실행:
```bash
java -jar demo.jar --spring.profiles.active=prod
```

## 4. (옵션) Maven 빌드 시 로컬 파일 제거

운영 빌드시 `local` 폴더가 불필요하므로 제외 처리:

### 4.1 `pom.xml` 하단에 추가
```xml
<profiles>
    <profile>
        <id>prod</id>
        <build>
            <resources>
                <resource>
                    <directory>src/main/resources</directory>
                    <excludes>
                        <exclude>static/local/**</exclude> <!-- local 폴더 제외 -->
                    </excludes>
                </resource>
            </resources>
        </build>
    </profile>
</profiles>
```

### 4.2 Maven 빌드 명령
Maven 빌드 시 `prod` 프로필을 사용하여 빌드:
```bash
mvn clean install -P prod
```
