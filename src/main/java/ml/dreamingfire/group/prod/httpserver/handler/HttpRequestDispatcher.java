package ml.dreamingfire.group.prod.httpserver.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import ml.dreamingfire.group.prod.httpserver.domain.RequestMappingObj;
import ml.dreamingfire.group.prod.httpserver.util.FormatUtil;
import ml.dreamingfire.group.prod.httpserver.util.HttpRequestUtil;
import ml.dreamingfire.group.prod.httpserver.util.RequestMappingContext;

import java.util.Arrays;
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
        String msg = "";
        String uri = (String) attrMap.get(HttpRequestUtil.URI);
        String method = (String) attrMap.get(HttpRequestUtil.METHOD);
        if (RequestMappingContext.contain(uri)) {
            RequestMappingObj rmObj = RequestMappingContext.getValue(uri);
            if (Arrays.binarySearch(rmObj.getAllowMethods(), method, (o1, o2) -> {
                if (o1.toUpperCase().equals(o2.toUpperCase())) {
                    return 0;
                }
                return -1;
            }) < 0) {
                msg = HttpRequestUtil.MethodNotAllowedPageMessage(method);
            } else {
                // 使用反射调用控制器方法
                Object ret;
                if (rmObj.getParameterTypes().length > 0) {
                    ret = rmObj.getMethodName().invoke(rmObj.getClassName().getDeclaredConstructor().newInstance(), attrMap);
                } else {
                    ret = rmObj.getMethodName().invoke(rmObj.getClassName().getDeclaredConstructor().newInstance());
                }
                msg = ret.toString();
            }
        } else {
            msg = HttpRequestUtil.NotFoundPageMessage(uri);
        }
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
