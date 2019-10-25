package xworker.ai.aima.logic.propositional;

import java.io.IOException;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import aima.core.logic.propositional.inference.DPLL;
import aima.core.logic.propositional.inference.DPLLSatisfiable;
import aima.core.logic.propositional.inference.OptimizedDPLL;
import aima.core.logic.propositional.inference.PLFCEntails;
import aima.core.logic.propositional.inference.PLResolution;
import aima.core.logic.propositional.inference.WalkSAT;
import aima.core.logic.propositional.kb.KnowledgeBase;
import aima.core.logic.propositional.kb.data.Model;
import aima.core.logic.propositional.parsing.PLParser;
import aima.core.logic.propositional.parsing.ast.PropositionSymbol;
import aima.core.logic.propositional.visitors.ConvertToConjunctionOfClauses;
import ognl.OgnlException;
import xworker.util.UtilData;

public class KnowledgeBaseActions {
	/**
	 * 创建知识库。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object createKnowledgeBase(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String sentences = (String) self.doAction("getSentences", actionContext);
		return createKnowledgeBase(sentences);
	}
	
	public static KnowledgeBase createKnowledgeBase(String sentences){
		KnowledgeBase kb = new KnowledgeBase();
		if(sentences != null){
			for(String s : sentences.split("[\n]")){
				s = s.trim();
				if("".equals(s)){
					continue;
				}
				
				kb.tell(s);
			}
		}
		
		return kb;
	}
	
	public static boolean ask(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		KnowledgeBase kb = self.doAction("getKnowledgeBase", actionContext);
		
		//更加更多规则和事实
		for(Thing tells : self.getChilds("Tells")){
			for(Thing tell : tells.getChilds()){
				String sentences = tell.doAction("getSentences", actionContext);
				tell(kb, sentences);				
			}
		}
		
		//查询语句
		String queryString = (String) self.doAction("getQueryString", actionContext);		
		
		//算法，推理问题的真假
		String algorithm = (String) self.doAction("getAlgorithm", actionContext);
		boolean result = false;
		if(queryString != null && !"".equals(queryString)){
			result = ask(kb, queryString, algorithm);
			
			//触发自身的事件
			if(result){
				self.doAction("onTrue", actionContext, "kb", kb, "queryString", queryString, "algorithm", algorithm);
			}else{
				self.doAction("onFalse", actionContext, "kb", kb, "queryString", queryString, "algorithm", algorithm);
			}
		}
		
		//触发子问题
		for(Thing asks : self.getChilds("Asks")){
			for(Thing ask : asks.getChilds()){
				String cqueryString = (String) ask.doAction("getQueryString", actionContext);
				boolean cresult = ask(kb, cqueryString, algorithm);
				if(cresult){
					ask.doAction("onTrue", actionContext, "kb", kb, "queryString", cqueryString, "algorithm", algorithm);
				}else{
					ask.doAction("onFalse", actionContext, "kb", kb, "queryString", cqueryString, "algorithm", algorithm);
				}
			}
		}
		
		return result;
	}
	
	public static boolean ask(KnowledgeBase kb, String queryString, String algorithm){
		boolean result = false;
		if("DPLLSatisfiable".equals(algorithm)){
			DPLLSatisfiable dpll = new DPLLSatisfiable();
			PLParser parser = new PLParser(); 
			result = dpll.isEntailed(kb, parser.parse(queryString));
		}else if("OptimizedDPLL".equals(algorithm)){
			OptimizedDPLL dpll = new OptimizedDPLL();
			PLParser parser = new PLParser(); 
			result =  dpll.isEntailed(kb, parser.parse(queryString));
		}else if("PLFCEntails".equals(algorithm)){
			PLFCEntails dpll = new PLFCEntails();
			result = dpll.plfcEntails(kb, new PropositionSymbol(queryString));
		}else if("PLResolution".equals(algorithm)){
			PLResolution dpll = new PLResolution();
			PLParser parser = new PLParser(); 
			result = dpll.plResolution(kb, parser.parse(queryString));
		}else{
			result = kb.askWithTTEntails(queryString);
		}
		
		return result;
	}
	
	public static void tell(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		KnowledgeBase kb = (KnowledgeBase) self.doAction("getKnowledgeBase", actionContext);
		String sentences = (String) self.doAction("getSentences", actionContext);
		tell(kb, sentences);
	}	
	
	public static void tell(KnowledgeBase kb, String sentences){
		if(sentences != null){
			for(String s : sentences.split("[\n]")){
				s = s.trim();
				if("".equals(s)){
					continue;
				}
				
				kb.tell(s);
			}
		}
	}
	
	public static void createSentences(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		KnowledgeBase kb = (KnowledgeBase) actionContext.get("kb");
		for(Thing sentenceThing : self.getChilds()){
			Object obj = sentenceThing.doAction("create", actionContext);
			if(obj instanceof String){
				String s = (String) obj;
				for(String str : s.split("[,]")){
					str = str.trim();
					if("".equals(str)){
						continue;
					}
					
					kb.tell(str);
				}
			}
		}
	}
	
	/**
	 * 创建语句。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object createSentence(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getString("sentence");
	}
	
	/**
	 * 获取知识库的方法。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException 
	 * @throws IOException 
	 */
	public static KnowledgeBase getKnowledgeBase(ActionContext actionContext) throws OgnlException, IOException{
		Thing self = actionContext.getObject("self");
		
		String knowledgeBaseStr = self.getStringBlankAsNull("knowledgeBase");
		Thing thing = self.doAction("getKnowlegeBaseThing", actionContext);
		
		//通过字符串获取知识库
		if (thing == null && (knowledgeBaseStr != null && !"".equals(knowledgeBaseStr))){
			if(knowledgeBaseStr.startsWith("ognl:") || knowledgeBaseStr.startsWith("var:")){
				Object obj = UtilData.getData(knowledgeBaseStr, actionContext);
				if(obj instanceof KnowledgeBase){
					return (KnowledgeBase) obj;
				}else if(obj instanceof Thing){
					thing = (Thing) obj;
				}else if(obj instanceof String){
					knowledgeBaseStr = (String) obj;
				}else{
					throw new ActionException("Can not found KnowledgeBase " + knowledgeBaseStr + ", action=" + self.getMetadata().getPath());
				}
			}else{
				return createKnowledgeBase(knowledgeBaseStr);
			}			
		}
		
		//通过事物创建知识库
		if(thing == null){
			Thing kbs = self.getThing("KnowledgeBases@0");
			if(kbs != null && kbs.getChilds().size() > 0){
				thing = kbs.getChilds().get(0);
			}
		}
		
		if(thing != null){
			return (KnowledgeBase) thing.doAction("create", actionContext);
		}else{
			throw new ActionException("KnowledgeBase is null, action=" + self.getMetadata().getPath());
		}
	}
	
