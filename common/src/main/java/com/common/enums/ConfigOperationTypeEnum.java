package com.common.enums;

/**
 * 配置操作类型枚举类（“历史配置”实体类需要用到）,也就是这个配置被执行了什么操作
 *
 * @author youzhengjie
 * @date 2023/10/19 22:51:11
 */
public enum ConfigOperationTypeEnum {

    INSERT("新增"),
    UPDATE("修改"),
    DELETE("删除");

    /**
     *
     */
    private String operationType;

    ConfigOperationTypeEnum(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
