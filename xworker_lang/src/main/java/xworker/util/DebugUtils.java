package xworker.util;

import java.util.Stack;

public class DebugUtils {
	private static ThreadLocal<Stack<Boolean>> debugLocal = new ThreadLocal<Stack<Boolean>>();
	
	public static void pushDebug(boolean debug) {
		Stack<Boolean> debugStack = debugLocal.get();
		if(debugStack == null) {
			debugStack = new Stack<Boolean>();
			debugLocal.set(debugStack);
		}
		
		debugStack.push(debug);
	}
	
	public static void popDebug() {
		Stack<Boolean> debugStack = debugLocal.get();
		if(debugStack != null) {
			debugStack.pop();
		}		
	}
	
	public static boolean isDebug() {
		Stack<Boolean> debugStack = debugLocal.get();
		if(debugStack != null) {
			for(Boolean d : debugStack) {
				if(d) {
					return d;
				}
			}
		}	
		
		return false;
	}
}
