package xworker.security;

import java.util.regex.Pattern;

/**
 * 内容路径校验器。
 * 
 * @author zyx
 *
 */
public class PathChecker extends SecurityChecker{
	Pattern pathPattern;
	String pathRegex;
	
	public PathChecker(String pathRegex) {
		if(pathRegex == null){
			this.pathPattern = Pattern.compile("([\\s\\S]*)");
		}else{
			this.pathPattern = Pattern.compile(pathRegex);
		}
	}
	
	public boolean accept(String path) {
		if(pathPattern == null || !pathPattern.matcher(path).matches()){
			return false;
		}
		
		return true;
	}

	public String getPathRegex() {
		return pathRegex;
	}
}
