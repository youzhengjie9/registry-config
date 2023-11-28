package com.client.misc;

import com.common.dto.UserLoginDTO;
import com.common.vo.TokenVO;

public interface LoginService {

    TokenVO login(UserLoginDTO userLoginDTO);


}
