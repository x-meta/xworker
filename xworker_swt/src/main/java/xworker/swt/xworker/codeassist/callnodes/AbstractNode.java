package xworker.swt.xworker.codeassist.callnodes;

import xworker.swt.xworker.codeassist.CallNode;

public abstract class AbstractNode implements CallNode {
	public boolean isFinished(char ch) {
		for(char c : this.getEndChars()) {
			if(c == ch) {
				return true;
			}
		}
		
		return false;
	}
}
