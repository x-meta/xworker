package xworker.chart.jfree.dataobject.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.xy.DefaultXYDataset;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class ReDefaultXYDataset extends DefaultXYDataset implements DataObjectReloadableDataset{
	private static final long serialVersionUID = 4616915019850582179L;
	private Thing config;
	private Map<String, List<Double[]>> series = new HashMap<String, List<Double[]>>();
	
	public ReDefaultXYDataset(Thing config){
		super();
		
		this.config = config;
	}
	
	@Override
	public void reload(List<DataObject> datas) {
		//先清除数据
		for(String key : series.keySet()){
			this.removeSeries(key);
		}
		series.clear();
		
		String seriesName = config.getString("xySeriesKey");
		String x = config.getString("x");
		String y = config.getString("y");
		
		for(DataObject r : datas){
			String name = r.getString(seriesName);
			List<Double[]> s = series.get(name);
			if(s == null){
				s = new ArrayList<Double[]>();				
				series.put(name, s);
			}
			
			Double[] ds = new Double[2];
			ds[0] = r.getDouble(x);
			ds[1] = r.getDouble(y);
			s.add(ds);
		}
		
		for(String key : series.keySet()){
			List<Double[]> s = series.get(key);
			double[][] ds = new double[2][s.size()];
			for(int i=0; i<ds.length; i++){
				Double[] ss = s.get(i);
				ds[0][i] = ss[0];
				ds[1][i] = ss[1];
			}
			
			this.addSeries(key, ds);
		}
	}

}
