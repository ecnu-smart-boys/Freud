package org.ecnusmartboys.application.dto.request.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMonthArrangementRequest {

    @NotNull(message = "年份不能为空")
    @Range(min = 2023, max = 2024, message = "年份只能为2023或2024年")
    private Integer year;

    @NotNull(message = "月份不能为空")
    @Range(min = 1, max = 12, message = "月份必须合法")
    private Integer month;
}
