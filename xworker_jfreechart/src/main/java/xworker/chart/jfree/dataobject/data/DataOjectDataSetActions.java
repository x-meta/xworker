package xworker.chart.jfree.dataobject.data;

import java.util.List;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.WaferMapDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.WindDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class DataOjectDataSetActions {
	
	@SuppressWarnings("unchecked")
	public static XYDataset createXYDataset(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<DataObject> datas = (List<DataObject>) actionContext.get("datas");
		
		ReDefaultXYDataset dataset = new ReDefaultXYDataset(self);
		dataset.reload(datas);
		
		return dataset;
	}
	
	@SuppressWarnings("unchecked")
	public static XYZDataset createXYZDataset(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<DataObject> datas = (List<DataObject>) actionContext.get("datas");
		
		ReDefaultXYZDataset dataset = new ReDefaultXYZDataset(self);
		dataset.reload(datas);
		
		return dataset;
	}
	
	@SuppressWarnings("unchecked")
	public static IntervalXYDataset createIntervalXYDataset(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<DataObject> datas = (List<DataObject>) actionContext.get("datas");
		
		ReXYIntervalSeriesCollection dataset = new ReXYIntervalSeriesCollection(self);
		dataset.reload(datas);
		
		return dataset;
	}

	@SuppressWarnings("unchecked")
	public static TaskSeriesCollection createTaskSeriesCollection(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<DataObject> datas = (List<DataObject>) actionContext.get("datas");
		
		ReTaskSeriesCollection dataset = new ReTaskSeriesCollection(self);
		dataset.reload(datas);
		
		return dataset;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static OHLCDataset createOHLCDataset(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<DataObject> datas = (List<DataObject>) actionContext.get("datas");
		
		ReOHLCSeriesCollection dataset = new ReOHLCSeriesCollection(self);
		dataset.reload(datas);
		return dataset;
	}
	
	@SuppressWarnings("unchecked")
	public static CategoryDataset createCategoryDataset(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<DataObject> datas = (List<DataObject>) actionContext.get("datas");		
		
		ReDefaultCategoryDataset dataset = new ReDefaultCategoryDataset(self);
	    dataset.reload(datas);
	    return dataset;
	}
	
	@SuppressWarnings("unchecked")
	public static BoxAndWhiskerCategoryDataset createBoxAndWhiskerCategoryDataset (ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<DataObject> datas = (List<DataObject>) actionContext.get("datas");	
		
		ReBoxAndWhiskerCategoryDataset dataset = new ReBoxAndWhiskerCategoryDataset(self);
		dataset.reload(datas);
		
		return dataset;
	}
	

	@SuppressWarnings("unchecked")
	public static TableXYDataset createTableXYDataset (ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<DataObject> datas = (List<DataObject>) actionContext.get("datas");	
		
		ReDefaultTableXYDataset dataset = new ReDefaultTableXYDataset(self);
		dataset.reload(datas);
		
		return dataset;
	}
	
	@SuppressWarnings("unchecked")
	public static TimePeriodValuesCollection createTimePeriodValuesCollection(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<DataObject> datas = (List<DataObject>) actionContext.get("datas");	
		
		ReTimePeriodValuesCollection dataset = new ReTimePeriodValuesCollection(self);
		dataset.reload(datas);
		
		return dataset;
	}
	
	@SuppressWarnings("unchecked")
	public static WaferMapDataset createWaferMapDataset(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<DataObject> datas = (List<DataObject>) actionContext.get("datas");	
		
		ReWaferMapDataset dataset = new ReWaferMapDataset(self);
		dataset.reload(datas);
		
		return dataset;
	}
	
	@SuppressWarnings("unchecked")
	public static WindDataset createWindDataset(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<DataObject> datas = (List<DataObject>) actionContext.get("datas");	
		
		ReWindDataset dataset = new ReWindDataset(self);
		dataset.reload(datas);
		
		return dataset;
	}
}
