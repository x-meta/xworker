package xworker.io;

import java.io.Console;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class ConsoleActions {
	public static Object run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//初始化Console
		Console console = System.console();
		if(console == null) {
			throw new ActionException("Console is null, thing=" + self.getMetadata().getPath());
		}
		actionContext.peek().put("console", console);
		actionContext.peek().put("consoleBindings", actionContext.peek()); //sonole节点执行的变量保存到这里
				
		String welcomeMessage = self.doAction("getWelcomeMessage", actionContext);
		if(welcomeMessage != null) {
			console.writer().println(welcomeMessage);
		}
		
		//执行Console
		Object result = null;
		List<Thing> childs = self.getChilds();
		if(childs.size() == 0) {
			return null;
		}else if(childs.size() == 1) {
			result = childs.get(0).doAction("run", actionContext);
		}else {
			//按照选择器执行
			result = runNodeExecutor(actionContext);
		}
		while(true) {
			if(result instanceof ConsoleNode) {
				ConsoleNode node = (ConsoleNode) result;
				result = node.run();
			}else {
				return result;
			}
		}		
	}
	
	public static Object runNodeSelector(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Console console = actionContext.getObject("console");
		String message = self.doAction("getMessage", actionContext);
		if(message != null) {
			console.writer().println(message);
		}
		
		if(self.getChilds().size() == 0) {
			return null;
		}

		while(true) {
			for(Thing child : self.getChilds()) {
				console.printf("%1$-20s :%2$s\n", child.getMetadata().getName(), child.getMetadata().getLabel());
			}
			String cmd = console.readLine("%s:", self.doAction("getSelectTips", actionContext));
			
			if(cmd != null) {
				cmd = cmd.trim();
				for(Thing child : self.getChilds()) {
					if(child.getMetadata().getName().equals(cmd)) {
						return child.doAction("run", actionContext);
					}
				}
			}
		}
	}
	
	public static Object runNodeExecutor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		if(self.getChilds().size() == 0) {
			return null;
		}
		
		Object result = null;
		List<Thing> childs = self.getChilds();
		for(int i=0; i<childs.size(); i++) {
			result = childs.get(i).doAction("run", actionContext);
			if(result instanceof ConsoleNode) {
				return result;
			}
		}
		
		return result;
	}
	
	public static Object runReadLine(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Console console = actionContext.getObject("console");
		
		while(true) {
			String line = console.readLine("%s", self.doAction("getMessage", actionContext));					
			if((line == null || "".equals(line.trim())) && UtilData.isTrue(self.doAction("isBlankable", actionContext)) == false) {
				//不能为空，重新读取
				continue;
			}
			
			Bindings bindings = actionContext.getObject("consoleBindings");
			bindings.put(self.getMetadata().getName(), line);
			return line;
		}
	}
	
	public static Object runReadPassword(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Console console = actionContext.getObject("console");
		
		while(true) {
			char[] password = console.readPassword("%s", self.doAction("getMessage", actionContext));					
			if((password == null || password.length == 0) && UtilData.isTrue(self.doAction("isBlankable", actionContext)) == false) {
				//不能为空，重新读取
				continue;
			}
			
			Bindings bindings = actionContext.getObject("consoleBindings");
			bindings.put(self.getMetadata().getName(), password);
			return password;
		}
	}
	
	
	public static Object runReadText(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Console console = actionContext.getObject("console");
		
		String text = null;
		while(true) {
			String line = null;
										
			if(text == null) {
				line = console.readLine("%s", self.doAction("getMessage", actionContext));
				text = line;
			}else {
				line = console.readLine();
				if(line != null && line.trim().equals(":exit")) {
					break;
				}
				text = text + "\n" + line;
			}
		}
		
		Bindings bindings = actionContext.getObject("consoleBindings");
		bindings.put(self.getMetadata().getName(), text);
		return text;
	}
	
	public static void runExit(ActionContext actionContext) {
		System.exit(0);
	}
	
	public static void runPrintr(ActionContext actionContext) {
		Console console = actionContext.getObject("console");
		console.writer().print("\r");
	}
	
	public static Object runReadInt(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Console console = actionContext.getObject("console");
		
		while(true) {
			int value = 0;
			String line = console.readLine("%s", self.doAction("getMessage", actionContext));					
			if((line == null || "".equals(line.trim()))) {
				if(UtilData.isTrue(self.doAction("isBlankable", actionContext)) == false) {
					//不能为空，重新读取					
					continue;
				}
			}
			
			try {
				value = Integer.parseInt(line.trim());
			}catch(Exception e) {
				console.printf("%s\n", e.getMessage());
				continue;
			}
			Bindings bindings = actionContext.getObject("consoleBindings");
			bindings.put(self.getMetadata().getName(), value);
			return line;
		}
	}
	
	public static Object runReadLong(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Console console = actionContext.getObject("console");
		
		while(true) {
			long value = 0;
			String line = console.readLine("%s", self.doAction("getMessage", actionContext));					
			if((line == null || "".equals(line.trim()))) {
				if(UtilData.isTrue(self.doAction("isBlankable", actionContext)) == false) {
					//不能为空，重新读取					
					continue;
				}
			}
			
			try {
				value = Long.parseLong(line.trim());
			}catch(Exception e) {
				console.printf("%s\n", e.getMessage());
				continue;
			}
			Bindings bindings = actionContext.getObject("consoleBindings");
			bindings.put(self.getMetadata().getName(), value);
			return line;
		}
	}
	
	public static Object runReadFloat(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Console console = actionContext.getObject("console");
		
		while(true) {
			float value = 0;
			String line = console.readLine("%s", self.doAction("getMessage", actionContext));					
			if((line == null || "".equals(line.trim()))) {
				if(UtilData.isTrue(self.doAction("isBlankable", actionContext)) == false) {
					//不能为空，重新读取					
					continue;
				}
			}
			
			try {
				value = Float.parseFloat(line.trim());
			}catch(Exception e) {
				console.printf("%s\n", e.getMessage());
				continue;
			}
			Bindings bindings = actionContext.getObject("consoleBindings");
			bindings.put(self.getMetadata().getName(), value);
			return line;
		}
	}
	
	public static Object runReadDouble(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Console console = actionContext.getObject("console");
		
		while(true) {
			double value = 0;
			String line = console.readLine("%s", self.doAction("getMessage", actionContext));					
			if((line == null || "".equals(line.trim()))) {
				if(UtilData.isTrue(self.doAction("isBlankable", actionContext)) == false) {
					//不能为空，重新读取					
					continue;
				}
			}
			
			try {
				value = Double.parseDouble(line.trim());
			}catch(Exception e) {
				console.printf("%s\n", e.getMessage());
				continue;
			}
			Bindings bindings = actionContext.getObject("consoleBindings");
			bindings.put(self.getMetadata().getName(), value);
			return line;
		}
	}
	
	public static Object runReadChar(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Console console = actionContext.getObject("console");
		
		while(true) {
			char value = 0;
			String line = console.readLine("%s", self.doAction("getMessage", actionContext));					
			if((line == null || "".equals(line.trim()))) {
				if(UtilData.isTrue(self.doAction("isBlankable", actionContext)) == false) {
					//不能为空，重新读取					
					continue;
				}
			}
			
			try {
				value = (char) Integer.parseInt(line.trim());
			}catch(Exception e) {
				console.printf("%s\n", e.getMessage());
				continue;
			}
			Bindings bindings = actionContext.getObject("consoleBindings");
			bindings.put(self.getMetadata().getName(), value);
			return line;
		}
	}
	
	public static Object runReadByte(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Console console = actionContext.getObject("console");
		
		while(true) {
			byte value = 0;
			String line = console.readLine("%s", self.doAction("getMessage", actionContext));					
			if((line == null || "".equals(line.trim()))) {
				if(UtilData.isTrue(self.doAction("isBlankable", actionContext)) == false) {
					//不能为空，重新读取					
					continue;
				}
			}
			
			try {
				value = Byte.parseByte(line.trim());
			}catch(Exception e) {
				console.printf("%s\n", e.getMessage());
				continue;
			}
			Bindings bindings = actionContext.getObject("consoleBindings");
			bindings.put(self.getMetadata().getName(), value);
			return line;
		}
	}
	
	public static Object runGoto(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing node = self.doAction("getNode", actionContext);
		if(node == null) {
			throw new ActionException("Node is null, path=" + self.getMetadata().getPath());
		}else {
			return new ConsoleNode(node, actionContext);
		}
	}
	
	public static Object runDoAction(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String message = self.doAction("getMessage", actionContext);
		if(message != null && !"".equals(message)) {
			Console console = actionContext.getObject("console");
			String it = console.readLine("%s", message);
			actionContext.peek().put("it", it);			
		}
		
		Object result = self.doAction("doAction", actionContext);
		//System.out.println("result=" + result);
		if(result instanceof String) {
			Thing r = self.getThing("Result@0");
			if(r != null) {
				for(Thing child : r.getChilds()) {
					if(child.getMetadata().getName().equals(result)) {
						return new ConsoleNode(child, actionContext);
					}
				}
			}
		}
		
		return result;
	}
	
	public static Object runPrintln(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Console console = actionContext.getObject("console");
		console.writer().println(self.doAction("getMessage", actionContext));
		Thing node = self.doAction("getNode", actionContext);
		if(node == null) {
			throw null;
		}else {
			return new ConsoleNode(node, actionContext);
		}
	}
	
	public static Object runPrintf(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Console console = actionContext.getObject("console");
		Object[] objs = self.doAction("getMessages", actionContext);
		String format = self.doAction("getFormat", actionContext);
		console.printf(format, objs);
		
		Thing node = self.doAction("getNode", actionContext);
		if(node == null) {
			throw null;
		}else {
			return new ConsoleNode(node, actionContext);
		}
	}
}
