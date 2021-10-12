package xworker.net.jsch.swt;

import com.jcraft.jsch.Session;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionField;
import org.xmeta.annotation.ActionParams;
import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;
import xworker.swt.util.SwtUtils;
import xworker.workbench.EditorParams;

import java.util.HashMap;
import java.util.Map;

public class JCTermActionContainer {
    private static String TAG = JCTermActionContainer.class.getName();
    @ActionField
    public ActionContext actionContext;
    @ActionField
    public xworker.net.jsch.JCTerm jcTerm;
    @ActionField
    public xworker.swt.app.Workbench workbench;

    Object session = null;
    String title = "No Session";

    @ActionParams(names = "params")
    public void setContent(Map<String, Object> params){
        session = params.get("session");

        if(session != this.session) {
            //先停止之前的
            jcTerm.stop();
        }

        try {
            if(workbench != null && workbench.getLogService() != null){
                Executor.push(workbench.getLogService());
            }

            if (session instanceof Thing) {
                title = ((Thing) session).getMetadata().getLabel();
                jcTerm.start((Thing) session);
            }else if(session instanceof DataObject){
                DataObject dataObject = (DataObject) session;
                title = dataObject.getString("name");
                if(title == null){
                    title = "Server: " + dataObject.getString("adminIP");
                }

                Thing sessionThing = (Thing) ((DataObject) session).doAction("getSessionThing", actionContext);
                if(sessionThing != null){
                    jcTerm.start(sessionThing);
                }else{
                    Executor.warn(TAG, "Can not get session from dataObject " + session);
                }
            }else if(session instanceof Session){
                title = ((Session) session).getHost();
                jcTerm.start((Session) session, false);
            }else{
                Executor.info(TAG, "Can not start JCTerm, unknown session type, session=" + session);
            }

            jcTerm.getMainComposite().getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    jcTerm.resize();
                }
            });

        }catch(Exception e){
            Executor.warn(TAG, "Start session error", e);
        }finally {
            if(workbench != null && workbench.getLogService() != null){
                Executor.pop();
            }
        }
    }

    @ActionParams(names = "params")
    public boolean isSameContent(Map<String, Object> params){
        Object session = params.get("session");
        return this.session == session;
    }

    public void doSave(){

    }

    public boolean isDirty(){
        return false;
    }

    public String getSimpleTitle(){
        return title;
    }

    public String getTitle(){
        return title;
    }

    public Image getIcon(){
        return SwtUtils.createImage(jcTerm.getMainComposite(), "icons/application_osx_terminal.png", actionContext);
    }

    public Composite getOutline(){
        return null;
    }

    public void doDispose(){
        jcTerm.stop();
        jcTerm.getMainComposite().dispose();
    }

    public void onActive(){

    }

    public void onUnActive(){

    }

    public static EditorParams<Object> createParams(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object content = actionContext.getObject("content");
        Object session = null;
        String path = null;
        if(content instanceof Thing){
            Thing thing = (Thing) content;
            if(thing.isThing("xworker.net.jsch.Session")){
                session = thing;
                path = "jcterm:" + path;
            }
        }else if(content instanceof  DataObject){
            DataObject dataObject = (DataObject) content;
            if(dataObject.getMetadata().getDescriptor().isThing("xworker.app.server.dataobjects.Server")){
                session = dataObject;
                path = "jcterm:" + dataObject.getString("id");
            }
        }else if(content instanceof Session){
            session = content;
            path = "jcterm:" + session;
        }
        if(session != null){
            return new EditorParams<Object>(self, path, session) {
                @Override
                public Map<String, Object> getParams() {
                    Map<String, Object> params = new HashMap<>();
                    params.put("session", this.getContent());

                    return params;
                }
            };
        }

        return null;
    }
}
