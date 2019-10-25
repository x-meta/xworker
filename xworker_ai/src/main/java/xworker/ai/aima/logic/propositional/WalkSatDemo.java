package xworker.ai.aima.logic.propositional;

import aima.core.logic.propositional.inference.WalkSAT;
import aima.core.logic.propositional.kb.KnowledgeBase;
import aima.core.logic.propositional.kb.data.Model;
import aima.core.logic.propositional.parsing.ast.PropositionSymbol;
import aima.core.logic.propositional.visitors.ConvertToConjunctionOfClauses;

public class WalkSatDemo {
	public static void main(String[] args) {
		System.out.println("\nWalkSatDemo\n");
		KnowledgeBase kb = new KnowledgeBase();
		kb.tell("P => Q");
		kb.tell("L & M => P");
		kb.tell("B & L => M");
		kb.tell("A & P => L");
		kb.tell("A & B => L");
		kb.tell("A");
		kb.tell("B");

		System.out.println("Example from  page 220 of AIMA 2nd Edition");
		System.out.println("KnowledgeBsse consists of sentences");
		System.out.println(kb.toString());

		WalkSAT walkSAT = new WalkSAT();
		Model m = walkSAT.walkSAT(ConvertToConjunctionOfClauses.convert(kb.asSentence()).getClauses(), 0.5, 1000);
		if (m == null) {
			System.out.println("failure");
		} else {
			for(PropositionSymbol p : m.getAssignedSymbols()){
				System.out.println(p.getSymbol());
			}
			
			m.print();
			System.out.println(m.isTrue(new PropositionSymbol("F")));
		}
	}
}
