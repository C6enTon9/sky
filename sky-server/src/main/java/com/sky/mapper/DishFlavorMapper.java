package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量添加口味
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 批量删除口味
     * @param ids
     */
    void deleteBatchByDishId(List<Long> ids);


    /**
     * 根据菜品id查询口味信息
     * @param id
     * @return
     */
    List<DishFlavor> selectByDishId(Long id);
}
