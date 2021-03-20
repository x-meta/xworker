package xworker.javafx.util;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionField;
import xworker.lang.executor.Executor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class FXThingLoader {
    private static final String TAG = FXThingLoader.class.getName();
    private static ThreadLocal<Stack<Object>> objectLocal = new ThreadLocal<>();

    /**
     * 执行thing的create(actionContext)方法，执行时如果是EventHandler等可能会吧object的相应方法绑定到handler上，
     * 最后会把对象的@FXML和@ActionField等注解的字段赋值。
     *
     * @param object
     * @param thing
     * @param actionContext
     */
    public static void load(Object object, Thing thing, ActionContext actionContext){
        Stack<Object> stack = objectLocal.get();
        if(stack == null) {
            stack = new Stack<>();
            objectLocal.set(stack);
        }

        stack.push(object);
        try{
            thing.doAction("create", actionContext);

            //查找字段的注解
            java.util.List<Field> allFieldList = new ArrayList<Field>() ;
            Class<?> tempClass = object.getClass();
            while (tempClass != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
                allFieldList.addAll(Arrays.asList(tempClass .getDeclaredFields()));
                tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
            }

            for(Field field : allFieldList) {
                ActionField actionField = field.getAnnotation(ActionField.class);
                if(actionField != null) {
                    String name = field.getName();
                    String vname = actionField.name();
                    if("".equals(vname)) {
                        vname = name;
                    }

                    boolean accessChanged = false;
                    try {
                        if(!field.isAccessible()) {
                            field.setAccessible(true);
                            accessChanged = true;
                        }
                        field.set(object, actionContext.get(vname));
                    } catch (IllegalAccessException e) {
                        Executor.warn(TAG, "Set field value exception, field=" + name + ", valueName=" + vname, e);
                    } finally {
                        if(accessChanged) {
                            field.setAccessible(false);
                        }
                    }
                }
            }
        }finally {
            stack.pop();
        }
    }

    public static Object getObject(){
        Stack<Object> stack = objectLocal.get();
        if(stack != null && stack.size() > 0){
            return stack.peek();
        }

        return null;
    }
}
