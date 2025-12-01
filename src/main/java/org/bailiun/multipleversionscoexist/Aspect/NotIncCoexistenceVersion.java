package org.bailiun.multipleversionscoexist.Aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *<P>让某个方法不受{@link CoexistenceVersion}的影响<br>Leave a method unaffected by {@link CoexistenceVersion}</P>
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
 *     @NotIncCoexistenceVersion
 *     @GetMapping("/bee")
 *     public void bee() {
 *         System.out.println("run!");
 *     }
 * }
 * }其结果为接口bee会被当做普通接口来进行注册,不会被分类到1.0.0的版本<br>As a result, cee will be registered as a normal interface and will not be classified as version 1.0.0</pre>
 *
 * <p>Supported targets:</p>
 * <ul>
 *     <li>Method</li>
 * </ul>
 *
 * @author Bailiun
 * @since 1.0.0
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotIncCoexistenceVersion {
}
