package xworker.ai.aima.logic.fol;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import aima.core.logic.fol.inference.InferenceResult;
import aima.core.logic.fol.inference.proof.Proof;
import aima.core.logic.fol.inference.proof.ProofPrinter;
import aima.core.logic.fol.kb.FOLKnowledgeBase;
import ognl.OgnlException;

public class FOLActions {
	//private static Logger logger = LoggerFactory.getLogger(FOLActions.class);
	
	public static Object ask(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//初始化FOLKnowledgeBase
		FOLKnowledgeBase kb = (FOLKnowledgeBase) self.doAction("getKnowledgeBase", actionContext);		
		if(kb == null){
			throw new ActionException("FOLKnowledgeBase can not be null, FOLAsk=" + self.getMetadata().getPath());
		}
		
		//更加更多规则和事实
		for(Thing tells : self.getChilds("Tells")){
			for(Thing tell : tells.getChilds()){
				String sentences = tell.doAction("getSentences", actionContext);
				tell(kb, sentences);				
			}
		}
		
		
		String askSentence = (String) self.doAction("getAskSentence", actionContext);
		if(askSentence == null){
			throw new ActionException("AskSentence can not be null, FOLAsk=" + self.getMetadata().getPath());
		}
				
				
		//触发子问题
		for(Thing asks : self.getChilds("Asks")){
			for(Thing ask : asks.getChilds()){
				String cqueryString = (String) ask.doAction("getQueryString", actionContext);
				ask(ask, kb, cqueryString, actionContext);				
			}
		}
		
		InferenceResult result = ask(self, kb, (String) self.doAction("getAskSentence", actionContext), actionContext);
		return result;
	}
	
	public static InferenceResult ask(Thing self, FOLKnowledgeBase kb, String queryString, ActionContext actionContext){
		if(queryString == null || "".equals(queryString)){
			return null;
		}
		
		InferenceResult result = kb.ask(queryString);
		actionContext.peek().put("result", result);
		actionContext.peek().put("kb", kb);
		//触发事件
		if(result.isTrue()){
			self.doAction("onTrue", actionContext);
		}else{
			self.doAction("onFalse", actionContext);
		}
		if(result.isPartialResultDueToTimeout()){
			self.doAction("onPartialResultDueToTimeOut", actionContext);
		}
		if(result.isPossiblyFalse()){
			self.doAction("onPossiblyFalse", actionContext);
		}
		if(result.isUnknownDueToTimeout()){
			self.doAction("onUnkownDueToTimeOut", actionContext);
		}
		
		return result;
	}
	
