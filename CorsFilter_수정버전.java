package com.example.springjwt.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CorsFilter.java
 * 
 * 이 클래스는 Cross-Origin Resource Sharing (CORS) 설정을 관리하는 필터입니다.
 * 클라이언트와 서버 간의 요청을 안전하게 허용하기 위한 규칙을 설정합니다.
 * 
 * 작성자: poglog
 * 작성일: 24/10/13
 */
@Component // 스프링의 컴포넌트로 등록하여 스프링 컨테이너에서 관리하도록 함
@Order(Ordered.HIGHEST_PRECEDENCE) // 필터의 순서를 설정, 가장 높은 우선순위를 가짐
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
        throws IOException, ServletException {
        
    	final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res; // ServletResponse를 HttpServletResponse로 캐스팅
        
    	List<String> allowedOrigins = Arrays.asList(
    	    "http://127.0.0.1:5500", 
    	    "http://localhost:8080", 
    	    "https://my.site.io", 
    	    "http://my.site.io" // 모바일 사파리에서는 http 로 들어온다 !!! 이건 무슨 상황 ???
    	);        

    	String forwardedProto = request.getHeader("X-Forwarded-Proto");
    	String origin = request.getHeader("Origin");
    	System.out.println("forwardedProto: " + forwardedProto);  // 로그로 Origin 값을 확인
    	System.out.println("Origin: " + origin);  // 로그로 Origin 값을 확인
        if (allowedOrigins.contains(origin)) {
        	System.out.println("if allow Origin: " + origin);  // 로그로 Origin 값을 확인
            response.setHeader("Access-Control-Allow-Origin", origin); // 허용할 출처 설정
        }
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE"); // 허용할 HTTP 메서드 설정
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, AUTH-TOKEN"); // 허용할 헤더 설정   
        response.setHeader("Access-Control-Max-Age", "3600"); // preflight 요청의 유효 기간 설정 (초 단위)
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 자격 증명(쿠키 등) 허용 설정
        // 이걸 추가해줘야 JS 에서 response header 중 Authorization 를 읽을 있음 
        response.setHeader("Access-Control-Expose-Headers", "Authorization"); // Authorization 헤더 노출 설정 추가 !! 이걸 추가해줘야 프론트에서 Authorization header 를 읽을 수 있음 !!!

        // OPTIONS 메서드 요청 처리 (preflight 요청)
        if ("OPTIONS".equalsIgnoreCase(((HttpServletRequest) req).getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK); // OPTIONS 요청에 대한 응답 상태 코드 설정
        } else {
            chain.doFilter(req, res); // 다른 요청은 필터 체인의 다음 필터로 전달
        }
    }

    @Override
    public void destroy() {
        // 필터가 소멸될 때 호출되는 메서드, 자원 정리를 위한 코드 작성 가능
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        // 필터가 초기화될 때 호출되는 메서드, 초기화 작업 수행 가능
    }
}
