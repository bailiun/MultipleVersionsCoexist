package org.bailiun.multipleversionscoexist.Realization;

import org.bailiun.multipleversionscoexist.Abstraction.MulRegisterHandlerMethod;
import org.bailiun.multipleversionscoexist.Aspect.CoexistenceVersion;
import org.bailiun.multipleversionscoexist.Properties.MultiVersionProperties;
import org.bailiun.multipleversionscoexist.en.DualMap;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
/**
 * <h2>默认多版本接口注册实现类 / Default Multi-Version Handler Method Implementation</h2>
 *
 * <p><b>中文说明：</b><br>
 * {@link MulRegisterHandlerMethod} 的默认实现类。
 * 提供多版本接口注册的基础逻辑，包括路径冲突检测、版本有效性验证等。
 * 通常情况下，框架默认使用该实现。
 * <br>如果开发者希望自定义注册逻辑，可以通过实现 {@link MulRegisterHandlerMethod}
 * 并在配置中手动注入替换此默认实现。</p>
 *
 * <p><b>English Description:</b><br>
 * The default implementation of {@link MulRegisterHandlerMethod}.
 * Provides basic logic for multi-version request mapping registration,
 * such as path conflict detection and version validation.
 * Developers can extend or replace this implementation to customize registration behavior.</p>
 *
 * <p><b>使用场景 / Usage:</b></p>
 * <pre>
 * // 在 Spring 配置类中替换默认实现：
 * &#64;Bean
 * public MulRegisterHandlerMethod customRegisterHandlerMethod() {
 *     return new MyCustomRegisterHandlerMethod();
 * }
 * </pre>
 *
 * @author ...
 * @since 1.0.0
 */
public class DefaultMulRegisterHandlerMethod implements MulRegisterHandlerMethod {
    /**
     * {@inheritDoc}
     * <p><b>中文说明：</b>默认实现：直接调用 {@code super.PrintIllegalStateException()}，
     * 在捕获 {@link IllegalStateException} 后进行异常处理或日志输出。</p>
     *
     * <p><b>English Description:</b> Default implementation that delegates
     * to {@code super.PrintIllegalStateException(...)} and handles
     * {@link IllegalStateException} by logging or reporting.</p>
     */
    @Override
    public void PrintIllegalStateException(Method method, String path, CoexistenceVersion cv) {
        System.err.println("\uD83D\uDD39 接口" + path + "发生重复冲突,已停止注入");
        System.err.println("\t所属类:" + method.getDeclaringClass().getName());
        System.err.println("\t文件路径:" + method.getDeclaringClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath());
        System.err.println("\t原因:存在路径完全一样的接口");
        System.err.println("\t所属版本:" + cv.version());
    }
    //
    @Override
    public RequestMappingInfo InterfaceInjection(RequestMappingInfo mapping, String path, CoexistenceVersion cv, DualMap<String> versionPrefixes) {
        RequestMappingInfo newMapping = RequestMappingInfo.paths(path)
                .methods(mapping.getMethodsCondition().getMethods().toArray(new RequestMethod[0]))
                .build();
        versionPrefixes.put("/" + cv.version(), path);
        return newMapping;
    }

    @Override
    public boolean SortingMethod(MultiVersionProperties mp,Integer a, Integer b) {
        if ("MAX".equalsIgnoreCase(mp.getSortingMethod())) {
            return a > b;
        }
        if ("MIN".equalsIgnoreCase(mp.getSortingMethod())) {
            return b > a;
        }
        throw new RuntimeException("SortingMethod配置失败");
    }
    @Override
    public String getMethodPath(RequestMappingInfo mapping){
        if (mapping.getPatternsCondition() != null) {
            return mapping.getPatternsCondition().getPatterns().iterator().next();
        } else if (mapping.getPathPatternsCondition() != null) {
            return mapping.getPathPatternsCondition().getPatternValues().iterator().next();
        } else {
            throw new RuntimeException("接口注入存在问题,请检查");
        }
    }
}
