package xworker.swt.actions.prototypes;

import java.io.File;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class CreateDirectoryDialog {
	public static void okButtonSelection(ActionContext actionContext) {
		Action showMessage = actionContext.getObject("showMessage");
		File rootDir = actionContext.getObject("rootDir");
		Text text = actionContext.getObject("text");
		Bindings _g = actionContext.g();
		ActionContext parentContext = actionContext.getObject("parentContext");
		Thing thing = actionContext.getObject("thing");
		Shell shell = actionContext.getObject("shell");
		
		if(actionContext.get("rootDir") == null){
		    showMessage.run(actionContext, "message", "rootDir不能为null");
		    return;
		}

		String name = text.getText().trim();
		if("" == name){
		    showMessage.run(actionContext, "message", "文件夹名不能为空！");
		    return;
		}

		File file = new File(rootDir, name);
		if(file.exists()){
		    showMessage.run(actionContext, "message", "文件夹" + file.getPath() + "已存在!");
		    return;
		}

		try{
		    if(file.mkdirs()){
		        _g.put("result", file);
		        
		        thing.doAction("ok", parentContext, "file", file);
		        shell.dispose();
		    }else{
		        showMessage.run(actionContext, "message", "创建文件夹" + file.getPath() + "失败");
		        return;
		    }
		}catch(Exception e){
		    showMessage.run(actionContext, "message", "创建文件夹" + file.getPath() + "失败，" + e.getMessage());
		    return;
		}
	}
}
