package com.disk.service.impl;

import com.disk.constant.FileTypeConstant;
import com.disk.constant.MessageConstant;
import com.disk.constant.StatusConstant;
import com.disk.context.BaseContext;
import com.disk.dto.UserDTO;
import com.disk.dto.UserLoginDTO;
import com.disk.dto.UserUpdateDTO;
import com.disk.entity.FileMessage;
import com.disk.entity.User;
import com.disk.entity.Verification;
import com.disk.exception.AccountLockedException;
import com.disk.exception.AccountNotFoundException;
import com.disk.exception.PasswordErrorException;
import com.disk.exception.UpdateUserFailException;
import com.disk.mapper.FileMapper;
import com.disk.mapper.UserMapper;
import com.disk.mapper.VerificationMapper;
import com.disk.service.UserService;
import com.disk.utils.AliOssUtil;
import com.disk.utils.EmailUtil;
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
    private VerificationMapper verificationMapper;
    @Autowired
    private EmailUtil emailUtil;
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

        String folderPath = user.getId()+"/";
//        String url = aliOssUtil.createFolder(folderPath);
        FileMessage fileMessage = FileMessage.builder()
                .userId(user.getId())
                .url("")
                .filePath("")
                .fileName(folderPath)
                .filePathId(-1l)
                .uploadTime(LocalDateTime.now())
                .fileType(FileTypeConstant.FOLDER_TYPE)
                .fileSize(0.0)
                .build();
        fileMapper.insert(fileMessage);
    }
    @Transactional
    public void update(UserUpdateDTO userUpdateDTO){
        //验证
        User user = userMapper.findById(userUpdateDTO.getId());
        Verification verification = verificationMapper.getByEmail(userUpdateDTO.getEmail());
        if(userUpdateDTO.getVerification() != verification.getVerificationValue()){//验证通过
            throw new UpdateUserFailException(MessageConstant.VERIFY_FAILED);
        }
        try{
            emailUtil.sendEmail(userUpdateDTO.getEmail(), userUpdateDTO.getPhone(), userUpdateDTO.getEmail());
        }catch (Exception e){
            throw new UpdateUserFailException(MessageConstant.SEND_EMAIL_FAILED);
        }

        User new_user = User.builder()
                .id(BaseContext.getCurrentId())
                .userName(userUpdateDTO.getUserName())
                .password(DigestUtils.md5DigestAsHex(userUpdateDTO.getPassword().getBytes()))
                .phone(userUpdateDTO.getPhone())
                .email(userUpdateDTO.getEmail())
                .build();
        userMapper.update(user);
    }


}
