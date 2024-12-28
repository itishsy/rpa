package com.seebon.rpa.runner;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.collect.Lists;
import com.seebon.rpa.actions.Action;
import com.seebon.rpa.actions.ActionFactory;
import com.seebon.rpa.actions.impl.tool.ScreenShotUtil;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.runtime.*;
import com.seebon.rpa.entity.robot.RobotExecutionDetail;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.repository.mapper.RobotExecutionDetailMapper;
import com.seebon.rpa.service.RobotExecutionFileService;
import com.seebon.rpa.utils.ELParser;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import static com.seebon.rpa.context.constant.RobotConstant.FINAL_ACTION_CODE;

/**
 * 调试执行器
 *
 * @author: xfz
 */
@Slf4j
@Setter
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public class RobotExecutor {
    private final Semaphore semaphore = new Semaphore(1);
    private final ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(10));
    private boolean isSubFlow = false;
    private boolean isFinalFlow = false;

    @Autowired
    private RobotContext ctx;
    @Autowired
    private ActionFactory actionFactory;
    @Autowired
    private RobotExecutionDetailMapper detailMapper;
    @Autowired
    private RobotExecutionFileService robotExecutionFileService;
    @Autowired
    private ScreenShotUtil screenShotUtil;

    public void start(List<RobotFlowStep> steps) {
        try {
            if (!semaphore.tryAcquire(60, TimeUnit.SECONDS)) {
                log.warn("等待1分钟，未获取执行信号量，返回");
                return;
            }
        } catch (InterruptedException e) {
            log.error("等待1分钟，未获取执行信号量，返回。 e" + (e == null ? "" : e.getMessage()), e);
            return;
        }
        if (CollectionUtils.isEmpty(steps)) {
            throw new RobotConfigException("无操作步骤.");
        }
        List<RobotFlowStep> finalSteps = Lists.newArrayList();
        for (RobotFlowStep step : steps) {
            if (step.getStatus() == 1 && FINAL_ACTION_CODE.equals(step.getActionCode())) {
                finalSteps.add(step);
            }
        }
        try {
            String skipTo = null;
            int size = steps.size();
            RobotExecutionDetail detail = null;
            for (int s = 0; s < size; s++) {
                RobotFlowStep step = steps.get(s);
                //禁用、最后子流程
                if (step.getStatus() == 0 || FINAL_ACTION_CODE.equals(step.getActionCode())) {
                    continue;
                }
                //查找直到skipTo等于stepName，否则执行跳过
                if (StringUtils.isNotBlank(skipTo)) {
                    if (!skipTo.equals(step.getStepName())) {
                        continue;
                    } else {
                        skipTo = null;
                    }
                }
                try {
                    if (ctx.contains(RobotConstant.EXECUTION_CODE_KEY)) {
                        detail = new RobotExecutionDetail();
                        detail.setExecutionCode(ctx.getVariable(RobotConstant.EXECUTION_CODE_KEY));
                        detail.setFlowCode(ctx.getVariable(RobotConstant.EXECUTION_FLOW_CODE_KEY));
                        detail.setStepCode(step.getStepCode());
                        detail.setStepName(step.getStepName());
                        detail.setStartTime(new Date());
                    }
                    runAction(step);
                    if (detail != null) {
                        detail.setStatus(1);
                    }
                    if (StringUtils.isNotBlank(step.getSkipTo())) {
                        s = -1;
                        skipTo = step.getSkipTo();
                    }
                } catch (RuntimeSkipTo rst) {
                    skipTo = rst.getStepName();
                    if (RobotConstant.NEXT_STEP.equals(skipTo)) {
                        skipTo = null;
                    } else {
                        s = -1;
                    }
                    if (detail != null) {
                        detail.setStatus(1);
                    }
                } catch (EmptyRuntimeException ers) {
                    skipTo = null;
                    // 寻找一个外层的指令
                    String nextStepName = "";
                    for (int i = s + 1; i < steps.size(); i++) {
                        if (steps.get(i).getLevel() == 0) {
                            nextStepName = steps.get(i).getStepName();
                            break;
                        }
                    }
                    if (!nextStepName.isEmpty()) {
                        skipTo = nextStepName;
                    }
                    if (detail != null) {
                        detail.setStatus(1);
                    }
                } catch (FinishRuntimeException fre) {
                    throw fre;
                } catch (ExitException fre) {
                    throw fre;
                } catch (Exception e) {
                    if (detail != null) {
                        detail.setStatus(0);

                        StringBuilder error = new StringBuilder();
                        if (e.getCause() != null) {
                            error.append(e.getCause().getMessage());
                        } else {
                            error.append(e.getMessage());
                        }
                        String errorStack = ExceptionUtil.stacktraceToString(e);
                        detail.setError(error.toString());
                        detail.setErrorStack(errorStack);
                    }
                    throw e;
                } finally {
                    try {
                        if (detail != null) {
                            detail.setEndTime(new Date());
                            detailMapper.insert(detail);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            //如果是子流程，异常往外抛。
            if (isSubFlow) {
                log.error("子流程执行异常。" + (e.getMessage() == null ? "" : e.getMessage()));
                e.printStackTrace();
                throw e;
            }
            if (e instanceof RobotConfigException || e instanceof RobotInterruptException || e instanceof RobotRuntimeException) {
                throw e;
            } else if (e instanceof FinishRuntimeException) {
                log.info("（报盘/核验）空数据正常退出流程");
            } else if (e instanceof ExitException) {
                log.warn("出错退出指令正常退出 {}", e.getMessage());
            } else {
                e.printStackTrace();
            }
        } finally {
            if (finalSteps.size() > 0) {
                try {
                    log.info("执行final流程. size:" + finalSteps.size());
                    for (RobotFlowStep step : finalSteps) {
                        runAction(step);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!isSubFlow) {
                ctx.release();
                isFinalFlow = false;
            }
            semaphore.release();
        }
    }

    private void runAction(RobotFlowStep step) {
        log.info("执行步骤：" + step.getStepName() + "(" + step.getActionCode() + ")");

        // 执行前等待
        randomWait(step.getWaitBefore());

        if (StringUtils.isNotBlank(step.getSkipCondition())
                && ELParser.parse(step.getSkipCondition(), ctx.getVariables(), Boolean.class)) {
            throw new RuntimeSkipTo(RobotConstant.NEXT_STEP);
        }

        int retry = (null == step.getFailedRetry()) ? 1 : step.getFailedRetry();

        int i = 0;
        Action action = null;
        RobotRuntimeException rre = null;
        try {
            while (i < retry) {
                Future future = null;
                try {
                    //检查计数器
                    ctx.checkCounter(step);
                    action = actionFactory.create(step);
                    future = executorService.submit(action);
                    future.get(action.getTimeout(), TimeUnit.SECONDS);
                    break;
                } catch (Exception e) {
                    Throwable throwable = e.getCause();
                    if (throwable instanceof RuntimeSkipTo) {
                        //运行时跳转，直接往外抛
                        throw (RuntimeSkipTo) throwable;
                    } else if (throwable instanceof EmptyRuntimeException) {
                        //遍历器 OfferIterator 空值跳转
                        throw (EmptyRuntimeException) throwable;
                    } else if (throwable instanceof FinishRuntimeException) {
                        //报盘空数据
                        throw (FinishRuntimeException) throwable;
                    } else if (throwable instanceof ExitException) {
                        //出错退出
                        throw (ExitException) throwable;
                    } else if (throwable instanceof RobotConfigException) {
                        //配置错误，直接往外抛
                        throw (RobotConfigException) throwable;
                    } else if (throwable instanceof RobotInterruptException) {
                        screenShotUtil.screenShot(step.getFlowCode(), step.getStepCode());
                        //自定义中断，直接往外抛
                        throw (RobotInterruptException) throwable;
                    } else if (throwable instanceof RobotRuntimeException) {
                        screenShotUtil.screenShot(step.getFlowCode(), step.getStepCode());
                        //运行时异常，执行异常策略
                        log.error("【Exception】 flow:" + step.getFlowCode() + ",step:" + step.getStepName(), throwable);
                        rre = (RobotRuntimeException) throwable;
                    } else if (e instanceof TimeoutException) {
                        screenShotUtil.screenShot(step.getFlowCode(), step.getStepCode());
                        //执行超时，执行异常策略
                        log.error("【Exception】 flow:" + step.getFlowCode() + ",step:" + step.getStepName(), throwable);
                        String message = throwable == null ? "执行步骤超时" : throwable.getMessage();
                        rre = new RobotRuntimeException(message);
                    } else {
                        screenShotUtil.screenShot(step.getFlowCode(), step.getStepCode());
                        //其它未定义的异常，执行异常策略
                        log.error("【Exception】 flow:" + step.getFlowCode() + ",step:" + step.getStepName(), e);
                        rre = new RobotRuntimeException(e.getMessage());
                    }
                } finally {
                    if (future != null) {
                        future.cancel(true);
                    }
                    i++;
                }
            }
        } finally {
            if (action != null) {
                //保存执行文件记录
                robotExecutionFileService.saveRobotExecutionFile(step.getFlowCode(), step.getStepCode());
                try {
                    action.release();
                } catch (Exception e) {
                    log.error("释放异常." + e.getMessage(), e);
                    rre = new RobotRuntimeException(e.getMessage());
                }
            }
        }

        if (rre != null) {
            //执行异常策略 0 中止，1 忽略，2 跳转
            Integer failedStrategy = step.getFailedStrategy();
            if (failedStrategy != null) {
                if (failedStrategy == 1) {
                    log.info("执行步骤：" + step.getStepName() + "，失败。忽略");
                    throw new RuntimeSkipTo(RobotConstant.NEXT_STEP);
                } else if (failedStrategy == 2) {
                    log.info("执行步骤：" + step.getStepName() + "，失败。跳转到" + step.getFailedSkipTo());
                    if (StringUtils.isBlank(step.getFailedSkipTo())) {
                        throw new RobotConfigException("失败后选择跳转，跳转步骤不能为空");
                    } else {
                        throw new RuntimeSkipTo(step.getFailedSkipTo());
                    }
                }
            }
            throw rre;
        }

        //执行后等待.
        randomWait(step.getWaitAfter());
    }

    /**
     * 等待时间.
     *
     * @param seconds Null&0 不等待 , <0 随机时间 , >0 按秒等待
     */
    private void randomWait(Integer seconds) {
        seconds = (seconds == null) ? 0 : seconds;
        int milliseconds;
        if (seconds < 0) {
            milliseconds = (int) (Math.random() * 1000);
            if (milliseconds < 300) {
                milliseconds += 300;
            }
        } else if (seconds > 0) {
            milliseconds = seconds * 1000;
        } else {
            milliseconds = 0;
        }
        if (milliseconds > 0) {
            log.info("等待时间：" + milliseconds);
        }
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            log.error("【Exception】线程休眠异常中断,errorMsg=" + e.getMessage(), e);
            throw new RobotInterruptException("线程休眠异常中断." + e.getMessage());
        }
    }
}
