package org.ecnusmartboys.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 判断当前开发环境工具类。
 */
@Component
public class EnvironmentUtil implements ApplicationContextAware {

    private static String[] profiles;

    public static boolean contains(String profile) {
        return ArrayUtils.contains(profiles, profile);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        profiles = applicationContext.getEnvironment().getActiveProfiles();
    }
}
