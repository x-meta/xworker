package xworker.ui.function.uiimpls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUI;
import xworker.ui.function.FunctionRequestUIFactory;

/**
 * 在一个Composite中运行函数，只能运行一个函数。
 * 
 * @author Administrator
 *
 */
public class CompositeUI extends FunctionRequestUI{
	private static Logger log = LoggerFactory.getLogger(CompositeUI.class);
	private Composite composite;
	private ActionContext parentActionContext;
	private ActionContext actionContext;
	private FunctionRequest functionRequest;
	private boolean created = false;
	
	public CompositeUI(Composite composite, ActionContext actionContext){
		this.composite = composite;
		this.composite.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent event) {
				if(functionRequest != null){
					FunctionRequestUIFactory.removeUI(functionRequest);
				}
			}
		});
		this.parentActionContext = actionContext;
		this.actionContext = new ActionContext(actionContext);
		this.actionContext.put("parent", composite);
	}
	
	public void run(FunctionRequest functionRequest){
		if(this.functionRequest != null){
			FunctionRequestUIFactory.removeUI(this.functionRequest);
		}
		
		this.functionRequest = functionRequest;
		FunctionRequestUIFactory.registUI(functionRequest, this);
		this.createUI(functionRequest);
	}
	
	public void run(Thing functionThing){
		FunctionRequest request = new FunctionRequest(functionThing, null, false, new ActionContext());
		run(request);
	}
	
	
	@Override
	public void createUI(FunctionRequest functionRequest) {
		this.functionRequest = functionRequest;
		if(created){
			return;
		}else{
			created = true;
		}
		
		String thingPath = "xworker.ui.function.FunctionRequestDialog/@mainShahForm";
		final Thing thing = World.getInstance().getThing(thingPath);
		final FunctionRequest functionRequest1 = this.functionRequest ;
		
		if(thing == null){
		    log.info("thing not exists, thingPath=" + thingPath);
		    return;
		}

		Display display = composite.getDisplay();
		for(Control control : composite.getChildren()){
			control.dispose();
		}
		//final FunctionRequestUI ui = this;
		Runnable runnable = new Runnable(){
			public void run(){							
				//创建UI
				thing.doAction("create", actionContext);
					
				//注册Handler
				ActionContainer actions = (ActionContainer) actionContext.get("actions");
				actions.doAction("initHandlers", actionContext, UtilMap.toMap(new Object[]{"handlers", handlers}));
				actions.doAction("initFunctionRequest", actionContext, UtilMap.toMap(new Object[]{"request", functionRequest1}));
				actions.doAction("hideFunctionTree", actionContext);
								
			}
		};
		
	    display.syncExec(runnable);
	}
	
	public ActionContext getParentActionContext(){
		return this.parentActionContext;
	}
	
	public Composite getComposite(){
		return this.composite;
	}
	
	@Override
	public void forceActive() {
		composite.forceFocus();	
	}
	
	@Override
	public void updateRequestUI(final FunctionRequest functionRequest) {
		final ActionContainer actions = (ActionContainer) actionContext.get("actions");
		Display display = composite.getDisplay();
		Runnable runnable = new Runnable(){
			public void run(){
				actions.doAction("initFunctionRequest", actionContext, UtilMap.toMap(new Object[]{"request", functionRequest}));
			}
		};
		display.asyncExec(runnable);
	}
	
	@Override
	public void close() {
	}

	/**
	 * 执行一个函数。
	 * 
	 * @param functionThing
	 * @param composite
	 * @param actionContext
	 */
	public  static void runFunction(Thing functionThing, Composite composite, ActionContext actionContext){
		FunctionRequest request = new FunctionRequest(functionThing, null, false, new ActionContext());
		
		FunctionRequestUI ui = new CompositeUI(composite, actionContext);
		FunctionRequestUIFactory.registUI(request, ui);
		ui.createUI(request);		
		
		request.run(actionContext);
	}
	
	/**
	 * 执行一个函数原型。
	 * 
	 * @param functionThing
	 * @param composite
	 * @param actionContext
	 */
	public static void runFunctionInstance(Thing functionThing, Composite composite, ActionContext actionContext){
		FunctionRequest request = new FunctionRequest(functionThing, null, true, new ActionContext());
		
		FunctionRequestUI ui = new CompositeUI(composite, actionContext);
		FunctionRequestUIFactory.registUI(request, ui);
		ui.createUI(request);		
		
		request.run(actionContext);
	}
	
	public static Composite create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Composite parent = actionContext.getObject("parent");
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		
		CompositeUI ui = new CompositeUI(composite, actionContext);
		
		actionContext.peek().put("parent", composite);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		Designer.attach(composite, self.getMetadata().getPath(), actionContext);
		actionContext.g().put(self.getMetadata().getName(), ui);
		
		Thing function = (Thing) self.doAction("getFunction", actionContext);//World.getInstance().getThing(self.getString("functionPath"));
		if(function != null){
			ui.run(function);
		}
		
		return composite;
	}
}
