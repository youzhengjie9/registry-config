package com.console.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 配置版本列表项VO
 *
 * @author youzhengjie
 * @date 2023/10/19 17:25:32
 */
public class ConfigVersionListItemVO implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 配置版本id
     */
    private Long id;

    /**
     * 配置所属的命名空间id
     */
    private String namespaceId;

    /**
     * 配置所属的分组名称
     */
    private String groupName;

    /**
     * dataId。也就是配置的名称
     */
    private String dataId;

    /**
     * 配置内容
     */
    private String content;

    /**
     * 这个配置被执行了什么操作（比如新增、修改、删除）
     */
    private String operationType;

    /**
     * 操作这个配置的时间
     */
    private LocalDateTime operationTime;

    public ConfigVersionListItemVO() {
    }

    public ConfigVersionListItemVO(Long id, String namespaceId, String groupName, String dataId, String content, String operationType, LocalDateTime operationTime) {
        this.id = id;
        this.namespaceId = namespaceId;
        this.groupName = groupName;
        this.dataId = dataId;
        this.content = content;
        this.operationType = operationType;
        this.operationTime = operationTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ConfigVersionListItemVO{" +
                "id=" + id +
                ", namespaceId='" + namespaceId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", dataId='" + dataId + '\'' +
                ", content='" + content + '\'' +
                ", operationType='" + operationType + '\'' +
                ", operationTime=" + operationTime +
                '}';
    }
}
