package xworker.swt.custom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Item;

/**
 * Item的行编辑器，是在Item存在时就一直存在的一种编辑方式，比如
 * 
 * @author zyx
 *
 */
public class ItemRowEditor implements DisposeListener{
	Item item;
	List<ItemEditor> editors = new ArrayList<ItemEditor>();
	
	public ItemRowEditor(Item item) {
		item.addDisposeListener(this);
	}
	
	public void addItemEditor(ItemEditor editor) {
		editors.add(editor);
	}
	
	public void update() {
		for(ItemEditor editor : editors) {
			editor.update();
		}
	}
	
	@Override
	public void widgetDisposed(DisposeEvent e) {
		for(ItemEditor editor : editors) {
			try {
				editor.dispose();
			}catch(Exception ee){
				ee.printStackTrace();
			}
		}
	}
}
