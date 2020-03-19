package ml.dreamingfire.group.prod.httpserver.util;


import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;

import java.util.*;

public class HttpRequestUtil {
    public static final String METHOD = "method";
    public static final String URI = "uri";
    public static final String PARAMS = "params";
    public static final String CONTENT_TYPE = "content-type";
    public static final String USER_AGENT = "user-agent";
    public static final String AUTHORIZATION = "authorization";

    // 分解请求信息
    public static Map<String, Object> resolveRequestIntoMap(FullHttpRequest request) {
        Map<String, Object> resultSet = new HashMap<>();
        resultSet.put(METHOD, request.method().name());
        resultSet.put(URI, request.uri().split("[ ?]+")[0]);
        Set<String> contentTypes = resolveContentType(request);
        resultSet.put(CONTENT_TYPE, contentTypes);
        resultSet.put(USER_AGENT, request.headers().get(HttpHeaderNames.USER_AGENT));
        resultSet.put(AUTHORIZATION, request.headers().get(HttpHeaderNames.AUTHORIZATION));
        resultSet.put(PARAMS, packParams(request, contentTypes));
        return resultSet;
    }

    // 请求参数打包
    private static Map<String, Object> packParams(FullHttpRequest request, Set<String> contentTypes) {
        Map<String, Object> requestParams = new HashMap<>();
        resolveGetParams(request, requestParams);
        if (request.method().name().toLowerCase().equals("post")) {
            resolvePostParams(request, contentTypes, requestParams);
        }
        return requestParams;
    }

    // 分解GET请求参数
    private static void resolveGetParams(FullHttpRequest request, Map<String, Object> requestParams) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> parame = decoder.parameters();
        Iterator<Map.Entry<String, List<String>>> iterator = parame.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> entry = iterator.next();
            requestParams.put(entry.getKey(), entry.getValue().get(0));
        }
    }

    // 分解POST请求参数
    private static void resolvePostParams(FullHttpRequest request, Set<String> contentTypes, Map<String, Object> requestParams) {
        if (contentTypes.contains("application/json")) {
            resolveJsonParams(request, requestParams);
        } else {
            HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(
                    new DefaultHttpDataFactory(false), request
            );
            List<InterfaceHttpData> postData = postDecoder.getBodyHttpDatas();
            for(InterfaceHttpData data: postData) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    MemoryAttribute attribute = (MemoryAttribute) data;
                    requestParams.put(attribute.getName(), attribute.getValue());
                }
            }
        }
    }

    // 分解JSON请求参数
    private static void resolveJsonParams(FullHttpRequest request, Map<String, Object> requestParams) {
        String content = request.content().toString(CharsetUtil.UTF_8);
        JSONObject jsonObj = JSONObject.parseObject(content);
        requestParams.putAll(jsonObj.getInnerMap());
    }

    // 分解content-type
    private static Set<String> resolveContentType(FullHttpRequest request) {
        Set<String> resultSet = new HashSet<>();
        String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
        String[] contentPack = contentType.split("[ ;]+");
        for(String contentItem: contentPack) {
            if (contentItem.equals("")) {
                continue;
            }
            resultSet.add(contentItem);
        }
        return resultSet;
    }
}
