package xworker.swt.xwidgets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;

public class JavaSourceViewer {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
				
		Thing compositeThing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.JavaSourceViewerShell/@mainComposite");
		ActionContext ac = new ActionContext();
		ac.put("parentContext", actionContext);
		ac.put("parent", actionContext.get("parent"));
		Composite compoiste = null;
		Designer.pushCreator(self);
		try{
			compoiste = compositeThing.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		
		//创建子节点
		actionContext.peek().put("parent", compoiste);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
				
		//演示的动作集
		ActionContainer actions = (ActionContainer) ac.get("actions");
		String className = self.doAction("getClassName", actionContext);
		actions.doAction("setClassName", ac, "className", className);
		
		Designer.attachCreator((Control) compoiste, self.getMetadata().getPath(), actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), actions);
		return compoiste;
	}
	
	/**
	 * actions的方法的实现。
	 * 
	 * @param actionContext
	 * @throws IOException 
	 */
	public static void setClassName(ActionContext actionContext) throws Exception {
		StackLayout stackLayout = actionContext.getObject("mainCompositeStackLayout");
		Composite mainComposite = actionContext.getObject("mainComposite");
		CodeViewer codeViewer = actionContext.getObject("codeViewer");
		Composite noSourceComposite = actionContext.getObject("noSourceComposite");
		Label classLabel = actionContext.getObject("classLabel");
		
		String className = actionContext.getObject("className");
		actionContext.g().put("className", className);
		if(className == null) {
			classLabel.setText("null");
			stackLayout.topControl = noSourceComposite;
			mainComposite.layout();
			return;
		}else {
			classLabel.setText(className);			
		}
		
		String javaFile  = className.replace('.', '/') + ".java";
		if(!javaFile.startsWith("/")) {
			javaFile = "/" + javaFile;
		}
		
		String code = null;
		InputStream fin = null;
		try {
			fin = World.getInstance().getResourceAsStream(javaFile);
			if(fin != null) {
				code =  readInputStream(fin);				
			}
		}catch(Exception e) {
		}finally {
			if(fin != null) {
				fin.close();
			}
		}
		
		if(code == null) {
			Thing paths = World.getInstance().getThing("_local.xworker.config.JavaSourcePaths");
			if(paths != null) {
				for(Thing pathThing : paths.getChilds()) {
					String path = pathThing.getStringBlankAsNull("path");
					if(path != null) {
						
						try {
							URL url = new URL(path + javaFile);
							byte[] bytes = IOUtils.toByteArray(url);
							if(bytes != null) {
								code = new String(bytes);
								break;
							}
						}catch(Exception e) {
							//e.printStackTrace();
						}
					}
				}
			}
		}
		
		if(code != null) {
			codeViewer.setCode("java", "java", code);
			//SwtTextUtils.setText(codeText, code);
			//codeText.setText(code);
			stackLayout.topControl = codeViewer.getControl();
		}else {
			stackLayout.topControl = noSourceComposite;			
		}
		
		mainComposite.layout();
		
		//设置ClassViewer，用来显示方和属性列表
		if(actionContext.get("classViewer") != null) {
			ActionContainer ac = actionContext.getObject("classViewer");
			ac.doAction("setClass", actionContext, "cls", className);
		}
	}	
	
	private static String readInputStream(InputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] bytes = new byte[4096];
		int length = -1;
		while((length = in.read(bytes)) > 0) {
			bout.write(bytes, 0, length);
		}
		
		return new String(bout.toByteArray());
	}
}
