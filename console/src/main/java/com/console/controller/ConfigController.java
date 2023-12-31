package com.console.controller;

import com.common.constant.Constants;
import com.common.constant.ParentMappingConstants;
import com.common.dto.ClientAddressBindConfigDTO;
import com.common.dto.CloneConfigDTO;
import com.common.dto.PublishConfigDTO;
import com.common.entity.Config;
import com.common.utils.PageData;
import com.common.utils.Result;
import com.console.cache.CacheConfigClientAddress;
import com.console.service.ConfigService;
import com.console.vo.ConfigListItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * 配置中心控制器
 *
 * @author youzhengjie
 * @date 2023/10/17 20:35:37
 */
@RestController
@RequestMapping(ParentMappingConstants.CONFIG_CONTROLLER)
public class ConfigController {

    @Autowired
    private ConfigService configService;

    private final CacheConfigClientAddress cacheConfigClientAddress = CacheConfigClientAddress.getSingleton();

    /**
     * 客户端地址绑定配置
     *
     * @return {@link Result}<{@link Boolean}>
     */
    @PostMapping("/clientAddressBindConfig")
    public Result<Boolean> clientAddressBindConfig(
            @RequestBody ClientAddressBindConfigDTO clientAddressBindConfigDTO){
        String namespaceId = clientAddressBindConfigDTO.getNamespaceId();
        String groupName = clientAddressBindConfigDTO.getGroupName();
        String dataId = clientAddressBindConfigDTO.getDataId();
        String clientAddress = clientAddressBindConfigDTO.getClientAddress();
        // 将客户端地址绑定配置
        Boolean bindResult = cacheConfigClientAddress.
                clientAddressBindConfig(namespaceId, groupName, dataId, clientAddress);
        return Result.ok(bindResult);
    }

    /**
     * 发布配置（创建配置和修改配置都是调用这个接口）
     *
     * @param publishConfigDTO publishConfigDTO
     * @return {@link Boolean}
     */
    @PreAuthorize("@pms.hasPermission('config:publish')")
    @PostMapping("/publishConfig")
    public Result<Boolean> publishConfig(@RequestBody PublishConfigDTO publishConfigDTO, HttpServletRequest request) {
        // 校验必填项
        publishConfigDTO.validateRequired();
        // 填充默认值
        publishConfigDTO.fillDefaultValue();
        // 构建对象
        Config config = publishConfigDTO.buildConfigByPublishConfigDTO();
        // 发布配置
        return Result.ok(configService.publishConfig(config, request));
    }

