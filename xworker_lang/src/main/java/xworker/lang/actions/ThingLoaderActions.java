package xworker.lang.actions;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionAnnotationHelper;
import org.xmeta.util.ThingLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ThingLoaderActions {
    public static Object executeObjectMethod(ActionContext actionContext) throws Exception {
        Thing self = actionContext.getObject("self");

        Object object = ThingLoader.getObject();
        if(object == null){
            throw new ActionException("Object from ThingLoader is null, path=" + self.getMetadata().getPath());
        }

        String methodName = self.doAction("getMethod", actionContext);
        Class<?> cls = object.getClass();
        Method method = null;
        ActionAnnotationHelper helper = null;
        for(Method method_ : cls.getDeclaredMethods()){
            if(method_.getName().equals(methodName)){
                helper = ActionAnnotationHelper.parse(cls, method_);
                break;
            }
        }

        if(helper != null){
            return helper.invoke(object, actionContext);
        }else{
            throw new ActionException("Method " + methodName + " not found, path=" + self.getMetadata().getPath());
        }
    }

    //xworker.lang.actions.ThingLoader/@actions/@run
    public static Object executeThingLoader(ActionContext actionContext) throws Exception{
        Thing self = actionContext.getObject("self");
        Object object = self.doAction("getObject", actionContext);
        Boolean init = self.doAction("isInit", actionContext);

        if(object == null) {
            Class<?> clss = self.doAction("getClass", actionContext);
            if(clss != null){
                object = clss.getConstructor(new Class<?>[]{}).newInstance(new Object[]{});
            }
        }

        if(object == null){
            throw new ActionException("Can not execute thingloader, object is null, path=" + self.getMetadata().getPath());
        }

        ThingLoader.init(object, init, actionContext);
        ThingLoader.push(object);
        try{
            Object result = null;
            for(Thing actions : self.getChilds("ChildActions")){
                for(Thing actionThing : actions.getChilds()){
                   result =  actionThing.getAction().run(actionContext);
                }
            }

            return result;
        }finally {
            ThingLoader.pop();
        }
    }
}
