package xworker.spring;

import org.springframework.context.ApplicationContext;
import org.xmeta.ActionException;

public class SpringConfig {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext(){
        if(applicationContext == null){
            throw new ActionException("Please set spring ApplicationContext first.");
        }

        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext){
        SpringConfig.applicationContext = applicationContext;
    }
}
