package org.ecnusmartboys.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ecnusmartboys.infrastructure.data.mysql.Consulvisor;

import java.util.List;

public interface ConsulvisorService extends IService<Consulvisor> {

    /**
     * 通过咨询师id得到督导姓名
     * @param consultantId 咨询师id
     * @return 返回督导名字
     */
    List<String> getSupervisors(Long consultantId);

    /**
     * 通过督导id获得绑定的咨询师姓名列表
     * @param supervisorId 督导id
     * @return 咨询师姓名列表
     */
    List<String> getConsultants(Long supervisorId);
}
