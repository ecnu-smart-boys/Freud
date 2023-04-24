package org.ecnusmartboys.model.request;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 基础查询参数封装类，包含创建时间和更新时间的参数
 *
 * @param <T> 实体类
 */
@Data
@ApiModel("基础查询参数封装类")
public class BaseQuery<T> {

    public static final int DEFAULT_PAGE_SIZE = 10;

    @ApiModelProperty(value = "分页大小，默认10")
    private int size = DEFAULT_PAGE_SIZE;

    @ApiModelProperty(value = "当前页码，默认1")
    private int current = 1;

    @ApiModelProperty(
            value = "排序字段",
            notes = "每一项以'字段名,[asc/desc]'表示，以';'分隔项",
            example = "id,desc;name,asc")
    private String order;

    private List<Long> createTime;

    private List<Long> updateTime;

    /**
     * 将查询参数封装成QueryWrapper
     *
     * @return QueryWrapper
     */
    public QueryWrapper<T> toQueryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        if (createTime != null && createTime.size() > 0) {
            if (createTime.size() > 1) {
                queryWrapper.between("create_time", new Date(createTime.get(0)), new Date(createTime.get(1)));
            } else {
                queryWrapper.ge("create_time", new Date(createTime.get(0)));
            }
        }
        if (updateTime != null && updateTime.size() > 0) {
            if (updateTime.size() > 1) {
                queryWrapper.between("update_time", new Date(updateTime.get(0)), new Date(updateTime.get(1)));
            } else {
                queryWrapper.ge("update_time", new Date(updateTime.get(0)));
            }
        }
        return queryWrapper;
    }

    /**
     * 将查询参数封装成Page
     *
     * @return page
     */
    public Page<T> toPage() {
        Page<T> page = new Page<>(current, size);
        if (order != null && !order.isEmpty()) {
            String[] orders = order.split(";");
            for (String o : orders) {
                String[] orderItem = o.split(",");
                if (!allowOrder(orderItem[0])) {
                    continue;
                }
                if (orderItem.length > 1 && orderItem[1].equalsIgnoreCase("desc")) {
                    page.addOrder(OrderItem.desc(orderItem[0]));
                } else {
                    page.addOrder(OrderItem.asc(orderItem[0]));
                }
            }
        }
        return page;
    }

    /**
     * 判断给定字段是否允许排序
     *
     * @param column /
     * @return /
     */
    protected boolean allowOrder(String column) {
        return false;
    }
}
