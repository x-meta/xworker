package xworker.ai.chatterbean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilThing;

import bitoflife.chatterbean.AliceBot;
import bitoflife.chatterbean.parser.AliceBotParser;
import bitoflife.chatterbean.parser.AliceBotParserConfigurationException;
import bitoflife.chatterbean.parser.AliceBotParserException;
import bitoflife.chatterbean.util.Searcher;
import freemarker.template.TemplateException;
import xworker.util.UtilTemplate;

/**
 * 注意事项：xworker改造了charrterBean，action这个元素是在Charrterbean这个项目里加入的。
 * 
 * @author zyx
 *
 */
public class Alice {
	private static Logger logger = LoggerFactory.getLogger(Alice.class);
	
	public static String run(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		AliceBot bot = (AliceBot) self.doAction("getAliceBot", actionContext);
		String message = (String) self.doAction("getMessage", actionContext);
		return bot.respond(message);
		
	}
	
	
	public static AliceBot getAliceBot(ActionContext actionContext) throws FileNotFoundException, AliceBotParserException, IOException, AliceBotParserConfigurationException, TemplateException{
		Thing self = actionContext.getObject("self");
		
		//优先从aliceReference中获取
		String aliceReference = self.getStringBlankAsNull("aliceReference");
		if(aliceReference != null){
			Thing ref = World.getInstance().getThing(aliceReference);
			if(ref != null){
				return (AliceBot) ref.doAction("getAliceBot", actionContext);
			}
		}
		
		//从自身的定义中获取
		AliceBot bot = (AliceBot) self.getData("bot");
		Long last = (Long) self.getData("lastModified");
		if(bot == null || last == null || last != self.getMetadata().getLastModified()){
			try{
				bot = createAliceBot(self, actionContext);
			}catch(Exception e){
				throw new ActionException("Create alice error, alice=" + self.getMetadata().getPath(), e);
			}
		}
		
		return bot;
	}
	
	public static AliceBot createAliceBot(Thing self, ActionContext actionContext) throws FileNotFoundException, AliceBotParserException, IOException, AliceBotParserConfigurationException{
		AliceBot bot = null;
		String botRoot = (String) self.doAction("getRootPath", actionContext);
		
		//初始化AliceBot
	    Searcher searcher = new Searcher();  
	    AliceBotParser parser = new AliceBotParser();  
	    bot = parser.parse(new FileInputStream(botRoot + self.getString("context")),  
	                                new FileInputStream(botRoot +  self.getString("splitters")),  
	                                new FileInputStream(botRoot +  self.getString("substitutions")),  
	                                searcher.search(botRoot +  self.getString("alicePath"),  self.getString("aimlRegex")));
	    
	    //从配置中学习
	    String aimlConfig = (String) self.doAction("getAimlConfig", actionContext);
	    if(aimlConfig != null){	    	
	    	Thing config = World.getInstance().getThing(aimlConfig);
	    	if("_local.xworker.config.AimlConfig".equals(aimlConfig) && config == null){
	    		//默认的，初始化
	    		config = UtilThing.getThingIfNotExistsCreate(aimlConfig, "_local", 
	    				World.getInstance().getThing("xworker.ai.chatterbean.AimlDefaultConfig"));
	    	}
	    	if(config != null){
	    		config.doAction("learn", actionContext, "bot",  bot);
	    	}
	    }
	    self.setData("bot", bot);
	    self.setData("lastModified", self.getMetadata().getLastModified());
	    
	    return bot;
	}
	
	public static String getRootPath(ActionContext actionContext) throws IOException, TemplateException{
		Thing self = actionContext.getObject("self");
		String botRoot = self.getString("rootPath"); 
		Map<String, Object> context = UtilMap.toMap("xworkerRootPath", World.getInstance().getPath());
		return UtilTemplate.processString(context, botRoot);
	}
	
	public static void reset(ActionContext actionContext) throws FileNotFoundException, AliceBotParserException, IOException, AliceBotParserConfigurationException{
		Thing self = actionContext.getObject("self");
		
		//优先从aliceReference中获取
		String aliceReference = self.getStringBlankAsNull("aliceReference");
		if(aliceReference != null){
			Thing ref = World.getInstance().getThing(aliceReference);
			if(ref != null){
				ref.doAction("reset", actionContext);
				return;
			}
		}
		
		//重新创建一个新的AliceBot
		createAliceBot(self, actionContext);
	}
	
	public static void httpDo(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");	
				
		HttpServletRequest request = actionContext.getObject("request");
		HttpServletResponse response = actionContext.getObject("response");
		
		//检验访问权限
		if(!UtilData.isTrue(self.doAction("httpCheck", actionContext))){
			response.setContentType("text/plain;charset=utf-8");
			response.getWriter().print("没有访问权限!");
			return;
		}
		
		//保存参数作为消息
		actionContext.peek().put("message", request.getParameter("message"));
		AliceBot bot = (AliceBot) self.doAction("getAliceBot", actionContext);
		
		String message = (String) self.doAction("getMessage", actionContext);
		String result = bot.respond(message);
		//格式化输出
		result = (String) self.doAction("httpFormatInputOutput", actionContext, "input", message, "output", result);
		
		String contentType = (String) self.doAction("getHttpContentType", actionContext);
		if(contentType != null){
			response.setContentType(contentType);
		}
		if(result != null){
			response.getWriter().print(result);
		}		
		
		if(UtilAction.getDebugLog(self, actionContext)){
			logger.info("Alice = " + self.getMetadata().getPath());
			logger.info("  request: " + message);
			logger.info("    response: " + result);
		}
	}
	
	public static String httpFormatInputOutput(ActionContext actionContext){
		String input = actionContext.getObject("input");
		String output = actionContext.getObject("output");
		
		return AliceUtils.renderMessageToHtml(input, false) + AliceUtils.renderMessageToHtml(output, true);
	}
	
	
}
