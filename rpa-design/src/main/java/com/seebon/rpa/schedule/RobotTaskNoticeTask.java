package com.seebon.rpa.schedule;

import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.robot.RobotAccountStatusHistory;
import com.seebon.rpa.entity.robot.RobotTask;
import com.seebon.rpa.entity.robot.RobotTaskNotice;
import com.seebon.rpa.repository.mysql.RobotAccountStatusHistoryDao;
import com.seebon.rpa.repository.mysql.RobotTaskDao;
import com.seebon.rpa.repository.mysql.RobotTaskNoticeDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class RobotTaskNoticeTask {
    @Autowired
    private SimpleLogin simpleLogin;
    @Autowired
    private RobotAccountStatusHistoryDao robotAccountStatusHistoryDao;
    @Autowired
    private RobotTaskNoticeDao robotTaskNoticeDao;
    @Autowired
    private RobotTaskDao robotTaskDao;

    @Value("${task.login.userId}")
    private Integer userId;
    @Value("${task.login.userName}")
    private String userName;
    @Value("${task.login.custId}")
    private Integer custId;

    @Scheduled(cron = "0 */1 * * * ?")
    public void task() {
        simpleLogin.doLogin(userId, userName, custId);
        this.handle();
        simpleLogin.loginOut();
    }

    public void handle() {
        Example example = new Example(RobotTaskNotice.class);
        example.createCriteria().andEqualTo("regainStatus", 0);
        List<RobotTaskNotice> notices = robotTaskNoticeDao.selectByExample(example);
        if (CollectionUtils.isEmpty(notices)) {
            return;
        }
        Date nowDate = new Date();
        for (RobotTaskNotice notice : notices) {
            if (notice.getRegainTime().getTime() > nowDate.getTime()) {
                continue;
            }
            RobotTaskNotice update = new RobotTaskNotice();
            update.setId(notice.getId());
            update.setRegainStatus(1);
            update.setUpdateTime(new Date());
            robotTaskNoticeDao.updateByPrimaryKeySelective(update);

            RobotTask robotTask = this.getRobotTask(notice.getTaskCode());
            //恢复申报账户
            robotTaskDao.updateStatus(robotTask.getAppCode(), notice.getTaskCode(), 1, "");
            //恢复历史记录
            Session user = SecurityContext.currentUser();
            RobotAccountStatusHistory accountStatusHistory = new RobotAccountStatusHistory();
            accountStatusHistory.setTaskCode(notice.getTaskCode());
            accountStatusHistory.setStatus(0);
            accountStatusHistory.setRemark("恢复");
            accountStatusHistory.setCreateId((int) user.getId());
            accountStatusHistory.setCreateName(user.getName());
            accountStatusHistory.setCreateTime(new Date());
            robotAccountStatusHistoryDao.insertSelective(accountStatusHistory);
        }
    }

    private RobotTask getRobotTask(String taskCode) {
        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("taskCode", taskCode);
        return robotTaskDao.selectOneByExample(example);
    }
}
