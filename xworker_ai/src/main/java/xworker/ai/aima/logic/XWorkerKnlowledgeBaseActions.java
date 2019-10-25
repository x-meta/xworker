package xworker.ai.aima.logic;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import aima.core.logic.fol.domain.FOLDomain;
import aima.core.logic.fol.inference.FOLBCAsk;
import aima.core.logic.fol.inference.FOLFCAsk;
import aima.core.logic.fol.inference.FOLModelElimination;
import aima.core.logic.fol.inference.FOLOTTERLikeTheoremProver;
import aima.core.logic.fol.inference.FOLTFMResolution;
import aima.core.logic.fol.inference.InferenceProcedure;
import aima.core.logic.fol.kb.FOLKnowledgeBase;
import aima.core.logic.propositional.kb.KnowledgeBase;

public class XWorkerKnlowledgeBaseActions {
	public static Object onSuccess(ActionContext actionContext) throws ParseException, IOException{
		Thing self = (Thing) actionContext.get("self");				
		HttpEntity entity = (HttpEntity) actionContext.get("entity");
		String content = EntityUtils.toString(entity, Charset.forName("utf-8"));
		KnowledgeBase kb = null;
		FOLKnowledgeBase folKb = null;
		FOLDomain folDomain = null;
		String lines[] = content.split("[\n]");
		String[][] lss = new String[lines.length][2];
		for(int i=0;i<lines.length; i++){
			String ls[] = lines[i].split("[|]");
			lss[i][0] = ls[0].trim();
			if(ls.length >=2 ){
				lss[i][1] = ls[1].trim();
			}
		}
		
		for(int i=0; i<lss.length; i++){
			if(lss[0].equals("-1")){
				throw new ActionException(lss[0][1]);
			}
		}
		
		if("-1".equals(lss[0][0])){
			//服务器异常
			throw new ActionException(lss[0][1]);
		}else if("1".equals(lss[0][0])){
			//命题逻辑
			kb = new KnowledgeBase();
			for(int i=1; i<lss.length; i++){
				if("1".equals(lss[i][0])){
					kb.tell(lss[i][1]);
				}
			}
			
			//附加的语句
			Thing sentences = self.getThing("Sentences@0");
			if(sentences != null){
				try{
					Bindings bindings = actionContext.push(null);
					bindings.put("kb", folKb);
					for(Thing sentenceThing : sentences.getChilds()){
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
				}finally{
					actionContext.pop();
				}
			}
			return kb;
		}else if("2".equals(lss[0][0])){
			//一阶逻辑
			folDomain = new FOLDomain();
			for(int i=1; i<lss.length; i++){
				if("2".equals(lss[i][0])){
					//常量
					folDomain.addConstant(lss[i][1]);
				}else if("3".equals(lss[i][0])){
					//谓词
					folDomain.addPredicate(lss[i][1]);
				}else if("4".equals(lss[i][0])){
					//函数
					folDomain.addFunction(lss[i][1]);
				}				
			}
			Thing domain = self.getThing("Domain@0");
			if(domain != null){
				try{
					Bindings bindings = actionContext.push(null);
					bindings.put("domain", folDomain);
					
					for(Thing child : self.getChilds()){
						child.doAction("create", actionContext);
					}
				}finally{
					actionContext.pop();
				}
			}
			
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
			folKb = new FOLKnowledgeBase(folDomain, ip);
			for(int i=1; i<lss.length; i++){
				if("1".equals(lss[i][0])){
					folKb.tell(lss[i][1]);
				}
			}
			
			//附加的语句
			Thing sentences = self.getThing("Sentences@0");
			if(sentences != null){
				try{
					Bindings bindings = actionContext.push(null);
					bindings.put("kb", folKb);
					for(Thing sentenceThing : sentences.getChilds()){
						Object obj = sentenceThing.doAction("create", actionContext);
						if(obj instanceof String){
							String s = (String) obj;
							for(String str : s.split("[,]")){
								str = str.trim();
								if("".equals(str)){
									continue;
								}
								
								try{
									folKb.tell(str);
								}catch(Exception e){
									throw new ActionException(e.getMessage() + ", " + str + ",thing=" + sentenceThing.getMetadata().getPath(), e);
								}
								
							}
						}
					}
				}finally{
					actionContext.pop();
				}
			}
			
			return folKb;
		}
		
		return null;
	}
}
