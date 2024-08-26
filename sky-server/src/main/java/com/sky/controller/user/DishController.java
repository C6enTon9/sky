package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@Api(tags = "C端菜品浏览相关接口")
@Slf4j
@RequestMapping("/user/dish")
public class DishController {

    @Autowired
    private DishService dishService;


    /**
     * 查询分类下全部菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId){

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);

        List<DishVO> result = dishService.listWithFlavor(dish);

        return Result.success(result);
    }
}
