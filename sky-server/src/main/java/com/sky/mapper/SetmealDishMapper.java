package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id统计套餐中该菜品的数量 before的写法
     * @param id
     * @return
     */
    @Select("select count(id) from sky_take_out.setmeal_dish where dish_id = #{id}")
    int countByDishId(Long id);

    /**
     * 根据菜品id查询对应的套餐id
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);
}
