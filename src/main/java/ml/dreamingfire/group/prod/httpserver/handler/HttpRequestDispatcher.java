package ml.dreamingfire.group.prod.httpserver.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import ml.dreamingfire.group.prod.httpserver.util.FormatUtil;
import ml.dreamingfire.group.prod.httpserver.util.HttpRequestUtil;

import java.util.Map;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

public class HttpRequestDispatcher extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        if (is100ContinueExpected(fullHttpRequest)) {
                channelHandlerContext.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        }
        Map<String, Object> attrMap = HttpRequestUtil.resolveRequestIntoMap(fullHttpRequest);
        FormatUtil.info(attrMap.get(HttpRequestUtil.URI)
                + "\tmethod: "        + attrMap.get(HttpRequestUtil.METHOD)
                + "\tuser-agent: "    + attrMap.get(HttpRequestUtil.USER_AGENT)
                + "\tauthorization: " + attrMap.get(HttpRequestUtil.AUTHORIZATION)
                + "\tparameters: "    + attrMap.get(HttpRequestUtil.PARAMS));
        String msg = "<html><header><title>请求信息</title></header><body>URI: "
                + attrMap.get(HttpRequestUtil.URI) + "<br/>method: "
                + attrMap.get(HttpRequestUtil.METHOD) + "<br/>content type: "
                + attrMap.get(HttpRequestUtil.CONTENT_TYPE) + "<br/>params: "
                + attrMap.get(HttpRequestUtil.PARAMS) + "<br/></body></html>";
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }
}
