package xworker.lang.executor;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ThingLoader;
import xworker.swt.design.Designer;

/**
 * SWT版本的只处理请求的界面。
 */
public class SwtRequestService {
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Designer.pushCreator(self);
        Composite composite = null;
        try{
            DefaultRequestService defaultRequestService = new DefaultRequestService();
            SwtDefaultRequestService swtDefaultRequestService = new SwtDefaultRequestService(defaultRequestService);
            actionContext.g().put(self.getMetadata().getName(), defaultRequestService);
            Thing prototype = World.getInstance().getThing("xworker.lang.executor.swt.DefaultRequestService/@mainComposite1");

            composite = ThingLoader.load(swtDefaultRequestService, prototype, actionContext);
            swtDefaultRequestService.init();
        }finally {
            Designer.popCreator();
        }

        actionContext.peek().put("parent", composite);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return composite;
    }
}
