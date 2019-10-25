package xworker.ai.chatterbean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import bitoflife.chatterbean.AliceBot;
import xworker.ai.wordsegment.jcseg.JcsegActions;
import xworker.swt.util.TextProxy;
import xworker.util.XWorkerUtils;

/**
 * AliceSWT控件的相关方法。
 * 
 * @author zyx
 *
 */
public class AliceSWT {
	public static Object create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		ActionContext ac = new ActionContext();
		ac.put("parentContext", actionContext);
		ac.put("parent", actionContext.get("parent"));
		
		//Alice的配置
		Thing aliceThing = (Thing) self.doAction("getAliceThing", actionContext);
		ac.put("aliceThing", aliceThing);
		
		//创建面板
		Thing compositeThing = World.getInstance().getThing("xworker.swt.xwidgets.ai.AlicePrototype/@aliceBotComposite");
		Object composite = compositeThing.doAction("create", ac);
		
		//创建子节点
		actionContext.peek().put("parent", composite);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		//保存变量
		actionContext.getScope(0).put(self.getMetadata().getName(), ac.get("actions"));
		
		return composite;
		
	}
	
	public static void send(ActionContext actionContext){
		//从配置中获取
		Thing alice = actionContext.getObject("aliceThing");
		AliceBot aliceBot = (AliceBot) alice.doAction("getAliceBot", actionContext);

		String request = actionContext.getObject("message");
		Browser browser = actionContext.getObject("browser");
		
		//显示Alice说的话
		AliceUtils.appendHtml(browser, "contentBody", request, false);

		if(request != "" && request.trim() != ""){
		    String res = aliceBot.respond(request);
		    if(res != null && res.trim() != ""){        
		        AliceUtils.appendHtml(browser, "contentBody", res, true);
		    }
		}
	}
	
	public static void clear(ActionContext actionContext){
		Browser browser = actionContext.getObject("browser");
		browser.execute("$(\"#contentBody\").html(\"\")");
	}
	
	public static void keyEvent(ActionContext actionContext){
		Event event = actionContext.getObject("event");

		if(event.keyCode == SWT.CR && event.stateMask == SWT.CTRL){
			sendButtonSelection(actionContext);
		    event.doit = false;
		}
	}
	
	public static void sendButtonSelection(ActionContext actionContext){
		//从配置中获取
		Thing alice = actionContext.getObject("aliceThing");
		AliceBot aliceBot = (AliceBot) alice.doAction("getAliceBot", actionContext);

		TextProxy aliceBotInputText = new TextProxy(actionContext.getObject("aliceBotInputText"));
		Browser browser = actionContext.getObject("browser");
		
		//显示Alice说的话
		String request = aliceBotInputText.getText().trim();
		AliceUtils.appendHtml(browser, "contentBody", request, false);

		if(request != "" && request.trim() != ""){
		    String res = aliceBot.respond(request);
		    if(res != null && res.trim() != ""){        
		        AliceUtils.appendHtml(browser, "contentBody", res, true);
		    }
		    
		    aliceBotInputText.setText("");
		}
	}
	
	public static void splitChar(ActionContext actionContext){
		TextProxy aliceBotInputText = new TextProxy(actionContext.getObject("aliceBotInputText"));
		Browser browser = actionContext.getObject("browser");
		
		//显示Alice说的话
		String request = aliceBotInputText.getText().trim();
		//AliceUtils.appendHtml(browser, "contentBody", request, false);

		if(request != "" && request.trim() != ""){
			String[] chars = JcsegActions.getSegment(request);
			String str = "";
			for(int i=0; i<chars.length; i++){
				if(i != 0){
					str = str + " ";
				}
				str = str + chars[i];
			}
			//str = str;
			AliceUtils.appendHtml(browser, "contentBody", str, true);
		}
		
	}
	public static void reset(ActionContext actionContext){
		Thing aliceThing = actionContext.getObject("aliceThing");
		aliceThing.doAction("reset", actionContext);
	}
	
	public static void init(ActionContext actionContext){
		Browser browser = actionContext.getObject("browser");
		World world = World.getInstance();
		Thing control = world.getThing("xworker.ai.chatterbean.AliceHttpView");
		browser.setUrl(XWorkerUtils.getWebControlUrl(control));
	}
}
