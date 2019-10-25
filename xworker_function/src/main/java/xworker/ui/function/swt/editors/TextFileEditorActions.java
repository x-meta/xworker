package xworker.ui.function.swt.editors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.swt.custom.StyledText;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.swt.form.TextEditor;
import xworker.ui.UIRequest;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;

public class TextFileEditorActions {
	public static void init(ActionContext actionContext) throws IOException{
		//参数
		UIRequest request = (UIRequest) actionContext.get("request");		
		FunctionRequest fnRequest = (FunctionRequest) request.getRequestMessage();
		FunctionParameter fileParam = fnRequest.getParameter("file");
		File file = (File) fileParam.getValue();

		FunctionParameter charsetParam = fnRequest.getParameter("charset");
		String charset = (String) charsetParam.getValue();
		
		//代码着色器
		Thing colorer = new Thing("xworker.swt.custom.StyledText/@Colorer");
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".");
		if(index != -1){
			String ext = fileName.substring(index + 1, fileName.length());
			colorer.put("codeName", ext);
			colorer.put("codeType", ext);
		}else{
			colorer.put("codeName", "txt");
			colorer.put("codeType", "txt");
		}
		
		StyledText editor = (StyledText) actionContext.get("editor");
		TextEditor.attach(editor);
		Bindings bindings = actionContext.push();
		try{
			bindings.put("parent", editor);
			colorer.doAction("create", actionContext);
		}finally{
			actionContext.pop();
		}
		
		FileInputStream fin = new FileInputStream(file);
		try{
			byte[] bytes = new byte[fin.available()];
			fin.read(bytes);
			if(charset != null){
				editor.setText(new String(bytes, charset));
			}else{
				editor.setText(new String(bytes));
			}
		}finally{
			if(fin != null){
				fin.close();
			}
		}
	}
	
	public static void save(ActionContext actionContext) throws IOException{
		//参数
		UIRequest request = (UIRequest) actionContext.get("request");		
		FunctionRequest fnRequest = (FunctionRequest) request.getRequestMessage();
		FunctionParameter fileParam = fnRequest.getParameter("file");
		File file = (File) fileParam.getValue();

		FunctionParameter charsetParam = fnRequest.getParameter("charset");
		String charset = (String) charsetParam.getValue();
				
		StyledText editor = (StyledText) actionContext.get("editor");
		byte[] bytes = charset != null ? editor.getText().getBytes(charset) : editor.getText().getBytes();
		
		FileOutputStream fout = new FileOutputStream(file);
		try{
			fout.write(bytes);
		}finally{
			if(fout != null){
				fout.close();
			}
		}
	}
}
