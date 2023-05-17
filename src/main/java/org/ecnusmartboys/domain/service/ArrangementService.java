package org.ecnusmartboys.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ecnusmartboys.application.dto.request.command.AddArrangementReq;
import org.ecnusmartboys.infrastructure.model.mysql.Arrangement;

public interface ArrangementService extends IService<Arrangement> {

    /**
     * 获得排班记录
     * @param req 排班参数
     * @return 排班记录
     */
    Arrangement getArrangement(AddArrangementReq req);

}
