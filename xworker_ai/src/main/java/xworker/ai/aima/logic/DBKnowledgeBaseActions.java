package xworker.ai.aima.logic;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import aima.core.logic.fol.domain.FOLDomain;
import aima.core.logic.fol.inference.FOLBCAsk;
import aima.core.logic.fol.inference.FOLFCAsk;
import aima.core.logic.fol.inference.FOLModelElimination;
import aima.core.logic.fol.inference.FOLOTTERLikeTheoremProver;
import aima.core.logic.fol.inference.FOLTFMResolution;
import aima.core.logic.fol.inference.InferenceProcedure;
import aima.core.logic.fol.kb.FOLKnowledgeBase;
import aima.core.logic.propositional.kb.KnowledgeBase;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;

public class DBKnowledgeBaseActions {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//知识库
		DataObject base = DataObjectUtil.load("xworker.app.ai.dataobjects.KnowlegeBase", self.getLong("knowledgeBaseId"), actionContext);
		if(base == null){
			throw new ActionException("KnowledgeBase not exists, path=" + self.getMetadata().getPath());
		}
		
		//语句列表
		List<DataObject> sentences = DataObjectUtil.query("xworker.app.ai.dataobjects.KnowlegeSentence", 
				UtilMap.toMap("knowlegeBaseId", base.get("id")), actionContext);
		
		if(base.getInt("type") == 2){
			//一阶逻辑
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
			
			FOLDomain domain = new FOLDomain();
			for(DataObject s : sentences){
				int type = s.getInt("type");
				String sentence = s.getString("sentence");
				if(sentence == null || "".equals(sentence)){
					continue;
				}
				
				if(type == 2){
					domain.addConstant(sentence);
				}else if(type == 3){
					domain.addPredicate(sentence);
				}else if(type == 4){
					domain.addFunction(sentence);
				}				
			}
			
			FOLKnowledgeBase kb = new FOLKnowledgeBase(domain, ip);
			for(DataObject s : sentences){
				int type = s.getInt("type");
				String sentence = s.getString("sentence");
				if(sentence == null || "".equals(sentence)){
					continue;
				}
				
				if(type == 1){
					kb.tell(sentence);
				}
			}
			
			return kb;
		}else{
			KnowledgeBase kb = new KnowledgeBase();
			for(DataObject s : sentences){
				int type = s.getInt("type");
				String sentence = s.getString("sentence");
				if(sentence == null || "".equals(sentence)){
					continue;
				}
				
				if(type == 1){
					kb.tell(sentence);
				}
			}
			
			return kb;
		}
	}
}
