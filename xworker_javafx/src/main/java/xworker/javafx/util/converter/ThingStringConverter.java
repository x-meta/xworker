package xworker.javafx.util.converter;

import java.util.List;

import org.xmeta.Thing;

import javafx.util.StringConverter;

public class ThingStringConverter extends StringConverter<Object>{
	List<Thing> items;
	
	public ThingStringConverter(List<Thing> items) {
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
}
