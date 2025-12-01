package org.bailiun.multipleversionscoexist.Aspect;

import org.bailiun.multipleversionscoexist.config.SynOpeImplementation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * <p>这个注解需要配和{@link SynchronousOperation}的方法集合父类{@link SynOpeImplementation}来使用,作用是让{@link SynOpeImplementation}内的方法在原方法之后执行,如未进行设定,默认使用AfterOperation注解
 * <br>This annotation needs to be used with {@link SynchronousOperation} method set parent, {@link SynOpeImplementation}, to cause the methods in the {@link SynOpeImplementation} to execute after the original method, or AfterOperati if not specified on Notes</p>
 *
 * <p>Example usage:</p>
 *
 * <pre>{@code
 * public class Dome extends SynOpeImplementation {
 *     @AfterOperation
 *     public String dome() {
 *         return "Hello from version 2";
 *     }
 * }
 * }</pre>
 *
 * <p>Supported targets:</p>
 * <ul>
 *     <li>Method</li>
 * </ul>
 *
 * @author Bailiun
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AfterOperation {
    /**
     * <P>用于单个方法执行多个Dome例子时,决定其运行顺序<br>Used to determine the order in which a single method executes multiple Dome examples</p>
     *
     * @return 优先值<br>Priority value
     */
    int value() default 0;
}
