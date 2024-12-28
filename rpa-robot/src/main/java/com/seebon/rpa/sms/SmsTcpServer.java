package com.seebon.rpa.sms;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * 接收短信设备主动推送过来的短信
 * 设备的配置请查看工程路径先src/main/doc的文档
 *
 * @Author MR. Li
 * @Description: TCP server
 * @Date: Created in 16:39 2021/8/12
 * @Modified By:
 */
@Component
@Slf4j
public class SmsTcpServer {

    @Value("${setting.sms.tcp.server.port:}")
    private Integer tcpServerPort;
    @Value("${setting.sms.tcp.server.ip:}")
    private String tcpServerIp;

    private EventLoopGroup group = null;

    public void start() throws InterruptedException {
        if (group == null) {
            log.info("成功启动短信TCP server,服务的ip/域名为:{},端口为:{}", tcpServerIp, tcpServerPort);
            group = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(group)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .localAddress(new InetSocketAddress(tcpServerPort))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws
                                    Exception {
                                socketChannel.pipeline().addLast(
                                        //添加读取超时
                                        new ReadTimeoutHandler(600),
                                        //添加写入超时
                                        new WriteTimeoutHandler(600),
                                        //消息处理Handler
                                        new SmsServerHandler()
                                );
                            }
                        });
                ChannelFuture future = bootstrap.bind().sync();
                future.channel().closeFuture().sync();
            } finally {
                group.shutdownGracefully().sync();
            }
        }
    }
}
