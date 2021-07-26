package xworker.lang.thingmenus;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.lang.executor.Executor;
import xworker.util.XWorkerUtils;

import java.io.File;

public class IdeOpen {
    private static final String TAG = IdeOpen.class.getName();

    public static void doAction(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String actionName = self.doAction("getActionName", actionContext);
        String type = self.doAction("getType", actionContext);

        Thing thing = actionContext.getObject("thing");
        Object obj = thing.doAction(actionName, actionContext);

        if(obj instanceof File){
            XWorkerUtils.ideOpenFile((File) obj);
        }else if("thing".equals(type)){
            Thing th = getThing(self, obj);
            if(th != null){
                XWorkerUtils.ideOpenThing(th);
            }
        }else if("composite".equals(type)){
            Thing th = getThing(self, obj);
            if(th != null){
                XWorkerUtils.ideOpenComposite(th);
            }
        }else if("url".equals(type)){
            XWorkerUtils.ideOpenUrl(String.valueOf(obj));
        }else if("webControl".equals(type)){
            Thing th = getThing(self, obj);
            if(th != null){
                XWorkerUtils.ideOpenWebControl(th);
            }
        }else{
            Executor.info(TAG, "IdeOpen not support type, menu=" + self.getMetadata().getPath() + ", obj=" + obj);
        }
    }

    private static Thing getThing(Thing self, Object obj){
        if(obj instanceof Thing){
            return (Thing) obj;
        }else if(obj instanceof String){
            Thing thing =  World.getInstance().getThing((String) obj);
            if(thing != null){
                return thing;
            }
        }

        Executor.info(TAG, "IdeOpen thing is null, menu=" + self.getMetadata().getPath() + ", obj=" + obj);
        return null;
    }
}
