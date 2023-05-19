package org.ecnusmartboys.infrastructure.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.infrastructure.service.VisitorService;
import org.ecnusmartboys.infrastructure.model.mysql.Visitor;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorRepository, Visitor> implements VisitorService {
}
