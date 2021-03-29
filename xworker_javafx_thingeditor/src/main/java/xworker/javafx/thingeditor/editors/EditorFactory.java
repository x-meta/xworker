package xworker.javafx.thingeditor.editors;

import org.xmeta.Thing;
import org.xmeta.ThingIndex;
import org.xmeta.World;
import xworker.javafx.thing.form.attributeeditors.HtmlEditor;
import xworker.javafx.thingeditor.Editor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EditorFactory {
    static Map<String, String> types = new HashMap<>();
    static{
        types.put("jpg", "image");
        types.put("bmp", "image");
        types.put("gif", "image");
        types.put("jpeg", "image");
        types.put("png", "image");
        types.put("acp", "audio");
        types.put("mid", "auido");
        types.put("mp3", "auido");
        types.put("wav", "audio");
        types.put("wma", "audio");
        types.put("m3u", "audio");
        types.put("midi", "audio");
        types.put("mp2", "audio");
        types.put("mp4", "video");
        types.put("wmv", "video");
        types.put("avi", "video");
        types.put("mpg", "video");
        types.put("wmx", "video");
        types.put("mkv", "video");
        types.put("ass", "text");
        types.put("asp", "text");
        types.put("cml", "text");
        types.put("dcd", "text");
        types.put("html", "html");
        types.put("htm", "html");
        types.put("java", "text");
        types.put("xml", "text");
        types.put("c", "text");
        types.put("cpp", "text");
        types.put("h", "text");
        types.put("txt", "text");
        types.put("rtf", "text");
        types.put("dtd", "text");
        types.put("js", "text");
        types.put("css", "text");
        }

    public static Editor<?> createEditor(Object content){
        if(content instanceof Thing){
            SThingEditor thingEditor = new SThingEditor();
            Thing thing = (Thing) content;
            thingEditor.setContent(thing);
            return thingEditor;
        } else if(content instanceof ThingIndex){
            SThingEditor thingEditor = new SThingEditor();
            Thing thing = World.getInstance().getThing(((ThingIndex)content).path);
            thingEditor.setContent(thing);
            return thingEditor;
        }else if(content instanceof File){
            File file = (File) content;
            String name = file.getName();
            int index = name.lastIndexOf(".");
            if(index != -1){
                String ext = name.substring(index + 1, name.length());
                String type = types.get(ext);
                if("text".equals(type)){
                    TextFileEditor editor = new TextFileEditor();
                    editor.setContent(file);
                    return editor;
                }else if("video".equals(type)){
                    MediaEditor editor = new MediaEditor();
                    editor.setContent(file);
                    return editor;
                }else if("html".equals(type)){
                    SHTMLEditor editor = new SHTMLEditor();
                    editor.setContent(file);
                    return editor;
                }
            }
        }

        return null;
    }
}
