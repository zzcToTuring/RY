package com.ruoyi.merchant.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.merchant.domain.DishFlavor;
import com.ruoyi.merchant.service.IDishFlavorService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 菜品配置Controller
 * 
 * @author zzc
 * @date 2025-03-31
 */
@RestController
@RequestMapping("/merchant/flavor")
public class DishFlavorController extends BaseController
{
    @Autowired
    private IDishFlavorService dishFlavorService;

    /**
     * 查询菜品配置列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:flavor:list')")
    @GetMapping("/list")
    public TableDataInfo list(DishFlavor dishFlavor)
    {
        startPage();
        List<DishFlavor> list = dishFlavorService.selectDishFlavorList(dishFlavor);
        return getDataTable(list);
    }

    /**
     * 导出菜品配置列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:flavor:export')")
    @Log(title = "菜品配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DishFlavor dishFlavor)
    {
        List<DishFlavor> list = dishFlavorService.selectDishFlavorList(dishFlavor);
        ExcelUtil<DishFlavor> util = new ExcelUtil<DishFlavor>(DishFlavor.class);
        util.exportExcel(response, list, "菜品配置数据");
    }

    /**
     * 获取菜品配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('merchant:flavor:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dishFlavorService.selectDishFlavorById(id));
    }

    /**
     * 新增菜品配置
     */
    @PreAuthorize("@ss.hasPermi('merchant:flavor:add')")
    @Log(title = "菜品配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DishFlavor dishFlavor)
    {
        return toAjax(dishFlavorService.insertDishFlavor(dishFlavor));
    }

    /**
     * 修改菜品配置
     */
    @PreAuthorize("@ss.hasPermi('merchant:flavor:edit')")
    @Log(title = "菜品配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DishFlavor dishFlavor)
    {
        return toAjax(dishFlavorService.updateDishFlavor(dishFlavor));
    }

    /**
     * 删除菜品配置
     */
    @PreAuthorize("@ss.hasPermi('merchant:flavor:remove')")
    @Log(title = "菜品配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dishFlavorService.deleteDishFlavorByIds(ids));
    }
}
