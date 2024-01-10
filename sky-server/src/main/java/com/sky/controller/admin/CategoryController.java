package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description CategoryController
 * @Author Zhilin
 * @Date 2023-09-23
 */
@RequestMapping("/admin/category")
@RestController
@Api(tags = "菜品分类套餐管理")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param categoryDTO
     * @return
     */
    @PostMapping("")
    @ApiOperation("新增分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {

        categoryService.addCategory(categoryDTO);

        return Result.success();
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param type
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result displayCategory(Integer page, Integer pageSize, Integer type, String name) {

        CategoryPageQueryDTO categoryPageQueryDTO = CategoryPageQueryDTO
                .builder()
                .page(page)
                .name(name)
                .pageSize(pageSize)
                .type(type)
                .build();

        PageResult pageResult = categoryService.displayCategory(categoryPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 删除分类
     *
     * @param id
     * @return
     */
    @DeleteMapping("")
    public Result deleteCategory(Integer id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * @param categoryDTO
     * @return
     */
    @PutMapping("")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {

        categoryService.updateCategory(categoryDTO);

        return Result.success();
    }

    /**
     * 修改状态
     *
     * @param status
     * @return
     */
    @ApiOperation("修改状态")
    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id) {
        Category category = new Category();
        category.setStatus(status);
        category.setId(id);
        categoryService.changeStatus(category);
        return Result.success();
    }

    /**
     * 根据分类类型查询接口
     * @param type
     * @return
     */
    @ApiOperation("根据分类类型查询接口")
    @GetMapping("/list")
    public Result findCategoryByType(Integer type) {

        List category = categoryService.findCategoryByType(type);

        return Result.success(category);
    }

}
