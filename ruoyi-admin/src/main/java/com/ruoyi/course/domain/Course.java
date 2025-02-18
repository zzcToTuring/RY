package com.ruoyi.course.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程管理对象 course
 * 
 * @author zzc
 * @date 2025-02-18
 */
public class Course extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 课程ID */
    @Excel(name = "课程ID")
    private Long id;

    /** 课程编码 */
    @Excel(name = "课程编码")
    private String code;

    /** 课程学科 */
    @Excel(name = "课程学科")
    private String subject;

    /** 课程名称 */
    @Excel(name = "课程名称")
    private String name;

    /** 课程价格 */
    @Excel(name = "课程价格")
    private Long price;

    /** 课程介绍 */
    @Excel(name = "课程介绍")
    private String info;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setSubject(String subject) 
    {
        this.subject = subject;
    }

    public String getSubject() 
    {
        return subject;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setPrice(Long price) 
    {
        this.price = price;
    }

    public Long getPrice() 
    {
        return price;
    }
    public void setInfo(String info) 
    {
        this.info = info;
    }

    public String getInfo() 
    {
        return info;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("subject", getSubject())
            .append("name", getName())
            .append("price", getPrice())
            .append("info", getInfo())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
