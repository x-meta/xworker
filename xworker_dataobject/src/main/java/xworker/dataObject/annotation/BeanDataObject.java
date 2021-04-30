package xworker.dataObject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>数据对象的注解，标记一个类为数据对象，对应模型是BeanDataObject。</p>
 *
 * <p>根据需要实现doCreate、doQuery、doUpdate、doDelete和doAction等方法。</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanDataObject {
    public boolean paging() default true;

    public int pageSize() default 200;

    public boolean gridEditable() default  false;

    public boolean autoLoad() default true;

    public boolean autoSave() default  false;

    public String valueName() default  "";

    public String displayName() default  "";

    /**
     * 可以设置额外的属性，这些属性要符合数据对象模型的定义。如editCols=2&amp;group=abc。
     * @return
     */
    public String attributes() default  "";
}
