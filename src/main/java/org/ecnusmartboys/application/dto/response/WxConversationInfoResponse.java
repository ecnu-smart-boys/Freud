package org.ecnusmartboys.application.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.conversation.ConsultationInfo;
import org.ecnusmartboys.application.dto.conversation.WxConsultationInfo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("一次已经结束的会话的基本信息，返回给微信小程序端，不需要有求助记录")
public class WxConversationInfoResponse {

    private WxConsultationInfo consultationInfo;

    @ApiModelProperty("用户评价分数")
    private int visitorScore;

    @ApiModelProperty("用户评价内容")
    private String visitorText;

    @ApiModelProperty("咨询师评价标签")
    private String tag;

    @ApiModelProperty("咨询师评价内容")
    private String consultantText;
}
