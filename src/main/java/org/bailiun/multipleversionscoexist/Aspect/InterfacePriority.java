package org.bailiun.multipleversionscoexist.Aspect;

import org.bailiun.multipleversionscoexist.Abstraction.MulRegisterHandlerMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *<P>为路径完全相同的接口赋予优先级,在注册接口时优先级高的接口会覆盖优先级低的接口<br>Priority is given to interfaces with exactly the same path, and interfaces with higher priority will overwrite interfaces with lower priority when registering interfaces</P>
 *<P>你可以通过继承{@link MulRegisterHandlerMethod}中的SortingMethod方法来修改优先值的判断规则</P>
 *
 * <p>Example usage:</p>
 *
 * <pre>{@code
 * @RestController
 * @RequestMapping("/DOME")
 * public class DOME {
 *     @InterfacePriority(2)
 *     @GetMapping("/cee")
 *     public void cee() {
 *         System.out.println("run!");
 *     }
 *     @InterfacePriority(1)
 *     @GetMapping("/bee")
 *     public void bee() {
 *         System.out.println("run!");
 *     }
 * }
 * }在默认情况下,其结果为仅接口cee被注册<br>By default, the result is that only the interface cee is registered</pre>
 *
 * <p>Supported targets:</p>
 * <ul>
 *     <li>Method</li>
 * </ul>
 *
 * @author Bailiun
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InterfacePriority {
    /**
     * @return 优先值<br>Priority value
     */
    int value() default 0;
}
