package org.ecnusmartboys.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.application.dto.request.command.AddArrangementReq;
import org.ecnusmartboys.domain.service.ArrangementService;
import org.ecnusmartboys.infrastructure.data.mysql.Arrangement;
import org.ecnusmartboys.infrastructure.repository.ArrangementRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;


@Slf4j
@RequiredArgsConstructor
@Service
public class ArrangeServiceImpl extends ServiceImpl<ArrangementRepository, Arrangement> implements ArrangementService {

    @Override
    public Arrangement getArrangement(AddArrangementReq req) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return getBaseMapper().selectOneArrangement(req.getUserId(), dateFormat.format(req.getDate()));
    }

}
