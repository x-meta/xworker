package xworker.workbench;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.util.XWorkerUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class WorkbenchActions {
    public static IWorkbench<?,?,?> getWorkbench(ActionContext actionContext){
        return XWorkerUtils.getWorkbench();
    }

    public static void openThingEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Thing thing = self.doAction("getThing", actionContext);

        XWorkerUtils.ideOpenThing(thing);
    }

    public static Object getThingEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Thing thing = self.doAction("getThing", actionContext);

        return XWorkerUtils.requireNonNull(XWorkerUtils.getWorkbench(), self).getEditor("thing:" + thing.getMetadata().getPath());
    }

    public static void openCompositeEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Thing composite = self.doAction("getComposite", actionContext);

        XWorkerUtils.ideOpenComposite(composite);
    }

    public static Object getCompositeEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Thing composite = self.doAction("getComposite", actionContext);

        return XWorkerUtils.requireNonNull(XWorkerUtils.getWorkbench(), self).getEditor("composite:" + composite.getMetadata().getPath());
    }

    public static void openFileEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        File file = self.doAction("getFile", actionContext);

        XWorkerUtils.ideOpenFile(file);
    }

    public static Object getFileEditor(ActionContext actionContext) throws IOException {
        Thing self = actionContext.getObject("self");
        File file = self.doAction("getFile", actionContext);

        return XWorkerUtils.requireNonNull(XWorkerUtils.getWorkbench(), self).getEditor("file:" + file.getCanonicalPath());
    }

    public static void openUrl(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String url = self.doAction("getUrl", actionContext);

        XWorkerUtils.ideOpenUrl(url);
    }

    public static IEditor<?, ?, ?> getWebBrowserEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        return XWorkerUtils.requireNonNull(XWorkerUtils.getWorkbench(), self).getEditor("WebBrowser");
    }

    public static Object openEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String id = self.doAction("getId", actionContext);
        Thing editor = self.doAction("getEditor", actionContext);
        Map<String, Object> params = self.doAction("getParams", actionContext);

        return XWorkerUtils.requireNonNull(XWorkerUtils.getWorkbench(), self).openEditor(id, editor, params);
    }

    public static Object getEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String id = self.doAction("getId", actionContext);

        return XWorkerUtils.requireNonNull(XWorkerUtils.getWorkbench(), self).getEditor(id);
    }

    public static Boolean isDirty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        return XWorkerUtils.requireNonNull(XWorkerUtils.getWorkbench(), self).isDirty();
    }

    public static void save(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        XWorkerUtils.requireNonNull(XWorkerUtils.getWorkbench(), self).save();
    }
    public static void saveAll(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        XWorkerUtils.requireNonNull(XWorkerUtils.getWorkbench(), self).saveAll();
    }

    public static void exit(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        XWorkerUtils.requireNonNull(XWorkerUtils.getWorkbench(), self).exit();
    }

    public static Object openView(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String id = self.doAction("getId", actionContext);
        Thing view = self.doAction("getView", actionContext);
        Map<String, Object> params = self.doAction("getParams", actionContext);
        String type = self.doAction("getType", actionContext);
        Boolean closeable = self.doAction("isCloseable", actionContext);


        return XWorkerUtils.requireNonNull(XWorkerUtils.getWorkbench(), self).openView(id, view, type, closeable, params);
    }

    public static Object getView(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String id = self.doAction("getId", actionContext);

        return XWorkerUtils.requireNonNull(XWorkerUtils.getWorkbench(), self).getView(id);
    }
}
