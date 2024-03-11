package com.disk.service.impl;

import com.disk.constant.MessageConstant;
import com.disk.constant.StatusConstant;
import com.disk.context.BaseContext;
import com.disk.dto.UserDTO;
import com.disk.dto.UserLoginDTO;
import com.disk.entity.FileMessage;
import com.disk.entity.User;
import com.disk.exception.AccountLockedException;
import com.disk.exception.AccountNotFoundException;
import com.disk.exception.PasswordErrorException;
import com.disk.mapper.FileMapper;
import com.disk.mapper.UserMapper;
import com.disk.service.UserService;
import com.disk.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private AliOssUtil aliOssUtil;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String password = userLoginDTO.getPassword();
        User user = userMapper.login(userLoginDTO);
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if(user==null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if(!password.equals(user.getPassword())){
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        if(user.getStatus()== StatusConstant.DISABLE){
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }
        return user;
    }

    public void save(UserDTO userDTO){
        User user = User.builder()
                .userName(userDTO.getUserName())
                .phone(userDTO.getPhone())
                .email(userDTO.getEmail())
                .sex(userDTO.getSex())
                .status(1)
                .build();
        String password = DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes());
        user.setPassword(password);
        userMapper.save(user);
        aliOssUtil.createFolder(user.getId()+"/");
        FileMessage fileMessage = FileMessage.builder()
                .userId(user.getId())
                .filePath(user.getId()+"/")
                .fileName(user.getId()+"/")
                .uploadTime(LocalDateTime.now())
                .build();
        fileMapper.insert(fileMessage);
    }

    public void update(UserDTO userDTO){
        User user = User.builder()
                .id(BaseContext.getCurrentId())
                .userName(userDTO.getUserName())
                .sex(userDTO.getSex())
                .build();
        userMapper.update(user);
    }


}
