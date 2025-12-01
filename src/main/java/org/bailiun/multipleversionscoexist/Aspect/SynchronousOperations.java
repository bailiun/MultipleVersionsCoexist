package org.bailiun.multipleversionscoexist.Aspect;

import org.bailiun.multipleversionscoexist.config.SynOpeImplementation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *<P>在方法或者类上进行标记,使其可以同步执行操作,若同时标注在方法和类上面,方法的优先级大于类的优先级,可支持多个{@link SynchronousOperation}<br>To mark a method or class so that it can perform operations synchronously. If it is marked on both, the method has higher priority than the class</P>
 *<P>注:此注解的使用必须对{@link SynOpeImplementation}类进行继承,详情请参照{@link SynOpeImplementation}类注释<br>Note: The use of this annotation must be inherited from the {@link SynOpeImplementation} class, see the {@link SynOpeImplementation} class annotation for details</P>
 *
 * <p>Example usage:</p>
 *
 * <pre>{@code
 * @RestController
 * @RequestMapping("/DOME")
 * public class DOME {
 *     @SynchronousOperation("hello")
 *     @GetMapping("/cee")
 *     public void cee() {
 *         System.out.println("run!");
 *     }
 * }
 * public class DOME2 extends SynOpeImplementation {
 *     public void hello(Object[] args, Object result, Throwable throwable) {
 *         System.out.println("hello!");
 *     }
 * }
 * }默认情况下在接口cee执行之后,会执行DOME2类中的hello方法<br>By default, the hello method of the DOME2 class is executed after the cee interface is executed</pre>
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
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SynchronousOperations {
    SynchronousOperation[] value();
}