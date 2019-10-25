package xworker.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Patterns {
	List<Pattern> patterns = new ArrayList<Pattern>();
	boolean defaultValue;
	
	public Patterns(String patternStr, boolean defaultValue) {
		if(patternStr != null) {
			for(String exs : patternStr.split("[\n]")) {
				for(String ex : exs.split("[,]")) {
					ex = ex.trim();
					if("".equals(ex)) {
						continue;
					}
					
					patterns.add(Pattern.compile(ex));
				}
			}
		}
		
		this.defaultValue = defaultValue;
	}
	
	public boolean mattches(String path) {
		for(Pattern pattern : patterns) {
			if(pattern.matcher(path).matches()) {
				return true;
			}
		}
		
		return defaultValue;
	}
}
