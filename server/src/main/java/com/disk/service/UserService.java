package com.disk.service;

import com.disk.dto.UserDTO;
import com.disk.dto.UserLoginDTO;
import com.disk.dto.UserUpdateDTO;
import com.disk.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    public User login(UserLoginDTO userLoginDTO);
    public void save(UserDTO userDTO);
    public void update(UserUpdateDTO userUpdateDTO);
}
