package com.seebon.rpa.sms;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.utils.sms.SmsUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @Author MR. Li
 * @Description:
 * @Date: Created in 16:40 2021/8/12
 * @Modified By:
 */
@ChannelHandler.Sharable
@Slf4j
public class SmsServerHandler extends ChannelInboundHandlerAdapter {
    private final static String charset = "GBK";

    private static String smsCode = null;

    private static String smsContent = null;

    public static String getSmsCode() {
        try {
            return smsCode;
        } finally {
            smsCode = null;
            smsContent = null;
        }
    }

    public static void clearSmsCode() {
        smsCode = null;
        smsContent = null;
    }


    public static Map<String, Object> getSmsCodeByTemplate(List<Map<String, Object>> temps) {
        Map<String, Object> smsCodeInfo = getSmsMap();

        if (StringUtils.isBlank(smsContent) || StringUtils.isBlank(smsCode)) {
            return smsCodeInfo;
        }

        for (Map<String, Object> temp : temps) {
            String smsTemplate = (String) temp.get("smsTemplate");
            if (smsContent.contains(smsTemplate)) {
                smsCodeInfo.put("smsCode", smsCode);
                smsCodeInfo.put("smsContent", smsContent);
                smsCodeInfo.putAll(temp);
                break;
            }
        }
        return smsCodeInfo;
    }

    private static Map<String, Object> getSmsMap() {
        Map<String, Object> smsCodeInfo = Maps.newHashMap();
        smsCodeInfo.put("smsCode", "");
        smsCodeInfo.put("smsContent", "");
        smsCodeInfo.put("phoneNumbers", "");
        smsCodeInfo.put("accountIds", Lists.newArrayList());
        smsCodeInfo.put("smsType", "");
        smsCodeInfo.put("smsTemplate", "");
        return smsCodeInfo;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        //连接激活后，可做处理
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = insocket.getAddress().getHostAddress();
        int port = insocket.getPort();
        ByteBuf in = (ByteBuf) msg;
        String sms = in.toString(Charset.forName(charset));
        if (StringUtils.isNotEmpty(sms)) {
            sms = sms.replaceAll("\\r|\\n|\\t|\\u0000", "");
        }
        log.info("【Receive from {}: {}】:{}", ip, port, sms);

        if (sms.indexOf("【浙江政务服务网】") >= 0) { // 浙江宁波
            smsCode = SmsUtils.getSmsCode(sms.substring(sms.indexOf("【浙江政务服务网】") + 12));
            smsContent = sms;
        } else {
            if (sms.contains("106575255158310")) { // 浙江宁波的验证码有时会分成两条短信，如果不包含验证码的就忽略掉
                return;
            }
            smsContent = sms;
            smsCode = SmsUtils.getSmsCode(sms.substring(sms.indexOf("010101") + 12));
        }
        log.info("解析得到的短信验证码为:{}", smsCode);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws
            Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
