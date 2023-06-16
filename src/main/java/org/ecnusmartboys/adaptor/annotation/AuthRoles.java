package org.ecnusmartboys.adaptor.annotation;

import java.lang.annotation.*;

/**
 * 用于controller的方法上，表示一个接口指定角色才能访问。
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRoles {

    String[] value() default {};
}
