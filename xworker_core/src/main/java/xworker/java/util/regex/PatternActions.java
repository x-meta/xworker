package xworker.java.util.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class PatternActions {
	public static Pattern create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String regex = self.doAction("getRegex", actionContext);
		int flags = 0;
		if(self.getBoolean("CANON_EQ")) {
			flags = flags | Pattern.CANON_EQ;
		}
		if(self.getBoolean("CASE_INSENSITIVE")) {
			flags = flags | Pattern.CASE_INSENSITIVE;
		}
		if(self.getBoolean("COMMENTS")) {
			flags = flags | Pattern.COMMENTS;
		}
		if(self.getBoolean("DOTALL")) {
			flags = flags | Pattern.DOTALL;
		}
		if(self.getBoolean("LITERAL")) {
			flags = flags | Pattern.LITERAL;
		}
		if(self.getBoolean("MULTILINE")) {
			flags = flags | Pattern.MULTILINE;
		}
		if(self.getBoolean("UNICODE_CASE")) {
			flags = flags | Pattern.UNICODE_CASE;
		}
		if(self.getBoolean("UNIX_LINES")) {
			flags = flags | Pattern.UNIX_LINES;
		}
		
		if(flags == 0) {
			return Pattern.compile(regex);
		}else {
			return Pattern.compile(regex, flags);
		}
	}
		
	public static boolean matches(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Pattern pattern = getPattern(self, actionContext);
		String input = self.doAction("getInput", actionContext);
		return pattern.matcher(input).matches();
	}
	
	public static Matcher matcher(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Pattern pattern = getPattern(self, actionContext);
		String input = self.doAction("getInput", actionContext);
		return pattern.matcher(input);
	}
	
	public static String[] split(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Pattern pattern = getPattern(self, actionContext);
		String input = self.doAction("getInput", actionContext);
		int limit = self.doAction("getLimit", actionContext);
		return pattern.split(input, limit);
	}
	
	private static Pattern getPattern(Thing self, ActionContext actionContext) {
		Object obj = self.doAction("getPattern", actionContext);
		if(obj instanceof Pattern) {
			return (Pattern) obj;
		}else if(obj instanceof Thing) {
			Thing thing = (Thing) obj;
			return thing.doAction("create", actionContext);
		}else if(obj instanceof String) {
			Thing thing = World.getInstance().getThing((String) obj);
			if(thing != null) {
				return thing.doAction("create", actionContext);
			}
		}
		
		String regex = self.doAction("getRegex", actionContext);
		return Pattern.compile(regex);
	}
}
