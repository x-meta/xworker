package xworker.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;


public class StringUtils {
	private static String[] emptyString = new String[]{}; 
	/**
	 * 对org.xmeta.util.StringUtil的补充。
	 * 
	 * @param value
	 * @param actionContext
	 * @return
	 * @throws IOException 
	 */
	public static String getString(String value, ActionContext actionContext) throws IOException{
		if(value == null || "".equals(value)){
			return value;
		}else if(value.startsWith("template:")){
			String template = value.substring(9, value.length());
			try{
				return UtilTemplate.processString(actionContext, template);
			}catch(Exception e){
				throw new ActionException("Get string from template error", e);
			}
		}else if(value.startsWith("descurl:")) {
			String thingPath = value.substring(8, value.length());
			return XWorkerUtils.getThingDescUrl(thingPath);
		}else{
			return UtilString.getString(value, actionContext);
		}
	}
	
	public static String getString(Thing thing, String attribute, ActionContext actionContext) throws IOException{
		String value = thing.getString(attribute);
		return getString(value, actionContext);
	}
	
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    //copyed from apacache common-lang
    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }
    
    //copyed from apacache common-lang
    @SuppressWarnings({ "rawtypes", "unchecked"})
	private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return emptyString;
        }
        List list = new ArrayList();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    
    /**
     * 返回分割符，尤其是csv的分隔符。默认返回英文逗号，如果是\t返回Tab，如果是\s返回空格，其它取第一个字符。
     * 
     * @param d
     * @return
     */
    public static char getDelimiter(String d){
    	if( d == null || "".equals(d)){
    		return ',';
    	}
    	
    	String dl = d.toLowerCase();
    	if("\\t".equals(dl)){
    		return '\t';
    	}else if("\\s".equals(dl)){
    		return ' ';
    	}else{
    		return d.charAt(0);
    	}
    }
}
