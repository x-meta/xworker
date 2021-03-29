package xworker.util.codeassist.callnodes;

import java.util.List;

import xworker.swt.xworker.codeassist.CallNode;

public class VariableNode extends AbstractNode{
	StringBuilder sb = new StringBuilder();
	
	public VariableNode(char ch) {
		addChar(ch);
	}
	
	@Override
	public byte getType() {
		return CallNode.VARIABLE;
	}

	@Override
	public String getText() {
		return sb.toString();
	}

	@Override
	public char getStartChar() {
		return 0;
	}

	@Override
	public char[] getEndChars() {
		return new char[] {'.', ',', ' ', '\'', '"', '\n', ')', ']', ';'};
	}

	@Override
	public char getEnd2StartChar() {
		return 0;
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
