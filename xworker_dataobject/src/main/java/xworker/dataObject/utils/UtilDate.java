/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.dataObject.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author zhangyuxiang
 *
 */
public class UtilDate {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
    
    static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    static SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
    static SimpleDateFormat timestampFormat = new SimpleDateFormat(TIMESTAMP);
    
    /**
     * 获取本星期的第一天。
     * 
     * @return
     */
    public static Date getWeekStart(){
    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.setTime(new Date());
    	
    	//把时分秒清空
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	
    	//设置本星期第一天
    	calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - calendar.get(Calendar.DAY_OF_WEEK) + 1);
    	
    	return calendar.getTime(); 
    }
    
    /**
     * 获取本星期的最后一天。
     * 
     * @return
     */
    public static Date getWeekEnd(){
    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.setTime(new Date());
    	
    	//把时分秒清空
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	
    	//设置本星期第一天
    	calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - calendar.get(Calendar.DAY_OF_WEEK) + 7);
    	
    	return calendar.getTime(); 
    }
    
    /**
     * 获取本月的第一天。
     * 
     * @return
     */
    public static Date getMonthStart(){
    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.setTime(new Date());
    	
    	//把时分秒清空
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	
    	//设置本星期第一天
    	calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - calendar.get(Calendar.DAY_OF_MONTH) + 1);
    	
    	return calendar.getTime(); 
    }
    
    /**
     * 获取本月的最后一天。
     * 
     * @return
     */
    public static Date getMonthEnd(){
    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.setTime(new Date());
    	
    	//把时分秒清空
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	
    	//本月的最后一天，也是下个月一号的前一天
    	calendar.set(Calendar.DATE, 1);
    	calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
    	calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);    	
    	
    	return calendar.getTime(); 
    }
    
    /**
     * 获取当年的第一天。
     * 
     * @return
     */
    public static Date getYearStart(){
    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.setTime(new Date());
    	
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	calendar.set(Calendar.DATE, 1);
    	calendar.set(Calendar.MONTH, 0);    	
    	
    	return calendar.getTime(); 
    }
    
    /**
     * 获取本年的最后一天。
     * @return
     */    
    public static Date getYearEnd(){
    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.setTime(new Date());
    	
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	calendar.set(Calendar.DATE, 31);
    	calendar.set(Calendar.MONTH, 11);    	
    	
    	return calendar.getTime(); 
    }
    
    /**
     * 返回指定日期所在月的天数。
     * 
     * @param date
     * @return
     */
    public static int getMonthDayCount(Date date){
    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.setTime(date);
    	
    	calendar.set(Calendar.DATE, 1);
    	calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
    	calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
    	return calendar.get(Calendar.DATE);
    }
    
    public static String getDateString(Date date, String format){
    	SimpleDateFormat f = new SimpleDateFormat(format);
    	return f.format(date);
    }
    
    /**
     * 是否是在一个时间段范围里。
     * 
     * @param adate
     * @param start
     * @param end
     * @return
     */
    public static boolean isBetween(Date adate, Date start, Date end){
    	if(adate == null) return false;
    	
    	if(start != null && start.getTime() <= adate.getTime()){
    		return true;
    	}
    	
    	if(end != null && end.getTime() >= adate.getTime()){
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * 获取一个时间加一个时间间隔（毫秒）后的时间。
     * 
     * @param date1
     * @param interval
     * @return
     */
    public static Date getDate(Date date1, long interval){
    	if(date1 == null) return null;
    	
    	long time = date1.getTime();
    	return new Date(time + interval);
    }
    
    /**
     * 获取一个时间加一个时间间隔（天）后的时间。
     * 
     * @param date
     * @param interval
     * @return
     */
    public static Date getDate(Date date, double interval){
    	if(date == null){
    		return null;
    	}
    	
    	return new Date(date.getTime() + (long)(interval * 86400000));
    }
    
    /**
     * 返回昨天
     * @return
     */
    public static Date getYesterday(){
    	Date date = new Date();
    	date = new Date(date.getTime() - 24 * 3600 * 1000);
    	try {
			return getDate(UtilDate.getDateString(date, "yyyy-MM-dd"), "yyyy-MM-dd");
		} catch (ParseException e) {
			return date;
		}
    }
    
    /**
     * 取得明天。
     * 
     * @return
     */
    public static Date getTomorrow(){
    	Date date = new Date();
    	date =  new Date(date.getTime() + 24 * 3600 * 1000);
    	try {
			return getDate(UtilDate.getDateString(date, "yyyy-MM-dd"), "yyyy-MM-dd");
		} catch (ParseException e) {
			return date;
		}
    }
        
    public static Date getDate(String dateStr) {
    	Date date = null;
    	if("current_date".equals(dateStr)){
    		date =  new Date();
    	}else{
    		try {
				date =  dateFormat.parse(dateStr);
			} catch (ParseException e) {
				//e.printStackTrace();
			}
    	}
    	return date;
    }
    
    public static Date getTime(String timeStr) throws ParseException{
    	Date date = null;
    	if("current_time".equals(timeStr)){
    		date = new Date();
    	}else{
    		date = timeFormat.parse(timeStr);
    	}
    	return date;
    }
    
    public static Date getTimestamp(String timeStr) throws ParseException{
    	Date date = null;
    	if("current_date".equals(timeStr)){
    		date =  new Date();
    	}else{	    
	    	try{
	    		date = timestampFormat.parse(timeStr);
	    	}catch(Exception e){
	    		try{
	    			date =  dateFormat.parse(timeStr);
	    		}catch(Exception ee){
	    			date = timestampFormat.parse("1970-01-01 " + timeStr);
	    		}
	    	}
    	}
    	return date;
    }
    
    public static Date getDate(String dateStr, String format) throws ParseException{
    	SimpleDateFormat f = new SimpleDateFormat(format);
    	return f.parse(dateStr);
    }
    
    /**
     * 取两个时间的时间差。
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static long getInterval(Date date1, Date date2){
    	//如果其中的一个时间为空，返回0
    	if(date1 == null || date2 == null){
    		return 0;
    	}
    	
    	long t1 = date1.getTime();
    	long t2 = date2.getTime();
    	
    	if(t1 > t2){
    		return t1 - t2;
    	}else{
    		return t2 - t1;
    	}
    }
}