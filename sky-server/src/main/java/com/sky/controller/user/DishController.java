package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 查询分类下全部菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId){

        //使用redis来保存数据

        //创建保存在redis中的数据的key
        String key = "dish_" + categoryId;

        //先在redis中查询是否有数据 有就在redis中取得 若没有则查询数据库并存入redis
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);

        //若list不为空 则证明redis中有数据 取出即可
        if(list != null && list.size() > 0){
            return Result.success(list);
        }

        //若redis中无数据 则查询数据库并将数据存入redis
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);

        list = dishService.listWithFlavor(dish);

        //存入数据库
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }
}
