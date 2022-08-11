package io.github.betterigo.job.client.common.core.annotation;

import java.lang.annotation.*;

@Target(value= {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JobRequireBean {
	public String value() default "";
}
