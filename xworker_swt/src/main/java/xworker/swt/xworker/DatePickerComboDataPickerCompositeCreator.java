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
package xworker.swt.xworker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;

import xworker.swt.ActionContainer;
import xworker.swt.util.UtilResource;

public class DatePickerComboDataPickerCompositeCreator {
    public static void initDateTable(ActionContext actionContext){
        Date date = (Date) actionContext.get("date");
        if(date == null){
            date = new Date();
        }
        //使用dateTable保存日期
        Table dateTable = (Table) actionContext.get("dateTable");
        dateTable.setData(date);
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        
        //时间
        DateTime dateTime = (DateTime) actionContext.get("dateTime");
        if(dateTime != null && !dateTime.isDisposed()){
            dateTime.setHours(calendar.get(Calendar.HOUR_OF_DAY));
            dateTime.setMinutes(calendar.get(Calendar.MINUTE));
            dateTime.setSeconds(calendar.get(Calendar.SECOND));
        }
        
        //指定日期的星期和所在星期几
        int week = calendar.get(Calendar.WEEK_OF_MONTH) - 1;
        if(week < 0){
            week = 0;
        }
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        if(weekDay == 7){
        	weekDay = 0;
        	week++;
        }
        int day = calendar.get(Calendar.DATE);
        
        //取上月和本月最大天数
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        int currentMonthMaxDays =  calendar.get(Calendar.DATE);
        
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        int preMonthMaxDays = calendar.get(Calendar.DATE);
        
        int[][] weekDays = new int[6][7];
        weekDays[week][weekDay] = day; //指定日期
        
        int[] preMonthIndex = new int[2];
        int[] nextMonthIndex = new int[2];
         
        //指定日期之前
        int _day = day;
        for(int i=week; i>=0; i--){
        	int _weekDay = weekDay;
        	if(i < week){
        		_weekDay = 6;
        	}
        	for(int n=_weekDay; n>=0; n--){
        		weekDays[i][n] = _day + 1;
        		_day--;
        		if(_day < 0){
        		    preMonthIndex[0] = i;
        		    preMonthIndex[1] = n-1;
        		    if(preMonthIndex[1] < 0){
        		        preMonthIndex[1] = 6;
                        preMonthIndex[0]--;
        		    }
        			_day = preMonthMaxDays - 1;
        		}
        	}
        }
        
        //指定日期之后
        _day = day;
        for(int i=week; i<6; i++){
        	int _weekDay = weekDay;
        	if(i > week){
        		_weekDay = 0;
        	}
        	for(int n=_weekDay; n<7; n++){
        		weekDays[i][n] = _day + 1;
        		_day++;
        		if(_day >= currentMonthMaxDays){
        			_day = 0;
        			nextMonthIndex[0] = i;
        			nextMonthIndex[1] = n + 1;
        			if(nextMonthIndex[1] == 7){
        			    nextMonthIndex[1] = 0;
        			    nextMonthIndex[0] ++;
        			}
        		}
        	}
        }
        
        dateTable.clearAll();
        for(int i=0; i<6; i++){
            TableItem item = dateTable.getItems()[i];
            item.setText(intArrayToStringArray(weekDays[i]));
        }
        
        //设置光标为指定日期
        //dateTable.pack();
        dateTable.showItem(dateTable.getItems()[week]);
        if(weekDay == 0){
            weekDay = 7;
            week = week -1;
        }
        
        TableCursor dateTableCursor = (TableCursor) actionContext.get("dateTableCursor");
        dateTableCursor.setSelection(week, weekDay-1);
        dateTableCursor.setVisible(true);
        //dateTableCursor.setSelection(dateTable.getItems()[week], weekDay);
        
        //World world = World.getInstance();
        //设置上月和下月的日期颜色
        int[][] itemMonths = new int[6][7];
        //Thing style = (Thing) world.get("xworker.swt.style.StyleSet/@Style");        
        Color color = (Color) UtilResource.createResource("#C0C0C0", "xworker.swt.graphics.Color", "rgb", actionContext);
        for(int i=preMonthIndex[0]; i>=0; i--){
            int _dayIndex = preMonthIndex[1];
            if(i < preMonthIndex[0]){
                _dayIndex = 6;
            }
            for(int n=_dayIndex; n>=0; n--){
                TableItem item = dateTable.getItems()[i];
                item.setForeground(n, color);
                itemMonths[i][n] = -1;
            }
        }
        for(int i=nextMonthIndex[0]; i<6; i++){
            int _dayIndex = nextMonthIndex[1];
            if(i > nextMonthIndex[0]){
                _dayIndex = 0;
            }
            for(int n=_dayIndex; n<7; n++){
                TableItem item = dateTable.getItems()[i];
                item.setForeground(n, color);
                itemMonths[i][n] = 1;
            }
        }
        dateTable.setData("itemMonths", itemMonths);
        
        //if(actionContext.get("setTextData") != null && (Boolean) actionContext.get("setTextData") != false){
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            actionContext.getScope(0).put("init", true);
            ((Spinner) actionContext.get("yearText")).setSelection(year);
            ((Combo) actionContext.get("monthCombo")).select(month);
            actionContext.getScope(0).put("init", false);
        //}
    }

    public static String[] intArrayToStringArray(int[] ints){
    	String[] strs = new String[ints.length];
    	for(int i=0; i<ints.length; i++){
    		strs[i] = String.valueOf(ints[i]);
    	}
    	
    	return strs;
    }
    
    public static void init(ActionContext actionContext){
    	Table dateTable = actionContext.getObject("dateTable");
    	ActionContainer actions = actionContext.getObject("actions");
    	TableCursor dateTableCursor = actionContext.getObject("dateTableCursor");
    	
    	//初始化6*7的表格数据
    	for(int i=0; i<6; i++){
    	    new TableItem(dateTable, SWT.NONE);    
    	}

    	actions.doAction("initDateTable", actionContext);
    	dateTableCursor.setFocus();
    }
}