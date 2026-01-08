package com.eys.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果封装
 *
 * @param <T> 数据类型
 * @author EYS
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<T> records;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "100")
    private long total;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private long size;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private long current;

    /**
     * 总页数
     */
    @Schema(description = "总页数", example = "10")
    private long pages;

    public PageResult() {
    }

    public PageResult(List<T> records, long total, long size, long current) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = size > 0 ? (total + size - 1) / size : 0;
    }

    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(List<T> records, long total, long size, long current) {
        return new PageResult<>(records, total, size, current);
    }

    /**
     * 创建空分页结果
     */
    public static <T> PageResult<T> empty(long size, long current) {
        return new PageResult<>(Collections.emptyList(), 0, size, current);
    }
}
