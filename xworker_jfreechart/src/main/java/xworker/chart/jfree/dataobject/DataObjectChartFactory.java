package xworker.chart.jfree.dataobject;

import java.io.IOException;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.CategoryToPieDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.WaferMapDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.WindDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.chart.jfree.utils.JFreeChartCommonAttributes;
import xworker.dataObject.DataObject;

public class DataObjectChartFactory {
	public static DataObjectChart create(ActionContext actionContext, List<DataObject> datas) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		String type = self.getString("chartType");
		JFreeChart chart = null;
		Dataset dataset = null;
		
		//获取相关属性
		JFreeChartCommonAttributes attrs = new JFreeChartCommonAttributes(self, actionContext);
		
		if("Area".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createAreaChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (CategoryDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls); 
		}else if("Bar".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createBarChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (CategoryDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("Bar3D".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createBarChart3D(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (CategoryDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("BoxAndWhisker".equals(type)){
			dataset = (BoxAndWhiskerCategoryDataset) self.doAction("createBoxAndWhiskerCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createBoxAndWhiskerChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (BoxAndWhiskerCategoryDataset) dataset, attrs.legend);
		}else if("Bubble".equals(type)){
			dataset = (XYZDataset) self.doAction("createXYZDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createBubbleChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (XYZDataset) dataset);
		}else if("Candlestick".equals(type)){
			dataset = (OHLCDataset) self.doAction("createOHLCDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createCandlestickChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (OHLCDataset) dataset, attrs.legend); 
		}else if("Gantt".equals(type)){
			dataset = (IntervalCategoryDataset ) self.doAction("createTaskSeriesCollection", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createGanttChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (IntervalCategoryDataset) dataset, attrs.legend, attrs.tooltips, attrs.urls); 
		}else if("HighLow".equals(type)){
			dataset = (OHLCDataset) self.doAction("createOHLCDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createHighLowChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel,(OHLCDataset) dataset, attrs.legend); 
		}else if("Histogram".equals(type)){
			dataset = (IntervalXYDataset) self.doAction("createIntervalXYDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createHistogram(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (IntervalXYDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("Line".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createLineChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (CategoryDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("Line3D".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createLineChart3D(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (CategoryDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("MultiplePie".equals(type)){
    		dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createMultiplePieChart(attrs.title, (CategoryDataset) dataset, attrs.order, attrs.legend, attrs.tooltips, attrs.urls); 
		}else if("MultiplePie3D".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createMultiplePieChart3D(attrs.title, (CategoryDataset) dataset, attrs.order, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("Pie".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			CategoryToPieDataset pdataset = new CategoryToPieDataset((CategoryDataset) dataset, attrs.order, 1);
			chart = ChartFactory.createPieChart(attrs.title, pdataset, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("Pie3D".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			CategoryToPieDataset pdataset = new CategoryToPieDataset((CategoryDataset) dataset, attrs.order, 1);
			chart = ChartFactory.createPieChart3D(attrs.title, pdataset, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("Polar".equals(type)){
			dataset = (XYDataset) self.doAction("createXYDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createPolarChart(attrs.title, (XYDataset) dataset, attrs.legend, attrs.tooltips, attrs.urls); 
		}else if("Ring".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			CategoryToPieDataset pdataset = new CategoryToPieDataset((CategoryDataset) dataset, attrs.order, 1);
			chart = ChartFactory.createRingChart(attrs.title, pdataset, attrs.legend, attrs.tooltips, attrs.urls); 
		}else if("ScatterPlot".equals(type)){
			dataset = (XYDataset) self.doAction("createXYDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createScatterPlot(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (XYDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls); 
		}else if("StackedArea".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createStackedAreaChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (CategoryDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("StackedBar".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createStackedBarChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (CategoryDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("StackedBar3D".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createStackedBarChart3D(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (CategoryDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("StackedXYArea".equals(type)){
			dataset = (TableXYDataset) self.doAction("createTableXYDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createStackedXYAreaChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (TableXYDataset) dataset);
		}else if("TimeSeries".equals(type)){
			dataset = (XYDataset) self.doAction("createTimePeriodValuesCollection", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createTimeSeriesChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel,(XYDataset) dataset, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("WaferMap".equals(type)){
			dataset = (WaferMapDataset) self.doAction("createWaferMapDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createWaferMapChart(attrs.title, (WaferMapDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("Waterfall".equals(type)){
			dataset = (CategoryDataset) self.doAction("createCategoryDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createWaterfallChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (CategoryDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("WindPlot".equals(type)){
			dataset = (WindDataset) self.doAction("createWindDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createWindPlot(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel, (WindDataset) dataset, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("XYArea".equals(type)){
			dataset = (XYDataset) self.doAction("createXYDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createXYAreaChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel,  (XYDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("XYBar".equals(type)){
			dataset = (IntervalXYDataset) self.doAction("createIntervalXYDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createXYBarChart(attrs.title, attrs.categoryAxisLabel, false, attrs.valueAxisLabel,  (IntervalXYDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("XYLine".equals(type)){
			dataset = (XYDataset) self.doAction("createXYDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createXYLineChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel,  (XYDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("XYStepArea".equals(type)){
			dataset = (XYDataset) self.doAction("createXYDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createXYStepAreaChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel,  (XYDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}else if("XYStep".equals(type)){
			dataset = (XYDataset) self.doAction("createXYDataset", actionContext, UtilMap.toMap("datas", datas));
			chart = ChartFactory.createXYStepChart(attrs.title, attrs.categoryAxisLabel, attrs.valueAxisLabel,  (XYDataset) dataset, attrs.orientation, attrs.legend, attrs.tooltips, attrs.urls);
		}
		
		DataObjectChart dataObjectChart = new DataObjectChart();
		dataObjectChart.dataset = dataset;
		dataObjectChart.chart = chart;
		return dataObjectChart;
	}
}	
