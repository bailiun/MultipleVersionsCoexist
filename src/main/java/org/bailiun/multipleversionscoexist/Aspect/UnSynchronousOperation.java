package org.bailiun.multipleversionscoexist.Aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *<P>让某个方法不受{@link SynchronousOperation}的影响<br>Leave a method unaffected by {@link SynchronousOperation}</P>
 *
 *
 * <p>Example usage:</p>
 *
 * <pre>{@code
 * @RestController
 * @SynchronousOperation("SOM")
 * @RequestMapping("/DOME")
 * public class DOME {
 *     @GetMapping("/cee")
 *     public void cee() {
 *         System.out.println("run!");
 *     }
 *     @UnSynchronousOperation
 *     @GetMapping("/bee")
 *     public void bee() {
 *         System.out.println("run!");
 *     }
 * }
 * public class DOME2 extends SynOpeImplementation {
 *     public void SOM(Object[] args, Object result, Throwable throwable) {
 *         System.out.println("hello!");
 *     }
 * }
 * }其结果为接口cee在执行完后会运行"SOM"方法,而接口bee不会<br>The result is that interface cee runs the "SOM" method when it is finished, but interface bee does not</pre>
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
public @interface UnSynchronousOperation {
}
