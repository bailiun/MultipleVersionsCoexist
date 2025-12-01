package org.bailiun.multipleversionscoexist.config;

import org.springframework.stereotype.Component;

/**
 * SynOpeImplementation的默认实现类,只是为了防止程序报错<br>The default implementation class of SynOpeImplementation, just to prevent program errors
 */

@Component
public class SynOpeImplementationInitialization extends SynOpeImplementation {
    public void SynOpeImplementationInitializationMethod(Object[] args, Object result, Throwable throwable) {
        System.out.println("SynOpeImplementation启动");
    }

}