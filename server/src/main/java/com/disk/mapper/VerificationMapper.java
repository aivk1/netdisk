package com.disk.mapper;

import com.disk.dto.VerificationDTO;
import com.disk.entity.Verification;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VerificationMapper {
    @Insert("insert into verification(email, verificationValue, create_time) values(#{email},#{verificationValue}, #{createTime})")
    void insert(Verification verification);
    @Delete("delete from verification where email=#{email}")
    void delete(String email);
    @Select("select * from verification where email=#{email} order by create_time DESC LIMIT 1")
    Verification getByEmail(String email);
    @Delete("delete from verification")
    void deleteAll();
}
