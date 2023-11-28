package com.client.misc;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.client.http.Requester;
import com.client.http.RestResult;
import com.common.constant.ParentMappingConstants;
import com.common.constant.RequestMethodConstants;
import com.common.dto.UserLoginDTO;
import com.common.vo.TokenVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录服务
 *
 * @author youzhengjie
 * @date 2023/11/01 00:04:27
 */
public class GraceLoginService implements LoginService {

    private static final Logger log = LoggerFactory.getLogger(GraceLoginService.class);

    private final Requester requester;

    public GraceLoginService(String consoleAddress) {
        this.requester = new Requester(consoleAddress);
    }

    public GraceLoginService(Properties properties) {
        this.requester = new Requester(properties);
    }

    @Override
    public TokenVO login(UserLoginDTO userLoginDTO) {
        final Map<String, String> requestBodyMap = new ConcurrentHashMap<>(32);
        requestBodyMap.put("username", userLoginDTO.getUsername());
        requestBodyMap.put("password", userLoginDTO.getPassword());
        RestResult<Object> result =
                requester.requestApi(ParentMappingConstants.USER_CONTROLLER + "/login",
                RequestMethodConstants.POST, null, null,requestBodyMap);
        JSONObject jsonObject = (JSONObject) result.getData();
        return JSONObject.parseObject(jsonObject.toJSONString(),TokenVO.class);
    }
}
