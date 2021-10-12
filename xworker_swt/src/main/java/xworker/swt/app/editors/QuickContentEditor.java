package xworker.swt.app.editors;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.annotation.ActionField;
import xworker.content.Content;
import xworker.content.QuickContent;
import xworker.swt.app.SwtEditor;
import xworker.swt.xworker.content.SwtQuickContentHandler;
import xworker.util.XWorkerUtils;
import xworker.workbench.EditorParams;

import java.util.HashMap;
import java.util.Map;

public class QuickContentEditor implements SwtEditor {
    @ActionField
    public org.eclipse.swt.widgets.Composite OutlineComposite;
    @ActionField
    public org.xmeta.ActionContext actionContext;
    @ActionField
    public org.eclipse.swt.browser.Browser browser;
    @ActionField
    public org.eclipse.swt.widgets.Composite contentComposite;
    @ActionField
    public xworker.swt.app.IEditorContainer editorContianer;
    @ActionField
    public xworker.swt.app.Workbench workbench;
    Thing content;
    SwtQuickContentHandler contentHandler = new SwtQuickContentHandler();

    public QuickContentEditor(){
    }

    @Override
    public void setContent() {
        Map<String, Object> params = actionContext.getObject("params");
        Thing content = (Thing) params.get("thing");

        if(browser != null && contentHandler.getBrowser() == null){
            contentHandler.setBrowser(browser);
        }

        //先销毁之前的
        for(Control control : contentComposite.getChildren()){
            control.dispose();
        }

        Content<?> quickContent = QuickContent.getContent(content, actionContext);
        if(quickContent != null) {
            actionContext.peek().put("parent", contentComposite);
            contentHandler.handle(content, quickContent, actionContext);
        }

        if(!(content.getBoolean("hideOutline") || "thingDesc".equals(content.getString("type")))) {
            contentHandler.setThingDesc(content);
        }else{
            contentHandler.setText("");
        }

        this.content = content;
    }

    @Override
    public boolean isSameContent() {
        Map<String, Object> params = actionContext.getObject("params");
        Thing content = (Thing) params.get("thing");
        if(content == null){
            return false;
        }

        return this.content != null && this.content.getMetadata().getPath().equals(content.getMetadata().getPath());
    }

    @Override
    public void doSave() {

    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public String getSimpleTitle() {
        if(content != null) {
            return content.getMetadata().getLabel();
        }else{
            return "QuickContent-No content";
        }
    }

    @Override
    public String getTitle() {
        if(content != null) {
            ThingManager thingManager = content.getMetadata().getThingManager();
            return thingManager.getName() + "/" + content.getMetadata().getPath();
        }else{
            return "QuickContent-No content";
        }
    }

    @Override
    public void doDispose() {

    }

    @Override
    public void onActive() {

    }

    @Override
    public void onUnActive() {

    }

    @Override
    public String getIcon() {
        if(content != null) {
            return XWorkerUtils.getThingIcon(content);
        }else{
            return null;
        }
    }

    @Override
    public Object getOutline() {
        return OutlineComposite;
    }

    @Override
    public void onOutlineCreated() {
        if(browser != null && contentHandler.getBrowser() == null){
            contentHandler.setBrowser(browser);
        }

        if(!(content.getBoolean("hideOutline") || "thingDesc".equals(content.getString("type")))) {
            contentHandler.setThingDesc(content);
        }else{
            contentHandler.setText("");
        }

    }

    public static EditorParams<Object> createParams(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object content = actionContext.getObject("content");
        Thing thing = null;
        if (content instanceof String) {
            thing = World.getInstance().getThing((String) content);
        }else  if(content instanceof Thing){
            thing = (Thing) content;
        }
        if(thing != null && (thing.isThing("xworker.content.QuickContent"))){
            return new EditorParams<Object>(self, "quickcontent:" + thing.getMetadata().getPath(), thing) {
                @Override
                public Map<String, Object> getParams() {
                    Map<String, Object> params = new HashMap<>();
                    params.put("thing", this.getContent());

                    return params;
                }
            };
        }

        return null;
    }
}
