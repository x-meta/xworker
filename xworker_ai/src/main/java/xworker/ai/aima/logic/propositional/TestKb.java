package xworker.ai.aima.logic.propositional;

import aima.core.logic.propositional.kb.KnowledgeBase;

public class TestKb {
	public static void main(String[] args) {
		KnowledgeBase kb = new KnowledgeBase();
		kb.tell("rain => library");
		kb.tell("~rain => shopping");
		kb.tell("rain");

		System.out.println("\nTTEntailsDemo\n");
		displayTTEntails(kb, "library");
		displayTTEntails(kb, "shopping");
		displayTTEntails(kb, "rain");

	}

	private static void displayTTEntails(KnowledgeBase kb, String s) {
		System.out.println(" ttentails (\"" + s + "\" ) returns "
				+ kb.askWithTTEntails(s));
	}
}
