package xworker.ai.aima.logic.fol;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import aima.core.logic.fol.domain.FOLDomain;
import aima.core.logic.fol.inference.FOLBCAsk;
import aima.core.logic.fol.inference.FOLFCAsk;
import aima.core.logic.fol.inference.FOLModelElimination;
import aima.core.logic.fol.inference.FOLOTTERLikeTheoremProver;
import aima.core.logic.fol.inference.FOLTFMResolution;
import aima.core.logic.fol.inference.InferenceProcedure;
import aima.core.logic.fol.kb.FOLKnowledgeBase;


public class FOLKnowledgeBaseActions {
	/**
	 * 创建知识库。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object createKnowledgeBase(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//推理程序
		String inferenceProcedure = self.getStringBlankAsNull("inferenceProcedure");
		InferenceProcedure ip = null;
		if("FOLFCAsk".equals(inferenceProcedure)){
			ip = new FOLFCAsk();
		}else if("FOLModelElimination".equals(inferenceProcedure)){
			ip = new FOLModelElimination();
		}else if("FOLOTTERLikeTheoremProver".equals(inferenceProcedure)){
			ip = new FOLOTTERLikeTheoremProver();
		}else if("FOLTFMResolution".equals(inferenceProcedure)){
			ip = new FOLTFMResolution();
		}else{
			ip = new FOLBCAsk();
		}
		
		//论域
		FOLDomain domain = null;
		String domainPath = self.getStringBlankAsNull("domain");
		if(domainPath != null){
			Thing domainThing = World.getInstance().getThing(domainPath);
			domain = (FOLDomain) domainThing.doAction("create", actionContext);
		}
		if(domain == null){
			Thing domainsThing = self.getThing("Domains@0");
			if(domainsThing != null && domainsThing.getChilds().size() > 0){
				domain = (FOLDomain) domainsThing.getChilds().get(0).doAction("create", actionContext);
			}
		}
		if(domain == null){
			domain = new FOLDomain();			
		}
		
		String constants = self.getStringBlankAsNull("constants");
		if(constants != null){
			for(String ss : constants.split("[,]")){
				for(String s : ss.split("[\n]")){
					s = s.trim();
					if(!"".equals(s)){
						domain.addConstant(s);
					}
				}
			}
		}
		
		String predicates = self.getStringBlankAsNull("predicates");
		if(predicates != null){
			for(String ss : predicates.split("[,]")){
				for(String s : ss.split("[\n]")){
					s = s.trim();
					if(!"".equals(s)){
						domain.addPredicate(s);
					}
				}
			}
		}
		
		String functions = self.getStringBlankAsNull("functions");
		if(functions != null){
			for(String ss : functions.split("[,]")){
				for(String s : ss.split("[\n]")){
					s = s.trim();
					if(!"".equals(s)){
						domain.addFunction(s);
					}
				}
			}
		}
		
		
		//创建知识库
		FOLKnowledgeBase kb = new FOLKnowledgeBase(domain, ip);
		try{
			Bindings bindings = actionContext.push(null);
			bindings.put("kb", kb);
			for(Thing sentenceThing : self.getChilds("Sentences")){
				sentenceThing.doAction("create", actionContext);				
			}
			
			String sentences = self.getStringBlankAsNull("sentences");
			if(sentences != null){
				for(String ss : sentences.split("[,]")){
					for(String s : ss.split("[\n]")){
						s = s.trim();
						if(!"".equals(s)){
							kb.tell(s);
						}
					}
				}
			}
		}finally{
			actionContext.pop();
		}
		
		return kb;
	}
	
	public static void createSentences(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		FOLKnowledgeBase kb = (FOLKnowledgeBase) actionContext.get("kb");
		for(Thing sentenceThing : self.getChilds()){
			Object obj = sentenceThing.doAction("create", actionContext);
			if(obj instanceof String){
				String s = (String) obj;
				for(String str : s.split("[,]")){
					str = str.trim();
					if("".equals(str)){
						continue;
					}
					
					try{
						kb.tell(str);
					}catch(Exception e){
						throw new ActionException(e.getMessage() + ", " + str + ",thing=" + sentenceThing.getMetadata().getPath(), e);
					}
					
				}
			}
		}
	}
}
