package xworker.swt.xwidgets;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.UtilData;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtTextUtils;

public class ProcessConsole {

	public static Composite create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//使用新的变量上下文
		ActionContext ac = new ActionContext();
		String style = self.doAction("getStyle", actionContext);
		ac.put("style", style);
		ac.put("parent", actionContext.get("parent"));
		ac.put("thing", self);
		ac.put(ActionContext.PARENT_CONTEXT, actionContext);
		
		//创建控件
		Thing compositeThing = null;
		if("seperate".equals(style)){
			compositeThing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.ProcessConsoleShell/@mainTabFodler/@sepereateConsole/@composite");
		}else{
			compositeThing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.ProcessConsoleShell/@mainTabFodler/@singleConsole/@composite");			
		}		
		Composite composite = null;
		Designer.pushCreator(self);
		try{
			composite = compositeThing.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		
		//创建子节点
		actionContext.peek().put("parent", composite);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		//创建动作容器
		Thing actionsThing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.ProcessConsoleShell/@mainTabFodler/@ActionContainer");
		actionsThing.doAction("create", ac);
		final ActionContainer actions = ac.getObject("actions");
		
		//绑定自动销毁
		if(UtilData.isTrue(self.doAction("isDestroyOnDispose", actionContext))) {
			composite.addListener(SWT.Dispose, new Listener() {
				@Override
				public void handleEvent(Event event) {
					Process process = actions.getActionContext().getObject("process");
					if(process != null) {
						try {
							process.destroy();
						}catch(Exception e) {							
						}
					}
				}
				
			});
		}
		
		//设置进程
		Process process = self.doAction("getProcess", actionContext);
		if(process != null){
			actions.doAction("setProcess", ac, "process", process);
		}
		
		actionContext.g().put(self.getMetadata().getName(), actions);		
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		return composite;
	}
	
	/**
	 * 输出文本框的默认选择事件。
	 * 
	 * @param actionContext
	 * @throws IOException 
	 */
	public static void outTextSelection(ActionContext actionContext) throws IOException{
		Event event = actionContext.getObject("event");
		Text text = (Text) event.widget;
		Object outText = actionContext.get("text");
		OutputStream out = actionContext.getObject("outputStream");
		if(out != null){
			try {
				String str = text.getText();
				if(!"".equals(str)){
					out.write(str.getBytes());
				}
				
				//写入换行
				out.write("\r\n".getBytes());
				out.flush();
			}catch(Exception e) {
				SwtTextUtils.append(outText, ExceptionUtil.toString(e));
				SwtTextUtils.showSelection(outText);
			}
		} else {
			SwtTextUtils.append(outText, "No process\r\n");
			SwtTextUtils.showSelection(outText);
		}
		text.setText("");
	}
}
