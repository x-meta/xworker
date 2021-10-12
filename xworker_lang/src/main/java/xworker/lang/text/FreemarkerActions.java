package xworker.lang.text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.UtilTemplate;

public class FreemarkerActions {
	public static Object run(ActionContext actionContext) throws Throwable{
		Thing self = actionContext.getObject("self");
		
		//模板
		String tempaltePath = self.doAction("getTemplatePath", actionContext);
		if(tempaltePath == null || "".equals(tempaltePath.trim())){
			tempaltePath = getCodePath(self, actionContext);
		}
		String templateEncoding = self.doAction("getTemplateEncoding", actionContext);
		String outputEncoding = self.doAction("getOutputEncoding", actionContext);
		Object out = self.doAction("getOutput", actionContext);
		OutputStream output = null;
		boolean close = false;
		if(out instanceof String){
			output = new FileOutputStream((String) out);
			close = true;
		}else if(out instanceof File){
			output = new FileOutputStream((File) out);
			close = true;
		}else {
			output = (OutputStream) self.doAction("getOutput", actionContext);
		}

		try {
			if (output == null) {
				return UtilTemplate.process(actionContext, tempaltePath, "freemarker", templateEncoding);
			} else {
				Writer writer = new OutputStreamWriter(output, outputEncoding);
				UtilTemplate.process(actionContext, tempaltePath, "freemarker", writer, templateEncoding);

				return null;
			}
		}finally {
			if(close){
				output.close();
			}
		}
		
	}
	
	public static String getCodePath(Thing self, ActionContext actionContext){
		 //取表单的临时文件名
		StringBuffer fileNameBuff = new StringBuffer(World.getInstance().getPath());		
		StringBuffer tName = new StringBuffer();
		tName.append("/work/forms");
		tName.append("/" + self.getRoot().getMetadata().getPath().hashCode());
		tName.append("/" + self.getMetadata().getPath().hashCode());
		tName.append("/" + self.getMetadata().getName());
		tName.append(".ftl");
		String fileName = fileNameBuff.append(tName).toString();
				
		File file = new File(fileName);
		if(!file.exists() || file.lastModified() < self.getMetadata().getLastModified()){
			if(!file.exists()){				
				File dir = file.getParentFile();
				if(!dir.exists() || !dir.isDirectory()){
					dir.mkdirs();
				}
			}
			try{
				String ftl = (String) self.doAction("getCode", actionContext);
				OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
				w.write(ftl);
				w.flush();
				w.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			file.setLastModified(self.getMetadata().getLastModified());
		}
					
		return tName.toString();
	}
}
