package org.bailiun.multipleversionscoexist.config;

import org.bailiun.multipleversionscoexist.Aspect.CoexistenceVersion;
import org.bailiun.multipleversionscoexist.Aspect.InterfacePriority;
import org.bailiun.multipleversionscoexist.Aspect.NotIncCoexistenceVersion;
import org.bailiun.multipleversionscoexist.Properties.MultiVersionFile;
import org.bailiun.multipleversionscoexist.Properties.MultiVersionInfo;
import org.bailiun.multipleversionscoexist.Properties.MultiVersionProperties;
import org.bailiun.multipleversionscoexist.Realization.DefaultMulRegisterHandlerMethod;
import org.bailiun.multipleversionscoexist.en.DualMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNullApi;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
/**
 * DualRequestMappingHandlerMapping — 核心多版本请求映射处理器 / Core Multi-Version Request Mapping Handler
 *
 * <p><b>中文说明：</b><br>
 * 该类继承自 Spring 的 {@link RequestMappingHandlerMapping}，用于在多版本环境下动态注册接口。
 * 主要职责包括：</p>
 *
 * <ul>
 *     <li>为接口和类方法注入版本前缀</li>
 *     <li>支持接口黑白名单与最大注册版本数控制</li>
 *     <li>处理优先级冲突，允许高优先级接口覆盖低优先级接口</li>
 *     <li>支持本地文件控制版本访问（{@link MultiVersionFile}）</li>
 *     <li>提供可视化版本信息输出（{@link MultiVersionInfo}）</li>
 *     <li>捕获并报告路径冲突异常（{@link DefaultMulRegisterHandlerMethod}）</li>
 *     <li>统一管理多版本接口的注册与注销逻辑</li>
 * </ul>
 *
 * <p><b>English Description:</b><br>
 * Extends Spring's {@link RequestMappingHandlerMapping} to support multi-version API registration.
 * Responsibilities include:</p>
 *
 * <ul>
 *     <li>Injecting version prefixes for API methods and classes</li>
 *     <li>Supporting whitelist/blacklist and maximum version limits</li>
 *     <li>Resolving priority conflicts; higher priority APIs can override lower ones</li>
 *     <li>Supporting file-based version access control via {@link MultiVersionFile}</li>
 *     <li>Providing version information output via {@link MultiVersionInfo}</li>
 *     <li>Capturing and reporting path conflict exceptions via {@link DefaultMulRegisterHandlerMethod}</li>
 *     <li>Managing registration and unregistration of multi-version endpoints</li>
 * </ul>
 *
 * <p><b>使用示例 / Example:</b></p>
 * <pre>{@code
 * @CoexistenceVersion("v1")
 * @RestController
 * public class MyController {
 *     @GetMapping("/api/hello")
 *     public String hello() { return "Hello v1"; }
 * }
 *
 * // DualRequestMappingHandlerMapping 会自动注册路径 "/v1/api/hello"
 * }</pre>
 *
 * <p><b>注意事项 / Notes:</b></p>
 * <ul>
 *     <li>未激活版本将被加入 {@link #UNACTIVATED_VERSION} 并排除注册</li>
 *     <li>重复路径注册会根据 {@link InterfacePriority} 注解决定是否覆盖</li>
 *     <li>本地文件控制功能需要 {@link VersionEnvironmentLoader} 配合使用</li>
 * </ul>
 *
 * @author bailiun
 * @version 1.0.0
 * @since 1.0.0
 */
