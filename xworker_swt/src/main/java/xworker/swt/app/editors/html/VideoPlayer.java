package xworker.swt.app.editors.html;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.util.ActionContainer;

import xworker.util.XWorkerUtils;

@ActionClass(creator="createInstance")
public class VideoPlayer {
	public void setContent(){
        //xworker.swt.app.editors.html.VideoPlayer/@actions/@setContent
        Map<String, Object> params = actionContext.getObject("params");
        
        //设置参数
        if(params.get("videoThing") != null){
            Object videoThing = params.get("videoThing");
            if(videoThing instanceof String){
                videoThing = world.getThing((String) videoThing);
            }
            if(videoThing != null){
                actionContext.g().put("videoThing", videoThing);
                
                video.doAction("setVideoThing", actionContext, "videoThing", videoThing);
            }
        }
        
        if(params.get("src") != null){
            actionContext.g().put("src", params.get("src"));
            
            video.doAction("setVideo", actionContext, "src", params.get("src"));
        }
        
        if(params.get("title") != null){
            actionContext.g().put("title", params.get("title"));
        }
        
        if(actionContext.get("browser") != null){
        	Browser browser = actionContext.getObject("browser");
            if(params.get("desc") != null && browser != null){
                Thing desc = (Thing) params.get("desc");
                String url = XWorkerUtils.getThingDescUrl(desc);
                browser.setUrl(url);
            }else {
                browser.setText("");
            }
        }
    }
    
    public Object getSimpleTitle(){
        //xworker.swt.app.editors.html.VideoPlayer/@actions/@getSimpleTitle
        if(actionContext.get("title") != null){
            return actionContext.get("title");
        }
        
        if(actionContext.get("src") != null){
        	String src = actionContext.getObject("src");
            File file = new File(src);
            return file.getName();
        }
        
        if(actionContext.get("videoThing") != null){
        	Thing videoThing = actionContext.getObject("videoThing");
            return videoThing.getMetadata().getLabel();
        }
        
        return "Video";
    }
    
    public Object getTitle(){
        //xworker.swt.app.editors.html.VideoPlayer/@actions/@getTitle
        if(actionContext.get("title") != null){
            return actionContext.getObject("title");
        }
        
        if(actionContext.get("src") != null){
            return actionContext.getObject("src");
        }
        
        if(actionContext.get("videoThing") != null){
        	Thing videoThing = actionContext.getObject("videoThing");
            return videoThing.getMetadata().getPath();
        }
        
        return "Video";
    }
    

    public static VideoPlayer createInstance(ActionContext actionContext){
        //return new MyClass();    
        String key = VideoPlayer.class.getName();
        VideoPlayer obj = actionContext.getObject(key);
        if(obj == null){
            obj = new VideoPlayer();
            actionContext.g().put(key, obj);
        }
        
        return obj;
    }    
        
    @ActionField
    public ActionContext actionContext;
    
    @ActionField
    public ActionContainer video;
    
    public World world = World.getInstance();
}
