package xworker.swt.actions.prototypes;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class CreateFileDialog {
	public static void okButtonSelection(ActionContext actionContext) {
		Action showMessage = actionContext.getObject("showMessage");
		File rootDir = actionContext.getObject("rootDir");
		Text text = actionContext.getObject("text");
		Shell shell = actionContext.getObject("shell");
		Thing thing = actionContext.getObject("thing");
		ActionContext parentContext = actionContext.getObject("parentContext");
		Bindings _g = actionContext.g();
		
		if(actionContext.get("rootDir") == null){
		    showMessage.run(actionContext, "message", "rootDir不能为null");
		    return;
		}
		
		String name = text.getText().trim();
		if("" == name){
		    showMessage.run(actionContext, "message", "文件名不能为空！");
		    return;
		}
		
		File file = new File(rootDir, name);
		if(file.exists()){
		    showMessage.run(actionContext, "message", "文件" + file.getPath() + "已存在!");
		    return;
		}
		
		try{
		    if(file.getParentFile().exists() == false){
		         file.getParentFile().mkdirs();
		    }
		    
		    FileOutputStream fout = new FileOutputStream(file);
		    fout.close();
		    _g.put("result", file);
		    
		    //触发事件
		    thing.doAction("ok", parentContext, "file", file);
		    
		    //关闭窗口
		    shell.dispose();
		}catch(Exception e){
		    showMessage.run(actionContext, "message", "创建文件" + file.getPath() + "失败，" + e.getMessage());
		    return;
		}
	}	
	
	public static void cancelButtonSelection(ActionContext actionContext) {
		Shell shell = actionContext.getObject("shell");
		Thing thing = actionContext.getObject("thing");
		ActionContext parentContext = actionContext.getObject("parentContext");
		
		if(actionContext.get("thing") != null){
		    thing.doAction("cancel", parentContext);
		}

		shell.dispose();
	}
}