package cn.freefly;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;

@SpringBootTest
class GatewayApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("时区时间："+ZonedDateTime.now());
    }

}
