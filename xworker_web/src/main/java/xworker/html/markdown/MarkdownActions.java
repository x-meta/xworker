package xworker.html.markdown;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.github.rjeschke.txtmark.Processor;

public class MarkdownActions {
	public static String toHtml(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String code = self.getStringBlankAsNull("code");
		if(code != null){
			return Processor.process(code);
		}else{
			return null;
		}
	}
}
