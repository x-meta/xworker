package xworker.chart.jfree.dataobject.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class ReDefaultTableXYDataset extends DefaultTableXYDataset implements DataObjectReloadableDataset{
	private static final long serialVersionUID = 4616915019850582179L;
	private Thing config;
	
	public ReDefaultTableXYDataset(Thing config){
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
		
		Map<String, XYSeries> series = new HashMap<String, XYSeries>();		
		for(DataObject r : datas){ 
			String name = r.getString(seriesName);
			XYSeries s = series.get(name);
			if(s == null){
				s = new XYSeries(name, true, false);
				series.put(name, s);
				this.addSeries(s);
			}
			
			try{
				s.add(r.getDouble(x), r.getDouble(y));
			}catch(Exception e){				
			}
		}
	}

}
