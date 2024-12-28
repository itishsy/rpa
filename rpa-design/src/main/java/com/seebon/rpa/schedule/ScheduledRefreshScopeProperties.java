package com.seebon.rpa.schedule;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
@Data
public class ScheduledRefreshScopeProperties {

    @Value("${schedule.generateQueueTask.open:false}")
    private Boolean scheduleGenerateQueueTaskOpen;

    @Value("${schedule.cleanDataTask.open:false}")
    private Boolean scheduleCleanDataTaskOpen;


}
