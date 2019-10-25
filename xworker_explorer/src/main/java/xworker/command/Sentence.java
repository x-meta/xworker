package xworker.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

public class Sentence {
	
	public List<Object> words = new ArrayList<Object>();
	public ActionContext actionContext = null;
	public Commander commander;
	
	public Sentence(Commander commander, ActionContext actionContext){
		this.commander = commander;
		this.actionContext = actionContext;
		
		//语句的根词汇
		if(commander.rootWord != null){
			addWord(commander.rootWord);
		}else{
			addWord(World.getInstance().getThing("xworker.command.Root"));
		}
	}
	
	public void execute(){		
		for(int i=0; i<words.size(); i++){
			Object command = words.get(i);
			if(command instanceof Thing){
				Thing cmd = (Thing) command;
				Object result = cmd.doAction("execute", actionContext, "words", words, "index", i, "commander", commander);
				if(UtilData.isTrue(result)){
					//为了用户选择方便，删除执行的词和后面的词
					while(words.size() > i){
						words.remove(words.size() - 1);
					}
					return;
				}
			}
		}
		
		//到这里应该执行是最后一个，删除最后一个，然后用户可以再之前的位置重新选择
		words.remove(words.size() - 1);
	}
	
	public Selector getSelector(Composite composite){
		if(composite.isDisposed()){
			return null;
		}
		
		List<Control> oldChilds = new ArrayList<Control>();
		for(Control child : composite.getChildren()){
			oldChilds.add(child);
		}
		
		for(int i=0; i<words.size(); i++){
			if(words.get(i) instanceof Thing){
				Thing word = (Thing) words.get(i);
				ActionContext ac = new ActionContext();
				ac.put("parent", composite);
				ac.put("commander", commander);
				ac.put("sentence", this);
				ac.put("words", words);
				ac.put("index", i);
				ac.put("word", word);
				
				Selector selector = (Selector) word.doAction("createCommandSelector", ac);
				if(selector != null){
					for(Control old : oldChilds){
						old.dispose();
					}
					
					composite.layout();
					return selector;
				}
			}
		}
		
		return null;
	}
	
	public void addWord(Object word){
		words.add(word);
		
		commander.wordAdded(word);
	}
	
	public String toString(){
		String str = "";
		for(int i=0; i<words.size(); i++){
			if(i > 0){
				str = str + " ";
			}
			
			Object word = words.get(i);
			if(word instanceof Thing){
				str = str + ((Thing) word).getMetadata().getLabel();
			}else{
				str = str + word;
			}
		}
		
		return str;
	}
}
