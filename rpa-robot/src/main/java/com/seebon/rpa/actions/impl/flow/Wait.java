package com.seebon.rpa.actions.impl.flow;

import com.seebon.rpa.actions.ActionFactory;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.TargetsTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.utils.Convert;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RobotAction(name = "等待",
        order = 40,
        targetType = TargetsTarget.class,
        comment = "存在条件，则在满足条件下执行等待；不存在条件，则直接等待")
public class Wait extends AbstractAction {

    @Setter
    @ActionArgs(value = "停止等待条件", parsed = false)
    private String condition;

    @Setter
    @ActionArgs(value = "等待时间(s)", required = true)
    private Integer timeout;

    @Autowired
    private ActionFactory actionFactory;

    @Override
    public void run() {
        long millis = timeout * 1000;
        long start = System.currentTimeMillis();
        if (StringUtils.isBlank(condition)) {
            try {
                Thread.sleep(millis);
            } catch (Exception e) {
                log.error("【Exception】", e);
            }
        } else {
            Boolean cond = parse(condition, Boolean.class);
            while (!cond) {
                long end = System.currentTimeMillis();
                if (end - start > millis) {
                    break;
                } else {
                    Convert.sleep(1);
                }
                actionFactory.fetchTarget(this, this.getTarget());
                cond = parse(condition, Boolean.class);
            }
            log.info("退出等待：" + cond);
        }
    }
}
