package xworker.chart.jfree.dataobject.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.time.Day;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class ReTimePeriodValuesCollection extends TimePeriodValuesCollection implements DataObjectReloadableDataset{
	private static final long serialVersionUID = -8811781197469143344L;
	private Thing config;
	
	public ReTimePeriodValuesCollection(Thing config){
		super();
		
		this.config = config;
	}
	
	@Override
	public void reload(List<DataObject> datas) {
		//先清空数据
		while(this.getSeriesCount() > 0){
			this.removeSeries(0);
		}
		
		String seriesName = config.getString("timeSeriesName");
		String timePeriod = config.getString("timePeriod");
		String timeName = config.getString("timeName");
		String timeValue = config.getString("timeValue");
		
		Map<String, TimePeriodValues> context = new HashMap<String, TimePeriodValues>();
		for(DataObject r : datas){
			String name = r.getString(seriesName);
			TimePeriodValues s = context.get(name);
			if(s == null){
				s = new TimePeriodValues(name);
				this.addSeries(s);
				context.put(name, s);
			}
			
			Date date = r.getDate(timeName);
			TimePeriod period = null;
			double value = r.getDouble(timeValue);
			
			if("FixedMillisecond".equals(timePeriod)){
				period = new FixedMillisecond(date);
			}else if("Hour".equals(timePeriod)){
				period = new Hour(date);
			}else if("Millisecond".equals(timePeriod)){
				period = new Millisecond(date);
			}else if("Minute".equals(timePeriod)){
				period = new Minute(date);
			}else if("Month".equals(timePeriod)){
				period = new Month(date);
			}else if("Quarter".equals(timePeriod)){
				period = new Quarter(date);
			}else if("Second".equals(timePeriod)){
				period = new Second(date);
			}else if("Week".equals(timePeriod)){
				period = new Week(date);
			}else if("Year".equals(timePeriod)){
				period = new Year(date);
			}else{
				period = new Day(date);
			}
			
			s.add(period, value);
		}	
	}

}
