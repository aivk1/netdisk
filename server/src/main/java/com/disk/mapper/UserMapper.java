package com.disk.mapper;

import com.disk.annotation.AutoFill;
import com.disk.dto.UserDTO;
import com.disk.dto.UserLoginDTO;
import com.disk.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.disk.entity.User;

@Mapper
public interface UserMapper {
    @Select("select * from user where email=#{account} or phone=#{account}")
    User login(UserLoginDTO userLoginDTO);
    @AutoFill(OperationType.INSERT)
    void save(User user);
    @AutoFill(OperationType.UPDATE)
    void update(User user);
    @Select("select * from user where id = #{id}")
    User findById(Long id);
}