	public static Object findModel(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//知识库
		KnowledgeBase kb = self.doAction("getKnowledgeBase", actionContext);
		
		//更加更多规则和事实
		for(Thing tells : self.getChilds("Tells")){
			for(Thing tell : tells.getChilds()){
				String sentences = tell.doAction("getSentences", actionContext);
				tell(kb, sentences);				
			}
		}
		
		//findModel
		WalkSAT walkSAT = new WalkSAT();
		double probability = self.doAction("getProbability", actionContext);
		int maxFlips = self.doAction("getMaxFlips", actionContext);
		Model m = walkSAT.walkSAT(ConvertToConjunctionOfClauses.convert(kb.asSentence()).getClauses(), probability, maxFlips);
	
		//触发自身的事件
		if(m == null){
			self.doAction("onFailure", actionContext, "kb", kb);
		}else{
			self.doAction("onSuccess", actionContext, "kb", kb, "model", m);			
		}
		
		//触发子问题
		String algorithm = "OptimizedDPLL";
		for(Thing asks : self.getChilds("Asks")){
			for(Thing ask : asks.getChilds()){
				String cqueryString = (String) ask.doAction("getQueryString", actionContext);
				boolean cresult = ask(kb, cqueryString, algorithm);
				if(cresult){
					ask.doAction("onTrue", actionContext, "kb", kb, "queryString", cqueryString, "algorithm", algorithm);
				}else{
					ask.doAction("onFalse", actionContext, "kb", kb, "queryString", cqueryString, "algorithm", algorithm);
				}
			}
		}
		
		//返回模型
		return m;
	}
	
	public static boolean satisfiable(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		KnowledgeBase kb = self.doAction("getKnowledgeBase", actionContext);
		
		DPLL dpll = new OptimizedDPLL();
		return dpll.dpllSatisfiable(kb.asSentence());
	}
}
