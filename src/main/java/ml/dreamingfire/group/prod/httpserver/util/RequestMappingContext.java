package ml.dreamingfire.group.prod.httpserver.util;

import ml.dreamingfire.group.prod.httpserver.domain.RequestMappingObj;

import java.util.Map;

public class RequestMappingContext {
    private static Map<String, RequestMappingObj> map = null;

    public static void setValue(Map<String, RequestMappingObj> rsMap) {
        map = rsMap;
    }

    public static RequestMappingObj getValue(String uri) {
        return map.get(uri);
    }

    public static boolean contain(String uri) {
        return map != null && map.containsKey(uri);
    }

}
