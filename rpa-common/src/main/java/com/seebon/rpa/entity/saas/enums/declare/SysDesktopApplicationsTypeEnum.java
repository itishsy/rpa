package com.seebon.rpa.entity.saas.enums.declare;

/**
 * @author : 阿祥
 * @desc :
 * @date : 2023/10/24  9:51
 */
public enum SysDesktopApplicationsTypeEnum {

    click("click", 1),//点击事件
    blur("blur", 2),//输入事件
    upload("upload", 3),//上传文件
    download("downLoad", 4),//下载
    xmlhttprequest("xmlhttprequest", 5),//http请求
    change("change", 6),//下拉输入
    table_index("table_index",7),//右键选择填充excel行数和列数
    table_index_select("table_index_select",8),//点击下拉，ctrl+右击,ctrl+单击
    process_error("process_error",9),    //出错退出
    register_table("register_table",10),//回盘在册明细
    register_table_name("register_table_name",11),//在册姓名列
    register_table_idCard("register_table_idCard",12),//在册身份证
    register_table_number("register_table_number",13),//在册个人编号
    register_table_base("register_table_base",14),//在册缴纳基数
    register_table_page("register_table_page",15),//在册表格翻页按钮
    cost_table("cost_table",16),//回盘费用明细
    cost_table_date("cost_table_date",17),//费用所属期
    cost_table_header("cost_table_header",18),//费用表头行
    cost_table_page("cost_table_page",19),//费用表格翻页按钮
    ;

    private String type;

    private Integer code;

    SysDesktopApplicationsTypeEnum(String type, Integer code) {
        this.type = type;
        this.code = code;
    }

    public static SysDesktopApplicationsTypeEnum getEnumByCode(String code) {
        SysDesktopApplicationsTypeEnum[] values = SysDesktopApplicationsTypeEnum.values();
        for (SysDesktopApplicationsTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static SysDesktopApplicationsTypeEnum getEnumByName(String name) {
        SysDesktopApplicationsTypeEnum[] values = SysDesktopApplicationsTypeEnum.values();
        for (SysDesktopApplicationsTypeEnum item : values) {
            if (item.getType().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
