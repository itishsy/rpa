package com.seebon.rpa.entity.robot.dto.history;

import com.seebon.rpa.entity.saas.po.social.SocialCostCust;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
import java.util.Date;

/**
 * 政策申领-更新客户的历史记录
 */
@Data
@Document(collection = "saas_social_cust_history")
public class SassSocialCustHistory implements Serializable {
    /**
     * 插入或修改后
     * */
    private SocialCostCust update;
    /**
     * 更新时间
     * */
    private Date updateTime;
    /**
     * 更新还是插入
     * */
    private String type;
}
