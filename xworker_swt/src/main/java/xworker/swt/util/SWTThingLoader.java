package xworker.swt.util;

import javafx.fxml.FXML;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionField;
import xworker.lang.executor.Executor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class SWTThingLoader {
    private static final String TAG = SWTThingLoader.class.getName();
    private static ThreadLocal<Stack<Object>> objectLocal = new ThreadLocal<>();

    /**
     * 执行thing的create(actionContext)方法，执行时如果是Event等可能会吧object的相应方法绑定到evnet上，
     * 最后会把对象的@ActionField等注解的字段赋值。
     *
     * @param object
     * @param thing
     * @param actionContext
     */
    public static <T> T load(Object object, Thing thing, ActionContext actionContext){
        Stack<Object> stack = objectLocal.get();
        if(stack == null) {
            stack = new Stack<>();
            objectLocal.set(stack);
        }

        stack.push(object);
        T result = null;
        try{
            result = thing.doAction("create", actionContext);

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

                    setFieldValue(object, field, vname, actionContext);
                }
            }
        }finally {
            stack.pop();
        }

        return result;
    }

    private static void setFieldValue(Object object, Field field, String valueName, ActionContext actionContext){
        if(valueName == null || "".equals(valueName)){
            valueName = field.getName();
        }

        boolean accessChanged = false;
        try {
            if(!field.isAccessible()) {
                field.setAccessible(true);
                accessChanged = true;
            }
            field.set(object, actionContext.get(valueName));
        } catch (IllegalAccessException e) {
            Executor.warn(TAG, "Set field value exception, field=" + field.getName() + ", valueName=" + valueName, e);
        } finally {
            if(accessChanged) {
                field.setAccessible(false);
            }
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
