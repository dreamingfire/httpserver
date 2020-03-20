package ml.dreamingfire.group.prod.httpserver.controller;

import com.alibaba.fastjson.JSONObject;
import ml.dreamingfire.group.prod.httpserver.anno.Controller;
import ml.dreamingfire.group.prod.httpserver.anno.RequestMapping;
import ml.dreamingfire.group.prod.httpserver.util.HttpRequestUtil;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TestController {
    @RequestMapping(value = "/test", method = {"get", "post"})
    public JSONObject test(Map<String, Object> attrMap) {
        Map<String, Object> json = new HashMap<>();
        json.put("status", 200);
        json.put("content", attrMap.get(HttpRequestUtil.PARAMS));
        json.put("timestamp", System.currentTimeMillis());
        return new JSONObject(json);
    }
}
