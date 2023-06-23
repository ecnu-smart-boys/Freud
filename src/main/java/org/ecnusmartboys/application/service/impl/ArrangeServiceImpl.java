package org.ecnusmartboys.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.dto.DayArrangeInfo;
import org.ecnusmartboys.application.dto.StaffBaseInfo;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.AddArrangementRequest;
import org.ecnusmartboys.application.dto.request.command.RemoveArrangeRequest;
import org.ecnusmartboys.application.dto.request.query.GetMonthArrangementRequest;
import org.ecnusmartboys.application.dto.request.query.NoArrangedRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.service.ArrangeService;
import org.ecnusmartboys.domain.model.arrangement.Arrangement;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.ecnusmartboys.domain.model.user.User;
import org.ecnusmartboys.domain.repository.ArrangementRepository;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ArrangeServiceImpl implements ArrangeService {

    private final UserRepository userRepository;
    private final ArrangementRepository arrangementRepository;

    @Override
    public Responses<Object> remove(RemoveArrangeRequest req) {
        Date date = new Date(req.getTimestamp());
        if (!(date.after(new Date()))) {
            throw new BadRequestException("只能移除未来的排班");
        }
        arrangementRepository.remove(req.getUserId(), date);

        return Responses.ok("成功删除排班");
    }

    @Override
    public Responses<Object> addArrangement(AddArrangementRequest req) {
        var user = userRepository.retrieveById(req.getUserId());
        if (user == null) {
            throw new BadRequestException("所要排班的用户不存在");
        }

        if ((!(user instanceof Consultant) && !(user instanceof Supervisor))) {
            throw new BadRequestException("只能给咨询师和督导进行排班");
        }

        Date date = new Date(req.getTimestamp());
        if (!date.after(new Date()) && !date.equals(new Date())) {
            throw new BadRequestException("只能给今天及以后的日期进行排班");
        }

        try {
            arrangementRepository.save(req.getUserId(), date);
        } catch (DuplicateKeyException e) {
            throw new BadRequestException("用户已被排班");
        }
        return Responses.ok("成功添加排班");
    }

    @Override
    public Responses<List<StaffBaseInfo>> getConsultants(Long timestamp) {
        List<Arrangement> arrangements = arrangementRepository.retrieveByDate(new Date(timestamp));

        List<StaffBaseInfo> result = new ArrayList<>();
        arrangements.forEach(arrangement -> {
            var user = userRepository.retrieveById(arrangement.getUserId());

            if (user instanceof Consultant) {
                result.add(new StaffBaseInfo(user.getId(), user.getName(), user.getAvatar()));
            }
        });

        return Responses.ok(result);
    }

    @Override
    public Responses<List<StaffBaseInfo>> getSupervisors(Long timestamp) {
        List<Arrangement> arrangements = arrangementRepository.retrieveByDate(new Date(timestamp));

        List<StaffBaseInfo> result = new ArrayList<>();
        arrangements.forEach(arrangement -> {
            var user = userRepository.retrieveById(arrangement.getUserId());

            if (user instanceof Supervisor) {
                result.add(new StaffBaseInfo(user.getId(), user.getName(), user.getAvatar()));
            }
        });

        return Responses.ok(result);
    }

    @Override
    public Responses<List<DayArrangeInfo>> getMonthArrangement(GetMonthArrangementRequest req) {
        List<DayArrangeInfo> result = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, req.getYear());
        calendar.set(Calendar.MONTH, req.getMonth() - 1);

        int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= lastDayOfMonth; day++) {
            result.add(new DayArrangeInfo(day, 0, 0));
        }

        var arrangementInfoList = arrangementRepository.retrieveMonthArrangement(req.getYear(), req.getMonth());
        arrangementInfoList.forEach(v -> {
            if (Objects.equals(v.getRole(), "supervisor")) {
                result.get(v.getDay() - 1).setSupervisors(v.getTotal());
            } else {
                result.get(v.getDay() - 1).setConsultants(v.getTotal());
            }
        });

        return Responses.ok(result);
    }

    @Override
    public Responses<List<StaffBaseInfo>> getNotArranged(NoArrangedRequest req, String role) {
        List<StaffBaseInfo> result = new ArrayList<>();

        List<Arrangement> arrangements = arrangementRepository.retrieveByDate(new Date(req.getTimestamp()));
        Set<String> ids = new TreeSet<>();
        arrangements.forEach(arrangement -> {
            ids.add(arrangement.getUserId());
        });

        List<User> users = userRepository.retrieveByRole(role, req.getName());
        users.forEach(user -> {
            if (!ids.contains(user.getId())) {
                result.add(new StaffBaseInfo(user.getId(), user.getName(), user.getAvatar()));
            }
        });
        return Responses.ok(result);
    }

    @Override
    public Responses<List<Integer>> getPersonalMonthArrangement(Common common) {
        List<Integer> result = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        int daysInMonth = currentDate.lengthOfMonth();
        for (int i = 0; i < daysInMonth; i++) {
            result.add(0);
        }

        var days = arrangementRepository.retrieveMonthArrangementByUserId(currentDate.getYear(), currentDate.getMonthValue(), common.getUserId());
        days.forEach(day -> {
            result.set(day - 1, 1);
        });
        return Responses.ok(result);
    }
}
