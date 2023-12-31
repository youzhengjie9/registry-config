package com.console.controller;

import com.common.constant.ParentMappingConstants;
import com.common.dto.CreateNamespaceDTO;
import com.common.dto.ModifyNamespaceDTO;
import com.common.entity.Namespace;
import com.common.utils.Result;
import com.console.service.NamespaceService;
import com.console.vo.NamespaceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 命名空间控制器
 *
 * @author youzhengjie
 * @date 2023/06/19 22:23:38
 */
@RestController
@RequestMapping(path = ParentMappingConstants.NAMESPACE_CONTROLLER)
public class NamespaceController {

    @Autowired
    private NamespaceService namespaceService;

    /**
     * 获取所有命名空间
     *
     * @return {@link Result}<{@link List}<{@link Namespace}>>
     */
    @PreAuthorize("@pms.hasPermission('namespace:list')")
    @GetMapping("/getNamespaceList")
    public Result<List<NamespaceVO>> getNamespaceList() {
        return Result.ok(namespaceService.getNamespaceList());
    }

    @PreAuthorize("@pms.hasPermission('namespace:add')")
    @PostMapping("/createNamespace")
    public Result<Boolean> createNamespace(@RequestBody CreateNamespaceDTO createNamespaceDTO){
        // 校验必填项
        createNamespaceDTO.validateRequired();
        // 填充默认值
        createNamespaceDTO.fillDefaultValue();
        // 构建Namespace对象
        Namespace namespace = createNamespaceDTO.buildNamespaceByCreateNamespaceDTO();
        return Result.ok(namespaceService.createNamespace(namespace));
    }

    @PreAuthorize("@pms.hasPermission('namespace:modify')")
    @PutMapping("/modifyNamespace")
    public Result<Boolean> modifyNamespace(@RequestBody ModifyNamespaceDTO modifyNamespaceDTO){
        // 校验必填项
        modifyNamespaceDTO.validateRequired();
        // 填充默认值
        modifyNamespaceDTO.fillDefaultValue();
        // 构建Namespace对象
        Namespace namespace = modifyNamespaceDTO.buildNamespaceByModifyNamespaceDTO();
        return Result.ok(namespaceService.modifyNamespace(namespace));
    }

    @PreAuthorize("@pms.hasPermission('namespace:delete')")
    @DeleteMapping("/deleteNamespace")
    public Result<Boolean> deleteNamespace(@RequestParam("namespaceId") String namespaceId){

        return Result.ok(namespaceService.deleteNamespace(namespaceId));
    }


}
