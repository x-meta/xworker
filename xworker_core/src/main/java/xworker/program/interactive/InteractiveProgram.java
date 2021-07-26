package xworker.program.interactive;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Request;
import xworker.lang.executor.Executor;

public class InteractiveProgram {
	//private static final String TAG = InteractiveProgram.class.getName();
	
	public static final byte TYPE_FUNCTION = 0;
	public static final byte TYPE_PARAMETER = 1;
	public static final byte TYPE_ONEXCEPTION = 2;
	public static final byte TYPE_ONRESULT = 3;
	
	public static final byte RESULTTYPE_SELF = 0;
	public static final byte RESULTTYPE_ONRESULT = 1;
	public static final byte RESULTTYPE_ONEXCEPTION = 2;
	
	byte type = TYPE_FUNCTION;
	Thing thing;
	ActionContext actionContext;
	InteractiveProgram parent = null;
	List<InteractiveProgram> parameters = new ArrayList<InteractiveProgram>();
	InteractiveProgram onResult = null;
	InteractiveProgram onException = null;	
	List<InteractiveProgram> childs = new ArrayList<InteractiveProgram>();
	
	boolean executed = false; //是否已经执行
	byte resultType = RESULTTYPE_SELF;
	Object result;  //执行的结果
	
	Request uiRequest = null;
	
	public InteractiveProgram(Thing thing, ActionContext actionContext, InteractiveProgram parent) {
		this.thing = thing;
		this.actionContext = actionContext;
		this.parent = parent;
		
		if(thing.isThing("xworker.program.interactive.InteractiveProgram")) {
			//函数
			this.type = TYPE_FUNCTION;						
		}else if(thing.isThing("xworker.program.interactive.InteractiveProgram/@Parameter")) {
			//参数
			this.type = TYPE_PARAMETER;
		}else if(thing.isThing("xworker.program.interactive.InteractiveProgram/@OnExcepion")) {
			//OnException
			this.type = TYPE_ONEXCEPTION;
		}else if(thing.isThing("xworker.program.interactive.InteractiveProgram/@OnResult")) {
			//OnResult
			this.type = TYPE_ONRESULT;
		}
		
		for(Thing childThing : thing.getChilds()) {
			InteractiveProgram child = new InteractiveProgram(childThing, actionContext, this);
			childs.add(child);
			
			switch(child.getType()) {
			case TYPE_PARAMETER:
				parameters.add(child);
				break;
			case TYPE_ONEXCEPTION:
				this.onException = child;
				break;
			case TYPE_ONRESULT:
				this.onResult = child;
				break;
			}
		}
	}
	
	/**
	 * 返回事物节点上设置的函数事物。
	 * 
	 * @param thing
	 * @return
	 */
	private Thing getFunctionThing(Thing thing) {
		for(Thing desc : thing.getDescriptors()) {
			if(desc.isThing("xworker.program.interactive.InteractiveFunction")) {
				return desc;
			}
			
			for(Thing ext : desc.getAllExtends()) {
				if(ext .isThing("xworker.program.interactive.InteractiveFunction")) {
					return desc;
				}
			}
		}
		
		return null;
	}
	
	public Thing getFunctionThing() {
		return getFunctionThing(thing);
	}
	
	public void setUnexecuted() {
		this.executed = false;
	}
	
	public void clear() {
		this.executed = false;
		for(InteractiveProgram child : childs) {
			child.clear();
		}
	}
	
	public void setFunctionThing(Thing functionThing) {
		//删除所有参数
		for(InteractiveProgram param : parameters) {
			thing.removeChild(param.getThing());
			childs.remove(param);
		}		
		parameters.clear();
		
		//设置新的描述者		
		switch(this.type) {
		case TYPE_FUNCTION:
			thing.set("descriptors", functionThing.getMetadata().getPath() + ",xworker.program.interactive.InteractiveProgram");
			break;
		case TYPE_ONEXCEPTION:
			thing.set("descriptors", functionThing.getMetadata().getPath() + ",xworker.program.interactive.InteractiveProgram/@OnExcepion");
			break;
		case TYPE_ONRESULT:
			thing.set("descriptors", functionThing.getMetadata().getPath() + ",xworker.program.interactive.InteractiveProgram/@OnResult");
			break;
		case TYPE_PARAMETER:
			thing.set("descriptors", functionThing.getMetadata().getPath() + ",xworker.program.interactive.InteractiveProgram/@Parameter");
			break;
		}
		
		//添加参数
		for(Thing paramThing : functionThing.getChilds("Parameter")) {
			Thing param = new Thing("xworker.program.interactive.InteractiveProgram/@Parameter");			
			param.put("parameter", paramThing.getMetadata().getPath());
			param.put("name", paramThing.getMetadata().getName());
			param.put("label", paramThing.getMetadata().getLabel());
			
			InteractiveProgram child = new InteractiveProgram(param, actionContext, this);
			childs.add(child);
			parameters.add(child);
		}
		
		save();
	}
	
	public void addOnException() {
		if(onException == null) {
			Thing exceptionThing = new Thing("xworker.program.interactive.InteractiveProgram/@OnExcepion");
			exceptionThing.set("name", "onException");
			thing.addChild(exceptionThing);
			onException = new InteractiveProgram(exceptionThing, actionContext, this);
			childs.add(onException);
		}
	}
	
