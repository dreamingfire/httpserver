package ml.dreamingfire.group.prod.httpserver.server.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ml.dreamingfire.group.prod.httpserver.initializer.HttpServerChannelInitializer;
import ml.dreamingfire.group.prod.httpserver.server.ServerApi;
import ml.dreamingfire.group.prod.httpserver.util.FormatUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpServer implements ServerApi {
    private static int count = 0;
    private static HttpServer self;

    private HttpServer() {
        count = 1;
    }

    public static HttpServer getInstance() {
        synchronized((Integer) count) {
            if (count <= 0) {
                self = new HttpServer();
            }
        }
        return self;
    }

    @Override
    public void start(int port) throws Exception {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();
        try {
            // 设置引导
            ServerBootstrap boot = new ServerBootstrap();
            // 添加线程池及处理逻辑
            boot.group(bossLoopGroup, workerLoopGroup)
                    .localAddress(port)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerChannelInitializer());
            // 阻塞并启动程序
            ChannelFuture future = boot.bind().sync();
            FormatUtil.info("http  server started and listen on " + future.channel().localAddress());
            // 阻塞并关闭服务器
            future.channel().closeFuture().sync();
        } finally {
            // 关闭服务
            bossLoopGroup.shutdownGracefully();
            workerLoopGroup.shutdownGracefully();
        }
    }
}
