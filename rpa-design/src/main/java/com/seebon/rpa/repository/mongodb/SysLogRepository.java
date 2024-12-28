package com.seebon.rpa.repository.mongodb;



import com.seebon.rpa.entity.SysLog;

import java.util.List;

public interface SysLogRepository {

    void insetList(List<SysLog> logs);

    void inset(SysLog sysLog);

}