	public void addOnResult() {
		if(onResult == null) {
			Thing resultThing = new Thing("xworker.program.interactive.InteractiveProgram/@OnResult");
			resultThing.set("name", "onResult");
			thing.addChild(resultThing);
			onResult = new InteractiveProgram(resultThing, actionContext, this);
			childs.add(onResult);
		}
	}
	
	public void removeOnException() {
		if(onException != null) {
			thing.removeChild(onException.getThing());
			childs.remove(onException);
			onException = null;
		}
		
		save();
	}
	
	public void removeOnResult() {
		if(onResult != null) {
			thing.removeChild(onResult.getThing());
			childs.remove(onResult);
			onResult = null;
		}
		
		save();
	}
	
	/**
	 * 移除当前节点。
	 */
	public void remove() {
		if(parent != null) {
			//删除事物
			parent.getThing().removeChild(this.getThing());
			
			//从父节点删除子节点
			parent.childs.remove(this);
			
			parent.parameters.remove(this);
			if(parent.onException == this) {
				parent.onException = null;
			}
			if(parent.onResult == this) {
				parent.onResult = null;
			}
			
			parent.save();
		} else {
			this.getThing().remove();
		}
	}
	
	public void save() {
		InteractiveProgram root = getRoot();
		root.getThing().save();
	}
	
	/**
	 * 是否已经准备好可以执行了。
	 * @return
	 */
	public boolean isReady() {
		return getFunctionThing(thing) != null; 
	}
	
	public InteractiveProgram getRoot() {
		InteractiveProgram root = parent;
		if(root != null) {
			while(root.parent != null) {
				root = root.parent;
			}
			
			return root;
		} else {		
			return this;
		}
	}
	
	public boolean isExecuted() {
		if(executed) {
			switch(resultType) {
			case RESULTTYPE_SELF:
				return true;
			case RESULTTYPE_ONRESULT:
				return onResult != null && onResult.isExecuted();
			case RESULTTYPE_ONEXCEPTION:
				return onException != null && onException.isExecuted();
			}			
		} 
		
		return false;
	}
	
	/**
	 * 执行，返回是否已经成功执行了。
	 */
	public void execute() {
		//检查是否已经设置了函数
		if(!isReady()) {
			//没有设置函数事物，请求编辑
			requestUI();
			return;
		}
		
		if(!isExecuted()) {
			//如果没有执行则执行
			for(InteractiveProgram param : parameters) {
				if(param.isExecuted() == false) {
					//参数未执行
					param.execute();
					//param执行时会回调，因此必须要返回
					return;
				}
			}
			
			Bindings bindings = actionContext.push();
			bindings.put("interactiveProgram", this);
			try {
				for(InteractiveProgram param : parameters) {
					bindings.put(param.getName(), param.getResult());
				}
				
				//执行
				result = thing.doAction("execute", actionContext);				
				executed = true;
				if(onResult == null) {
					resultType = RESULTTYPE_SELF;
				}else {
					resultType = RESULTTYPE_ONRESULT;
					bindings.put("result", result);
					onResult.execute();
				}
			}catch(Exception e) {
				result = e;				
				if(onException != null) {
					executed = true;
					resultType = RESULTTYPE_ONEXCEPTION;
					bindings.put("exception", e);
					onException.execute();		
					if(onException.isExecuted()) {
						executed = true;
					}
				} else {
					requestUI();
				}
			} finally {
				actionContext.pop();
			}
		}
		
		if(this.isExecuted() && this.getType() == TYPE_PARAMETER) {
			if(parent != null) {
				parent.execute();
			}
		}
	}
	
	public List<InteractiveProgram> getChilds(){
		return childs;
	}
	
	public Object getResult() {
		if(isExecuted()) {
			switch(resultType) {
			case RESULTTYPE_SELF:
				return result;
			case RESULTTYPE_ONRESULT:
				return onResult.getResult();
			case RESULTTYPE_ONEXCEPTION:
				return onException.getResult();
			}			
		} 
		
		return null;
	}
	
	public void requestUI() {
		InteractiveProgram root = getRoot();
		if(root.uiRequest == null || root.uiRequest.isFinished()) {
			Thing uiThing = World.getInstance().getThing("xworker.program.interactive.InteractiveUI").detach();
			uiThing.putAll(root.thing.getAttributes());

			root.uiRequest = Executor.requestUI(uiThing, actionContext);
			root.uiRequest.putVariable("interactiveProgram", root);
		}
		
		root.uiRequest.putVariable("interactiveNode", this);
	}
	
	public InteractiveProgram getParent() {
		return parent;
	}
	
	public byte getType() {
		return type;
	}
	
	public String getName() {
		return thing.getMetadata().getName();
	}
	
	public String getLabel() {
		return thing.getMetadata().getLabel();
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public InteractiveProgram getOnResult() {
		return onResult;
	}

	public InteractiveProgram getOnException() {
		return onException;
	}
	
	public static void run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		InteractiveProgram program = new InteractiveProgram(self, actionContext, null);
		program.execute();
	}
	
}
