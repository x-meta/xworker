package xworker.swt.custom;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;

/**
 * XWorker的Item编辑方法最初是建立在TableCursor上的，现在扩充到了其它Item的编辑上，比如Tree使用TreeCurosr。
 * 
 * 对于没有Cursor的控件，使用ItemCursor来模拟相应的Cursor，从而可以实现Item的编辑。
 * 
 * @author zyx
 *
 */
public interface ItemCursor {
	/** 
	 * 返回当前要编辑的Item的列的索引。
	 */
	public int getColumn();
	
	/**
	 * 一些编辑器需要显示在Cursor位置的位置来显示，这里返回Curosr的位置，其中位置是相对于父控件的。
	 * 
	 * @return
	 */
	public Point getLocation();
	
	/**
	 * 返回Cursor的大小，比如表格的单元格的大小。
	 * 
	 * @return
	 */
	public Point getSize();
	
	/**
	 * 返回正在编辑的条目。
	 * 
	 * @return
	 */
	public Item getRow();
	
	/**
	 * 获取Cursor的父控件，是用来创建编辑的父控件，比如Item所属的Table，Tree等。
	 * 
	 * @return
	 */
	public Composite getParent();
}
