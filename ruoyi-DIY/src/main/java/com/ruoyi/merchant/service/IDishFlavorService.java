package com.ruoyi.merchant.service;

import java.util.List;
import com.ruoyi.merchant.domain.DishFlavor;

/**
 * 菜品配置Service接口
 * 
 * @author zzc
 * @date 2025-03-31
 */
public interface IDishFlavorService 
{
    /**
     * 查询菜品配置
     * 
     * @param id 菜品配置主键
     * @return 菜品配置
     */
    public DishFlavor selectDishFlavorById(Long id);

    /**
     * 查询菜品配置列表
     * 
     * @param dishFlavor 菜品配置
     * @return 菜品配置集合
     */
    public List<DishFlavor> selectDishFlavorList(DishFlavor dishFlavor);

    /**
     * 新增菜品配置
     * 
     * @param dishFlavor 菜品配置
     * @return 结果
     */
    public int insertDishFlavor(DishFlavor dishFlavor);

    /**
     * 修改菜品配置
     * 
     * @param dishFlavor 菜品配置
     * @return 结果
     */
    public int updateDishFlavor(DishFlavor dishFlavor);

    /**
     * 批量删除菜品配置
     * 
     * @param ids 需要删除的菜品配置主键集合
     * @return 结果
     */
    public int deleteDishFlavorByIds(Long[] ids);

    /**
     * 删除菜品配置信息
     * 
     * @param id 菜品配置主键
     * @return 结果
     */
    public int deleteDishFlavorById(Long id);
}
