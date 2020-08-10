package cn.freefly.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResCodeEnum {
    成功("0000","成功"),
    失败("9999","失败");

    public String code;
    public String msg;

}
