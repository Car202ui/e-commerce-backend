package com.proyecto.backend.e_commerce.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringCtx  implements ApplicationContextAware {
    private static ApplicationContext CTX;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CTX = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        return CTX.getBean(type);
    }
}
