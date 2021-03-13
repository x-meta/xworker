package xworker.javafx.beans.property;

import javafx.beans.property.adapter.*;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class JavaBeanAdapter {
    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Object javaBean = self.doAction("getJavaBean", actionContext);
        Class<?> javaBeanClass = self.doAction("getJavaBeanClass", actionContext);

        actionContext.peek().put("bean", javaBean);
        actionContext.peek().put("beanClass", javaBeanClass);

        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createBooleanProperty(ActionContext actionContext) throws NoSuchMethodException {
        Thing self = actionContext.getObject("self");
        Object javaBean = actionContext.get("bean");
        Class<?> javaBeanClass = actionContext.getObject("beanClass");
        String name = self.getMetadata().getName();
        Boolean readOnly = self.doAction("isReadOnly", actionContext);
        String getter = self.doAction("getGetter", actionContext);
        String setter = self.doAction("getSetter", actionContext);

        Object property = null;
        if(readOnly){
            ReadOnlyJavaBeanBooleanPropertyBuilder builder = ReadOnlyJavaBeanBooleanPropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(name);
            }
            property = builder.build();
        }else{
            JavaBeanBooleanPropertyBuilder builder = JavaBeanBooleanPropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(getter);
            }
            if(setter != null){
                builder.setter(setter);
            }
            property = builder.build();
        }
        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createDoubleProperty(ActionContext actionContext) throws NoSuchMethodException {
        Thing self = actionContext.getObject("self");
        Object javaBean = actionContext.get("bean");
        Class<?> javaBeanClass = actionContext.getObject("beanClass");
        String name = self.getMetadata().getName();
        Boolean readOnly = self.doAction("isReadOnly", actionContext);
        String getter = self.doAction("getGetter", actionContext);
        String setter = self.doAction("getSetter", actionContext);

        Object property = null;
        if(readOnly){
            ReadOnlyJavaBeanDoublePropertyBuilder builder = ReadOnlyJavaBeanDoublePropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(name);
            }
            property = builder.build();
        }else{
            JavaBeanDoublePropertyBuilder builder = JavaBeanDoublePropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(getter);
            }
            if(setter != null){
                builder.setter(setter);
            }
            property = builder.build();
        }
        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createFloatProperty(ActionContext actionContext) throws NoSuchMethodException {
        Thing self = actionContext.getObject("self");
        Object javaBean = actionContext.get("bean");
        Class<?> javaBeanClass = actionContext.getObject("beanClass");
        String name = self.getMetadata().getName();
        Boolean readOnly = self.doAction("isReadOnly", actionContext);
        String getter = self.doAction("getGetter", actionContext);
        String setter = self.doAction("getSetter", actionContext);

        Object property = null;
        if(readOnly){
            ReadOnlyJavaBeanFloatPropertyBuilder builder = ReadOnlyJavaBeanFloatPropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(name);
            }
            property = builder.build();
        }else{
            JavaBeanFloatPropertyBuilder builder = JavaBeanFloatPropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(getter);
            }
            if(setter != null){
                builder.setter(setter);
            }
            property = builder.build();
        }
        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createIntegerProperty(ActionContext actionContext) throws NoSuchMethodException {
        Thing self = actionContext.getObject("self");
        Object javaBean = actionContext.get("bean");
        Class<?> javaBeanClass = actionContext.getObject("beanClass");
        String name = self.getMetadata().getName();
        Boolean readOnly = self.doAction("isReadOnly", actionContext);
        String getter = self.doAction("getGetter", actionContext);
        String setter = self.doAction("getSetter", actionContext);

        Object property = null;
        if(readOnly){
            ReadOnlyJavaBeanIntegerPropertyBuilder builder = ReadOnlyJavaBeanIntegerPropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(name);
            }
            property = builder.build();
        }else{
            JavaBeanIntegerPropertyBuilder builder = JavaBeanIntegerPropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(getter);
            }
            if(setter != null){
                builder.setter(setter);
            }
            property = builder.build();
        }
        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createLongProperty(ActionContext actionContext) throws NoSuchMethodException {
        Thing self = actionContext.getObject("self");
        Object javaBean = actionContext.get("bean");
        Class<?> javaBeanClass = actionContext.getObject("beanClass");
        String name = self.getMetadata().getName();
        Boolean readOnly = self.doAction("isReadOnly", actionContext);
        String getter = self.doAction("getGetter", actionContext);
        String setter = self.doAction("getSetter", actionContext);

        Object property = null;
        if(readOnly){
            ReadOnlyJavaBeanLongPropertyBuilder builder = ReadOnlyJavaBeanLongPropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(name);
            }
            property = builder.build();
        }else{
            JavaBeanLongPropertyBuilder builder = JavaBeanLongPropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(getter);
            }
            if(setter != null){
                builder.setter(setter);
            }
            property = builder.build();
        }
        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createObjectProperty(ActionContext actionContext) throws NoSuchMethodException {
        Thing self = actionContext.getObject("self");
        Object javaBean = actionContext.get("bean");
        Class<?> javaBeanClass = actionContext.getObject("beanClass");
        String name = self.getMetadata().getName();
        Boolean readOnly = self.doAction("isReadOnly", actionContext);
        String getter = self.doAction("getGetter", actionContext);
        String setter = self.doAction("getSetter", actionContext);

        Object property = null;
        if(readOnly){
            ReadOnlyJavaBeanObjectPropertyBuilder builder = ReadOnlyJavaBeanObjectPropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(name);
            }
            property = builder.build();
        }else{
            JavaBeanObjectPropertyBuilder builder = JavaBeanObjectPropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(getter);
            }
            if(setter != null){
                builder.setter(setter);
            }
            property = builder.build();
        }
        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createStringProperty(ActionContext actionContext) throws NoSuchMethodException {
        Thing self = actionContext.getObject("self");
        Object javaBean = actionContext.get("bean");
        Class<?> javaBeanClass = actionContext.getObject("beanClass");
        String name = self.getMetadata().getName();
        Boolean readOnly = self.doAction("isReadOnly", actionContext);
        String getter = self.doAction("getGetter", actionContext);
        String setter = self.doAction("getSetter", actionContext);

        Object property = null;
        if(readOnly){
            ReadOnlyJavaBeanStringPropertyBuilder builder = ReadOnlyJavaBeanStringPropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(name);
            }
            property = builder.build();
        }else{
            JavaBeanStringPropertyBuilder builder = JavaBeanStringPropertyBuilder.create();
            if(javaBean != null){
                builder.bean(javaBean);
            }else if(javaBeanClass != null){
                builder.beanClass(javaBeanClass);
            }

            builder.name(name);
            if(getter != null){
                builder.getter(getter);
            }
            if(setter != null){
                builder.setter(setter);
            }
            property = builder.build();
        }
        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }
}
