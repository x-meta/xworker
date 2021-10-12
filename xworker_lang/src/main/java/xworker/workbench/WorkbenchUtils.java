package xworker.workbench;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.util.XWorkerUtils;

import java.util.*;

public class WorkbenchUtils {
    /**
     * 根据平台和要编辑的内容返回适合的编辑器列表。
     */
    public static List<EditorParams<Object>> getEditors(String platform, Object content, ActionContext actionContext){
        Thing editorIndex = World.getInstance().getThing("xworker.workbench.Editors");
        List<Thing> allEditors = XWorkerUtils.searchRegistThings(editorIndex, "child", Collections.emptyList(), actionContext);
        List<EditorParams<Object>> editors = new ArrayList<>();
        for(Thing editor : allEditors){
            List<String> platforms = editor.doAction("getPlatforms", actionContext);
            if(platforms == null || platforms.size() == 0 || platforms.contains(platform)){
                EditorParams<Object> params = editor.doAction("createParams", actionContext, "content", content);
                if(params != null){
                    editors.add(params);
                }
            }
        }

        Collections.sort(editors);
        return editors;
    }
}
