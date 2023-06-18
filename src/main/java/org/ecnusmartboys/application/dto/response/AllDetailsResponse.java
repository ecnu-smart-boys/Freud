package org.ecnusmartboys.application.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.MessageInfo;
import org.ecnusmartboys.application.dto.conversation.ConsultationInfo;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("一次咨询记录的所有信息")
public class AllDetailsResponse {

    private ConsultationInfo consultationInfo;

    @ApiModelProperty("咨询记录列表")
    private List<MessageInfo> consultation;

    @ApiModelProperty("用户评价分数")
    private int visitorScore;

    @ApiModelProperty("用户评价内容")
    private String visitorText;

    @ApiModelProperty("咨询师评价标签")
    private String tag;

    @ApiModelProperty("咨询师评价内容")
    private String consultantText;
}
