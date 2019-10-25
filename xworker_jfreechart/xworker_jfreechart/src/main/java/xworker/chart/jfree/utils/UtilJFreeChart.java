package xworker.chart.jfree.utils;

import java.util.Date;

import org.jfree.data.time.Day;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;

public class UtilJFreeChart {
	/**
	 * 创建时间间隔。
	 * 
	 * @param type
	 * @param time
	 * @return
	 */
	public static RegularTimePeriod createRegularTimePeriod(String type, Date time){
		if("Day".equals(type)){
			return new Day(time);			
		}else if("FixedMillisecond".equals(type)){
			return new FixedMillisecond(time);
		}else if("Hour".equals(type)){
			return new Hour(time);
		}else if("Minute".equals(type)){
			return new Minute(time);
		}else if("Month".equals(type)){
			return new Month(time);
		}else if("Quarter".equals(type)){
			return new Quarter(time);
		}else if("Second".equals(type)){
			return new Second(time);
		}else if("Week".equals(type)){
			return new Week(time);
		}else if("Year".equals(type)){
			return new Year(time);
		}else{
			return new Millisecond(time);
		}
	}
}
