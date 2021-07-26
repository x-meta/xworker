package xworker.util;

import org.xmeta.util.ThingLoader;
import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;

public class ThreadHelper {
    Object thingLoadObject;
    ExecutorService logService;
    ExecutorService requestService;

    public ThreadHelper(){
        this.thingLoadObject = ThingLoader.getObject();
        this.logService = Executor.getLogExecutorService();
        this.requestService = Executor.getRequestExecutorService();
    }

    public void begin(){
        if(thingLoadObject != null){
            ThingLoader.push(thingLoadObject);
        }
        if(logService != null){
            Executor.push(logService);
        }
        if(requestService != null){
            Executor.push(requestService);
        }
    }

    public void end(){
        if(requestService != null){
            Executor.pop();
        }
        if(logService != null){
            Executor.pop();
        }
        if(thingLoadObject != null){
            ThingLoader.pop();
        }
    }

    public static ThreadHelper create(){
        return new ThreadHelper();
    }

    public static Runnable wrap(final Runnable runnable){
        final ThreadHelper threadHelper = create();
        return new Runnable() {
            @Override
            public void run() {
                threadHelper.begin();

                try{
                    runnable.run();
                }finally {
                    threadHelper.end();
                }
            }
        };
    }

    public static void startThread(String name, Runnable runnable){
        new Thread(wrap(runnable),name).start();
    }
}
