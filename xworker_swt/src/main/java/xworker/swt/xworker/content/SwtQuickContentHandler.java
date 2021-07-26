package xworker.swt.xworker.content;

import ognl.Ognl;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import xworker.content.Content;
import xworker.content.ContentHandler;
import xworker.content.StringContent;
import xworker.content.ThingContent;
import xworker.lang.executor.Executor;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;
import xworker.util.UtilData;
import xworker.util.XWorkerUtils;

import java.util.List;

public class SwtQuickContentHandler implements ContentHandler {
    private static final String TAG = SwtQuickContentHandler.class.getName();

    @Override
    public Object handle(Thing quickContent, Content<?> content, ActionContext actionContext) {
        String type = content.getType();

        World world = World.getInstance();
        if("url".equals(type)){
            return createUrl(quickContent, (StringContent) content, actionContext);
        }else if("thingDesc".equals(type)){
            return createThingDesc(quickContent, (ThingContent) content, actionContext);
        }else if("thingControl".equals(type)){
            return createThingControl(quickContent, (ThingContent) content, actionContext);
        }else if("composite".equals(type)){
            return ((ThingContent) content).getContent().doAction("create", actionContext);
        }else if("uiFlow".equals(type)){
            return createUiFlow(quickContent, (ThingContent) content, actionContext);
        }else if("uiFunction".equals(type)){
            return createUiFunction(quickContent, (ThingContent) content, actionContext);
        }else if("autoDemo".equals(type)){
            return createAutoDemo(quickContent, (ThingContent) content, actionContext);
        }else if("swtDemo".equals(type)){
            Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@DemoSWT");
            return thing.doAction("create", actionContext, "content", content.getContent());
        }else if("webDemo".equals(type)){
            Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@DemoWeb");
            return thing.doAction("create", actionContext, "content", content.getContent());
        }else if("thingDemo".equals(type)){
            Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@DemoThing");
            return thing.doAction("create", actionContext, "content", content.getContent());
        }else if("shell".equals(type)){
            return createShell(quickContent, (ThingContent) content, actionContext);
        }else if("thingEditor".equals(type)){
            Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@thingEditor");
            return thing.doAction("create", actionContext, "content", content.getContent());
        }else if("action".equals(type)){
            return createAction(quickContent, (ThingContent) content, actionContext);
        }else if("groovy".equals(type)){
            return creategroovy(quickContent, (ThingContent) content, actionContext);
        }else if("thingRegist".equals(type)){
            Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@thingRegist");

            return thing.doAction("create", actionContext, "content", content.getContent());
        }else if("dataObjectEditor".equals(type)){
            Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@dataObjectEditor");
            return thing.doAction("create", actionContext,"content", content.getContent());
        }else if("dataObjectSelector".equals(type)){
            Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@dataObjectSelector");
            return thing.doAction("create", actionContext, "content", content.getContent());
        }else if("dataObjectMultiSelector".equals(type)){
            Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@dataObjectMultiSelector");
            return thing.doAction("create", actionContext, "content", content.getContent());
        }else if("code".equals(type)){
            return createCode(quickContent, (StringContent) content, actionContext);
        }else if("swtGuide".equals(type)){
            return createSwtGuide(quickContent, (ThingContent) content, actionContext);
        }else if("html".equals(type)){
            return createHtml(quickContent, (StringContent) content, actionContext);
        }else if("state".equals(type)){
            return createState(quickContent, (ThingContent) content, actionContext);
        }

        return null;
    }

    public static Object createState(Thing self, ThingContent content, ActionContext actionContext){
        World world = World.getInstance();
        Thing thing = world.getThing("xworker.swt.xworker.prototype.StateShell/@stateComposite").detach();
        thing.put("state", content.getContent().getMetadata().getPath());
        thing.put("label", content.getContent().getMetadata().getLabel());

        return thing.doAction("create", actionContext);
    }

    public static Object createHtml(Thing self, StringContent content, ActionContext actionContext){
        World world = World.getInstance();
        //创建浏览器
        Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@simpleBrowser");
        Browser browser = thing.doAction("create", actionContext);

        //打开html
        if(content != null){
            String html = content.getContent();
            if(html != null){
                browser.setText(html);
            }
        }
        return browser;
    }

    public static Object createSwtGuide(Thing self, ThingContent content, ActionContext actionContext){
        Thing thing = new Thing("xworker.swt.guide.GuideComposite");
        thing.put("guide", content.getContent().getMetadata().getPath());

        return thing.doAction("create", actionContext);
    }

    public static Object createCode(Thing self, StringContent content, ActionContext actionContext){
        World world = World.getInstance();
        Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@codeComposite");

        Composite composite = thing.doAction("create", actionContext);

        //代码着色
        String codeType = self.getString("codeType");
        Thing colorer = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@groovyComposite/@mainSashForm/@codeComposite/@codeText/@Colorer").detach();
        colorer.put("codeType", codeType);
        colorer.put("codeName", codeType);
        actionContext.peek().put("parent", actionContext.get("codeText"));
        colorer.doAction("create", actionContext);

        String code =  self.getString("code");
        if(code == null){
            code = "";
        }
        Object codeText = actionContext.getObject("codeText");
        SwtTextUtils.setText(codeText, code);

        Button insertButton = actionContext.getObject("insertButton");
        try{
            Object styledTextForInsert = UtilData.getParentContextValue(actionContext, self.getString("styledTextForInsert"));
            if(styledTextForInsert == null){
                styledTextForInsert = Ognl.getValue(self.getString("styledTextForInsert"), actionContext);
            }
            if(styledTextForInsert != null){
                actionContext.g().put("styledTextForInsert", styledTextForInsert);
            }else{

                insertButton.dispose();
            }
        }catch(Exception e){
            Executor.warn(TAG, "get styledTextForInsert error", e);
            insertButton.dispose();
        }

        return composite;
    }

