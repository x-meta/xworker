package xworker.dataObject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public  @interface BeanDataObjectField {
    public String name() default "";

    /**
     * 标签名称，用于界面显示。
     *
     * @return
     */
    public String label() default "";

    /**
     * 表格中的宽度。
     *
     * @return
     */
    public int gridWith() default 120;

    public boolean sortable() default false;

    /**
     * 是否是主键。
     *
     * @return
     */
    public boolean key() default false;

    /**
     * 是否可编辑。
     *
     * @return
     */
    public boolean editable() default true;

    /**
     * 可以设置额外的属性，这些属性要符合数据对象模型的定义。如：optional=true&amp;length=20。
     * @return
     */
    public String attributes() default  "";

    /**
     * 获取属性值得get方法。
     * @return
     */
    public String getter() default "";

    /**
     * 设置属性值得set方法。
     * @return
     */
    public String setter() default "";

}
