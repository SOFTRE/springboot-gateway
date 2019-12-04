package com.xxm.filter;

import com.xxm.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Program: IntelliJ IDEA xxm
 * @Description: TODO
 * @Author: Mr Liu
 * @Creed: Talk is cheap,show me the code
 * @CreateDate: 2019-12-02 23:20:05 周一
 * @LastModifyDate:
 * @LastModifyBy:
 * @Version: V1.0
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    //令牌头名字
    private static final String AUTHORIZE_TOKEN = "Authorization";

    /**
     * 全局过滤器
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取request、response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取请求的URI
        String path = request.getURI().getPath();
        //如果是登录、goods等开放的微服务[这里的goods部分开放],则直接放行,这里不做完整演示，完整演示需要设计一套权限系统
        if (path.startsWith("/api/user/login") || path.startsWith("/api/brand/search")) {
            //返回filter就是放行
            Mono<Void> filter = chain.filter(exchange);
            return filter;
        }
        //获取头文件中的令牌信息
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        if (StringUtils.isEmpty(token)) {
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }
        //从Cookie中获取令牌数据
        HttpCookie first = request.getCookies().getFirst(AUTHORIZE_TOKEN);
        if (first != null) {
            token = first.getValue();
        }
        //如果还是为空，则输出错误代码
        if (StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete();
        }
        //解析令牌数据
        try {
            Claims claims = JwtUtil.parseJWT(token);
            //将令牌数据添加到头文件中
            request.mutate().header(AUTHORIZE_TOKEN, claims.toString());
        } catch (Exception e) {
            e.printStackTrace();
            //解析失败,响应401错误
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //放行
        return chain.filter(exchange);
    }

    /**
     * 过滤执行顺序
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