    public static Object creategroovy(Thing self, ThingContent thingContent, ActionContext actionContext){
        World world = World.getInstance();
        Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@groovyComposite");

        Composite composite = thing.doAction("create", actionContext, "content", self);

        Thing content = thingContent.getContent();
        List<Thing> actions = content.getChilds("GroovyAction");
        if(actions.size() > 0){
            content = actions.get(0);
            actionContext.g().put("content", content);
        }else{
            actionContext.g().put("content", content);
        }

        String code =  content.getString("code");
        if(code == null){
            code = "";
        }
        SwtTextUtils.setText(actionContext.getObject("codeText"), code);

        return composite;
    }

    public static Object createAction(Thing self, ThingContent content, ActionContext actionContext){
        World world = World.getInstance();
        Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@actionTabFolder");

        Composite composite = thing.doAction("create", actionContext, "content", content.getContent());
        Browser browser = actionContext.getObject("browser");

        if(self.getBoolean("useTargetThingDoc")){
            SwtUtils.setThingDesc(content.getContent(), browser);
        }else{
            SwtUtils.setThingDesc(self, browser);
        }

        Text actionText = actionContext.getObject("actionText");
        actionText.setText(content.getContent().getMetadata().getPath());

        Thing thingEditor = actionContext.getObject("thingEditor");
        ActionContainer editorActions = thingEditor.getObject("editorActions");
        editorActions.doAction("setThing", actionContext, "thing", content);

        return composite;
    }

    public static Object createShell(Thing self, ThingContent thingContent, ActionContext actionContext){
        World world = World.getInstance();
        Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@shellComposite");

        Composite composite = thing.doAction("create", actionContext, "content", thingContent.getContent());
        Browser browser = actionContext.getObject("browser");
        SwtUtils.setThingDesc(self, browser);

        return composite;
    }

    public Object createAutoDemo(Thing self, ThingContent content, ActionContext actionContext){
        World world = World.getInstance();
        Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@AutoDemoComposite");
        return thing.doAction("create", actionContext, "content", content.getContent());
    }

    public Object createUiFunction(Thing self, ThingContent content, ActionContext actionContext){
        World world = World.getInstance();
        Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@FunctionRunner");
        return thing.doAction("create", actionContext,"content", content.getContent());
    }

    public Object createUiFlow(Thing self, ThingContent content, ActionContext actionContext){
        World world = World.getInstance();
        Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@UiFlow");
        return thing.doAction("create", actionContext, "content", content.getContent());
    }

    public Object createThingControl(Thing self, ThingContent content, ActionContext actionContext){
        World world = World.getInstance();
        //打开url
        String url = XWorkerUtils.getWebControlUrl(content.getContent());
        return showUrl(content.getContent().getMetadata().getPath(), url, self.getBoolean("simpleBrowser"), actionContext);
    }

    public Object createThingDesc(Thing self, ThingContent content, ActionContext actionContext){
        //打开url
        String url = XWorkerUtils.getThingDescUrl(content.getContent());

        World world = World.getInstance();
        if(self.getBoolean("simpleBrowser")){
            //创建浏览器
            Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@simpleBrowser");
            Browser browser = thing.doAction("create", actionContext);

            //打开url
            //browser.setUrl(url);
            SwtUtils.setThingDesc(content.getContent(), browser);
            //println(composite);
            return browser;
        }else{
            //创建浏览器
            Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@browser");
            Composite composite = thing.doAction("create", actionContext);
            ActionContainer browser = actionContext.getObject("browser");
            browser.doAction("openUrl", actionContext, "name",content.getContent().getMetadata().getPath(), "url", url);

            return composite;
        }
    }

    public Object createUrl(Thing self, StringContent content, ActionContext actionContext){
        return showUrl(self.getMetadata().getPath(), content.getContent(), self.getBoolean("simpleBrowser"), actionContext);
    }

    private static Object showUrl(String name, String url, boolean simple, ActionContext actionContext){
        World world = World.getInstance();
        if(simple){
            //创建浏览器
            Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@simpleBrowser");
            Browser browser = thing.doAction("create", actionContext);

            //打开url
            browser.setUrl(url);
            return browser;
        }else{
            //创建浏览器
            Thing thing = world.getThing("xworker.swt.xworker.ThingRegistThing/@composite/@browser");
            Object composite = thing.doAction("create", actionContext);

            //打开url
            ActionContainer actions = actionContext.getObject("browser");
            actions.doAction("openUrl", actionContext, "name", name, "url", url);
            //println(composite);
            return composite;
        }
    }
}
