package org.bailiun.multipleversionscoexist.Abstraction;

import org.bailiun.multipleversionscoexist.Aspect.CoexistenceVersion;
import org.bailiun.multipleversionscoexist.Aspect.InterfacePriority;
import org.bailiun.multipleversionscoexist.Properties.MultiVersionProperties;
import org.bailiun.multipleversionscoexist.en.DualMap;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
/**
 * MulRegisterHandlerMethod
 *
 * <p>部分功能方法的抽象类,开发者可以通过继承此抽象类来覆盖开发这个组件<br>An abstract class of partially functional methods that the developer can override by inherits from</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>异常捕捉后的信息输出<br>Information output after exception catching</li>
 *   <li>为不同版本相同路径的接口生成一个新注册名<br>Generate a new registered name for interfaces with different versions of the same path</li>
 *   <li>@InterfacePriority相关注解的优先值判断方法<br>@InterfacePriority Priority method for related annotations</li>
 *   <li>获取接口的实际路径<br>Gets the actual path of the interface</li>
 * </ul>
 *
 * @author Bailiun
 * @since 1.0.0
 */
public interface MulRegisterHandlerMethod {
    /**
     * 用于捕捉多版本路径冲突异常后打印具体信息<br>Used to capture multi-version path conflict exceptions and print specific information
     *
     * @param method 接口注入过程中正在被注入的方法<br> The method interface being injected during the interface injection process
     * @param path 被注入方法的路径<br>h The path that has been injected with the method
     * @param cv 注解@CoexistenceVersion的实例,可为空<br> cv Annotation for the instance of @CoexistenceVersion can be empty.
     */
    void PrintIllegalStateException(Method method,String path,CoexistenceVersion cv);
    /**
     * 为不同版本但路径重复的接口生成一个独立注册名<br>Generate an independent registration name for interfaces of different versions but with the same path.
     *
     * @param mapping 接口注入过程中正在被注入的方法的RequestMappingInfo<br>RequestMappingInfo of the method being injected during interface injection
     * @param path 被注入方法的原始路径<br>The original path of the injected method
     * @param cv 注解{@link CoexistenceVersion}的实例,可为空 <br>Annotation for the instance of {@link CoexistenceVersion} can be empty.
     * @param vp 用于存放[版本名称,接口集合]的数据集 <br>Data set for [version name, interface collection ]
     * @return RequestMappingInfo
     */
    RequestMappingInfo InterfaceInjection(RequestMappingInfo mapping, String path, CoexistenceVersion cv, DualMap<String> vp);
    /**
     * 使用{@link InterfacePriority}相关注解时,规定其优先值判断标准<br>When using the {@link InterfacePriority} related annotation, specify a priority criterion
     *
     * @param mp 当前环境的MultiVersion配置实例<br> The MultiVersion configuration instance for the current environment
     * @param a 已被注入的方法的优先值<br> The priority value of the method that has been injected
     * @param b 可能被注入的方法的优先值<br> The priority value of a method that may be injected
     * @return 判断结果
     */
    boolean SortingMethod(MultiVersionProperties mp,Integer a, Integer b);
    /**
     * 获取接口的实际路径<br>Gets the actual path of the interface
     * <p>
     * 路径匹配兼容性说明（Path Matching Compatibility Notice）
     * 由于 Spring Boot 在 2.6 版本之后引入了新的 `PathPatternParser` 路径匹配机制，原始使用 `AntPathMatcher` 或直接字符串比较的方式将无法完全兼容。为了保障组件在多个 Spring Boot 版本下的适配能力，本项目对以下两类路径匹配机制都进行了支持：
     * - ✅ Ant-style 路径匹配（`PatternsRequestCondition`）——兼容旧版本 Spring Boot（2.5 及以下）
     * - ✅ PathPatternParser 路径匹配（`PathPatternsCondition`）——兼容新版 Spring Boot（2.6 及以上）
     * 我们提供了自动探测机制，会优先匹配 `PathPatternParser`，若不可用则回退到 `AntPathMatcher`。因此，无论你使用的 Spring Boot 版本如何，都无需调整配置即可正常使用本组件的动态接口注册功能。
     * 建议：如果你在手动扩展或二次开发本功能，请注意使用适当的路径匹配 API，以保证兼容性。<br>Since Spring Boot introduced `PathPatternParser` in version 2.6, traditional path matching based on `AntPathMatcher` or direct string equality may not work as expected anymore.
     * To ensure cross-version compatibility, this project:
     * - ✅ Supports Ant-style path matching using `PatternsRequestCondition` (for Spring Boot 2.5 and below)
     * - ✅ Supports PathPatternParser-based matching using `PathPatternsCondition` (for Spring Boot 2.6 and above)
     * The component automatically detects the available mechanism and applies the appropriate strategy, ensuring full compatibility **without requiring any changes** from you, regardless of your Spring Boot version.
     * Tip: If you extend or customize the component, make sure to account for both path matchers to maintain compatibility.
     * </p>
     * @param mapping 接口注入过程中正在被注入的方法的RequestMappingInfo<br>RequestMappingInfo of the method being injected during interface injection
     * @return 接口实际路径
     */
    String getMethodPath(RequestMappingInfo mapping);
}
