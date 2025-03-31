package com.ruoyi.merchant.mapper;

import java.util.List;
import com.ruoyi.merchant.domain.DishFlavor;

/**
 * 菜品配置Mapper接口
 * 
 * @author zzc
 * @date 2025-03-31
 */
public interface DishFlavorMapper 
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
     * 删除菜品配置
     * 
     * @param id 菜品配置主键
     * @return 结果
     */
    public int deleteDishFlavorById(Long id);

    /**
     * 批量删除菜品配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDishFlavorByIds(Long[] ids);
}
