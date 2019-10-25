package xworker.chart.jfree.dataobject.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.xmeta.Thing;

import xworker.chart.jfree.utils.UtilJFreeChart;
import xworker.dataObject.DataObject;

public class ReOHLCSeriesCollection extends OHLCSeriesCollection implements DataObjectReloadableDataset{
	private static final long serialVersionUID = 7307637541426321847L;
	private Thing config;
	
	public ReOHLCSeriesCollection(Thing config) {
		super();
		
		this.config = config;
	}

	@Override
	public void reload(List<DataObject> datas) {
		this.removeAllSeries();
		
		String timePeriod = config.getString("timePeriod");
		String ohlcSeriesName = config.getString("ohlcSeriesName");
		
		Map<String, OHLCSeries> context = new HashMap<String, OHLCSeries>();
		for(DataObject record : datas){
			String name = record.getString(ohlcSeriesName);
			OHLCSeries series = context.get(name);
			if(series == null){
				series = new OHLCSeries(name);
				context.put(name, series);
				this.addSeries(series);
			}			
			series.add(UtilJFreeChart.createRegularTimePeriod(timePeriod, 
					record.getDate(config.getString("date"))),
					record.getDouble(config.getString("open")), 
					record.getDouble(config.getString("high")), 
					record.getDouble(config.getString("low")), 
					record.getDouble(config.getString("close")));
		}
		
	}

}
