// 스프링부트 CORS 처리

package com.example.demo;

import java.io.IOException;
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

@Component // 스프링의 컴포넌트로 등록하여 스프링 컨테이너에서 관리하도록 함
@Order(Ordered.HIGHEST_PRECEDENCE) // 필터의 순서를 설정, 가장 높은 우선순위를 가짐
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
        throws IOException, ServletException {
        
        final HttpServletResponse response = (HttpServletResponse) res; // ServletResponse를 HttpServletResponse로 캐스팅

        // CORS 설정
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000"); // 허용할 출처 설정
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE"); // 허용할 HTTP 메서드 설정
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, AUTH-TOKEN"); // 허용할 헤더 설정
        response.setHeader("Access-Control-Max-Age", "3600"); // preflight 요청의 유효 기간 설정 (초 단위)
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 자격 증명(쿠키 등) 허용 설정

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
