package xworker.chart.jfree.dataobject.swt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.app.view.swt.data.DataStore;
import xworker.app.view.swt.data.DataStoreDisposeListener;
import xworker.chart.jfree.JfreeInit;
import xworker.chart.jfree.dataobject.DataObjectChart;
import xworker.chart.jfree.dataobject.DataObjectChartFactory;
import xworker.chart.jfree.dataobject.data.DataObjectReloadableDataset;
import xworker.dataObject.DataObject;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class SwtDataObjectChart {
	static {
		JfreeInit.init();
	}
	
	public static Object create(ActionContext actionContext) throws OgnlException, IOException{
		Thing self = (Thing) actionContext.get("self");
		
		//设置字体，默认字体显示不了中文
		JfreeInit.init();

		//chart
		JFreeChart jfreechart = null;

		int width = self.getInt("width", 0, actionContext);
		if(width <= 0){
		    width = ChartComposite.DEFAULT_WIDTH;
		}

		int height = self.getInt("height", 0, actionContext);
		if(height <= 0){
		    height = ChartComposite.DEFAULT_HEIGHT;
		}

		int minimumDrawW = self.getInt("minimumDrawW", 0, actionContext);
		if(minimumDrawW <= 0){
		    minimumDrawW = ChartComposite.DEFAULT_MINIMUM_DRAW_WIDTH;
		}

		int minimumDrawH = self.getInt("minimumDrawH", 0, actionContext);
		if(minimumDrawH <= 0){
		    minimumDrawH = ChartComposite.DEFAULT_MINIMUM_DRAW_HEIGHT;
		}

		int maximumDrawW = self.getInt("maximumDrawW", 0, actionContext);
		if(maximumDrawW <= 0){
		    maximumDrawW = ChartComposite.DEFAULT_MAXIMUM_DRAW_WIDTH;
		}

		int maximumDrawH = self.getInt("maximumDrawH", 0, actionContext);
		if(maximumDrawH <= 0){
		    maximumDrawH = ChartComposite.DEFAULT_MAXIMUM_DRAW_HEIGHT;
		}

		boolean usingBuffer = self.getBoolean("usingBuffer", false, actionContext);
		boolean properties = self.getBoolean("properties", false, actionContext);
		boolean save = self.getBoolean("save", false, actionContext);
		boolean print = self.getBoolean("print", false, actionContext);
		boolean zoom = self.getBoolean("zoom", false, actionContext);
		boolean tooltips = self.getBoolean("tooltips", false, actionContext);

		Composite parent = (Composite) actionContext.get("parent");
		Control chartComposite = null;
		if(SwtUtils.isRWT()) {
			chartComposite = new Canvas(parent, SWT.NONE | SWT.DOUBLE_BUFFERED);
			
			//销毁jfreechart生成的图片
			chartComposite.addListener(SWT.Dispose, new Listener() {
				@Override
				public void handleEvent(Event event) {
					Canvas canvas = (Canvas) event.widget;
					Image image = canvas.getBackgroundImage();
					if(image != null && !image.isDisposed()) {
						image.dispose();
					}
				}
				
			});
			chartComposite.addListener(SWT.Resize, new Listener() {
				@Override
				public void handleEvent(Event event) {
					Canvas canvas = (Canvas) event.widget;
					updateCanvasJFreechart(canvas);
				}
			});
		}else {
			chartComposite = new ChartComposite(parent, SWT.NONE, jfreechart,width,height,
			        minimumDrawW,minimumDrawH,maximumDrawW,maximumDrawH,
			        usingBuffer,properties,save,print,zoom,tooltips);
		}
		        
		Designer.attach(chartComposite, self.getMetadata().getPath(), actionContext);
		//创建子事物
		try{
		    Bindings bindings = actionContext.push(null);
		    bindings.put("parent", chartComposite);
		    bindings.put("__jfreeChartComposite__", chartComposite);
		    for(Thing child : self.getChilds()){
		    	if("DataStores".equals(child.getThingName())){
		    		//应为会在getDataStore中创建DataStore，所以过滤DataStore节点
		    		continue;
		    	}
		    	
		        child.doAction("create", actionContext);
		    }
		    
		    Thing dataStore = (Thing) self.doAction("getDataStore", actionContext);
		    attachToDataStore(dataStore, chartComposite, actionContext);		    
		}finally{
		    actionContext.pop();
		}        

		//放入变量范围
		actionContext.getScope(0).put(self.getString("name"), chartComposite);
		return chartComposite;
	}
	
	public static void attachDataStore(ActionContext actionContext) throws IOException {
		Thing dataStore = actionContext.getObject("dataStore");
		Control control = actionContext.getObject("control");
		
		attachToDataStore(dataStore, control, actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static void attachToDataStore(Thing dataStore, Control chartComposite, ActionContext actionContext) throws IOException {
		if(dataStore == null) {
			return;
		}
		
		Thing self = Designer.getThing(chartComposite);
		
		List<DataObject> datas = Collections.emptyList();
	    if(dataStore != null && dataStore.get("records") != null){
	    	datas = (List<DataObject>) dataStore.get("records");
	    }
	    
	    actionContext.peek().put("self", self);
	    DataObjectChart chart = DataObjectChartFactory.create(actionContext, datas);
	    if(chart != null){
	    	if(SwtUtils.isRWT()) {
	    		//ChartUtilities.wr
	    		chartComposite.setData("__jfreeChartComposite__chart__", chart);
	    	}else {
	    		((ChartComposite) chartComposite).setChart(chart.chart);
	    	}
	    	
	    	if(dataStore != null && chart.dataset instanceof DataObjectReloadableDataset){
	    		dataStore.put("dataObjectDataset", chart.dataset);
	    	}
	    }
	    
	    if(dataStore != null){
	    	dataStore.put("__jfreeChartComposite__", chartComposite);
	    	dataStore.doAction("addListener", actionContext, UtilMap.toMap("listener", self));
	    	
	    	chartComposite.setData(DataStore.STORE, dataStore);
	    	chartComposite.setData(DataStore.LISTENER, self);
	    	DataStoreDisposeListener.attach(chartComposite);
	    	
	    }
	}
	
	public static void updateCanvasJFreechart(final Canvas canvas) {
		canvas.getDisplay().asyncExec(new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				try {
					DataObjectChart chart = (DataObjectChart) canvas.getData("__jfreeChartComposite__chart__");
					DataObjectReloadableDataset dataSet = null;
					if(chart.dataset instanceof DataObjectReloadableDataset) {
						dataSet = (DataObjectReloadableDataset) chart.dataset;
						List<DataObject> datas = (List<DataObject>) canvas.getData("__datas__");
						dataSet.reload(datas);
					}					
					
					int width = canvas.getClientArea().width;
					int height = canvas.getClientArea().height;
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					ChartUtilities.writeChartAsPNG(bout, chart.chart, width, height);
					
					Image image = canvas.getBackgroundImage();
					if(image != null && !image.isDisposed()) {
						image.dispose();
					}
					
					ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
					image = new Image(canvas.getDisplay(), bin);
					canvas.setBackgroundImage(image);
					bin.close();
					bout.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});		
	}
	
	@SuppressWarnings("unchecked")
	public static void onLoaded(ActionContext actionContext){
		Thing dataStore = (Thing) actionContext.get("store");
		final DataObjectReloadableDataset dataset = (DataObjectReloadableDataset) dataStore.get("dataObjectDataset");
		final List<DataObject> datas = (List<DataObject>) dataStore.get("records");
		
		if(SwtUtils.isRWT()) {
			final Canvas canvas = (Canvas) dataStore.get("__jfreeChartComposite__");
			canvas.setData("__datas__", datas);
			updateCanvasJFreechart(canvas);
		}else {
			
			ChartComposite chartComposite = (ChartComposite) dataStore.get("__jfreeChartComposite__");
			if(chartComposite == null || chartComposite.isDisposed()) {
				return;
			}
			
			chartComposite.getDisplay().asyncExec(new Runnable(){
				public void run(){
					try{
						dataset.reload(datas);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		}
		
	}
}
