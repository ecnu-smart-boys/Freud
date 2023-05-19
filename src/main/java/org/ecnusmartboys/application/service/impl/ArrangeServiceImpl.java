package org.ecnusmartboys.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.dto.request.command.RemoveArrangeRequest;
import org.ecnusmartboys.application.dto.response.Response;
import org.ecnusmartboys.application.service.ArrangeService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ArrangeServiceImpl implements ArrangeService {
    @Override
    public Response<Object> remove(RemoveArrangeRequest req) {
        return null;
    }
}
