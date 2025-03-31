package com.ruoyi.merchant.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.merchant.mapper.DishFlavorMapper;
import com.ruoyi.merchant.domain.DishFlavor;
import com.ruoyi.merchant.service.IDishFlavorService;

/**
 * 菜品配置Service业务层处理
 * 
 * @author zzc
 * @date 2025-03-31
 */
@Service
public class DishFlavorServiceImpl implements IDishFlavorService 
{
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 查询菜品配置
     * 
     * @param id 菜品配置主键
     * @return 菜品配置
     */
    @Override
    public DishFlavor selectDishFlavorById(Long id)
    {
        return dishFlavorMapper.selectDishFlavorById(id);
    }

    /**
     * 查询菜品配置列表
     * 
     * @param dishFlavor 菜品配置
     * @return 菜品配置
     */
    @Override
    public List<DishFlavor> selectDishFlavorList(DishFlavor dishFlavor)
    {
        return dishFlavorMapper.selectDishFlavorList(dishFlavor);
    }

    /**
     * 新增菜品配置
     * 
     * @param dishFlavor 菜品配置
     * @return 结果
     */
    @Override
    public int insertDishFlavor(DishFlavor dishFlavor)
    {
        return dishFlavorMapper.insertDishFlavor(dishFlavor);
    }

    /**
     * 修改菜品配置
     * 
     * @param dishFlavor 菜品配置
     * @return 结果
     */
    @Override
    public int updateDishFlavor(DishFlavor dishFlavor)
    {
        return dishFlavorMapper.updateDishFlavor(dishFlavor);
    }

    /**
     * 批量删除菜品配置
     * 
     * @param ids 需要删除的菜品配置主键
     * @return 结果
     */
    @Override
    public int deleteDishFlavorByIds(Long[] ids)
    {
        return dishFlavorMapper.deleteDishFlavorByIds(ids);
    }

    /**
     * 删除菜品配置信息
     * 
     * @param id 菜品配置主键
     * @return 结果
     */
    @Override
    public int deleteDishFlavorById(Long id)
    {
        return dishFlavorMapper.deleteDishFlavorById(id);
    }
}
