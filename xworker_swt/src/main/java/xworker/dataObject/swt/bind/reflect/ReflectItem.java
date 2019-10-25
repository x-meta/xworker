package xworker.dataObject.swt.bind.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.dataObject.DataObject;
import xworker.dataObject.swt.bind.DataObjectBinder;
import xworker.dataObject.swt.bind.WidgetBinderItem;

public class ReflectItem  extends WidgetBinderItem{
	private static Logger logger = LoggerFactory.getLogger(ReflectItem.class);
	
	Object defaultValue;
	Method getMethod;
	Method setMethod;
	Class<?> paramType;
	
	public ReflectItem(Thing thing,  Widget widget, ActionContext actionContext, String getMethod, String setMethod, Class<?> paramType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super(thing, widget, actionContext);
		
		this.getMethod = widget.getClass().getMethod(getMethod, new Class<?>[] {});
		this.setMethod = widget.getClass().getMethod(setMethod, new Class<?>[] {paramType});
		defaultValue = this.getMethod.invoke(widget, new Object[] {});
		this.paramType = paramType;
	}

	@Override
	public void doUpdate(DataObjectBinder binder, DataObject dataObject, Object value) {
		if(widget == null || widget.isDisposed()) {
			return;
		}
		
		if(value == null) {
			value = defaultValue;
		}
		
		//基本常量先转换
		if(int.class == paramType) {
			value = UtilData.getInt(value, 0);
		}else if(double.class == paramType){
			value = UtilData.getDouble(value, 0);
		}else if(float.class == paramType) {
			value = UtilData.getFloat(value, 0);
		}else if(byte.class == paramType) {
			value = UtilData.getByte(value, (byte) 0);
		}else if(short.class == paramType) {
			value = UtilData.getShort(value, (short) 0);
		}else if(long.class == paramType) {
			value = UtilData.getLong(value, 0);
		}else if(boolean.class == paramType) {
			value = UtilData.getBoolean(value, (Boolean) defaultValue);
		}else if(String.class == paramType) {
			if(value == null) {
				value = "";
			}else {
				value = String.valueOf(value);
			}
		}else if(Date.class == paramType) {
			value = UtilData.getDate(value, (Date) defaultValue);
		}
		
		try {
			setMethod.invoke(widget, new Object[] {value});
		}catch(Exception e) {
			logger.error("set attribute error, path=" + thing.getMetadata().getPath() + ", method=" + setMethod, e);
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
	}

	@Override
	public Object getValue() {
		try {
			return this.getMethod.invoke(widget, new Object[] {});
		}catch(Exception e) {
			logger.error("get value error, path=" + thing.getMetadata().getPath() + ", method=" + setMethod, e);
			return null;
		}
	}
}
