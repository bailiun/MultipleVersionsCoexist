package org.bailiun.multipleversionscoexist.Aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *<P>指定被标记类里的所有方法为某个版本的特有功能,或指定某个方法为某个版本的特有功能<br>Specify that all methods in the marked class are specific to a version, or that a method is specific to a version</P>
 *
 *
 * <p>Example usage:</p>
 *
 * <pre>{@code
 * @RestController
 * @RequestMapping("/DOME")
 * @CoexistenceVersion(version = "1.0.0")
 * public class DOME {
 *     @GetMapping("/cee")
 *     public void cee() {
 *         System.out.println("run!");
 *     }
 *     @CoexistenceVersion(version = "2.0.0")
 *     @GetMapping("/bee")
 *     public void bee() {
 *         System.out.println("run!");
 *     }
 * }
 * }其结果为接口cee被注册到版本1.0.0,bee被注册到版本2.0.0<br>The result is that cee is registered to version 1.0.0 and bee is registered to version 2.0.0</pre>
 *
 * <p>Supported targets:</p>
 * <ul>
 *     <li>Method</li>
 *     <li>Class</li>
 * </ul>
 *
 * @author Bailiun
 * @since 1.0.0
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CoexistenceVersion {
    /**
     * @return 版本名称,建议英文或数字<br>Version name, recommended in English or numerical
     */
    String version() default "";
}
