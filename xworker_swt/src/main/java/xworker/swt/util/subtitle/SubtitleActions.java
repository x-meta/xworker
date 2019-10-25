package xworker.swt.util.subtitle;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.util.XWorkerUtils;

public class SubtitleActions {
	public static void run(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Shell shell = (Shell) XWorkerUtils.getIDEShell();
		String code = UtilString.getString(self, "subtitles", actionContext);
		if(code == null){
			code = "";
		}
		
		final String texts = code;
		
		shell.getDisplay().asyncExec(new Runnable(){
			public void run(){
				//初始化
				new Subtitle(texts, self.getString("font"), self.getString("color"), 10, self.getBoolean("refresh"));							
			}
		});
		
	}
}
