package org.bailiun.multipleversionscoexist.config;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * <h2>同步操作基础实现类 / Base Class for Synchronous Operation Implementation</h2>
 *
 * <p><b>中文说明：</b><br>
 * 此抽象类用于定义统一的同步操作实现逻辑。
 * 所有继承该类的实现类可通过反射机制执行其内部指定的方法，
 * 例如在执行主逻辑前后调用日志、通知或自定义拦截逻辑。</p>
 *
 * <p><b>English Description:</b><br>
 * This abstract base class provides a unified structure for synchronous operation implementations.
 * Subclasses can use reflection to invoke specific methods within themselves, typically for
 * actions like logging, notifications, or custom pre/post execution hooks.</p>
 *
 * <p><b>执行方法参数要求 / Method Signature Requirement:</b><br>
 * {@code Object[] args, Object result, Throwable throwable}</p>
 *
 * <ul>
 *     <li><b>args：</b> 原方法的输入参数 / Original method arguments</li>
 *     <li><b>result：</b> 方法最终返回值 / The final result of the method</li>
 *     <li><b>throwable：</b> 方法抛出的异常（若有） / The exception thrown by the method (if any)</li>
 * </ul>
 *
 * <p><b>使用场景 / Use Case:</b><br>
 * 用于框架中的同步任务控制模块，例如：</p>
 * <ul>
 *     <li>接口执行前后钩子</li>
 *     <li>日志收集器</li>
 *     <li>方法级别同步控制器</li>
 * </ul>
 * / Used in synchronous operation management modules such as:
 * <ul>
 *     <li>Pre/Post API hooks</li>
 *     <li>Logging or auditing components</li>
 *     <li>Custom method-level synchronizers</li>
 * </ul>
 *
 * @author Bailiun
 * @since 1.0.0
 */
public abstract class SynOpeImplementation {


    /**
     * 判断当前实现类中是否存在指定的方法。
     * <br>Checks whether a method with the given name exists in the current implementation class.
     *
     * @param methodName 方法名称 / The name of the method to check
     * @return 如果存在则返回 true，否则返回 false / {@code true} if the method exists, otherwise {@code false}
     */
    public boolean hasMethod(String methodName) {
        return Arrays.stream(this.getClass().getMethods())
                .anyMatch(m -> m.getName().equals(methodName));
    }

    /**
     * 使用反射执行当前类中的指定方法。
     * <br>Executes a specified method within the current class using Java reflection.
     *
     * <p><b>中文说明：</b><br>
     * 该方法通过方法名和参数动态定位并调用目标方法，可用于通用型同步逻辑执行。
     * 若方法不存在，则抛出 {@link NoSuchMethodException} 异常。</p>
     *
     * <p><b>English Description:</b><br>
     * This method dynamically locates and invokes a target method by its name and arguments.
     * It is commonly used for generic synchronization logic execution.
     * If the target method is not found, a {@link NoSuchMethodException} will be thrown.</p>
     *
     * @param methodName 要执行的方法名 / The name of the method to invoke
     * @param args       传入的参数 / The arguments to be passed to the method
     * @return 执行结果对象 / The result of the method execution
     * @throws Exception 当方法未找到或调用失败时抛出 / Thrown if the method cannot be found or invoked
     */
    public Object execute(String methodName, Object... args) throws Exception {
        Method method = findMethod(methodName, args);
        if (method == null) {
            throw new NoSuchMethodException("Method " + methodName + " not found in " + this.getClass().getName());
        }
        return method.invoke(this, args);
    }

    /**
     * 查找指定名称且参数数量匹配的方法。
     * <br>Finds a method by its name with the same parameter count as the provided arguments.
     *
     * <p><b>中文说明：</b><br>
     * 本方法仅根据方法名和参数数量进行匹配，不严格校验参数类型。</p>
     *
     * <p><b>English Description:</b><br>
     * This method performs a relaxed search — it matches by name and parameter count only,
     * without strict type checking.</p>
     *
     * @param methodName 方法名称 / The name of the target method
     * @param args       方法参数 / The arguments used for parameter count comparison
     * @return 匹配到的方法实例或 {@code null} / The matching {@link Method} instance, or {@code null} if not found
     */
    private Method findMethod(String methodName, Object... args) {
        return Arrays.stream(this.getClass().getMethods())
                .filter(m -> m.getName().equals(methodName) && m.getParameterCount() == args.length)
                .findFirst()
                .orElse(null);
    }
}