    /**
     * 模糊获取配置列表
     *
     * @param namespaceId namespaceId（精确搜索）
     * @param groupName   groupName
     * @param dataId      dataId
     * @param page        当前页（最小页是: 1）
     * @param size        每一页的大小（最小值为: 1）
     * @param fuzzySearch 是否为模糊查询
     * @return {@link Result}<{@link PageData}<{@link ConfigListItemVO}>>
     */
    @PreAuthorize("@pms.hasPermission('config:list')")
    @GetMapping("/getConfigList")
    public Result<PageData<ConfigListItemVO>> getConfigList(
            @RequestParam(value = "namespaceId", required = false, defaultValue = Constants.DEFAULT_NAMESPACE_ID) String namespaceId,
            @RequestParam(value = "groupName", required = false, defaultValue = "") String groupName,
            @RequestParam(value = "dataId",required = false, defaultValue = "") String dataId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "fuzzySearch",required = false,defaultValue = "true") Boolean fuzzySearch) {
        // 如果 page < 1 ,则要把page恢复成 1 ,因为page的最小值就为 1
        if(page < 1){
            page = 1;
        }
        // 如果 size <= 0 ,则要把size恢复成 1 ,因为size的最小值为 1
        if(size <= 0 ){
            size = 1;
        }
        // 将当前页转成MySQL分页的起始位置！
        page = (page-1)*size;
        // 对size的大小进行限制,防止一次性获取太多的数据（下面的代码意思是一次“最多”获取500条记录,如果size的值小于500,则size还是原来的值不变）
        size = Math.min(size,500);
        PageData<ConfigListItemVO> pageData = new PageData<>();
        pageData.setPagedList(configService.getConfigListItemVOByPage(namespaceId, groupName, dataId, page, size,fuzzySearch));
        pageData.setTotalCount(configService.getConfigTotalCount(namespaceId, groupName, dataId,fuzzySearch));
        
        return Result.ok(pageData);
    }

    /**
     * 导入配置
     *
     * @param configConflictPolicy 配置冲突策略
     * @param configFile 单个配置文件
     * @return {@link Result}<{@link Boolean}>
     */
    @PreAuthorize("@pms.hasPermission('config:import')")
    @PostMapping("/importConfig")
    public Result<Boolean> importConfig(String namespaceId,String groupName,
                                        MultipartFile configFile,String configConflictPolicy,
                                        HttpServletRequest request){

        return Result.ok(configService.importConfig(namespaceId,groupName,configFile, configConflictPolicy,request));
    }

    /**
     * 导出被选择的配置
     *
     * @param exportConfigIdArray 导出的配置的id数组
     * @return {@link Result}<{@link Boolean}>
     */
    @PreAuthorize("@pms.hasPermission('config:export')")
    @GetMapping("/exportSelectedConfig")
    public ResponseEntity<FileSystemResource> exportSelectedConfig(
            @RequestParam("exportConfigIdArray[]") String[] exportConfigIdArray,
            HttpServletResponse response) throws IOException {

        // 导出配置
        return configService.exportSelectedConfig(Arrays.asList(exportConfigIdArray), response);
    }

    @PreAuthorize("@pms.hasPermission('config:detail')")
    @GetMapping("/getConfig")
    public Result<Config> getConfig(
            @RequestParam(value = "namespaceId", required = false, defaultValue = Constants.DEFAULT_NAMESPACE_ID) String namespaceId,
            @RequestParam(value = "groupName", required = false, defaultValue = "") String groupName,
            @RequestParam(value = "dataId",required = false, defaultValue = "") String dataId){

        return Result.ok(configService.getConfig(namespaceId, groupName, dataId));
    }

    /**
     * 删除配置
     *
     * @param namespaceId namespaceId
     * @param groupName groupName
     * @param dataId dataId
     * @param request request
     * @return {@link Result}<{@link Boolean}>
     */
    @PreAuthorize("@pms.hasPermission('config:delete')")
    @DeleteMapping("/deleteConfig")
    public Result<Boolean> deleteConfig(
            @RequestParam(value = "namespaceId", required = false, defaultValue = Constants.DEFAULT_NAMESPACE_ID) String namespaceId,
            @RequestParam(value = "groupName", required = false, defaultValue = "") String groupName,
            @RequestParam(value = "dataId",required = false, defaultValue = "") String dataId,
            HttpServletRequest request){


        return Result.ok(configService.deleteConfig(namespaceId,groupName,dataId,request));
    }

    /**
     * 批量删除配置
     *
     * @param batchDeleteConfigIdArray 批量删除的配置的id数组
     * @return {@link Result}<{@link Boolean}>
     */
    @PreAuthorize("@pms.hasPermission('config:delete')")
    @DeleteMapping("/batchDeleteConfig")
    public Result<Boolean> batchDeleteConfig(
            @RequestParam("batchDeleteConfigIdArray[]") String[] batchDeleteConfigIdArray,
            HttpServletRequest request){

        return Result.ok(configService.batchDeleteConfig(Arrays.asList(batchDeleteConfigIdArray), request));
    }

    /**
     * 克隆配置
     *
     * @param cloneConfigDTO 克隆配置DTO
     * @return {@link Result}<{@link Boolean}>
     */
    @PreAuthorize("@pms.hasPermission('config:clone')")
    @PostMapping("/cloneConfig")
    public Result<Boolean> cloneConfig(@RequestBody CloneConfigDTO cloneConfigDTO, HttpServletRequest request){
        return Result.ok(configService.cloneConfig(cloneConfigDTO,request));
    }


}
