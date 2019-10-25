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
package xworker.swt.xworker.attributeEditor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.swt.design.Designer;
import xworker.swt.xworker.AttributeEditor;

public class TimeEditorCreator {
    public static Object create(ActionContext actionContext){
        World world = World.getInstance();
		Thing self = actionContext.getObject("self");
        
		//创建时间控件
		DateTime dateTime = new DateTime((Composite) actionContext.get("parent"), SWT.TIME | SWT.LONG | SWT.BORDER);
		
		//修改事件，如果控件能够触发该事件
		if(actionContext.get("modifyListener") != null){
		    //dateTime.addSelectionListener(modifyListener);
		}
		
		//设置布局数据
		dateTime.setLayoutData((GridData) actionContext.get("layoutData"));
		
		xworker.swt.form.GridData gridData = (xworker.swt.form.GridData) actionContext.get("gridData");
		Thing attribute = gridData.source;
		
		//创建并返回ActionContainer
		ActionContext ac = new ActionContext();
		ac.put("control", dateTime);
		String pattern = attribute.getString("pattern");
		if(pattern == null || pattern == ""){
		    pattern = "HH:mm:ss";
		}
		ac.put("pattern", pattern);
		Thing actionThing = world.getThing("xworker.swt.xworker.attributeEditor.TimeEditor/@actions1");
		ActionContainer actionContainer = actionThing.doAction("create", ac);
		
		Designer.attachCreator(dateTime, self.getMetadata().getPath(), actionContext);
		dateTime.setData(AttributeEditor.ACTIONCONTAINER, actionContainer);
		
		return dateTime;
	}
    
    public static void setValue(ActionContext actionContext) throws ParseException{
    	Object value = actionContext.get("value");
    	
    	Date timeValue = null;
    	if(value instanceof Date){
    	    timeValue = (Date) value;
    	}else if(value == null){
    		timeValue = new Date(0);
    	}else{
    	    value = value.toString();
    	    if(value != ""){
    	        SimpleDateFormat sf = new SimpleDateFormat((String) actionContext.get("pattern"));
    	        timeValue = sf.parse(value.toString());
    	    }
    	}

    	if(timeValue != null){
    		DateTime dt = (DateTime) actionContext.get("control");
    	    GregorianCalendar c = new GregorianCalendar();
    	    c.setTime(timeValue);
    	    dt.setHours(c.get(Calendar.HOUR_OF_DAY));
    	    dt.setMinutes(c.get(Calendar.MINUTE));
    	    dt.setSeconds(c.get(Calendar.SECOND));
    	}
    }
    
    public static Object getValue(ActionContext actionContext){
    	DateTime dt = (DateTime) actionContext.get("control");
    	GregorianCalendar c = new GregorianCalendar();
    	c.set(Calendar.HOUR_OF_DAY, dt.getHours());
    	c.set(Calendar.MINUTE, dt.getMinutes());
    	c.set(Calendar.SECOND, dt.getSeconds());
    	Date time = c.getTime();

    	SimpleDateFormat sf = new SimpleDateFormat((String) actionContext.get("pattern"));
    	return sf.format(time);
    }

}