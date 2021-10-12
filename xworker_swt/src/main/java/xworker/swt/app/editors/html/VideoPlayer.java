package xworker.swt.app.editors.html;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.util.ActionContainer;

import xworker.swt.util.SwtUtils;
import xworker.util.XWorkerUtils;
import xworker.workbench.EditorParams;

@ActionClass(creator="createInstance")
public class VideoPlayer {
    public static final String[] videos = new String[]{".m2v", ".m4e", ".mp2v", ".mp4", ".mpeg", ".mps", ".mpv", ".rv", ".wmv", ".asf",
            ".asx", ".avi", ".ivf", ".m1v", ".movie", ".mpa", ".mpe", ".mpg", ".mpv2", ".wm", ".wmx", ".wvx"};

    public static boolean isVideo(String name){
        if(name == null){
            return false;
        }

        name = name.toLowerCase();
        for(String video : videos){
            if(name.endsWith(video)){
                return true;
            }
        }

        return false;
    }

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
                actionContext.g().put("desc", desc);
                String url = XWorkerUtils.getThingDescUrl(desc);
                browser.setUrl(url);
            }else if(browser != null){
                browser.setText("");
            }
        }
    }

    public void onOutlineCreated(){
        Thing thing = actionContext.getObject("desc");
        Browser browser = actionContext.getObject("browser");
        if(thing != null) {
            SwtUtils.setThingDesc(thing, browser);
        }else{
            if(browser != null && !browser.isDisposed()){
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

    public static EditorParams<Object> createParams(ActionContext actionContext) throws MalformedURLException {
        Thing self = actionContext.getObject("self");
        Object content = actionContext.getObject("content");

        String path = null;
        String src = null;
        Thing videoThing = null;
        if(content instanceof File){
            File file = (File) content;
            String name = file.getName();
            if(isVideo(name)){
                src = file.toURI().toURL().toString();
                path = file.getAbsolutePath();
            }else{
                return null;
            }
        } else if(content instanceof String){
            String url = (String) content;
            if(isVideo(url)){
                src = url;
                path = url;
            }else{
                return null;
            }
        }else if(content instanceof Thing){
            Thing thing = (Thing) content;
            if(thing.isThing("xworker.swt.html.Video")){
                videoThing = thing;
                path = thing.getMetadata().getPath();
            }else{
                return null;
            }
        }else{
            return null;
        }

        final Map<String, Object> params = new HashMap<>();
        if(src != null){
            params.put("src", src);
        }
        if(videoThing != null){
            params.put("videoThing", videoThing);
        }
        return new EditorParams<Object>(self, "Video:" + path, content) {
            @Override
            public Map<String, Object> getParams() {
                return params;
            }
        };
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
