package xworker.util.limit;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.util.UtilData;

/**
 * 通过正则表达式判断是否限制。
 * 
 * @author zyx
 *
 */
public class RegexLimit {
	List<RegexLimitConfig> configs = new ArrayList<RegexLimitConfig>();
	
	public RegexLimit(String config){
		if(config != null){
			setConfig(config);
		}
	}
	
	/**
	 * 输入的内容是否被限制了。
	 * 
	 * @param input
	 * @return
	 */
	public boolean isLimited(String input){
		if(input == null){
			return false;
		}
		
		input = input.trim();
		if("".equals(input)){
			return false;
		}
		
		for(RegexLimitConfig cfg : configs){
			if(cfg.matches(input)){
				return cfg.isAllow();
			}
		}
		
		return false;
	}
	
	public void setConfig(String config){
		List<RegexLimitConfig> configs_ = new ArrayList<RegexLimitConfig>();
		if(config != null){
			for(String line : config.split("[\n]")){
				line = line.trim();
				if("".equals(line)){
					return;
				}
				
				String ll[] = line.split("[,]");
				if(ll.length == 2){
					RegexLimitConfig cfg = new RegexLimitConfig(ll[0].trim(), UtilData.getBoolean(ll[1].trim(), false));
					configs_.add(cfg);
				}
			}
		}
		
		this.configs = configs_;
	}
}
