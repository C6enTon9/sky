package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Max;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //向菜品表插入一条数据
        dishMapper.insert(dish);

        //获得回显的菜品id
        Long dishId = dish.getId();

        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if (flavors != null & flavors.size() > 0){
            flavors.forEach(flavor ->{
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {

        //分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        //VO是后端返回给前端的数据
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
//            if (setmealDishMapper.countByDishId(id) > 0){
//                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
//            }
            //判断菜品的状态 若为起售中则不能删除
            Dish dish = dishMapper.selectById(id);
            if(Objects.equals(dish.getStatus(), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断当前菜品是否能够删除--是否关联套餐？
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);

        if(setmealIds != null & setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        dishMapper.deleteBatchById(ids);
        dishFlavorMapper.deleteBatchByDishId(ids);

    }

    /**
     * 根据id查询菜品信息以及口味信息
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {

        //查询菜品信息
        Dish dish = dishMapper.selectById(id);

        //查询口味信息
        List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(id);

        //封装数据
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Transactional
    @AutoFill(OperationType.UPDATE)
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {

        //修改dish表信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        //修改口味表信息

        //先删除
        List<Long> ids = new ArrayList<>();
        ids.add(dish.getId());
        dishFlavorMapper.deleteBatchByDishId(ids);

        //再插入
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors != null & flavors.size() > 0){
            flavors.forEach(flavor ->{
                flavor.setDishId(dish.getId());
            });
        }

        dishFlavorMapper.insertBatch(flavors);

    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {

        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);

    }


    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> list(Long categoryId) {

        List<DishVO> list = dishMapper.list(categoryId);
        return list;
    }


    /**
     * C端根据分类id查询分类下全部菜品
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {

        List<Dish> dishList = dishMapper.listWithStatus(dish);

        ArrayList<DishVO> dishVOList = new ArrayList<>();
        dishList.forEach(d -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectByDishId(d.getId());
            dishVO.setFlavors(dishFlavors);
            dishVOList.add(dishVO);

        });

        return dishVOList;

    }
}
