package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;



    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {

        //分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }


    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void save(SetmealDTO setmealDTO) {

        //数据插入setmeal表

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealMapper.insert(setmeal);

        //利用usegeneratekey 和 keyproperties 实现主键回显
        Long setmealId = setmeal.getId();

        //数据插入setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && setmealDishes.size() > 0){

            //给在setmeal_dish表中的每个菜品设置套餐id
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            setmealDishMapper.insertBatch(setmealDishes);

        }
    }


    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {

        //查询setmeal数据
        Setmeal setmeal = setmealMapper.getById(id);

        //查询setmeal_dish信息
        List<SetmealDish> list = setmealDishMapper.getBySetmealId(id);

        //封装数据
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(list);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {

        //修改setmeal表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);

        //修改setmeal_dish表
        Long id = setmealDTO.getId();
        if(id != null){
            setmealDishMapper.deleteBatchBySetmealId(id);
        }

        List<SetmealDish> list = setmealDTO.getSetmealDishes();

        for (SetmealDish setmealDish : list) {
            setmealDish.setSetmealId(id);
        }

        if(list != null && list.size() > 0){
            setmealDishMapper.insertBatch(list);
        }
    }


    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void deleteBatchByid(Long[] ids) {

        //查询套餐状态 若为起售则不允许删除
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //setmeal批量删除
        setmealMapper.deleteBatch(ids);


        //setmeal_dish批量删除
        for (Long id : ids) {
            setmealDishMapper.deleteBatchBySetmealId(id);
        }

    }


    /**
     * 起售停售套餐
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {

        //判断是否从停售变为起售
        if(status == StatusConstant.ENABLE){

            //根据套餐id得到套餐内所有菜品
            List<SetmealDish> setmealDishList = setmealDishMapper.getBySetmealId(id);

            if(setmealDishList != null && setmealDishList.size() > 0){

                //判断套餐内每一道菜品的售卖状态 若为停售则套餐状态无法从停售变为起售
                for (SetmealDish setmealDish : setmealDishList) {
                    Long dishId = setmealDish.getDishId();

                    Dish dish = dishMapper.selectById(dishId);

                    if(dish.getStatus() == StatusConstant.DISABLE){

                        //抛出异常
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                }
            }
        }

        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        setmealMapper.update(setmeal);
    }


    /**
     * C端根据分类类型（type==2）和套餐状态（status==1）条件查询套餐
     *
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {

        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }



    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
