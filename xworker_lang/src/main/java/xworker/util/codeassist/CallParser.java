package xworker.util.codeassist;

import java.util.ArrayList;
import java.util.List;

public class CallParser {
	/**
	 * 分析调用的语法树。
	 * 
	 * @param text
	 * @param offset
	 * @return
	 */
	public static List<CallNode> parse(String text, int offset){
		List<CallNode> nodes = new ArrayList<CallNode>();
		CallNode current = null;
		while(offset >= 0) {
			char ch = text.charAt(offset);
			if(current == null) {
				//开始处
				if(ch == '.' || ch == ' ') {
					offset--;
					continue;
				}else if(ch == '\n') {
					break;
				}else {
					current = CallNodeFactory.createNode(ch);
					if(current == null) {
						//终止
						break;
					}else {
						
						nodes.add(0, current);
					}
				}
			}else {
				if(current.isFinished(ch)) {
					offset --;
					current = null;
					if(ch == ';' || ch == '\n') {
						break;
					}else {
						continue;
					}
				}else {
					current = current.addChar(ch);
				}
			}
			
			offset--;
		}
		
		return nodes;
	}
	
	public static void test(String text) {
		List<CallNode> nodes = parse(text, text.length() - 1);
		
		System.out.println("Parse : " + text);
		for(CallNode node : nodes) {
			System.out.println("    " + node.getType() + ":" + node.getText());
		}
	}
	public static void main(String[] args) {
		try {
			//test("abcd");
			//test("abcd.abcd");
			//test("abcd.abcd.abcd");
			//test("abcd()");
			test("abcd(abcd())");
			test("abcd(abcd(), abcd(), abcd(abcd(),abcd(abcd(), abcd())),abcd())");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
