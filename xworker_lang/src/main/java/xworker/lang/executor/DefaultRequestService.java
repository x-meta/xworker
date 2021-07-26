package xworker.lang.executor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.lang.executor.services.AbstractRequestService;
import xworker.task.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * UI请求处理的一个实现，便于界面实现UI请求。是系统默认的请求处理器。
 */
public class DefaultRequestService extends AbstractRequestService implements RequestListener, Runnable{
    /** 默认会加入到Executor中的实例 */
    private static DefaultRequestService instance = new DefaultRequestService();
    public static final DefaultRequestService getInstance(){
        return instance;
    }

    List<Request> requests = new ArrayList<>();
    List<DefaultRequestServiceListener> listeners = new ArrayList<>();

    public DefaultRequestService(){
        TaskManager.getScheduledExecutorService().scheduleAtFixedRate(this, 0, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run(){
        //检查超时的请求
        for(int i=0; i<requests.size(); i++){
            Request request = requests.get(i);

            if(request.isTimeout()){
                Object result = request.doAction("timeout");
                if(!(result instanceof Boolean && (Boolean) result == false)){
                    request.finish();

                    for(DefaultRequestServiceListener listener : listeners){
                        listener.requestRemoved(this, request);
                    }
                }
            }
        }
    }

    public void addListener(DefaultRequestServiceListener listener){
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    public void removeListener(DefaultRequestServiceListener listener){
        listeners.remove(listener);
    }

    @Override
    public synchronized Request requestUI(Thing thing, ActionContext actionContext) {
        Request oldRequest = null;
        //判断是否重复
        for(Request request : requests){
            if(request.equals(thing, actionContext)){
                oldRequest = request;
                break;
            }
        }

        Request request = new Request(thing, actionContext);
        if(oldRequest != null){
            request.setCount(oldRequest.getCount() + 1);
            oldRequest.finish();
        }
        request.setExecutorService(this);
        request.addListener(this);
        requests.add(request);

        for(DefaultRequestServiceListener listener : listeners){
            listener.requestAdded(this, request);
        }
        return request;
    }

    @Override
    public List<Request> getRequests() {
        return requests;
    }

    @Override
    public void removeRequest(Request request) {
        requests.remove(request);

        for(DefaultRequestServiceListener listener : listeners){
            listener.requestRemoved(this, request);
        }
    }

    @Override
    public Thread getThread() {
        return null;
    }

    @Override
    public void readed(Request request) {
        for(DefaultRequestServiceListener listener : listeners){
            listener.requestUpdated(this, request);
        }
    }

    @Override
    public void finished(Request request) {
    }

    /**
     * 返回未读的
     *
     * @return
     */
    public int getUnreadCount(){
        int count = 0;
        for(Request request : requests){
            if(request.isReaded() == false){
                count++;
            }
        }

        return count;
    }
}
