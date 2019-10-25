package xworker.ui.function.swt.editors;

import java.io.IOException;

import org.eclipse.swt.custom.StyledText;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.swt.form.TextEditor;
import xworker.ui.UIRequest;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;

public class TextEditorActions {
	public static void init(ActionContext actionContext) throws IOException{
		//参数
		UIRequest request = (UIRequest) actionContext.get("request");		
		FunctionRequest fnRequest = (FunctionRequest) request.getRequestMessage();
		FunctionParameter textParam = fnRequest.getParameter("text");
		String text = (String) textParam.getValue();

		FunctionParameter codeTypeParam = fnRequest.getParameter("codeType");
		String codeType = (String) codeTypeParam.getValue();
		
		//代码着色器
		Thing colorer = new Thing("xworker.swt.custom.StyledText/@Colorer");
		colorer.put("codeName", codeType);
		colorer.put("codeType", codeType);
		
		StyledText editor = (StyledText) actionContext.get("editor");
		TextEditor.attach(editor);
		Bindings bindings = actionContext.push();
		try{
			bindings.put("parent", editor);
			colorer.doAction("create", actionContext);
		}finally{
			actionContext.pop();
		}
		
		if(text != null){
			editor.setText(text);
		}
	}
}
