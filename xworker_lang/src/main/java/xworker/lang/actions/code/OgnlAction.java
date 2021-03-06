package xworker.lang.actions.code;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.ClassResolver;
import ognl.Ognl;
import ognl.OgnlException;
import xworker.lang.executor.Executor;

public class OgnlAction {
	private static final String TAG = OgnlAction.class.getName(); 
	
	public static Object run(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//获取Ognl表达式
		Object exp = self.getData("exp");
		Long expTime = (Long) self.getData("expTime");
		if(expTime == null || expTime.longValue() != self.getMetadata().getLastModified()){
			String expression = self.getStringBlankAsNull("expression");
			if(expression == null){
				Executor.warn(TAG, "Ognl expression is null, action=" + self.getMetadata().getPath());
				return null;
			}
			exp = Ognl.parseExpression(expression);
			self.setData("exp", exp);
			self.setData("expTime", self.getMetadata().getLastModified());
		}
		
		//Ognl.getC
		//Ognl.setClassResolver(context, classResolver)		
		return OgnlUtil.getValue(exp, actionContext);
	}
	
	public static class OgnlClassResolver implements ClassResolver{

		@SuppressWarnings("rawtypes")
		@Override
		public Class classForName(String className, Map context)
				throws ClassNotFoundException {
			return World.getInstance().getClassLoader().loadClass(className);
		}
		
	}
}
