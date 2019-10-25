package xworker.ui.function;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class FunctionQuietRunner {
	/**
	 * 静默执行交互函数，没有交互界面，静默执行时request变量设置为null。
	 * 
	 * @param functionThing
	 * @return
	 */
	public static Object runFunction(Thing functionThing, ActionContext actionContext){
		Bindings bindings = actionContext.push();
		bindings.put("request", null);		
		try{
			//函数的描述者
			Thing descriptor = functionThing.getDescriptor();
			if(descriptor != null && descriptor.getBoolean("executeParameterMySelf")){
				return functionThing.doAction("doFunction", actionContext);
			}else{
				for(Thing paramThing : functionThing.getChilds()){
					//参数的描述者
					Thing paramDesc = null;
					for(Thing param : descriptor.getChilds()){
						if(paramThing.getMetadata().getName().equals(param.getMetadata().getName())){
							paramDesc = param;
							break;
						}
					}
					
					if(paramDesc != null && paramDesc.getBoolean("callByFuntion")){
						continue;
					}
					
					Object value = runFunction(paramThing, actionContext);
					bindings.put(paramThing.getMetadata().getName(), value);
				}
				
				return functionThing.doAction("doFunction", actionContext);
			}
		}finally{
			actionContext.pop();
		}
	}
}
