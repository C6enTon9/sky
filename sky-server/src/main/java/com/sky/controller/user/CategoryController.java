package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategoryController")
@Slf4j
@RequestMapping("/user/category")
@Api(tags = "C端分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询分类
     * @param type
     * @return
     */
    @ApiOperation("查询分类")
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){



        log.info("C端根据type查询分类：{}",type);

        List<Category> list = categoryService.list(type);

        return Result.success(list);

    }
}
