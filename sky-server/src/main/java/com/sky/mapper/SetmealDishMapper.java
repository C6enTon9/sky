package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
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

    /**
     * 新增菜品--向setmeal_dish表中插入数据
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据id查询套餐信息--根据套餐id查询setmeal_dish表中套餐内菜品的信息
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);


    /**
     * 根据套餐id删除setmeal_dish中的信息
     * @param id
     */
    void deleteBatchBySetmealId(Long id);
}
