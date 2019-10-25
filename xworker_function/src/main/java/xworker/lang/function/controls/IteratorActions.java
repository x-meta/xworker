package xworker.lang.function.controls;

import java.util.Iterator;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilJava;

import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionQuietRunner;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUtil;
import xworker.ui.function.FunctionStatus;

public class IteratorActions {
	public static Object it(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			return actionContext.get("It");
		}else{
			return request.getLocalVariable("It");
		}
	}
	
	public static Object These(ActionContext actionContext){
		return getLocalVar("These", actionContext);
	}
	
	public static Object This(ActionContext actionContext){
		return getLocalVar("This", actionContext);
	}
	
	public static Object That(ActionContext actionContext){
		return getLocalVar("That", actionContext);
	}
	
	public static Object Those(ActionContext actionContext){
		return getLocalVar("Those", actionContext);
	}
	
	private static Object getLocalVar(String name, ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			return actionContext.get(name);
		}else{
			return request.getLocalVariable(name);
		}
	}
	
	public static Object itIndex(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			return actionContext.get("It_Index");
		}else{
			return request.getLocalVariable("It_Index");
		}
	}
	
	public static Object itHasMore(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			return actionContext.get("It_Has_More");
		}else{
			return request.getLocalVariable("It_Has_More");
		}
	}
	
	public static Object iteratorObject(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			return actionContext.get("IteratorObject");
		}else{
			return request.getLocalVariable("IteratorObject");
		}
	}
	
	public static Object iterator(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			//安静执行时的遍历
			Thing iteratorThing = null;
			Thing doThing = null;
			for(Thing child : self.getChilds()){
				if("iteratorObject".equals(child.getMetadata().getName())){
					iteratorThing = child;
				}else if("do".equals(child.getMetadata().getName())){
					doThing = child;
				}
			}
			
			Object iteratorObject = FunctionQuietRunner.runFunction(iteratorThing, actionContext);
			Iterator<Object> iterator = UtilJava.getIterable(iteratorObject).iterator();
			int index = 0;
			while(iterator.hasNext()){
				try{
					Object obj = iterator.next();
					Bindings bindings = actionContext.push();
					bindings.put("IteratorObject", iteratorObject);
					bindings.put("It", obj);
					bindings.put("It_Index", index);
					bindings.put("It_Has_More", iterator.hasNext());
					
					Object result = FunctionQuietRunner.runFunction(doThing, actionContext);

					if(ActionContext.CONTINUE == actionContext.getStatus()){
						actionContext.setStatus(ActionContext.RUNNING);
					}else if(ActionContext.BREAK == actionContext.getStatus()){
						actionContext.setStatus(ActionContext.RUNNING);
						return null;
					}else if(ActionContext.RETURN == actionContext.getStatus() || 
			                    ActionContext.CANCEL == actionContext.getStatus() || 
			                    ActionContext.EXCEPTION == actionContext.getStatus()){
						return result;
			        }
					
					index++;
				}finally{
					actionContext.pop();
				}
			}
		}else{
			IneratorInfo info = (IneratorInfo) request.getCachedData();
			FunctionParameter lastParameter = ControlActions.getLastParameter(actionContext);
			
			FunctionParameter iterObjParameter = request.getParameter("iteratorObject");
			if(lastParameter == null){				
				//执行选取遍历对象的参数					
				request.executeParam(iterObjParameter);
				return null;
			}else if(lastParameter == iterObjParameter){
				//已获取要遍历的对象
				Iterator<Object> iterator = UtilJava.getIterable(lastParameter.getValue()).iterator();
				info = new IneratorInfo();
				info.IteratorObject = lastParameter.getValue();
				info.iterator = iterator;
				request.setCachedData(info);
			}
			
			if(request.getStatus() == FunctionStatus.BREAK || request.getStatus() == FunctionStatus.CONTINUE){
				request.setStatus(FunctionStatus.RUNNING);
				FunctionRequestUtil.callbakMyselfOk(request, request.getStatus(), null, actionContext);			
				return null;
			}else if(request.getStatus() == FunctionStatus.RETURN){
				request.setStatus(FunctionStatus.RUNNING);
				Object value = lastParameter != null ? lastParameter.getValue() : null;
				FunctionRequestUtil.callbakMyselfOk(request, FunctionStatus.RETURN, value, actionContext);
				return null;
			}
			
			//执行遍历
			if(info.iterator.hasNext()){
				FunctionParameter doParameter = request.getParameter("do");
					
				info.It = info.iterator.next();
				info.It_Index ++;
				info.It_Has_More = info.iterator.hasNext();
				
				request.putLocalVariable("IteratorObject", info.IteratorObject, actionContext);
				request.putLocalVariable("It", info.It, actionContext);
				request.putLocalVariable("It_Index", info.It_Index, actionContext);
				request.putLocalVariable("It_Has_More", info.It_Has_More, actionContext);
				
				request.executeParam(doParameter);
			}else{
				FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
			}
		}
		return null;
	}
	
	static class IneratorInfo{
		Object IteratorObject;
		Object It;
		int It_Index = -1;
		boolean It_Has_More;
		Iterator<Object> iterator;		
	}
}