	public static void tell(FOLKnowledgeBase kb, String sentences){
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
	
	
	public static Object createFOLKnowledgeBase(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//初始化FOLKnowledgeBase
		FOLKnowledgeBase kb = null;		
		String knowledgeBaseVar = self.getStringBlankAsNull("knowledgeBaseVar");
		if(knowledgeBaseVar != null){
			kb = (FOLKnowledgeBase) actionContext.get(knowledgeBaseVar);
		}
		if(kb == null){
			String knowledgeBaseThingPath = self.getStringBlankAsNull("knowledgeBaseThing");
			if(knowledgeBaseThingPath != null){
				Thing knowledgeBaseThing = World.getInstance().getThing(knowledgeBaseThingPath);
				if(knowledgeBaseThing != null){
					kb = (FOLKnowledgeBase) knowledgeBaseThing.doAction("create", actionContext);
				}
			}
		}
		if(kb == null){
			Thing fOLKnowledgeBaseThing = self.getThing("knowledgeBase@0");
			if(fOLKnowledgeBaseThing != null && fOLKnowledgeBaseThing.getChilds().size() > 0){
				kb = (FOLKnowledgeBase) fOLKnowledgeBaseThing.getChilds().get(0).doAction("create", actionContext);
			}
		}

		return kb;
	}
	
	public static void printProofs(InferenceResult result){
		if(result != null){
			System.out.println("InferenceResult:");
			System.out.println("    isPossiblyFalse:" + result.isPossiblyFalse());
			System.out.println("    isTrue:" + result.isTrue());
			System.out.println("    isUnknownDueToTimeout:" + result.isUnknownDueToTimeout());
			System.out.println("    isPartialResultDueToTimeout:" + result.isPartialResultDueToTimeout());
			System.out.println("    Proofs:" + result.isPartialResultDueToTimeout());
			for (Proof p : result.getProofs()) {
				System.out.print(ProofPrinter.printProof(p));
				System.out.println("");
			}
		}
	}
	
	public static Object runInferenceResultUtil(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String inferenceResultVar = self.getStringBlankAsNull("inferenceResultVar");
		InferenceResult result = null;
		if(inferenceResultVar != null){
			result = (InferenceResult) actionContext.get(inferenceResultVar);
		}
		if(result == null){
			throw new ActionException("InferenceResult is null, InferenceResultUtil=" + self.getMetadata().getPath());
		}
		
		if(self.getBoolean("printResult")){
			System.out.println("InferenceResult:");
			System.out.println("    isPossiblyFalse:" + result.isPossiblyFalse());
			System.out.println("    isTrue:" + result.isTrue());
			System.out.println("    isUnknownDueToTimeout:" + result.isUnknownDueToTimeout());
			System.out.println("    isPartialResultDueToTimeout:" + result.isPartialResultDueToTimeout());
			System.out.println("    Proofs:" + result.isPartialResultDueToTimeout());
			for (Proof p : result.getProofs()) {
				System.out.print(ProofPrinter.printProof(p));
				System.out.println("");
			}
		}
		String type = self.getStringBlankAsNull("returnType");
		if("isPossiblyFalse".equals(type)){
			return result.isPossiblyFalse();
		}else if("isTrue".equals(type)){
			return result.isTrue();
		}else if("isUnknownDueToTimeout".equals(type)){
			return result.isUnknownDueToTimeout();
		}else if("isPartialResultDueToTimeout".equals(type)){
			return result.isPartialResultDueToTimeout();
		}else{
			return result.isTrue();
		}
	}
	
	public static Object getKnowledgeBase(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		FOLKnowledgeBase kb = null;		
		String knowledgeBaseVar = self.getStringBlankAsNull("knowledgeBaseVar");
		if(knowledgeBaseVar != null){
			kb = (FOLKnowledgeBase) actionContext.get(knowledgeBaseVar);
		}
		if(kb == null){
			String knowledgeBaseThingPath = self.getStringBlankAsNull("knowledgeBaseThing");
			if(knowledgeBaseThingPath != null){
				Thing knowledgeBaseThing = World.getInstance().getThing(knowledgeBaseThingPath);
				if(knowledgeBaseThing != null){
					kb = (FOLKnowledgeBase) knowledgeBaseThing.doAction("create", actionContext);
				}
			}
		}
		if(kb == null){
			Thing fOLKnowledgeBaseThing = self.getThing("knowledgeBase@0");
			if(fOLKnowledgeBaseThing != null && fOLKnowledgeBaseThing.getChilds().size() > 0){
				kb = (FOLKnowledgeBase) fOLKnowledgeBaseThing.getChilds().get(0).doAction("create", actionContext);
			}
		}
		
		return kb;
	}
	
	public static String getAskSentence(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		return self.getString("askSentence", null, actionContext);
	}
	
	public static Object getResult(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		return self.getObject("result", actionContext);
	}
	
	public static void printProofs(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		InferenceResult result = (InferenceResult) self.doAction("getResult", actionContext);
		printProofs(result);
	}
	
	public static Object onTrue(ActionContext actionContext){
		InferenceResult result = (InferenceResult) actionContext.get("result");
		printProofs(result);
		
		return  null;
	}
}
