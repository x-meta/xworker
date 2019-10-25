package xworker.lang.function.text;

import java.io.File;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.util.UtilTemplate;

public class TextTemplateFunction {
	public static String doFunction(ActionContext actionContext) throws Throwable{
		Thing self = (Thing) actionContext.get("self");
		Object template = actionContext.get("template");
		if(template instanceof String){
			return UtilTemplate.processString(actionContext, (String) template);
		}else if(template instanceof File){
			return UtilTemplate.process(actionContext, ((File) template).getAbsolutePath(), UtilTemplate.FREEMARKER);
		}else{
			throw new ActionException("TextTemplate not surrport template, path=" + self.getMetadata().getPath() 
					+ ", template=" + template);
		}		
	}
}
