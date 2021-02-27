package xworker.swt.app.editors;

import java.io.IOException;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.util.ActionContainer;

import xworker.util.StringUtils;
import xworker.util.XWorkerUtils;

@ActionClass(creator = "createInstance")
public class WebBrowserEditor {
	public void setContent() {
		// xworker.swt.app.editors.WebBrowserEditor/@actions/@setContent
		Map<String, Object> params = actionContext.getObject("params");

		// 判断参数是否存在
		if (params == null) {
			return;
		}

		// url
		Object url = params.get("url");
		if ("thing".equals(params.get("type"))) {
			url = XWorkerUtils.getThingDescUrl((Thing) url);
		} else if ("control".equals(params.get("type"))) {
			Object thing = url;
			if (thing instanceof String) {
				thing = world.getThing((String) thing);
			}
			url = XWorkerUtils.getWebControlUrl((Thing) thing);
		}

		String name = (String) params.get("name");
		if (name == null || "".equals(name)) {
			name = "new";
		}
		actionContext.g().put("params", params);

		if (url != null && !"".equals(url)) {
			Composite EditorComposite = actionContext.getObject("EditorComposite");
			StackLayout stackLayout = actionContext.getObject("stackLayout");

			if (!"true".equals(params.get("simple")) || params.get("simple") == null) {
				ActionContainer webBrowser = actionContext.getObject("webBrowser");
				webBrowser.doAction("openUrl", actionContext, "name", name, "url", url);
				stackLayout.topControl = webBrowser.getActionContext().getObject("mainComposite");
				EditorComposite.getLayout();
			} else {
				Browser browser = actionContext.getObject("browser");
				browser.setUrl((String) url);
				stackLayout.topControl = browser;
				EditorComposite.getLayout();
			}
		}
	}

	public Object isSameContent() {
		// xworker.swt.app.editors.WebBrowserEditor/@actions/@isSameContent
		return false;
	}

	public void doDispose() {
		// xworker.swt.app.editors.WebBrowserEditor/@actions/@doDispose
		Composite editorComposite = actionContext.getObject("editorComposite");
		if (editorComposite.isDisposed() == false) {
			editorComposite.dispose();
		}
	}

	public void doSave() {
		// xworker.swt.app.editors.WebBrowserEditor/@actions/@doSave
		// if(actionContext.get("editorActions") != null){
		// editorActions.doAction("doSave", actionContext);
		// }
	}

	public Object isDirty() {
		// xworker.swt.app.editors.WebBrowserEditor/@actions/@isDirty
		// if(actionContext.get("editorActions") != null){
		// return editorActions.doAction("isDirty", actionContext);
		// }

		return false;
	}

	public Object getOutline() {
		// xworker.swt.app.editors.WebBrowserEditor/@actions/@isDirty1
		// if(actionContext.get("editorActions") != null){
		// editorActions.doAction("getOutline", actionContext);
		// }

		return null;
	}

	public Object getSimpleTitle() throws IOException{
	    //xworker.swt.app.editors.WebBrowserEditor/@actions/@getSimpleTitle
		Map<String, Object> params = actionContext.getObject("params");
	    String title = (String) params.get("title");
	    if(title == null || title == ""){
	        title = "lang:d=浏览器&en=Web Browser";
	    }
	    
	    return StringUtils.getString(title, actionContext);
	}

	public static WebBrowserEditor createInstance(ActionContext actionContext) {
		// return new MyClass();
		String key = WebBrowserEditor.class.getName();
		WebBrowserEditor obj = actionContext.getObject(key);
		if (obj == null) {
			obj = new WebBrowserEditor();
			actionContext.g().put(key, obj);
		}

		return obj;
	}

	@ActionField
	public ActionContext actionContext;

	public World world = World.getInstance();
}
