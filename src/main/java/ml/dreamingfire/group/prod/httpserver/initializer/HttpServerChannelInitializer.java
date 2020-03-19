package ml.dreamingfire.group.prod.httpserver.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import ml.dreamingfire.group.prod.httpserver.handler.HttpRequestDispatcher;

public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast("httpCodec", new HttpServerCodec())
                .addLast("httpAggregator", new HttpObjectAggregator(512 * 1024))
                .addLast("h1", new HttpRequestDispatcher());
    }
}
