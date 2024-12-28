package com.seebon.rpa.entity.saas.dto.ai;

import com.seebon.rpa.entity.saas.po.ai.AIPersonDetail;
import lombok.Data;

import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-08-25 16:18
 */
@Data
public class AIPersonDetailDTO{
   List<AIPersonDetail> aiPersonDetails;

   public String uuid;
}
