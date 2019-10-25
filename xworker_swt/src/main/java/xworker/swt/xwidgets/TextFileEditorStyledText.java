package xworker.swt.xwidgets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import net.sf.colorer.swt.TextColorer;
import ognl.OgnlException;
import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.form.TextEditor;

public class TextFileEditorStyledText {
	public static Object create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//创建主composite
		Thing compositeThing = World.getInstance().getThing("xworker.swt.xwidgets.TextFileEditor/@mainComposite");
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("thing", self);
		
		Composite composite = null;
		Designer.pushCreator(self);
		try{
			composite = (Composite) compositeThing.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		
		//创建子节点
		Bindings bindings = actionContext.push();
		bindings.put("parent", composite);
		try{
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		//动作
		ActionContainer actions = (ActionContainer) ac.get("actions");
		//如果文件存在，保存文件
		Object file = UtilData.getObject(self, "file", actionContext);
		if(file == null){
			file = self.getStringBlankAsNull("file");
		}
		if(file instanceof File){
			actions.doAction("setFile", actionContext, UtilMap.toMap("file", file));
		}else if(file instanceof String){
			File f = new File((String) file);
			if(!f.exists()){
				f = new File(World.getInstance().getPath() + "/" + file);
			}
			if(f.exists() && f.isFile()){
				actions.doAction("setFile", actionContext, UtilMap.toMap("file", f));
			}
		}
		
		//返回值和变量等
		StyledText text = (StyledText) ac.get("codeEditor");
		TextEditor.attach(text);
		
		//Designer.attach(text, self.getMetadata().getPath(), actionContext);
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), actions);
		return composite;
		
	}
	
	public static void codeModifyed(ActionContext actionContext){
		Thing thing = (Thing) actionContext.get("thing");
		
		thing.doAction("modified", actionContext, UtilMap.toMap("text", actionContext.get("codeEditor")));
	}
	
	public static void setFile(ActionContext actionContext) throws IOException{
		File file = (File) actionContext.get("file");
		//ActionContext ac = (ActionContext) actionContext.get("ac");		
		StyledText codeEditor = (StyledText) actionContext.get("codeEditor");
		
		actionContext.getScope(0).put("file", file);

		FileInputStream fin = new FileInputStream(file);
		byte[] bytes = new byte[fin.available()];
		fin.read(bytes);
		fin.close();

		String str = new String(bytes);
		codeEditor.setText(str);
		TextColorer colorer = (TextColorer) codeEditor.getData("textColorer");
		if(colorer != null){
			colorer.chooseFileType(file.getName());
		}
	}
	
	public static void saveFile(ActionContext actionContext) throws IOException{
		File file = (File) actionContext.get("file");
		//ActionContext ac = (ActionContext) actionContext.get("ac");
		StyledText codeEditor = (StyledText) actionContext.get("codeEditor");
		
		FileOutputStream fout = new FileOutputStream(file);
		fout.write(codeEditor.getText().getBytes());
		fout.close();
	}
}
