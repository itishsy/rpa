package com.seebon.rpa.entity.robot.enums;

/**
 * @author : 阿祥
 * @desc :
 * @date : 2023/11/15  16:33
 */
public enum WebExcelSfzNumLine {

    A(1),
    B(2),
    C(3),
    D(4),
    E(5),
    F(6),
    G(7),
    H(8),
    I(9),
    J(10),
    K(11),
    L(12),
    M(13),
    N(14),
    O(15),
    P(16),
    Q(17),
    R(18),
    S(19),
    T(20);

    Integer name;


    public Integer getName() {
        return name;
    }

    public void setName(Integer name) {
        this.name = name;
    }

    WebExcelSfzNumLine(Integer name){
        this.name = name;
    }

}