public class DualRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    /** <b>中文：</b>存储<版本名称,接口集合> <br>
     <b>English:</b>Stores mapping from version name to associated endpoint paths */
    DualMap<String> versionPrefixes = new DualMap<>();

    /** <b>中文：</b>存储<接口名称,优先级> <br>
     <b>English:</b>Stores interface priority for each endpoint */
    Map<String, Integer> interfacePriorities = new HashMap<>();

    /** <b>中文：</b>未激活版本列表 <br>
     <b>English:</b>List of unactivated versions */
    List<String> UNACTIVATED_VERSION = new ArrayList<>();

    /** <b>中文：</b>文件控制的可访问版本列表 <br>
     <b>English:</b>Accessible versions controlled by local file */
    List<String> FileConfiguration = new ArrayList<>();

    /** <b>中文：</b>多版本基础配置 <br>
     <b>English:</b>Multi-version base properties */
    MultiVersionProperties mp;

    /** <b>中文：</b>文件访问控制配置 <br>
     <b>English:</b>File-based version access control */
    MultiVersionFile mf;

    /** <b>中文：</b>版本信息输出控制 <br>
     <b>English:</b>Version info display control */
    MultiVersionInfo mi;

    /** <b>中文：</b>环境加载器 <br>
     <b>English:</b>Environment loader for active versions */
    @Resource
    VersionEnvironmentLoader v;

    /** <b>中文：</b>默认实现接口方法工具 <br>
     <b>English:</b>Default implementation of registration helper */
    DefaultMulRegisterHandlerMethod drm = new DefaultMulRegisterHandlerMethod();

    /**
     * <b>中文：</b>构造函数 <br>
     * <b>English:</b>Constructor to inject core configuration classes
     */
    public DualRequestMappingHandlerMapping(MultiVersionProperties mp,
                                            MultiVersionFile mf,
                                            MultiVersionInfo mi) {
        this.mp = mp;
        this.mf = mf;
        this.mi = mi;
    }

    /**
     * <b>中文：</b>注册 Handler 方法（接口注入） <br>
     * <b>English:</b>Register handler method with multi-version support
     */
    @Bean
    @Primary
    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        if (!mp.isStart()) {
            super.registerHandlerMethod(handler, method, mapping);
            return;
        }
        if (method.getAnnotation(NotIncCoexistenceVersion.class) != null) {
            super.registerHandlerMethod(handler, method, mapping);
            return;
        }
        CoexistenceVersion cv = getCV(method);
        String path = drm.getMethodPath(mapping);
        String newPath;
        if (cv != null) {
            if (UNACTIVATED_VERSION.contains(cv.version())) {
                return;
            }
            if (mi.getVersionsInfo().versionIsOk(cv.version()) == 2) {
                UNACTIVATED_VERSION.add(cv.version());
                return;
            }
            newPath = "/" + cv.version() + path;
        } else {
            super.registerHandlerMethod(handler, method, mapping);
            return;
        }
        if (!mp.VersionIsOk(cv.version(),path)) {
            return;
        }
        if (versionPrefixes.keySet().size() >= mp.getMaxNum()) {
            System.err.println("超出可注册版本最大数量,以下版本被拒绝注册:" + cv.version());
            return;
        }

        InterfacePriority ip = method.getAnnotation(InterfacePriority.class);
        if (ip != null) {
            int priority = ip.value(); // 默认最低优先级
            if (interfacePriorities.containsKey(newPath)) {
                int existingPriority = interfacePriorities.get(newPath);
                if (drm.SortingMethod(mp,priority, existingPriority)) {
                    unregisterPath(newPath);
                    // 当前接口优先级更高，替换原来的注册
                    interfacePriorities.put(newPath, priority);
                    RequestMappingInfo newMapping = drm.InterfaceInjection(mapping, newPath, cv, versionPrefixes);
                    registerHandlerMethodD(handler,method,newMapping,path,cv);
                } else if (priority == existingPriority) {
                    drm.PrintIllegalStateException(method, path, cv);
                } else {
                    // 当前接口优先级低，不注册
                    System.out.println("忽略低优先级接口:" + newPath + "/--优先级:" + ip.value());
                }
            } else {
                interfacePriorities.put(newPath, priority);
                // 为重复路径生成一个独立注册名
                RequestMappingInfo newMapping = drm.InterfaceInjection(mapping, newPath, cv, versionPrefixes);
                registerHandlerMethodD(handler,method,newMapping,path,cv);
            }
        } else {
            interfacePriorities.put(newPath, 0);
            // 为重复路径生成一个独立注册名
            RequestMappingInfo newMapping = drm.InterfaceInjection(mapping, newPath, cv, versionPrefixes);
            registerHandlerMethodD(handler,method,newMapping,path,cv);
        }
    }

    protected void registerHandlerMethodD(Object handler, Method method, RequestMappingInfo mapping,String path,CoexistenceVersion cv){
        try {
            super.registerHandlerMethod(handler, method, mapping);
        } catch (IllegalStateException e) {
            drm.PrintIllegalStateException(method,path,cv);
        }
    }
    /**
     * <b>中文：</b>查找 Handler 方法（含版本控制和路径匹配） <br>
     * <b>English:</b>Lookup handler method with version-aware path matching
     */
    @Override
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        if ("/error".equals(lookupPath)) {
            return super.lookupHandlerMethod(lookupPath, request);
        }
        // 获取所有注册的 handler 方法,进行自定义判断
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = this.getHandlerMethods();
        for (String versionPrefix : versionPrefixes.keySet()) {
            // 判断是否开启本地文件控制版本访问,如果开启则判断此版本能不能访问
            if(mf.isFileConfiguration() && !FileConfiguration.contains(versionPrefix)){
                continue;
            }
            // 若直接访问版本原路径,则直接抛出报错
            if (versionPrefixes.get(versionPrefix).contains(lookupPath)) {
                throw new NoHandlerFoundException(request.getMethod(), lookupPath, new ServletServerHttpRequest(request).getHeaders());
            }
            String newLookupPath = versionPrefix + lookupPath;
            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                RequestMappingInfo info = entry.getKey();
                if (info.getPatternsCondition() != null) {
                    for (String pattern : info.getPatternsCondition().getPatterns()) {
                        if (PathMatch(pattern,newLookupPath)) {
                            System.out.println("匹配成功 => " + pattern);
                            return entry.getValue();
                        }
                    }
                }
            }
        }

        AntPathMatcher antMatcher = new AntPathMatcher();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo info = entry.getKey();
            // For newer Spring versions (PathPatternParser)
            if (info.getPathPatternsCondition() != null) {
                for (PathPattern pattern : info.getPathPatternsCondition().getPatterns()) {
                    if (PathPatternParserMatch(pattern,lookupPath)) {
                        System.out.println("匹配成功 => " + lookupPath);
                        return entry.getValue();
                    }
                }
            }

            // For backward compatibility (Ant-style patterns)
            if (info.getPatternsCondition() != null) {
                for (String pattern : info.getPatternsCondition().getPatterns()) {
                    if (AntStylePatternsMatch(antMatcher,pattern,lookupPath)) {
                        System.out.println("匹配成功 => " + pattern);
                        return entry.getValue();
                    }
                }
            }
        }
        throw new NoHandlerFoundException(request.getMethod(), lookupPath, new ServletServerHttpRequest(request).getHeaders());
    }
    /**
     * <b>中文：</b>判断访问者路径lookupPath是否匹配当前路径,如果匹配则执行此路径下的接口 <br>
     * <b>English:</b>Determine whether the visitor path lookupPath matches the current path, if so, execute the interface under this path
     */
    public boolean PathMatch(String pattern,String lookupPath){
        return pattern.equals(lookupPath);
    }
    /**
     * <b>中文：</b>判断访问者路径lookupPath是否匹配当前路径,如果匹配则执行此路径下的接口 <br>
     * <b>English:</b>Determine whether the visitor path lookupPath matches the current path, if so, execute the interface under this path
     */
    public boolean PathPatternParserMatch(PathPattern pattern,String lookupPath){
        return pattern.matches(PathContainer.parsePath(lookupPath));
    }
    /**
     * <b>中文：</b>判断访问者路径lookupPath是否匹配当前路径pattern,如果匹配则执行此路径下的接口 <br>
     * <b>English:</b>Determine whether the visitor path lookupPath matches the current path, if so, execute the interface under this path
     */
    public boolean AntStylePatternsMatch(AntPathMatcher antMatcher,String pattern,String lookupPath){
        return antMatcher.match(pattern, lookupPath);
    }

    /**
     * <b>中文：</b>注销指定路径 <br>
     * <b>English:</b>Unregister a handler mapping by path
     */
    public void unregisterPath(String path) {
        getHandlerMethods().keySet().stream()
                .filter(info -> info.getPatternsCondition() != null &&
                        info.getPatternsCondition().getPatterns().contains(path))
                .findFirst()
                .ifPresent(this::unregisterMapping);  // 调用父类的注销方法
    }
    /**
     * <b>中文：</b>检查方法或类上是否有 @CoexistenceVersion 注解 <br>
     * <b>English:</b>Check if method or declaring class has @CoexistenceVersion
     */
    private CoexistenceVersion getCV(Method method){
        CoexistenceVersion cv = method.getAnnotation(CoexistenceVersion.class);
        return cv != null?cv:method.getDeclaringClass().getAnnotation(CoexistenceVersion.class);
    }
    /**
     * <b>中文：</b>在所有 Handler 方法初始化完成后刷新激活版本列表 <br>
     * <b>English:</b>Refresh active version list after all handler methods are initialized
     */
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet(); // 确保父类逻辑执行
        // UNACTIVATED_VERSION
        v.refreshActiveVersions(UNACTIVATED_VERSION);
    }

}