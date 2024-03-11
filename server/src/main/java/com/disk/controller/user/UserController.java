package com.disk.controller.user;

import com.disk.constant.JwtClaimsConstant;
import com.disk.dto.UserDTO;
import com.disk.dto.UserLoginDTO;
import com.disk.entity.User;
import com.disk.properties.JwtProperties;
import com.disk.result.Result;
import com.disk.service.UserService;
import com.disk.utils.JwtUtil;
import com.disk.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 测试接口
     * @return
     */
    @PostMapping("/test")
    public Result test(){
        return Result.success();
    }

    /**
     * 用户登录接口
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserLoginDTO userLoginDTO){

        User user = userService.login(userLoginDTO);

        //登录成功后生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        UserVO userVO = UserVO.builder()
                .userName(user.getUserName())
                .id(user.getId())
                .token(token)
                .build();
        return Result.success(userVO);

    }

    /**
     * 用户注册接口
     * @param userDTO
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestBody UserDTO userDTO){
        userService.save(userDTO);
        return Result.success();
    }

    /**
     * 修改用户信息接口
     * @param userDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody UserDTO userDTO){
        userService.update(userDTO);
        return Result.success();
    }

}
