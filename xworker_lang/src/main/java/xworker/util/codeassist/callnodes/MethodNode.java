package xworker.util.codeassist.callnodes;

import xworker.util.codeassist.CallNode;

import java.util.ArrayList;
import java.util.List;


public class MethodNode extends AbstractNode{
	StringBuilder sb = new StringBuilder();
	List<CallNode> params = new ArrayList<CallNode>();
	CallNode paramNode = null;
	boolean paramPhrase = true;  //解析参数阶段
	
	@Override
	public byte getType() {
		return CallNode.METHOD;
	}

	@Override
	public String getText() {
		return sb.toString();
	}

	@Override
	public char getStartChar() {
		return ')';
	}

	@Override
	public char[] getEndChars() {
		return new char[] {'('};
	}

	@Override
	public char getEnd2StartChar() {
		return '(';
	}

	@Override
	public CallNode addChar(char ch) {
		if(paramPhrase == false) {
			if(ch != '(') {
				sb.insert(0, ch);
			}
			return this;
		}
		
		if(paramNode != null) {
			if(paramNode.isFinished(ch)) {
				paramNode = null;
			}else {
				paramNode.addChar(ch);
				return this;
			}
		}

		if(ch == ' ' || ch == ',') {
			return this;
		}
		
		if(ch == '(') {
			paramPhrase = false;
			return this;
		}
		
		if(paramPhrase) {
			//是参数
			paramNode = new ParameterNode();
			params.add(0, paramNode);
			paramNode = paramNode.addChar(ch);
			return this;
		}
		
		return null;
	}

	@Override
	public List<CallNode> getChilds() {
		return params;
	}
	
	public boolean isFinished(char ch) {
		if(paramPhrase == false ) {
			if(ch == '.' || ch == ' ' || ch == '\n' || ch == ',' || ch == ';' || ch == '(') {;
				return true;
			}
		}
		
		return false;
	}
	
}
