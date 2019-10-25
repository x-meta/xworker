package xworker.org.akrogen.tkui.css;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.akrogen.tkui.css.core.engine.CSSEngine;
import org.akrogen.tkui.css.swt.engine.CSSSWTEngineImpl;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class SwtActions {
	public static void applay(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		Control parent = actionContext.getObject("parent");
		
		CSSEngine engine = new CSSSWTEngineImpl(parent.getDisplay());
				
		String path = self.doAction("getCssPath", actionContext);
		if(path != null){
			World world = World.getInstance();
			InputStream in = world.getResourceAsStream(path);
			if(in != null){
				try{
					engine.parseStyleSheet(in);
				}finally{
					in.close();
				}
			}
		}else{
			String css = self.doAction("getCssCode", actionContext);
			if(css != null){
				StringReader sr = new StringReader(css);
				engine.parseStyleSheet(sr);
				sr.close();
			}
		}
		
		//应用样式单
		engine.applyStyles(parent, true);
	}
}
