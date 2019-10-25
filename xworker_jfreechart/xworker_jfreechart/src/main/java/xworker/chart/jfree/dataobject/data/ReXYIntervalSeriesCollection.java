package xworker.chart.jfree.dataobject.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class ReXYIntervalSeriesCollection extends XYIntervalSeriesCollection implements DataObjectReloadableDataset{
	private static final long serialVersionUID = 4616915019850582179L;
	private Thing config;
	
	public ReXYIntervalSeriesCollection(Thing config){
		super();
		
		this.config = config;
	}
	
	@Override
	public void reload(List<DataObject> datas) {
		//先清除数据
		 this.removeAllSeries();
		
		String seriesName = config.getString("xySeriesKey");
		String x = config.getString("x");
		String y = config.getString("y");
		String xLow = config.getString("xLow");
		String xHigh = config.getString("xHigh");
		String yLow = config.getString("yLow");
		String yHigh = config.getString("yHigh");
		
		Map<String, XYIntervalSeries> series = new HashMap<String, XYIntervalSeries>();
		for(DataObject r : datas){
			String name = r.getString(seriesName);
			
			XYIntervalSeries xs = series.get(name);
			if(xs == null){
				xs = new XYIntervalSeries(name);
				series.put(name, xs);
				
				this.addSeries(xs);
			}
			
			xs.add(r.getDouble(x),r.getDouble(xLow), r.getDouble(xHigh), r.getDouble(y), r.getDouble(yLow), r.getDouble(yHigh));
		}
		
	}
}