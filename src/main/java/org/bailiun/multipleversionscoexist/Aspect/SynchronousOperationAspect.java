package org.bailiun.multipleversionscoexist.Aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.bailiun.multipleversionscoexist.config.RetryPolicy;
import org.bailiun.multipleversionscoexist.config.SynOpeImplementation;
import org.bailiun.multipleversionscoexist.config.SynchronousOperationAsyncRetryExecutor;
import org.bailiun.multipleversionscoexist.en.ExecutionMode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

//这个类是SynchronousOperation注解的实现类
@Aspect
@Component
public class SynchronousOperationAspect {
    @Resource
    private List<SynOpeImplementation> implementations;

    SynchronousOperationAsyncRetryExecutor are;
    public void setImplementations(List<SynOpeImplementation> implementations,
                                   ExecutorService executor,
                                   RetryPolicy retryPolicy) {
        this.implementations = implementations;
        this.are = new SynchronousOperationAsyncRetryExecutor(executor, retryPolicy);
    }

    @Pointcut(value = "@within(single) || @annotation(single)", argNames = "single")
    public void synchronousOperationSingle(SynchronousOperation single) {}
    @Pointcut(value = "@annotation(multiple) || @within(multiple)", argNames = "multiple")
    public void synchronousOperationMultiple(SynchronousOperations multiple) {}
    @Around(value = "synchronousOperationSingle(single)", argNames = "joinPoint,single")
    public Object logAllMethodsSingle(ProceedingJoinPoint joinPoint, SynchronousOperation single) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Class<?> clazz = method.getDeclaringClass();
        // 1. 优先读取方法注解，如无则读取类注解
        SynchronousOperation ops = method.getAnnotation(SynchronousOperation.class);
        if (ops == null) {
            ops = clazz.getAnnotation(SynchronousOperation.class);
        }
        // 没有注解？照常执行
        if (ops== null) {
            return joinPoint.proceed();
        }

        // 3. BEFORE 操作
        BeforeOperation bo = method.getAnnotation(BeforeOperation.class);
        if (bo != null) {
            runMethod(ops.value(), joinPoint.getArgs(), null, null,ops.mode());
            return joinPoint.proceed();
        }

        // 4. 原方法执行
        Object result = null;
        Throwable throwable = null;

        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            throwable = t;
        }

        // 5. AFTER 操作
        runMethod(ops.value(), joinPoint.getArgs(), result, throwable,ops.mode());

        if (throwable != null) throw throwable;
        return result;
    }
    @Around(value = "synchronousOperationMultiple(multiple)", argNames = "joinPoint,multiple")
    public Object logAllMethodsMultiple(ProceedingJoinPoint joinPoint,SynchronousOperations multiple) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Class<?> clazz = method.getDeclaringClass();
        // 1. 优先读取方法注解，如无则读取类注解
        SynchronousOperations ops = method.getAnnotation(SynchronousOperations.class);
        if (ops == null) {
            ops = clazz.getAnnotation(SynchronousOperations.class);
        }
        // 没有注解？照常执行
        if (ops == null) {
            return joinPoint.proceed();
        }
        // 3. BEFORE 操作
        BeforeOperation bo = method.getAnnotation(BeforeOperation.class);
        if (bo != null) {
            for (SynchronousOperation op : ops.value()) {
                runMethod(op.value(), joinPoint.getArgs(), null, null,op.mode());
            }
            return joinPoint.proceed();
        }
        // 4. 原方法执行
        Object result = null;
        Throwable throwable = null;

        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            throwable = t;
        }

        // 5. AFTER 操作
        for (SynchronousOperation op : ops.value()) {
            runMethod(op.value(), joinPoint.getArgs(), result, throwable,op.mode());
        }
        if (throwable != null) throw throwable;
        return result;
    }

    /** 提取多个注解（兼容单个和多个） */
    private List<SynchronousOperation> collectOperations(Annotation[] annotations) {
        List<SynchronousOperation> list = new ArrayList<>();

        for (Annotation ann : annotations) {
            if (ann instanceof SynchronousOperation op) {
                list.add(op);
            }
            if (ann instanceof SynchronousOperations container) {
                list.addAll(Arrays.asList(container.value()));
            }
        }
        return list;
    }

    /** 请保留原来的逻辑结构，不改变 SynOpeImplementation 的用法 */
    private void runMethod(String methodName, Object[] args, Object result, Throwable throwable) {
        boolean found = false;
        for (SynOpeImplementation impl : implementations) {
            Method method = Arrays.stream(impl.getClass().getMethods())
                    .filter(m -> m.getName().equals(methodName))
                    .findFirst()
                    .orElse(null);

            if (method != null) {
                try {
                    // 强制传递三个参数
                    method.invoke(impl, args, result, throwable);
                } catch (Exception ex) {
                    System.err.println("Failed to execute sync method: " + ex.getMessage());
                }
                found = true;
                // 不 break，这样多个实现类的方法都会执行
            }
        }
        if (!found) {
            System.out.println("不存在此方法: " + methodName);
        }
    }


    /** 请保留原来的逻辑结构，不改变 SynOpeImplementation 的用法 */
    private void runMethod(String methodName, Object[] args, Object result, Throwable throwable, ExecutionMode mode) throws InvocationTargetException, IllegalAccessException {
        for (SynOpeImplementation impl : implementations) {
            Method method = Arrays.stream(impl.getClass().getMethods())
                    .filter(m -> m.getName().equals(methodName) && m.getParameterCount() == 3)
                    .findFirst()
                    .orElse(null);

            if (method == null) {
                System.err.println("未找到方法"+methodName+"请检查方法名称是否正确或参数是否为:"+methodName+"(Object[] args, Object result, Throwable throwable)");
                System.err.println(
                        "Method '" + methodName + "' was not found. Please verify that the method name is correct " +
                                "and that it has the expected signature: " + methodName + "(Object[] args, Object result, Throwable throwable)"
                );
                continue;};

            if (mode == ExecutionMode.SYNC) {
                method.invoke(impl, args, result, throwable);
            }

            if (mode == ExecutionMode.ASYNC) {
                are.submitWithRetry(
                        () -> { method.invoke(impl, args, result, throwable); return null; },
                        ex -> System.err.println("异步失败: " + ex.getMessage()),
                        () -> System.out.println("异步成功: " + methodName)
                );
            }
        }
    }

}

