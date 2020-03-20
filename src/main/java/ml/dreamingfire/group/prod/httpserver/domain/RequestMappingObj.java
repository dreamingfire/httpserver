package ml.dreamingfire.group.prod.httpserver.domain;

import java.lang.reflect.Method;

public class RequestMappingObj {
    // 类名
    private Class<?> className;
    // 方法名
    private Method methodName;
    // 参数类型列表
    private Class<?>[] parameterTypes;
    // 允许的请求方式
    private String[] allowMethods;

    public Class<?> getClassName() {
        return className;
    }

    public void setClassName(Class<?> className) {
        this.className = className;
    }

    public Method getMethodName() {
        return methodName;
    }

    public void setMethodName(Method methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public String[] getAllowMethods() {
        return allowMethods;
    }

    public void setAllowMethods(String[] allowMethods) {
        this.allowMethods = allowMethods;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

}
