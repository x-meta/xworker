package xworker.util.codeassist;

import java.util.List;

public interface CallNode {
	/** 字符串 */
	public static final byte STRING = 0;
	/** 方法 */
	public static final byte METHOD = 1;
	/** 参数 */
	public static final byte PARAMETER = 2;
	/** 变量 */
	public static final byte VARIABLE = 3;
	
	/**
	 * 调用节点类型。
	 * 
	 * @return
	 */
	public byte getType();
	
	/**
	 * 文本。
	 * 
	 * @return
	 */
	public String getText();
	
	/**
	 * 开始字符，标识该节点的开始。
	 * 
	 * @return
	 */
	public char getStartChar();
	
	/**
	 * 截至的字符列表。
	 * 
	 * @return
	 */
	public char[] getEndChars();
	
	/**
	 * 开始和截至成对的符号，比如()，如果存在。
	 * 
	 * @return
	 */
	public char getEnd2StartChar();
	
	/**
	 * 添加文本，在开头位置。
	 * 
	 */
	public CallNode addChar(char ch);
	
	/**
	 * 返回子节点调用列表。
	 * 
	 * @return
	 */
	public List<CallNode> getChilds();
	
	/**
	 * 是否已经结束了。
	 * 
	 * @return
	 */
	public boolean isFinished(char ch);
}
