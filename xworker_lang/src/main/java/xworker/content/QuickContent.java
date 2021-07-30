package xworker.content;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.lang.executor.Executor;

public class QuickContent {
    private final static String TAG = QuickContent.class.getName();

    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Content<?> content = getContent(self, actionContext);
        actionContext.g().put("thingSelf", self);
        ContentHandler contentHandler = actionContext.getObject("contentHandler");
        if(contentHandler != null && content != null){
            return contentHandler.handle(self, content, actionContext);
        }else{
            Executor.warn(TAG, "Content or contentHanlder is null, can not handle.");
            return null;
        }
    }

    public static Content<?> getContent(Thing self, ActionContext actionContext){
        String type = self.getString("type");
        String[] strings = new String[]{"url"};
        String[] selfs = new String[]{"groovy", "code", "html"};
        String[] regists = new String[]{"thingRegist"};
        String[] nullself = new String[]{"thingDesc"};

        if(isType(type, strings)){
            //String类型
            return new StringContent(type, self.doAction("getString", actionContext));
        }else if(isType(type, regists)){
            String content = self.doAction("getString", actionContext);
            if(content == null){
                Executor.warn(TAG, "Content is null, path=" + self.getMetadata().getPath());
                return null;
            }

            String[] ps = content.split("[|]");
            if(ps.length < 2){
                return null;
            }
            Thing registThing = World.getInstance().getThing(ps[1]);
            ThingRegistContent registTContent = new ThingRegistContent(type, registThing);
            registTContent.setRegistType(ps[0]);
            if(registThing == null){
                Executor.warn(TAG, "RegistThing is null, path=" + self.getMetadata().getPath());
                return null;
            }
            if(ps.length >= 3){
                registTContent.setDescriptor(ps[2]);
                if(ps.length >= 4){
                    registTContent.setContentDefaultOpenMethod(ps[3]);
                }
                if(ps.length >= 5){
                    registTContent.setContentDisplayType(ps[4]);
                }
            }else{
                if("xworker.lang.util.ThingIndex".equals(registThing.getDescriptor().getMetadata().getPath() )){
                    //如果事物是ThingIndex，使用ThingIndex自身的设置
                    registTContent.setDescriptor(registThing.getString("descritporForNewThing"));
                    registTContent.setContentDefaultOpenMethod(registThing.getString("contentDefaultOpenMethod"));
                    registTContent.setContentDisplayType(registThing.getString("contentDisplayType"));
                }else{
                    registTContent.setContentDefaultOpenMethod("thingDesc");
                    registTContent.setContentDisplayType("Composite");
                }
            }
            return registTContent;
        }else {
            //其它内容都是Thing
            Thing content;
            if(isType(type, selfs)){
                content = self;
            }else {
                content = self.doAction("getThing", actionContext);
            }
            if(content == null){
                if(isType(type, nullself)){
                    content = self;
                }else if("composite".equals(type) || "swtDemo".equals(type)){
                    content = self.getThing("Composite@0");
                }else if("uiFlow".equals(type)){
                    content = self.getThing("UiFlow@0");
                }else if("thingControl".equals(type)){
                    content = self.getThing("SimpleControl@0");
                }else if("shell".equals(type)){
                    content = self.getThing("Shell@0");
                }else if("swtGuide".equals(type)){
                    content = self.getThing("SwtGuide@0");
                }else if("action".equals(type)){
                    content = self.getThing("ActionThing@0");
                }else if("state".equals(type)){
                    content = self.getThing("State@0");
                }else if(self.getChilds().size() > 0){
                    content = self.getChilds().get(0);
                }
            }

            if(content  == null){
                Executor.warn(TAG, "Content is null, path=" + self.getMetadata().getPath());
                return null;
            }

            return new ThingContent(type, content);
        }
    }

    private static boolean isType(String type, String[] types){
        for(String t : types){
            if(t.equals(type)){
                return true;
            }
        }

        return false;
    }
}
