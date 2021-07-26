package xworker.lang.executor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionField;
import xworker.swt.util.SwtUtils;
import xworker.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.List;

public class SwtDefaultRequestService implements DefaultRequestServiceListener{
    private static final String TAG = SwtDefaultRequestService.class.getName();

    @ActionField
    public org.eclipse.swt.widgets.Table requestTable;
    @ActionField
    public org.eclipse.swt.widgets.Composite contentComposite;
    @ActionField
    public org.eclipse.swt.browser.Browser browser;
    @ActionField
    public org.eclipse.swt.widgets.Composite defaultContent;
    @ActionField
    public org.eclipse.swt.graphics.Font boldFont;
    @ActionField
    public org.eclipse.swt.graphics.Color urgentColor;

    DefaultRequestService defaultRequestService;

    public SwtDefaultRequestService(){
        defaultRequestService = DefaultRequestService.getInstance();
    }

    public SwtDefaultRequestService(DefaultRequestService defaultRequestService){
        this.defaultRequestService = defaultRequestService;
    }

    public void init(){
        defaultRequestService.addListener(this);

        requestTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                RequestItem requestItem = (RequestItem) selectionEvent.item.getData();

                //请求界面
                Composite requestComposite = requestItem.getRequestComposite();
                if(requestComposite != null) {
                    StackLayout layout = (StackLayout) contentComposite.getLayout();
                    layout.topControl = requestComposite;
                    contentComposite.layout();
                }

                //文档
                Request request = requestItem.request;
                request.setReaded();
                SwtUtils.setThingDesc(request.getThing(), browser);
            }
        });

        requestTable.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent disposeEvent) {
                //移除监听
                defaultRequestService.removeListener(SwtDefaultRequestService.this);
            }
        });

        List<Request> requestList = defaultRequestService.getRequests();
        for(Request request : requestList){
            addRequest(request);
        }

    }

    public void ok(Request request) {
        try {
            Executor.push(request.getExecutorService());

            Object result = request.doAction("ok");
            if(result instanceof Boolean && !(Boolean) result){
                return;
            }else{
                request.finish();
            }
        }finally {
            Executor.pop();
        }
    }

    public void cancel(Request request) {
        try {
            Executor.push(request.getExecutorService());
            Object result = request.doAction("cancel");
            if(result instanceof Boolean && !(Boolean) result){
                return;
            }else{
                request.finish();
            }
        }finally {
            Executor.pop();
        }
    }

    @Override
    public void requestAdded(DefaultRequestService defaultRequestService, Request request) {
        if(requestTable.isDisposed()){
            return;
        }

        requestTable.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                addRequest(request);
            }
        });

    }

    @Override
    public void requestRemoved(DefaultRequestService defaultRequestService, final Request request) {
        if(requestTable.isDisposed()){
            return;
        }

        requestTable.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                for(TableItem item : requestTable.getItems()){
                    RequestItem requestItem = (RequestItem) item.getData();
                    if(requestItem.request == request){
                        requestItem.finished(request);
                        item.dispose();
                        break;
                    }
                }

                if(defaultRequestService.getRequests().size() == 0 || requestTable.getSelectionCount() == 0){
                    StackLayout layout = (StackLayout) contentComposite.getLayout();
                    if(layout.topControl != defaultContent) {
                        layout.topControl = defaultContent;
                        contentComposite.layout();

                        browser.setText("");
                    }
                }
            }
        });
    }

    @Override
    public void requestUpdated(DefaultRequestService defaultRequestService, Request request) {
        if(requestTable.isDisposed()){
            return;
        }

        requestTable.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                for(TableItem item : requestTable.getItems()){
                    RequestItem requestItem = (RequestItem) item.getData();
                    if(requestItem.request == request){
                        requestItem.update();
                        return;
                    }
                }
            }
        });
    }

    public void addRequest(final Request request){
        new RequestItem(this, requestTable, request);
    }

    static class RequestItem implements RequestListener{
        Table requestTable;
        Request request;
        TableItem item;
        SwtDefaultRequestService swtDefaultRequestServiceComposite;
        Composite composite;

        public RequestItem(SwtDefaultRequestService swtDefaultRequestServiceComposite, Table requestTable, Request request){
            this.swtDefaultRequestServiceComposite = swtDefaultRequestServiceComposite;
            this.requestTable = requestTable;
            this.request = request;

            item = new TableItem(requestTable, SWT.NONE, 0);
            item.setData(this);

            request.addListener(this);
            item.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent disposeEvent) {
                    request.removeListener(RequestItem.this);
                }
            });
            update();

        }

        public void update(){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String texts[] = new String[]{
                    request.getLabel(), String.valueOf(request.getCount()), sf.format(request.getCreateDate())
            };
            item.setText(texts);

            if(request.isReaded()){
                item.setFont(null);
            }else{
                item.setFont(swtDefaultRequestServiceComposite.boldFont);
            }

            if(Request.URGENT.equals(request.getPriority())){
                item.setBackground(swtDefaultRequestServiceComposite.urgentColor);
            }else{
                item.setBackground(null);
            }
        }

        @Override
        public void readed(Request request) {
            update();
        }

        @Override
        public void finished(Request request) {
            if(composite != null && !composite.isDisposed()){
                composite.dispose();
            }
        }

        public Composite getRequestComposite(){
            if(composite == null || composite.isDisposed()){
                request.addListener(this);
                try {
                    Thing prototype = World.getInstance().getThing("xworker.lang.executor.requests.prototypes.QuestPrototype/@contentComposite");
                    composite = prototype.doAction("create", request.getActionContext(), "parent", swtDefaultRequestServiceComposite.contentComposite);
                    Composite contentComposite = request.getActionContext().getObject("contentComposite");
                    Composite buttonComposite = request.getActionContext().getObject("buttonComposite");

                    //创建请求自身的界面
                    request.create(contentComposite, "swt");

                    //创建按钮
                    Button okButton = new Button(buttonComposite, SWT.NONE);
                    okButton.setText(StringUtils.getString("lang:d=确定&en=Ok", request.getActionContext()));
                    okButton.addListener(SWT.Selection, new Listener() {
                        @Override
                        public void handleEvent(Event event) {
                            swtDefaultRequestServiceComposite.ok(request);
                        }
                    });

                    for(Thing buttons : request.getThing().getChilds("Buttons")){
                        for(Thing buttonThing : buttons.getChilds()){
                            Button button = new Button(buttonComposite, SWT.NONE);
                            button.setText(buttonThing.getMetadata().getLabel());
                            button.addListener(SWT.Selection, new Listener() {
                                @Override
                                public void handleEvent(Event event) {
                                    buttonThing.doAction("doAction", request.getActionContext());
                                }
                            });
                        }
                    }

                    Button cancelButton = new Button(buttonComposite, SWT.NONE);
                    cancelButton.setText(StringUtils.getString("lang:d=取消&en=Cancel", request.getActionContext()));
                    cancelButton.addListener(SWT.Selection, new Listener() {
                        @Override
                        public void handleEvent(Event event) {
                            swtDefaultRequestServiceComposite.cancel(request);
                        }
                    });

                }catch(Exception e){
                    Executor.error(TAG, "Create composite error", e);
                    return null;
                }
            }

            return composite;
        }
    }
}
