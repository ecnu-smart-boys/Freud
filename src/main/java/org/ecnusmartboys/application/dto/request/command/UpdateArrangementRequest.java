package org.ecnusmartboys.application.dto.request.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateArrangementRequest {

    @NotNull(message = "所要排班的用户不能为空")
    private String id;

    @NotNull(message = "排班不能为空")
    @Range(min = 0, max = 127, message = "排班计划必须是0到127的一个整数")
    private Integer arrangement;
}
