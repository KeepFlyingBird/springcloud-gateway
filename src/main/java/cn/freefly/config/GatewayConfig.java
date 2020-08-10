package cn.freefly.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 自定义过滤器-resilience4j 限流
 */
@Configuration
@Slf4j
public class GatewayConfig {
    @Autowired
    CustomGatewayFilter customGatewayFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2020, 8, 6, 0, 0, 0,123, zoneId);
        return builder.routes()
                .route("consul-service-consumer"
                        ,r-> r.path("/consul-gateway/**").and().after(zonedDateTime)//断言
                                .filters(f-> f.filter(customGatewayFilter) //过滤器
                                        .stripPrefix(1)
                                        .hystrix(config->{
                                            config.setName("hystrix-cmd");
                                            config.setFallbackUri("forward:/api/gateway/fallback");
                                        }))
                                .uri("lb://consul-service-consumer") // 路径
                        ).build();
    }
}
