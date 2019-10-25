package xworker.ai.aima.logic.propositional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import aima.core.logic.propositional.inference.PLFCEntails;
import aima.core.logic.propositional.inference.PLResolution;
import aima.core.logic.propositional.inference.WalkSAT;
import aima.core.logic.propositional.kb.KnowledgeBase;
import aima.core.logic.propositional.kb.data.Model;

public class PropositionalActions {
	public static Object createKnowledgeBase(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//知识库
		Thing knowledgeBase = null;
		String knowledgeBasePath = self.getStringBlankAsNull("knowledgeBase");
		if(knowledgeBasePath != null){
			knowledgeBase = World.getInstance().getThing(knowledgeBasePath);
		}
		if(knowledgeBase == null){
			knowledgeBase = self.getThing("knowledgeBase@0");
		}
		if(knowledgeBase == null || knowledgeBase.getChilds().size() == 0){
			throw new ActionException("KnowlegeBase thing is null, thing=" + self.getMetadata().getPath());
		}
		KnowledgeBase kb = (KnowledgeBase) knowledgeBase.getChilds().get(0).doAction("create", actionContext);
		if(kb == null){
			throw new ActionException("KnowlegeBase thing create null kb, thing=" + knowledgeBase.getMetadata().getPath());
		}
		
		return kb;
	}
	
	public static String getAskSentence(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//需要询问的语句
		String askSentence = self.getStringBlankAsNull("askSentence");
		if(askSentence == null){
			Thing askThing = self.getThing("AskSentence");
			if(askThing != null){
				askSentence = (String) askThing.doAction("create", actionContext);
			}
		}
		
		return askSentence;
	}
	
	/**
	 * 命题逻辑的动作。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//知识库
		KnowledgeBase kb = (KnowledgeBase) self.doAction("createKnowledgeBase", actionContext);
		if(kb == null){
			throw new ActionException("Knowledgebase is null, thing=" + self.getMetadata().getPath());
		}
		
		//需要询问的语句
		List<String> asks = new ArrayList<String>();
		String askSentence = (String) self.doAction("getAskSentence", actionContext);
		if(askSentence == null){
			throw new ActionException("AskSentence is null, thing=" + self.getMetadata().getPath());
		}
		for(String str : askSentence.split("[,]")){
			str = str.trim();
			if(!"".equals(str)){
				asks.add(str);
			}
		}
		if(asks.size() == 0){
			throw new ActionException("AskSentence is null, thing=" + self.getMetadata().getPath());
		}
		
		String algorithm = self.getStringBlankAsNull("algorithm");
		String returnType = self.getStringBlankAsNull("returnType");
		
		List<Result> results = new ArrayList<Result>();
		PLFCEntails plfce = null;
		PLResolution plr = null;
		WalkSAT walkSAT = null;
		Model m = null;
		for(String ask : asks){
			boolean r = kb.askWithTTEntails(ask);
			/*
			if("DPLL".equals(algorithm)){
				r = kb.askWithDpll(ask);
			}else if("PLFCEntail".equals(algorithm)){
				if(plfce == null){
					plfce = new PLFCEntails();
				}
				r = plfce.plfcEntails(kb, ask);
			}else if("PLResolution".equals(algorithm)){
				if(plr == null){
					plr = new PLResolution();
				}
				r = plr.plResolution(kb, ask);
			}else if("WalkSAT".equals(algorithm)){
				if(walkSAT == null){
					walkSAT = new WalkSAT();
					m = walkSAT.findModelFor(kb.asSentence().toString(), 
							self.getInt("numberOfFlips", 1000), self.getDouble("probabilityOfRandomWalk", 0.5));
				}
				r = m.isTrue(new Symbol(ask));
			}else{
				//默认TTEntails
				r = kb.askWithTTEntails(ask);
			}*/
			
			results.add(new Result(ask, r));
		}
		
		if("truefalseList".equals(returnType)){
			List<Boolean> r = new ArrayList<Boolean>();
			for(Result result : results){
				r.add(result.result);
			}
			return r;
		}else if("truefalsemap".equals(returnType)){
			Map<String, Boolean> rmap = new HashMap<String, Boolean>();
			for(Result result : results){				
				rmap.put(result.sentence, result.result);
			}
			return rmap;
		}else if("truesentence".equals(returnType)){
			for(Result result : results){
				if(result.result){
					return result.sentence;
				}
			}
			return null;
		}else if("trueSentenceList".equals(returnType)){
			List<String> list = new ArrayList<String>();
			for(Result result : results){
				if(result.result){
					list.add(result.sentence);
				}
			}
			return list;
		}else if("falseSentence".equals(returnType)){
			for(Result result : results){
				if(!result.result){
					return result.sentence;
				}
			}
			return null;
		}else if("falseSentenceList".equals(returnType)){
			List<String> list = new ArrayList<String>();
			for(Result result : results){
				if(!result.result){
					list.add(result.sentence);
				}
			}
			return list;
		}else {
			//默认truefalse
			return results.get(0).result;
		}
	}
	
	static class Result{
		String sentence;
		boolean result;
		
		public Result(String sentence, boolean result){
			this.sentence = sentence;
			this.result = result;
		}
	}
}
