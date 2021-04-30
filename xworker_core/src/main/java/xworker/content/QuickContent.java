package xworker.content;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.lang.executor.Executor;

public class QuickContent {
    private final static String TAG = QuickContent.class.getName();

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
            Thing registThing = World.getInstance().getThing(ps[0]);
            ThingRegistContent registTContent = new ThingRegistContent(type, registThing);
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
