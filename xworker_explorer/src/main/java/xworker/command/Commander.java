package xworker.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.actions.ActionContainer;

/**
 * 命令执行器。
 * 
 * @author zyx
 *
 */
public class Commander {
	/** 命令选择器的父面板 */
	Composite selectorComposite;
	
	/** 变量上下文 */
	ActionContext actionContext = null;
	
	/** 命令SWT的动作容器 */
	ActionContainer actionContainer = null;

	/** 语句列表 */
	List<Sentence> sentences = new ArrayList<Sentence>();
	
	/** 当前想选择器 */
	Selector currentSelector = null;	
	
	/** 当前语句 */
	Sentence currentSentence = null;
	
	/** 根词语 */
	Thing rootWord = null;
	
	public Commander(Composite selectorComposite,  ActionContainer actionContainer, ActionContext actionContext){		
		this.selectorComposite = selectorComposite;
		this.actionContext = actionContext;
		this.actionContainer = actionContainer;		
		
		createSentence();
	}
	
	public ActionContext getActionContext(){
		return actionContext;
	}
	
	/**
	 * 设置根词语。
	 * 
	 * @param rootWord
	 */
	public void setRootWord(Thing rootWord){
		this.rootWord = rootWord;
		
		this.createSentence();
	}
	
	/**
	 * 返回当前语句。
	 * 
	 * @return
	 */
	public Sentence getCurrentSentence(){
		return this.currentSentence;
	}
	
	/**
	 * 返回到指定索引位置的词。
	 * 
	 * @param index
	 */
	public void backToWordIndex(int index){
		if(currentSentence != null){
			while(currentSentence.words.size() > index + 1){
				currentSentence.words.remove(currentSentence.words.size() - 1);
				
				this.currentSelector = currentSentence.getSelector(selectorComposite);
				((StyledText) actionContext.get("displayText")).setText(currentSentence.toString());
			}
		}
	}
	
	/**
	 * 删除最后一个词。
	 * 
	 */
	public void removeLastWord(){
		if(currentSentence != null && currentSentence.words.size() > 1){
			currentSentence.words.remove(currentSentence.words.size() - 1);
			this.currentSelector = currentSentence.getSelector(selectorComposite);	
			
			((StyledText) actionContext.get("displayText")).setText(currentSentence.toString());
		}
	}
	
	/**
	 * 一个检查偏移量的工具，用位置判断一个词是否需要生成它自己的选择器。
	 * 
	 * @param words
	 * @param index
	 * @param self
	 * @return
	 */
	public static boolean checkIndexDiff(List<Object> words, int index, Thing self){
		String indexDiff = self.getStringBlankAsNull("indexDiff");
		if(indexDiff != null){
		    boolean ok = false;
		    String[] diffs = indexDiff.split("[,]");
		    int rdiff = words.size() - index;
		    for(String diff : diffs){
		        if(Integer.parseInt(diff) == rdiff){
		            return true;
		        }
		    }
		    
		    if(!ok){
		        return false;
		    }
		}
		
		return true;
	}
	/**
	 * 创建一个新的语句。
	 */
	public void createSentence(){
		currentSentence = new Sentence(this, actionContext);
		//sentences.add(currentSentence);
		
		this.currentSelector = currentSentence.getSelector(selectorComposite);		
	}
	
	public void putWord(Object word){
		currentSentence.addWord(word);
		
		this.currentSelector = currentSentence.getSelector(selectorComposite);	
		
		if(currentSelector == null){
			try{
				currentSentence.execute();
			}finally{
				//执行后不再全部清空，而是回到用户最有可能选择位置
				//this.currentSelector = currentSentence.getSelector(selectorComposite);	
				
				((StyledText) actionContext.get("displayText")).setText(currentSentence.toString());
			}
		}
	}
	
	
	public void wordAdded(Object word){
		actionContainer.doAction("wordAdded", actionContext, "word", word);
	}
	
	
	public void handleKeyEvent(Event event){
		if(currentSelector != null){
			currentSelector.handleKeyEvent(event);
		}
	}
	
	public void handleSearchEvent(String text){
		if(currentSelector != null){
			currentSelector.handleSearchEvent(text);
		}
	}
	
	/**
	 * 清空命令。
	 */
	public void clear(){
		actionContainer.doAction("clear", actionContext);		
		
		this.createSentence();
	}
}
