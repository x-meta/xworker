package xworker.swt.reacts;

import java.util.Stack;

public class DataReactorContext {
	/** 调用者的缓存，避免重复递归的调用，为什么被注释了？问于2020-05-12*/
	//private Map<Object, Object> context = new HashMap<Object, Object>();
	
	private static Stack<DataReactor> dataReactorStack = new Stack<DataReactor>();
	
	public DataReactorContext() {
	}
	
	public boolean isExists(Object object) {
		return false;
		/*
		System.out.println("isExists: " + object);

		if(context.get(object) != null) {
			return true;
		}else{
			context.put(object, object);
			return false;
		}*/
	}
	
	public void push(DataReactor dataReactor) {
		dataReactorStack.push(dataReactor);
	}
	
	public DataReactor pop() {
		return dataReactorStack.pop();
	}
	
	public DataReactor peek() {
		return dataReactorStack.peek();
	}
	
	public Stack<DataReactor> getStack(){
		return dataReactorStack;
	}
}	
