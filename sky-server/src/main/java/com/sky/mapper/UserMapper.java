package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 通过openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from sky_take_out.user where openid = #{openid}")
    User selectByOpenid(String openid);

    /**
     * 新增user对象
     * @param user
     */
    void insert(User user);
}
