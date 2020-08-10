package cn.freefly.dto;

import cn.freefly.enums.ResCodeEnum;
import lombok.Data;

@Data
public class BaseResponse {
    public String resCode;
    public String resMsg;
    public Object resData;

    public static cn.freefly.dto.BaseResponse resSuccess(String resMsg){
        return resSuccess(resMsg,null);
    }
    public static cn.freefly.dto.BaseResponse resSuccess(String resMsg, Object resData){
        cn.freefly.dto.BaseResponse baseResponse = new cn.freefly.dto.BaseResponse();
        baseResponse.setResCode(ResCodeEnum.成功.getCode());
        baseResponse.setResMsg(resMsg);
        baseResponse.setResData(resData);
        return baseResponse;
    }
    public static BaseResponse resFail(String resMsg){
        return resFail(resMsg,null);
    }
    public static BaseResponse resFail(String resMsg,Object resData){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setResCode(ResCodeEnum.失败.getCode());
        baseResponse.setResMsg(resMsg);
        baseResponse.setResData(resData);
        return baseResponse;
    }
}
