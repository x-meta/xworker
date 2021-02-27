package xworker.util.limit;

import java.util.regex.Pattern;

public class RegexLimitConfig {
	Pattern pattern;
	boolean allow;
	
	public RegexLimitConfig(String regex, boolean allow){
		this.pattern = Pattern.compile(regex);
		this.allow = allow;
	}
	
	public boolean matches(String content){
		return pattern.matcher(content).matches();
	}
	
	public boolean isAllow(){
		return allow;
	}
}
