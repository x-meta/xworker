package xworker.ai.aima.logic.utils;

import java.util.List;

import aima.core.logic.fol.kb.FOLKnowledgeBase;
import aima.core.logic.fol.parsing.ast.Sentence;
import aima.core.logic.propositional.kb.KnowledgeBase;

public class LogicUtil {
	public static void kbTell(Object kb, String sentence){
		if(kb instanceof FOLKnowledgeBase){
			((FOLKnowledgeBase) kb).tell(sentence);
		}else{
			((KnowledgeBase) kb).tell(sentence);
		}
	}
	
	public static void tell(Object kb, List<? extends Sentence> sentences) {
		if(kb instanceof FOLKnowledgeBase){
			((FOLKnowledgeBase) kb).tell(sentences);
		}else{
			KnowledgeBase kkb = (KnowledgeBase) kb;
			for(Sentence s : sentences){
				kkb.tell(s.toString());
			}
		}
	}
	
	public static  void tell(Object kb, Sentence sentence) {
		if(kb instanceof FOLKnowledgeBase){
			((FOLKnowledgeBase) kb).tell(sentence);
		}else{
			KnowledgeBase kkb = (KnowledgeBase) kb;
			kkb.tell(sentence.toString());			
		}
	}
	
	public static void tellAll(Object kb, String[] percepts) {
		if(kb instanceof FOLKnowledgeBase){
			FOLKnowledgeBase fkb = (FOLKnowledgeBase) kb;
			for(String s : percepts){
				fkb.tell(s);
			}
		}else{
			KnowledgeBase kkb = (KnowledgeBase) kb;
			kkb.tellAll(percepts);			
		}
	}
}
