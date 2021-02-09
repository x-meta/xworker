package xworker.swt.data;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorFactory;
import xworker.swt.reacts.ProxyDataReactor;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

/**
 * 给定数据和操作方式，就能生成相关界面。应该还未实现，可能会废弃。
 * 
 * @author zhangyuxiang
 *
 */
public class DataComposite {
	private static Map<String, String> dataComposites = new HashMap<String, String>();
	
	private static String defualtFactory = "xworker.swt.data.DataComposites/@objectViewer";	
	
	public static void registDataComposite(String dataCompositeName, String dataCompositePath) {
		dataComposites.put(dataCompositeName, dataCompositePath);
	}	
	
	static {
		//注册默认的
		dataComposites.put("object.view", "xworker.swt.data.DataComposites/@objectViewer");
		dataComposites.put("thing.form", "xworker.swt.data.dataComposites.DataComposites/@thingForm");
		dataComposites.put("thing.editor", "xworker.swt.data.dataComposites.DataComposites/@thingEditor");
		dataComposites.put("throwable.txt", "xworker.swt.data.dataComposites.DataComposites/@throwableStackTraceText");
		
	}
	
	Thing thing;
	ActionContext actionContext;
	
	/** DataComposite自己的Compostie，创建数据相关的控件时使用的parent */
	Composite composite;
	
	/** 已经创建的Control */
	Control dataControl;
	
	/** 数据创建Control时的上下文， 每次创建数据都会使用新的变量上下文 */
	ActionContext dataContext;
	
	/** 创建数据控件时，控件保存在变量上下文中的对象 */
	Object dataObject = null; 
	
	ProxyDataReactor dataReactor;
	
	public DataComposite(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		
		Thing compositeThing = thing.doAction("getComposite", actionContext);
		if(compositeThing == null) {
			compositeThing = World.getInstance().getThing("xworker.swt.data.prototypes.DataCompositeComposite");
		}
		
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(thing, actionContext);
		cc.setCompositeThing(compositeThing);
		composite = cc.create();
		
		Object data = thing.doAction("getObject", actionContext);
		String dataCompositeName = thing.doAction("getDataCompositeName", actionContext);
		
		if(data != null) {
			this.open(data, dataCompositeName);
		}
	}
	
	public void setDataReactor(ProxyDataReactor dataReactor) {
		this.dataReactor = dataReactor;
		
		initDataReactor();
	}
	
	private void initDataReactor() {
		if(this.dataObject != null && this.dataReactor != null) {
			String action = dataReactor.getSelf().getString("dataReactorAction");
			
			DataReactor objReactor = DataReactorFactory.create(action, null, dataObject, actionContext);
			if(objReactor != null) {
				this.dataReactor.setDataReactor(objReactor);
			}
		}
	}
	
	protected void clear() {
		if(dataControl != null && dataControl.isDisposed() == false) {
			dataControl.dispose();
		}
	}
	
	public Thing getDataComposite(String dataCompositeName) {
		String factory = dataComposites.get(dataCompositeName);
		Thing dataComposite = null;
		if(factory != null) {
			dataComposite = World.getInstance().getThing(factory);
		}
		
		if(dataComposite == null) {
			dataComposite = World.getInstance().getThing(defualtFactory);
		}
		
		return dataComposite;
	}
	
	public void open(Object data , String dataCompositeName) {
		open(data, getDataComposite(dataCompositeName));
	}
	
	public void open(Object data , Thing dataComposite) {
		this.open(data, dataComposite, new Object[] {});
	}

	public void open(Object data , Thing dataComposite, Object ...params) {
		//先清空原来的Composite
		clear();
		
		//床架新的Composite，使用新的变量上下文
		dataContext = new ActionContext(); 
		dataContext.put("parent", composite);
		dataContext.put("data", data);
		dataContext.put("parentContext", actionContext);
		dataContext.put("parentActionContext", actionContext);
		if(params != null) {
			for(int i=0; i<params.length; i++) {
				dataContext.put(String.valueOf(params[i]), params[i + 1]);
				i++;
			}
		}
		
		dataControl = dataComposite.doAction("create", dataContext);
		
		dataObject = dataContext.get(dataComposite.getMetadata().getName());
		initDataReactor();
	}
	
	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public Object getParent() {
		return composite;
	}

	public Control getDataControl() {
		return dataControl;
	}

	public ActionContext getDataContext() {
		return dataContext;
	}

	public Composite getComposite() {
		return composite;
	}
	
	public Control getControl() {
		return composite;
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataComposite dataComposite = new DataComposite(self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), dataComposite);
		
		return dataComposite.getControl();
	}
}
