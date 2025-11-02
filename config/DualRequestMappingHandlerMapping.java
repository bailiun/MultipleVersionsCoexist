package org.bailiun.multipleversionscoexist.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bailiun.multipleversionscoexist.Aspect.CoexistenceVersion;
import org.bailiun.multipleversionscoexist.Aspect.InterfacePriority;
import org.bailiun.multipleversionscoexist.en.DualMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DualRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    DualMap<String> versionPrefixes = new DualMap<>();
    Map<String, Integer> interfacePriorities = new HashMap<>();
    List<String> FileConfiguration = new ArrayList<>();
    MultiVersionProperties mp;
    public DualRequestMappingHandlerMapping(MultiVersionProperties mp) {
        this.mp = mp;
        if(mp.isFileConfiguration()) {
            Path path = Paths.get(mp.getFilePath());
            try {
                FileConfiguration = Files.readAllLines(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Scheduled(fixedDelayString = "${multi.version.properties.FileRefreshTime}")
    public void watchConfigFile() {
        if (mp.isFileConfiguration()) {
            Path configPath = Paths.get(mp.getFilePath());
            try {
                if (Files.exists(configPath)) {
                    FileConfiguration = Files.readAllLines(configPath);
                    System.out.println("配置文件已刷新");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    private boolean SortingMethod(Integer a,Integer b) {
        if ("MAX".equalsIgnoreCase(mp.getSortingMethod())) {
            return a > b;
        }
        if ("MIN".equalsIgnoreCase(mp.getSortingMethod())) {
            return b > a;
        }
        throw new RuntimeException("SortingMethod配置失败");
    }
    @Bean
    @Primary
    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        if(!mp.isStart()){
            super.registerHandlerMethod(handler, method, mapping);
        }
        CoexistenceVersion cv = method.getDeclaringClass().getAnnotation(CoexistenceVersion.class);
        String path;
        if (mapping.getPatternsCondition() != null) {
            path = mapping.getPatternsCondition().getPatterns().iterator().next();
        } else if (mapping.getPathPatternsCondition() != null) {
            path = mapping.getPathPatternsCondition().getPatternValues().iterator().next();
        } else {
            throw new RuntimeException("接口注入存在问题,请检查");
        }
        if(!mp.VersionIsOk(path)){
            return;
        }
        if(versionPrefixes.keySet().size()>=mp.getMaxNum()){
            System.err.println("超出可注册版本最大数量,以下版本被拒绝注册:"+cv.version());
            return;
        }
        String newPath;
        if(cv != null){
            newPath = "/" +cv.version() + path;
        }else {
            super.registerHandlerMethod(handler, method, mapping);
            return;
        }
        InterfacePriority ip = method.getAnnotation(InterfacePriority.class);
        if (ip != null) {
            int priority = ip.value(); // 默认最低优先级
            if (interfacePriorities.containsKey(newPath)) {
                int existingPriority = interfacePriorities.get(newPath);
                if (SortingMethod(priority , existingPriority)) {
                    // 当前接口优先级更高，替换原来的注册
                    interfacePriorities.put(newPath, priority);
                    unregisterPath(newPath);
                    RequestMappingInfo newMapping = RequestMappingInfo.paths(newPath)
                            .methods(mapping.getMethodsCondition().getMethods().toArray(new RequestMethod[0]))
                            .build();
                    versionPrefixes.put("/" + cv.version(), newPath);
                    super.registerHandlerMethod(handler, method, newMapping);
                }else if(priority == existingPriority){
                    String beanName = handler.getClass().getSimpleName();
                    String methodName = method.getName();
                    throw new IllegalStateException(
                            String.format(
                                    "Ambiguous mapping. Cannot map '%s' method %s#%s() to {%s}: " +
                                            "There is already a method with the same priority",
                                    beanName, beanName, methodName, path
                            )
                    );
                } else {
                    System.out.println("忽略低优先级接口:" + newPath + "/--" + ip.value());
                }
            } else {
                interfacePriorities.put(newPath, priority);
                RequestMappingInfo newMapping = RequestMappingInfo.paths(newPath)
                        .methods(mapping.getMethodsCondition().getMethods().toArray(new RequestMethod[0]))
                        .build();
                versionPrefixes.put("/" + cv.version(), newPath);
                super.registerHandlerMethod(handler, method, newMapping);
            }
        }else {
            interfacePriorities.put(newPath, 0);
            RequestMappingInfo newMapping = RequestMappingInfo.paths(newPath)
                    .methods(mapping.getMethodsCondition().getMethods().toArray(new RequestMethod[0]))
                    .build();
            versionPrefixes.put("/" + cv.version(), newPath);
            super.registerHandlerMethod(handler, method, newMapping);
        }
    }

    @Override
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        if ("/error".equals(lookupPath)) {
            return super.lookupHandlerMethod(lookupPath, request);
        }
        System.out.println("接收到请求路径: " + lookupPath);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = this.getHandlerMethods();
        for (String versionPrefix : versionPrefixes.keySet()) {
            if (versionPrefixes.get(versionPrefix).contains(lookupPath)) {
                throw new NoHandlerFoundException(request.getMethod(), lookupPath, new ServletServerHttpRequest(request).getHeaders());
            }
            String newLookupPath = versionPrefix + lookupPath;
            System.out.println("尝试匹配版本路径: " + newLookupPath);
            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                RequestMappingInfo info = entry.getKey();
                if (info.getPatternsCondition() != null) {
                    for (String pattern : info.getPatternsCondition().getPatterns()) {
                        if (pattern.equals(newLookupPath)) {
                            System.out.println("匹配成功 => " + pattern);
                            return entry.getValue();
                        }
                    }
                }
            }
        }
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo info = entry.getKey();
            if (info.getPatternsCondition() != null) {
                for (String pattern : info.getPatternsCondition().getPatterns()) {
                    if (pattern.equals(lookupPath)) {
                        return entry.getValue();
                    }
                }
            }
        }
        throw new NoHandlerFoundException(request.getMethod(), lookupPath, new ServletServerHttpRequest(request).getHeaders());
    }


    public void unregisterPath(String path) {
        getHandlerMethods().keySet().stream()
                .filter(info -> info.getPatternsCondition() != null &&
                        info.getPatternsCondition().getPatterns().contains(path))
                .findFirst()
                .ifPresent(this::unregisterMapping);      }
}