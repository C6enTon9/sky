package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

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

    /**
     * 根据id查询user对象
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 查询日期内全部
     * @param beginTime
     * @param endTime
     * @return
     */
    Integer countUserNumber(LocalDateTime beginTime, LocalDateTime endTime);
}
