package com.seebon.rpa.entity.agent.dto.declare;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author xumin
 * @Date 2023/2/23 19:02
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeclareInfoDTO implements Serializable {
    private static final long serialVersionUID = 4496574994998917363L;
    private String key;
    private String value;
}
