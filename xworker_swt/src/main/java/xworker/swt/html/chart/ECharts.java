package xworker.swt.html.chart;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.app.view.swt.data.DataStoreListener;
import xworker.app.view.swt.data.ThingDataStoreListener;
import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;
import xworker.swt.reacts.DataReactor;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;
import xworker.util.UtilData;
import xworker.util.XWorkerUtils;

public class ECharts implements DataStoreListener{
	private static final String TAG = ECharts.class.getName();
	
	public Thing thing;
	public ActionContext actionContext;
	
	ActionContainer actions;
	
	public ECharts(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		this.actions = actionContext.getObject("actions");
		Browser browser = actionContext.getObject("browser");
		browser.addProgressListener(new ProgressListener() {

			@Override
			public void changed(ProgressEvent event) {
				
			}

			@Override
			public void completed(ProgressEvent event) {
			}
		});
	}
	
	public void update(String option) {
		//要执行的代码是保存到全局变量code中的
		String code = "swtChart.setOption(" + option + ");";
		actionContext.g().put("option", code);
		
		//已经改为网页定时500毫秒自动刷新读取option了
		/*if(actions == null) {
			return;
		}
		actions.doAction("execute", actionContext, "code", "update()");*/
	}
	
	public void update(Object ... params) {
		String option = thing.doAction("getOption", actionContext, params);
		if(option != null) {
			update(option);
		}
	}

	/**
	 * 创建控件。
	 * 
	 * @param actionContext
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static Object create(ActionContext actionContext) throws UnsupportedEncodingException {
		Thing self = actionContext.getObject("self");
		World world = World.getInstance();
		
		//创建控件
		ThingCompositeCreator creator = SwtUtils.createCompositeCreator(self, actionContext);
		creator.setCompositeThing(world.getThing("xworker.swt.html.chart.prototypes.EChartsShell/@chartComposite"));
		Composite composite = creator.create();		
		
		//设置参数等
		ActionContext ac = creator.getNewActionContext();
		ac.g().put("thing", self);
		Browser browser = ac.getObject("browser");
		
		//创建函数
		actionContext.peek().put("parent", browser);
		for(Thing functions : self.getChilds("Functions")) {
			for(Thing function : functions.getChilds("BrowserFunction")) {
				function.doAction("create", actionContext);
			}
		}
		
		String url = XWorkerUtils.getWebControlUrl(world.getThing("xworker.swt.html.chart.prototypes.EChartsWeb"));
		String jsUrl = self.doAction("getJsUrl", actionContext);
		if(jsUrl != null && !"".equals(jsUrl)) {
			url = url + "&jsUrl=" + URLEncoder.encode(jsUrl, "utf-8");
		}
		browser.setUrl(url);
		
		ActionContainer actions = ac.getObject("actions");
		ECharts echarts = new ECharts(self, ac);
		ac.put("echarts", echarts);
		
		//绑定DataStore
		Thing dataStore = self.doAction("getDataStore", actionContext);
		if(dataStore != null) {
			ThingDataStoreListener.attach(dataStore, echarts, actionContext);
		}
		actionContext.g().put(self.getMetadata().getName(), actions);
		return composite;
	}
	
	/**
	 * 浏览器的事件。
	 * 
	 * @param actionContext
	 */
	public static String getScript(ActionContext actionContext) {
		String code = (String) actionContext.g().get("code");
		if(code != null && !"".equals(code)) {
			return code;
		}
		
		ActionContext parentContext = actionContext.getObject("parentContext");
		Thing thing = actionContext.getObject("thing");
		//Action evaluate = actionContext.getObject("evaluate");
		
		String script = thing.doAction("getScript", parentContext);
		if(script != null) {
			return script;
			//option = option.replace('"', '\'');
			//evaluate.run(actionContext, "code", "setChartOption(\"" + option + "\")");
		}else {
			Executor.info(TAG, "script is null, thing=" + thing.getMetadata().getPath());
			return "";
		}
	}
	
	public static String getOption(ActionContext actionContext){
		String option =  (String) actionContext.g().get("option");
		if(option != null) {
			//重置option为null
			actionContext.g().put("option", null);
		}
		
		return option;
	}
	
	/**
	 * 执行脚本.
	 * 
	 * @param actionContext
	 */
	public static void execute(ActionContext actionContext) {
		actionContext.g().put("code", actionContext.get("code"));
		Action evaluate = actionContext.getObject("evaluate");
		if(UtilData.getBoolean(actionContext.get("clear"), false)) {
		    evaluate.run(actionContext, "code", "executeScript(true)");
		}else {
			evaluate.run(actionContext, "code", "executeScript(false)");
		}
	}
	
	/**
	 * 绑定到DataStore。
	 * 
	 * @param actionContext
	 */
	public static void attachDataStore(ActionContext actionContext) {
		ECharts echarts = actionContext.getObject("echarts");
		Thing dataStore = actionContext.getObject("dataStore");
		ThingDataStoreListener.attach(dataStore, echarts, actionContext);
	}
	
	/**
	 * 重新调整大小。
	 * 
	 * @param actionContext
	 */
	public static void onResize(ActionContext actionContext) {
		try {
			Action evaluate = actionContext.getObject("evaluate");
			evaluate.run(actionContext);
		}catch(Exception e) {
			
		}
	}


	@Override
	public void onInsert(Thing store, int index, List<DataObject> records) {
		update("records", store.get("records"));
	}


	@Override
	public void onLoaded(Thing store, List<DataObject> records) {
		update("records", store.get("records"));
	}


	@Override
	public void onReconfig(Thing store) {
		//重新执行脚本
		actionContext.g().put("code", null);
		
	    actions.doAction("execute", actionContext, "code", "executeScript(true)");
	    
	}


	@Override
	public void onRemove(Thing store, List<DataObject> records) {
		update("records", store.get("records"));
	}


	@Override
	public void onUpdate(Thing store, List<DataObject> records) {
		update("records", store.get("records"));
	}


	@Override
	public void beforeLoad(Thing store) {
		
	}


	@Override
	public Control getControl() {		
		return (Control) actionContext.get("chartComposite");
	}
	
	public static DataReactor createDataReactor(ActionContext actionContext) {
		ActionContainer actions = actionContext.getObject("actions");
		Thing thing = new Thing("xworker.swt.html.chart.EChartsDataReactor");
		Map<String, Object> params = actionContext.getObject("params");
		if(params != null) {
			thing.getAttributes().putAll(params);
		}
		return new EChartsDataReactor(actions, thing,  actionContext);
	}
}
