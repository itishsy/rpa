package com.seebon.rpa;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.seebon.rpa.utils.HttpUtils;

import java.util.Map;

/**
 * 南通-社保-在册 SR10-2309-5XI0=3016638837
 */
public class RobotTaskTest {
    //生产
    private static final String url = "https://rpa.seebon.com/api/robot/openApi/task";
    private static final String authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ4dWZ1emhvdSIsInNjb3BlIjpbInJ3Il0sIm5hbWUiOiLlvpDlr4zlkagiLCJjdXN0SWQiOjEsInVzZXJUeXBlIjoxLCJtYWluVXNlciI6bnVsbCwiZXhwIjoxODA3OTM1ODA4LCJjdXN0TmFtZSI6IuW5v-W3nuWNl-aWueS7lemAmue9kee7nOenkeaKgOaciemZkOWFrOWPuCIsInVzZXJJZCI6MTYsImF1dGhvcml0aWVzIjpbImFkbWluIl0sImp0aSI6IjU2MGM5N2UyLTEwOTgtNDFkYy1iZjI5LWI2ZWI0NTI4ODk2NCIsImNsaWVudF9pZCI6InJwYSJ9.-mNdMrNUvNTNKsqrZZ9PO0_drFJRvCtYOVOx5BENKLw";
    //本地
    //private static final String url = "http://172.172.4.142:9527/robot/openApi/task/";
    //准生产
    //private static final String url = "http://192.168.0.99:8000/api/robot/openApi/task";
    //private static final String authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ4dWZ1emhvdSIsInNjb3BlIjpbInJ3Il0sIm5hbWUiOiJ4dWZ1emhvdSIsImN1c3RJZCI6MCwidXNlclR5cGUiOjEsIm1haW5Vc2VyIjpudWxsLCJleHAiOjE4MDc3MDU0MDYsImN1c3ROYW1lIjoi5Y2X5pa55LuV6YCa572R57uc56eR5oqA5pyJ6ZmQ5YWs5Y-4IiwidXNlcklkIjoxNiwiYXV0aG9yaXRpZXMiOlsiYWRtaW4iXSwianRpIjoiYTg0ZDk1YWEtN2M0ZC00OWMyLWI5MzMtNjIwMWNhZjQyMmNmIiwiY2xpZW50X2lkIjoicnBhIn0.31BFlCHDUFQx86Go8t_sZGfH-Mt2Xx24YAzi3YfDiGA";

    public static void main(String[] args) {
        getRobot();
    }

    public static void getRobot() {
        Map<String, Object> data = Maps.newHashMap();
        data.put("businessType", "社保");
        data.put("addrName", "南通");

        post("/getRobot", data);
    }

    public static void taskRun() {
        Map<String, Object> data = Maps.newHashMap();
        data.put("businessType", "社保");
        data.put("declareType", "在册");
        data.put("machineCode", "SR10-2309-5XI0");

        data.put("addrName", "南通");
        data.put("accountNumber", "3016638837");

        post("/run", data);
    }

    public static void getRunStatus() {
        Map<String, Object> data = Maps.newHashMap();
        data.put("businessType", "社保");
        data.put("declareType", "在册");
        data.put("machineCode", "SR10-2309-5XI0");

        data.put("addrName", "南通");
        data.put("accountNumber", "3016638837");

        post("/getRunStatus", data);
    }

    public static void getRunDetail() {
        Map<String, Object> data = Maps.newHashMap();
        data.put("businessType", "社保");
        data.put("declareType", "在册");
        data.put("machineCode", "SR10-2309-5XI0");

        data.put("addrName", "南通");
        data.put("accountNumber", "3016638837");

        post("/getRunDetail", data);
    }

    private static String post(String method, Map<String, Object> data) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Authorization", authorization);

        String json = HttpUtils.post(url + "" + method, JSON.toJSONString(data), headers);
        System.out.println(json);
        return json;
    }
}
