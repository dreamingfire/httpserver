package ml.dreamingfire.group.prod.httpserver.application;

import ml.dreamingfire.group.prod.httpserver.server.impl.HttpServer;
import ml.dreamingfire.group.prod.httpserver.server.impl.HttpsServer;

public class HttpServerApplication {
    public static void main(String[] args) {
        int httpPort = 12321;
        int httpsPort = 21312;
        new Thread(()-> {
            try {
                HttpServer.getInstance().start(httpPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "http-server").start();
        new Thread(()-> {
            try {
                HttpsServer.getInstance().start(httpsPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "https-server").start();
    }
}
