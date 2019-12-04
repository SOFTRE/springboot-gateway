package com.xxm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 描述
 *
 * @author Mr Liu
 * @version 1.0
 * @package com.xxm *
 * @Date 2019-12-2
 * @since 1.0
 */
@SpringBootApplication
@EnableEurekaClient
public class GateWayWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(GateWayWebApplication.class, args);
    }

    /**
     * keyresolve 定义限流的身份的标识 :可以按照用户名 或者 ip .这里使用IP地址来限制
     *
     * @return
     */
    @Bean(name = "ipKeyResolver")
    public KeyResolver keyResolver() {
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                ServerHttpRequest request = exchange.getRequest();
                String hostString = request.getRemoteAddress().getHostString();//ip地址
                return Mono.just(hostString);
            }
        };
    }
}
