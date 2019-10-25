package xworker.lang.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.XWorkerUtils;

public class NotImplemented {
	private final static Logger logger = LoggerFactory.getLogger(NotImplemented.class);
	
	public static void run(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//打开如何参与XWorker的文档
		XWorkerUtils.ideOpenUrl(XWorkerUtils.getThingDescUrl("xworker.things.p2016.p05.p25.JoinXWorkerDevelop"));
				
		logger.info("希望大家能够参与XWorker的开发，详情请参看http://www.xworker.org/html/0/11/1136.html。");
		throw new RuntimeException("Action not implements, name=" + self.getMetadata().getName() + ",path=" + self.getMetadata().getPath());
	}
}
