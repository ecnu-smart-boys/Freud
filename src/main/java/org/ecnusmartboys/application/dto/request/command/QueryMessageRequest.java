package org.ecnusmartboys.application.dto.request.command;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.ecnusmartboys.application.dto.request.query.BaseQuery;
import org.ecnusmartboys.infrastructure.data.mysql.table.MessageDO;

import javax.validation.constraints.NotBlank;

@Data
public class QueryMessageRequest extends BaseQuery<MessageDO> {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotBlank(message = "toId不能为空")
    private String toId;

    @Override
    public QueryWrapper<MessageDO> toQueryWrapper() {
        var wrapper = super.toQueryWrapper();
        wrapper.eq("from_id", fromId);
        wrapper.eq("to_id", toId);
        return wrapper;
    }

    @Override
    protected boolean allowOrder(String column) {
        return "time".equals(column);
    }
}
