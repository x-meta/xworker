package xworker.util.codeassist.callnodes;

import java.util.ArrayList;
import java.util.List;

import xworker.swt.xworker.codeassist.CallNode;
import xworker.swt.xworker.codeassist.CallNodeFactory;

public class ParameterNode extends AbstractNode{
	CallNode callNode;
	
	@Override
	public byte getType() {
		return CallNode.PARAMETER;
	}

	@Override
	public String getText() {
		if(callNode == null) {
			return "";
		}else {
			return callNode.getText();
		}
	}

	@Override
	public char getStartChar() {
		return 0;
	}

	@Override
	public char[] getEndChars() {
		return new char[] {'(', ','};
	}

	@Override
	public char getEnd2StartChar() {
		return 0;
	}

	@Override
	public CallNode addChar(char ch) {
		if(isFinished(ch)) {
			return null;
		}
		
		if(callNode != null) {
			callNode = callNode.addChar(ch);
			return this;
		}else if(ch == ' ' || ch == '\n') {
			return null;
		}else {
			callNode = CallNodeFactory.createNode(ch);
			if(callNode == null) {
				return null;
			}else {
				return this;
			}
		}
	}

	@Override
	public List<CallNode> getChilds() {
		List<CallNode> nodes = new ArrayList<CallNode>();
		if(callNode != null) {
			nodes.add(callNode);
		}
		return nodes;
	}

	public boolean isFinished(char ch) {
		if(callNode != null) {
			return callNode.isFinished(ch);
		}else if(ch == ',' || ch == '(' || ch == '\n' || ch == ' ') {
			return true;
		}
		
		return false;
	}
}
