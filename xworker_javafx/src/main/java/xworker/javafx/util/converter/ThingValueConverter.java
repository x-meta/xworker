package xworker.javafx.util.converter;

import java.util.Collections;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.util.StringConverter;

public class ThingValueConverter extends StringConverter<Object>{
	List<Thing> items;
	
	public ThingValueConverter(List<Thing> items) {
		this.items = items;
	}
	
	@Override
	public String toString(Object object) {
		if(object instanceof Thing) {
			return ((Thing) object).getMetadata().getLabel();
		}else {
			return String.valueOf(object);
		}
	}

	@Override
	public Object fromString(String string) {
		for(Thing item : items) {
			if(item.getMetadata().getLabel().equals(string)) {
				return item;
			}
		}
		
		return null;
	}

	public static ThingValueConverter create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		Thing thing = self.doAction("getThing", actionContext);
		ThingValueConverter tc = null;
		if(thing == null){
			tc = new ThingValueConverter(Collections.emptyList());
		}else{
			tc = new ThingValueConverter(thing.getChilds(self.getString("valueThingName")));
		}
		actionContext.g().put(self.getMetadata().getName(), tc);
		return tc;
	}
}
