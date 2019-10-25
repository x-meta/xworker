package xworker.swt.xworker.codeassist.callnodes;

import java.util.List;

import xworker.swt.xworker.codeassist.CallNode;

public class StringNode extends AbstractNode{
	StringBuilder sb = new StringBuilder();
	
	@Override
	public byte getType() {
		return CallNode.STRING;
	}

	@Override
	public String getText() {
		return sb.toString();
	}

	@Override
	public char getStartChar() {
		return '"';
	}

	@Override
	public char[] getEndChars() {
		return new char[] {'"', '\n'};
	}

	@Override
	public char getEnd2StartChar() {
		return '"';
	}

	@Override
	public CallNode addChar(char ch) {
		sb.insert(0, ch);
		return this;
	}


	@Override
	public List<CallNode> getChilds() {
		return null;
	}
	
}
