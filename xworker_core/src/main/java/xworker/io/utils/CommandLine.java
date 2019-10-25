package xworker.io.utils;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

/**
 * 命令行工具。
 * 
 * @author bookroom
 *
 */
public class CommandLine implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(CommandLine.class);
	
	Thing thing;
	ActionContext actionContext;
	Charset charset;
	
	public CommandLine(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		String ch = (String) thing.doAction("getCharset", actionContext);
		if(ch != null && !"".equals(ch.trim())){
			charset = Charset.forName(ch);
		}else{
			//charset = Charset.defaultCharset();
		}		
	}

	public void useConsole(){
		try{
			Console console = System.console();
			
			String getWelcomeMessage = (String) thing.doAction("getWelcomeMessage", actionContext);
			PrintWriter out = console.writer();
			if(getWelcomeMessage != null){
				out.println(getWelcomeMessage);
			}
			printPrompt(out);
			String line = null;
			while((line = console.readLine()) != null){
				Object result = thing.doAction("doLine", actionContext, "line", line, "thing", thing);
				if(result != null && !"".equals(result)){
					out.println(result);
					out.flush();
				}
				
				if(UtilData.isTrue(thing.doAction("isClosed", actionContext))){
					break;
				}
				printPrompt(out);
			}
			
			String exitMessage = (String) thing.doAction("getExitMessage", actionContext);
			if(exitMessage != null){
				out.println(exitMessage);
				out.flush();
			}
		}catch(Exception e){
			logger.error("CommandLine error, thing=" + thing.getMetadata().getPath(), e);
		}
	}
	
	@Override
	public void run() {
		try{
			if((Boolean) thing.doAction("isUseConsole", actionContext)){
				useConsole();
				return;
			}
			
			InputStream in = (InputStream) thing.doAction("getInputStream", actionContext);
			OutputStream out = (OutputStream) thing.doAction("getOutputStream", actionContext);
			PrintWriter pr = charset == null ? new PrintWriter(new OutputStreamWriter(out)) :
				new PrintWriter(new OutputStreamWriter(out, charset));
			
			BufferedReader br = charset == null ? new BufferedReader(new InputStreamReader(in)) :
				new BufferedReader(new InputStreamReader(in, charset));
			String line = null;
			
			String getWelcomeMessage = (String) thing.doAction("getWelcomeMessage", actionContext);
			if(getWelcomeMessage != null){
				pr.println(getWelcomeMessage);
			}
			printPrompt(pr);
			while((line = br.readLine()) != null){
				Object result = thing.doAction("doLine", actionContext, "line", line, "thing", thing);
				if(result != null && !"".equals(result)){
					pr.println(result);
					pr.flush();
				}
				
				if(UtilData.isTrue(thing.doAction("isClosed", actionContext))){
					break;
				}
				printPrompt(pr);
			}
			
			String exitMessage = (String) thing.doAction("getExitMessage", actionContext);
			if(exitMessage != null){
				pr.println(exitMessage);
				pr.flush();
			}
		}catch(Exception e){
			logger.error("CommandLine error, thing=" + thing.getMetadata().getPath(), e);
		}
	}
	
	private void printPrompt(PrintWriter pr){
		String prompt = (String) thing.doAction("getPrompt", actionContext);
		if(prompt != null){
			pr.print(prompt);
		}
		pr.flush();
	}
	
	public static void run(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		CommandLine cl = new CommandLine(self, actionContext);
		if(self.getBoolean("thread")){
			new Thread(cl, self.getMetadata().getLabel()).start();
		}else{
			cl.run();
		}
	}
	
	public static String doAction(ActionContext actionContext){
		return (String) actionContext.getString("line");
	}
	
	public static boolean isClosed(ActionContext actionContext){
		return false;
	}
}
