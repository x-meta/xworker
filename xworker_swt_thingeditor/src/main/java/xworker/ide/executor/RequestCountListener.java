package xworker.ide.executor;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.ActionContext;
import org.xmeta.util.UtilString;
import xworker.lang.executor.DefaultRequestService;
import xworker.lang.executor.DefaultRequestServiceListener;
import xworker.lang.executor.Request;

/**
 * 在Toolbar上显示未处理的通知和请求的数量。
 */
public class RequestCountListener implements DefaultRequestServiceListener {
    ToolItem toolItem;
    ActionContext actionContext;

    public RequestCountListener(ToolItem toolItem, ActionContext actionContext){
        this.toolItem = toolItem;
        this.actionContext = actionContext;

        //增加监听
        DefaultRequestService.getInstance().addListener(this);

        //界面销毁时增加移除的监听
        toolItem.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent disposeEvent) {
                DefaultRequestService.getInstance().removeListener(RequestCountListener.this);
            }
        });
    }

    private void update(){
        if(toolItem.isDisposed() == false){


            toolItem.getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    String label = UtilString.getString("lang:d=通知和请求&en=Notification &amp; Request", actionContext);

                    int unreadCount = DefaultRequestService.getInstance().getUnreadCount();
                    if(unreadCount > 0){
                        label = label + "(" + unreadCount + ")";
                    }

                    toolItem.setText(label);
                }
            });
        }
    }

    @Override
    public void requestAdded(DefaultRequestService defaultRequestService, Request request) {
        update();
    }

    @Override
    public void requestRemoved(DefaultRequestService defaultRequestService, Request request) {
        update();
    }

    @Override
    public void requestUpdated(DefaultRequestService defaultRequestService, Request request) {
        update();
    }
}
