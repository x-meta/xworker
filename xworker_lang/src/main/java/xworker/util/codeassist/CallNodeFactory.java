package xworker.util.codeassist;

import xworker.util.codeassist.callnodes.MethodNode;
import xworker.util.codeassist.callnodes.StringNode;
import xworker.util.codeassist.callnodes.VariableNode;

public class CallNodeFactory {
	public static final char[] consts = new char[] {' ', ',', '.', '\n', '(', ')', '{', '}', '[', ']'};
	
	public static CallNode createNode(char ch) {
		if(ch == '"') {
			return new StringNode();
		}else if(ch == ')') {
			return new MethodNode();
		}
		
		for(char c : consts) {
			if(c == ch) {
				return null;
			}
		}
		
		return new VariableNode(ch);
	}
}
