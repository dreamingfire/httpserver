package ml.dreamingfire.group.prod.httpserver.reflection;

import ml.dreamingfire.group.prod.httpserver.anno.Controller;
import ml.dreamingfire.group.prod.httpserver.anno.RequestMapping;
import ml.dreamingfire.group.prod.httpserver.domain.RequestMappingObj;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ControllerScanner {
    // 找到类集合中所有包含Controller注解的类
    public static Map<String, RequestMappingObj> scanAllClassesByAnno(String pkgName, final boolean recursive) throws Exception{
        Map<String, RequestMappingObj> resultMap = new HashMap<>();
        String pkgPath = getPkgPath(pkgName);
        Set<Class<?>> allClazz = scanAllClasses(pkgPath, pkgName, recursive);
        for (Class<?> curClass: allClazz) {
            if (curClass.isAnnotationPresent(Controller.class)) {
                resultMap.putAll(scanMethodsByAnno(curClass));
            }
        }
        return resultMap;
    }

    // 扫描类中含有RequestMap注解的方法
    private static Map<String, RequestMappingObj> scanMethodsByAnno(Class<?> classObj) {
        Map<String, RequestMappingObj> resultMap = new HashMap<>();
        Method[] methods = classObj.getMethods();
        for(Method method: methods) {
            if (!method.isAnnotationPresent(RequestMapping.class)) {
                continue;
            }
            RequestMapping anno = method.getAnnotation(RequestMapping.class);
            String uri = anno.value();
            String[] reqMethods = anno.method();
            RequestMappingObj reqMapping = new RequestMappingObj();
            reqMapping.setClassName(classObj);
            reqMapping.setMethodName(method);
            reqMapping.setParameterTypes(method.getParameterTypes());
            reqMapping.setAllowMethods(reqMethods);
            resultMap.put(uri, reqMapping);
        }
        return resultMap;
    }

    // 根据包名找到URL
    private static String getPkgPath(String pkgName) throws Exception{
        String pkgDir = pkgName.replace('.', File.separatorChar);
        URL url = Thread.currentThread().getContextClassLoader().getResource(pkgDir);
        return url == null ? null : URLDecoder.decode(url.getPath(), "UTF-8");
    }

    // 获取包下所有对象的集合
    private static Set<Class<?>> scanAllClasses(String pkgPath, String pkgName, final boolean recursive) throws Exception {
        Set<Class<?>> classSet = new HashSet<>();
        Collection<File> files = getAllClassFile(pkgPath, recursive);
        for (File file: files) {
            String absPath = file.getAbsolutePath().substring(0,file.getAbsolutePath().length() - "class".length() - 1);
            String className = absPath.substring(pkgPath.length()).replace(File.separatorChar, '.');
            className = className.startsWith(".") ? pkgName + className : pkgName + "." + className;
            classSet.add(Thread.currentThread().getContextClassLoader().loadClass(className));
        }
        return classSet;
    }

    private static Collection<File> getAllClassFile(String pkgPath, boolean recursive) throws Exception {
        File fPkgDir = new File(pkgPath);

        if (!(fPkgDir.exists() && fPkgDir.isDirectory())){
            throw new Exception("error occur when handle pkDir: no such directory found");
        }

        return FileUtils.listFiles(fPkgDir, new String[]{"class"}, recursive);
    }
}
