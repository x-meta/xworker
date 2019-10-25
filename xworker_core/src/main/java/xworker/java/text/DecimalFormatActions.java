package xworker.java.text;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

public class DecimalFormatActions {
	public static String format(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		FormatEntry format = getFormatEntry(self);
		
		Object value = self.doAction("getValue", actionContext);
		return format.format(value, actionContext);
	}
	
	public static Object parse(ActionContext actionContext) throws ParseException {
		Thing self = actionContext.getObject("self");
		FormatEntry format = getFormatEntry(self);
		
		String value = self.doAction("getValue", actionContext);
		return format.parse(value, actionContext);
	}
	
	private static FormatEntry getFormatEntry(Thing self) {
		String key = "__DecimalFormator__";
		FormatEntry format = (FormatEntry) self.getThreadData(key);
		if(format == null) {
			format = new FormatEntry(self);
			self.setThreadData(key, format);
		}
		
		return format;
	}
	
	
	public static class FormatEntry{
		ThingEntry entry;
		DecimalFormat formater;
	
		public FormatEntry(Thing thing) {
			entry = new ThingEntry(thing);
		}
		
		public void check(ActionContext actionContext) {
			if(entry.isChanged() || formater == null) {
				String pattern = entry.getThing().doAction("getPattern", actionContext);
				formater = new DecimalFormat(pattern);
			}
		}
		
		public String format(Object object, ActionContext actionContext) {			
			check(actionContext);
			
			return formater.format(object);
		}
		
		public Object parse(String value, ActionContext actionContext) throws ParseException {
			check(actionContext);
			
			return formater.parse(value);
		}
	}
}
