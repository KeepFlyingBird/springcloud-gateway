package cn.freefly.controller;

import cn.freefly.dto.BaseResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {
    @RequestMapping("/fallback")
    public BaseResponse hystrixFallBack(){
        return BaseResponse.resFail("路由失败，请检查服务器状况！");
    }
}
