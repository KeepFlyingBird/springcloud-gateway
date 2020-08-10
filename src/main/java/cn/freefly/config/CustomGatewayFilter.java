package cn.freefly.config;

import cn.freefly.dto.BaseResponse;
import cn.freefly.enums.ResCodeEnum;
import com.alibaba.fastjson.JSON;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Component
@Slf4j
public class CustomGatewayFilter implements GatewayFilter, Ordered {
    @Autowired //注入限流器注册机
    private RateLimiterRegistry rateLimiterRegistry = null;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst("token");
        log.info("获取自定义全局过滤器中token:{}",token);
        BaseResponse baseResponse = rateLimit();//执行限速逻辑
        if (!ResCodeEnum.成功.getCode().equals(baseResponse.getResCode())) {
            log.info("限流code：{},msg:{}",baseResponse.getResCode(),baseResponse.getResMsg());
            //获取相应对象
            ServerHttpResponse response = exchange.getResponse();
            //响应码为429  请求过多
            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            //响应类型为JSON
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
            //转换为Json字符串
            String body = JSON.toJSONString(baseResponse);
            //响应数据放入缓冲区
            DataBuffer dataBuffer = response.bufferFactory().wrap(body.getBytes());
            //使用限流结果响应请求，不再继续执行过滤器链
            return response.writeWith(Mono.just(dataBuffer));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    /**
     * 限流逻辑-resilience4j
     * @return
     */
    private BaseResponse rateLimit() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("user");
        Callable<BaseResponse> call = RateLimiter.decorateCallable(rateLimiter, () -> BaseResponse.resSuccess("通过"));
        Try<BaseResponse> tryRes = Try.of(() -> call.call()).recover(ex -> BaseResponse.resFail("请求太多，不通过"));
        return tryRes.get();
    }
}
