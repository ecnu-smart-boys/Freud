package org.ecnusmartboys.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.domain.service.VisitorService;
import org.ecnusmartboys.infrastructure.data.mysql.Visitor;
import org.ecnusmartboys.infrastructure.repository.VisitorRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorRepository, Visitor> implements VisitorService {
}
