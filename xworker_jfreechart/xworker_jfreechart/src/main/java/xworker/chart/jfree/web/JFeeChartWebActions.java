package xworker.chart.jfree.web;

import java.io.IOException;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.CategoryToPieDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.chart.jfree.utils.JFreeChartCommonAttributes;
import xworker.dataObject.DataObject;

public class JFeeChartWebActions {
	/**
	 * 导出图片到客户端。
	 * 
	 * @param actionContext
	 * @throws OgnlException
	 */
	@SuppressWarnings("unchecked")
	public static void exportChartImage(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//查询数据
		Object conditionData = UtilData.getData(self, "conditionData", actionContext);		
		List<DataObject> datas = (List<DataObject>) self.doAction("query",actionContext, UtilMap.toMap("conditionData", conditionData));
		
		//把数据转化为CategoryData
	}
	
	public static JFreeChart createChart(Thing self, String type, List<DataObject> datas, ActionContext actionContext) throws IOException{
		//获取相关属性
		JFreeChartCommonAttributes attrs = new JFreeChartCommonAttributes(self, actionContext);
		
		if("Area".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createAreaChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls); 
		}else if("Bar".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createBarChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("Bar3D".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createBarChart3D(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("BoxAndWhisker".equals(type)){
			return null;
		}else if("Bubble".equals(type)){
			return null;
		}else if("Candlestick".equals(type)){
			OHLCDataset dataset = (OHLCDataset) self.doAction("createOHLCDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createCandlestickChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, dataset, attrs.legend); 
		}else if("Gantt".equals(type)){
			IntervalCategoryDataset  dataset = (IntervalCategoryDataset ) self.doAction("createTaskSeriesCollection", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createGanttChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, dataset, attrs.legend, attrs.tooltips, attrs.urls); 
		}else if("HighLow".equals(type)){
			OHLCDataset dataset = (OHLCDataset) self.doAction("createOHLCDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createHighLowChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel,dataset, attrs.legend); 
		}else if("Histogram".equals(type)){
			return null;
		}else if("Line".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createLineChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("Line3D".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createLineChart3D(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("MultiplePie".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createMultiplePieChart(attrs.title, dataset, attrs.order, attrs.legend, attrs.tooltips, attrs.urls); 
		}else if("MultiplePie3D".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createMultiplePieChart3D(attrs.title, dataset, attrs.order, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("Pie".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			CategoryToPieDataset pdataset = new CategoryToPieDataset(dataset, attrs.order, 1);
			return ChartFactory.createPieChart(attrs.title, pdataset, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("Pie3D".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			CategoryToPieDataset pdataset = new CategoryToPieDataset(dataset, attrs.order, 1);
			return ChartFactory.createPieChart3D(attrs.title, pdataset, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("Polar".equals(type)){
			XYDataset dataset = (XYDataset) self.doAction("createXYDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createPolarChart(attrs.title, dataset, attrs.legend, attrs.tooltips, attrs.urls); 
		}else if("Ring".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			CategoryToPieDataset pdataset = new CategoryToPieDataset(dataset, attrs.order, 1);
			return ChartFactory.createRingChart(attrs.title, pdataset, attrs.legend, attrs.tooltips, attrs.urls); 
		}else if("ScatterPlot".equals(type)){
			XYDataset dataset = (XYDataset) self.doAction("createXYDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createScatterPlot(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls); 
		}else if("StackedArea".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createStackedAreaChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("StackedBar".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createStackedBarChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("StackedBar3D".equals(type)){
			CategoryDataset dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			return ChartFactory.createStackedBarChart3D(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("StackedXYArea".equals(type)){
			return null;
		}else if("TimeSeries".equals(type)){
			return null;
		}else if("WaferMap".equals(type)){
			return null;
		}else if("Waterfall".equals(type)){
			return null;
		}else if("WindPlot".equals(type)){
			return null;
		}else if("XYArea".equals(type)){
			return null;
		}else if("XYBar".equals(type)){
			return null;
		}else if("XYLine".equals(type)){
			return null;
		}else if("XYStepArea".equals(type)){
			return null;
		}else if("XYStep".equals(type)){
			return null;
		}
		
		return null;
	}
	
}
