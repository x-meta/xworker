package xworker.swt.html;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.actions.ActionContainer;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;
import xworker.util.XWorkerUtils;

public class Video {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
				
		//创建控件		
		ThingCompositeCreator creator = new ThingCompositeCreator(self, actionContext);
		String prototypePath = "xworker.swt.html.prototypes.VideoShell/@videoComposite";
		creator.setCompositeThing(World.getInstance().getThing(prototypePath));		
		Composite composite = null;
		int style = SWT.NONE;
		if(self.getBoolean("BORDER")) {
			style = style | SWT.BORDER;
		}
		
		try {
			SwtUtils.pushInitStyle();
			SwtUtils.setInitStyle(prototypePath, style);
			composite = (Composite) creator.create();
		}finally {
			SwtUtils.popInitStyle();
		}
				
		ActionContext ac = creator.getNewActionContext();
		ac.put("thing", self);
		ActionContainer actions = ac.getObject("actions");
		
		//初始化视频
		actions.doAction("setVideo", ac);
		
		//保存变量
		actionContext.g().put(self.getMetadata().getName(), actions);
		return composite;
	}
	
	/**
	 * 打开一个视频，其中src是参数，其他都是创建时保存的变量。
	 * 
	 * @param actionContext
	 */
	public static void setVideo(ActionContext actionContext) {
		String src = actionContext.getObject("src");
		Thing thing = actionContext.getObject("thing");
 		Browser browser = actionContext.getObject("browser");
		String url = XWorkerUtils.getWebUrl() + "do?sc=xworker.swt.html.prototypes.VideoWeb";
		url = url + "&thing=" + thing.getMetadata().getPath();
		if(src != null && !"".equals(src)) {
			url = url + "&src=" + src;
		}
		
		browser.setUrl(url);
	}
}
