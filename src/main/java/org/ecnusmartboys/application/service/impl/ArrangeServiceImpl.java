package org.ecnusmartboys.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.dto.request.command.AddArrangementRequest;
import org.ecnusmartboys.application.dto.request.command.RemoveArrangeRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.service.ArrangeService;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ArrangeServiceImpl implements ArrangeService {
    private final UserRepository userRepository;
    @Override
    public Responses<Object> remove(RemoveArrangeRequest req) {
        return null;
    }

    @Override
    public Responses<Object> addConsultant(AddArrangementRequest req) {
        if(userRepository.retrieveById(req.getUserId()) == null) {
            throw new BadRequestException("所要排班的督导不存在");
        }

        if(arrangementService.getArrangement(req) != null) {
            throw new BadRequestException("请勿重复排班");
        }

        Arrangement arrangement = new Arrangement(req.getDate(), req.getUserId());
        arrangementService.save(arrangement);
        return Responses.ok("成功添加排班");
    }
}
