package org.ecnusmartboys.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.ecnusmartboys.domain.entity.User;

public interface UserRepository {
    User retrieveByOpenId(String openID);
    User retrieveByName(String name);
}
