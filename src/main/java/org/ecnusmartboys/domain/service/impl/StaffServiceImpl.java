package org.ecnusmartboys.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.domain.repository.StaffRepository;
import org.ecnusmartboys.domain.service.StaffService;
import org.ecnusmartboys.infrastructure.model.mysql.Staff;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StaffServiceImpl extends ServiceImpl<StaffRepository, Staff> implements StaffService {
}
