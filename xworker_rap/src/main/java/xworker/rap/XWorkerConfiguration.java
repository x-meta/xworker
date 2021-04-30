package xworker.rap;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;

public class XWorkerConfiguration implements ApplicationConfiguration {
	public void configure(Application application) {
		//application.setExceptionHandler(new XWorkerExceptionHandler());		
		application.addEntryPoint("/rap", XWorkerEntryPoint.class, null);
	}
}
