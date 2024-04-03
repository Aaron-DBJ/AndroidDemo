package com.aaron.diview;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dbj
 * @date 1/24/24
 * @description
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ServiceLoaderInterface {
    /**
     * 实现类的唯一标识
     * @return
     */
    String key() default "";

    /**
     * ServiceLoader加载的接口（也就是spi中的服务）
     * @return
     */
    Class<?> interfaceClass() default Void.class;

    /**
     * 接口全限定名称
     * @return
     */
    String interfaceName() default "";
}
