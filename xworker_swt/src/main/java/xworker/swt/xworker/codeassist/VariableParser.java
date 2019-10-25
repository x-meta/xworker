package xworker.swt.xworker.codeassist;

import java.util.List;

import xworker.lang.VariableDesc;

/**
 * 根据变量和调用顺序解析最后的变量定义。
 * 
 * @author zyx
 *
 */
public class VariableParser {
	int index = 1;
	List<String> statements;
	VariableDesc var;
	
	public VariableParser(List<String> statements, VariableDesc var) {
		this.statements = statements;
		this.var = var;
	}
	
	public VariableDesc parse() {
		if(statements.size() == 1) {
			return var;
		}
				
		VariableDesc temp = var;
		for(int i = 1; i<statements.size(); i++) {
			VariableDesc next = temp.getFiledVariableDesc(statements.get(i));
			if(next == null) {
				next = temp.getMethodDesc(statements.get(i));
			}
			
			if(next == null) {
				break;
			}else {
				temp = next;
			}
		}
		
		return temp;
	}
}
