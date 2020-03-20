package ml.dreamingfire.group.prod.httpserver.controller;

import com.alibaba.fastjson.JSONObject;
import ml.dreamingfire.group.prod.httpserver.anno.Controller;
import ml.dreamingfire.group.prod.httpserver.anno.RequestMapping;
import ml.dreamingfire.group.prod.httpserver.util.HttpRequestUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * The first controller for netty http server
 * */
@Controller
public class IndexController {

    @RequestMapping(value = "/", method = {"get"})
    public JSONObject index() {
        Map<String, Object> json = new HashMap<>();
        json.put("status", 200);
        json.put("content", "welcome to visit Li Dafei's http server with netty");
        json.put("timestamp", System.currentTimeMillis());
        return new JSONObject(json);
    }
}
