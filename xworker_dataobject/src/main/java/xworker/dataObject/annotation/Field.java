package xworker.dataObject.annotation;

import org.xmeta.annotation.ActionField;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public  @interface Field {
    public String name() default "";

    public String label() default "";

    public int gridWith() default 120;

    public boolean sortable() default false;
}
