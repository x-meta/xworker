package xworker.ai.chatterbean;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.xmeta.World;

import bitoflife.chatterbean.AliceBot;
import bitoflife.chatterbean.Context;
import bitoflife.chatterbean.parser.AliceBotParser;
import bitoflife.chatterbean.util.Searcher;

public class TestChatterbean {
	public static void main(String args[]){
		try{
			World world = World.getInstance();
			World.getInstance().init("xworker");
			
			String botRoot = world.getPath() + "/projects/xworker_ai/chatterbean/";
		    Searcher searcher = new Searcher();  
		    AliceBotParser parser = new AliceBotParser();  
		    AliceBot bot = parser.parse(new FileInputStream(botRoot + "Bots/context.xml"),  
		                                new FileInputStream(botRoot + "Bots/splitters.xml"),  
		                                new FileInputStream(botRoot + "Bots/substitutions.xml"),  
		                                searcher.search(botRoot + "Bots/Alice/", ".*\\.aiml"));  
		  
		    Context context = bot.getContext();  
		   // context.outputStream(new ByteArrayOutputStream());  
		    
		    System.err.println("Alice>" + bot.respond("welcome"));
		    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		    String request = null;
		    while((request = br.readLine()) != null){
		    	try{
		    	request = new String(request.getBytes("GBK"), "utf-8");
		    	System.err.println("Alice>" + bot.respond(request));
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
