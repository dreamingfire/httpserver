package ml.dreamingfire.group.prod.httpserver.application;

import ml.dreamingfire.group.prod.httpserver.reflection.ControllerScanner;
import ml.dreamingfire.group.prod.httpserver.server.impl.HttpServer;
import ml.dreamingfire.group.prod.httpserver.server.impl.HttpsServer;
import ml.dreamingfire.group.prod.httpserver.util.RequestMappingContext;

public class HttpServerApplication {
    public static void main(String[] args) throws Exception{
        int httpPort = 12321;
        int httpsPort = 21312;
        String scanPackage = "ml.dreamingfire.group.prod.httpserver.controller";
        // 加载请求配置到内存中
        RequestMappingContext.setValue(ControllerScanner.scanAllClassesByAnno(scanPackage, true));
        // 启动HTTP服务器
        new Thread(()-> {
            try {
                HttpServer.getInstance().start(httpPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "http-server").start();
        // 启动HTTPS服务器
        new Thread(()-> {
            try {
                HttpsServer.getInstance().start(httpsPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "https-server").start();
    }
}
