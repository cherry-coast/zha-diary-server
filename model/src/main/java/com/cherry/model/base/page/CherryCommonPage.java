package com.cherry.model.base.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cherry.base.utils.CherryCollectionUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : ganxiongwen
 * Date: 2022/4/21 15:40
 * Description:
 * ClassName: CherryCommonPage
 * Package: com.cherry.common.domain
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@Tag(name = "分页模型")
@SuppressWarnings(value = "unused")
public class CherryCommonPage<T> {

    @Schema(description = "页码")
    private long pageNo;

    @Schema(description = "数量")
    private long pageSize;

    @Schema(description = "总页数")
    private long totalPage;

    @Schema(description = "总数量")
    private long total;

    @Schema(description = "分页数据实体")
    private List<T> data;

    private static final String IGNORE_PROPERTIES = "data";

    /**
     * Convert the mybatis native paginated list to pagination information
     *
     */
    public static <T> CherryCommonPage<T> restPage(IPage<T> page) {
        CherryCommonPage<T> cherryCommonPage = new CherryCommonPage<>();
        cherryCommonPage.setPageNo(page.getCurrent());
        cherryCommonPage.setPageSize(page.getSize());
        cherryCommonPage.setTotalPage(page.getTotal());
        cherryCommonPage.setTotal(page.getPages());
        cherryCommonPage.setData(page.getRecords());
        return cherryCommonPage;
    }

    /**
     * Turn the page Helper plugin's paginated list into pagination information
     *
     */
    public static <T> CherryCommonPage<T> restPage(List<T> list) {
        CherryCommonPage<T> cherryCommonPage = new CherryCommonPage<>();
        PageInfo<T> pageInfo = new PageInfo<>(list, 20);
        cherryCommonPage.setTotalPage(pageInfo.getPages());
        cherryCommonPage.setPageNo(pageInfo.getPageNum());
        cherryCommonPage.setPageSize(pageInfo.getPageSize());
        cherryCommonPage.setTotal(pageInfo.getTotal());
        cherryCommonPage.setData(pageInfo.getList());
        return cherryCommonPage;
    }

    /**
     *  Convert the Spring Data paginated list to paginated information
     *
     */
    public static <T> CherryCommonPage<T> restPage(Page<T> pageInfo) {
        CherryCommonPage<T> cherryCommonPage = new CherryCommonPage<>();
        cherryCommonPage.setTotalPage(pageInfo.getTotalPages());
        cherryCommonPage.setPageNo(pageInfo.getNumber());
        cherryCommonPage.setPageSize(pageInfo.getSize());
        cherryCommonPage.setTotal(pageInfo.getTotalElements());
        cherryCommonPage.setData(pageInfo.getContent());
        return cherryCommonPage;
    }


    /**
     *  Convert the Spring Data paginated list to paginated information
     *
     */
    public static <T> CherryCommonPage<T> restPage(List<T> data, long total, CherryPageRequest pageRequest) {
        CherryCommonPage<T> cherryCommonPage = new CherryCommonPage<>();
        cherryCommonPage.setTotalPage(Math.ceilDiv(total, pageRequest.getPageSize()));
        cherryCommonPage.setPageNo(pageRequest.isRowFlag() ? pageRequest.getPageNo() : pageRequest.getPageNoDuplicate());
        cherryCommonPage.setPageSize(pageRequest.getPageSize());
        cherryCommonPage.setTotal(total);
        cherryCommonPage.setData(data);
        return cherryCommonPage;
    }

    /**
     *  entity response
     *
     */
    public static <T> CherryCommonPage<T> entity(CherryPageRequest pageRequest) {
        CherryCommonPage<T> cherryCommonPage = new CherryCommonPage<>();
        cherryCommonPage.setTotalPage(0);
        cherryCommonPage.setPageNo(pageRequest.isRowFlag() ? pageRequest.getPageNo() : pageRequest.getPageNoDuplicate());
        cherryCommonPage.setPageSize(pageRequest.getPageSize());
        cherryCommonPage.setTotal(0);
        cherryCommonPage.setData(new ArrayList<>());
        return cherryCommonPage;
    }

    /**
     * enforce the replacement of the return value type of the paging object, typically used in DO, DTO, to VO cases
     *
     * @param sourcePage Object before conversion
     * @param targetClazz Object after conversion
     * @return Converted page
     */
    public static <T> CherryCommonPage<T> transferPageData(CherryCommonPage<?> sourcePage, Class<T> targetClazz) {
        List<T> targetList = CherryCollectionUtil.copyList(sourcePage.getData(), targetClazz);
        CherryCommonPage<T> targetPage = CherryCommonPage.restPage(targetList);
        BeanUtils.copyProperties(sourcePage, targetPage, IGNORE_PROPERTIES);
        return targetPage;
    }


    /**
     * enforce the replacement of the return value type of the paging object, typically used in DO, DTO, to VO cases
     *
     * @param sourcePage Object before conversion
     * @param targetClazz Object after conversion
     * @return Converted page
     */
    public static <T> CherryCommonPage<T> transferPageData(CherryCommonPage<?> sourcePage, List<T> data, Class<T> targetClazz) {
        List<T> targetList = CherryCollectionUtil.copyList(sourcePage.getData(), targetClazz);
        CherryCommonPage<T> targetPage = CherryCommonPage.restPage(targetList);
        BeanUtils.copyProperties(sourcePage, targetPage, IGNORE_PROPERTIES);
        targetPage.setData(data);
        return targetPage;
    }


}