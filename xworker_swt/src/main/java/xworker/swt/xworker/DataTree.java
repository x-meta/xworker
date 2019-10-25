/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.swt.xworker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

/**
 * 模仿SWing和数据绑定Tree，由于swt本身仅仅只是显示，数据支持太弱，所以加入一点修饰。
 * 
 * @author Administrator
 *
 */
public class DataTree extends Tree{
	/** 数据 */
	List<Object> datas;
	/** 表格列的名称，用于从数据中取数据和放置数据 */
	String[] columnNames;	
	/** 行状态，用来记录一行的状态，0 保持原样, 1 新增, 2 更新, 3 删除 */
	List<RowStatus> rowStatus = new ArrayList<RowStatus>();
	/** Tree的定义 */
	Thing treeThing;
	/** Tree定义的路径 */
	String treeThingPath;
	/** Tree所在的变量环境 */
	ActionContext binding;
	
	public DataTree (Composite parent, int style) {
		super (parent, ccheckStyle (style));
	}
		
	public List<Object> getNewRows(){
		return getRows(1);
	}
	
	public List<Object> getUpdatedRows(){
		return getRows(2);
	}
	
	public List<Object> getRemovedRows(){
		return getRows(3);
	}		
	
	private List<Object> getRows(int type){
		List<Object> rows = new ArrayList<Object>();
		for(Iterator<RowStatus> iter = rowStatus.iterator(); iter.hasNext();){
			RowStatus rs = iter.next();
			if(type == rs.getStatus()){
				rows.add(rs.getData());
			}
		}
		
		return rows;
	}
	
	/**
	 * 向Tree填充数据值。
	 * 
	 * @param datas
	 */
	@SuppressWarnings("unchecked")
	public void setDatas(List<Object> datas){
		this.datas = datas;		
		
		//清空原有的数据
		this.removeAll();
				
		//更新行的状态
		rowStatus.clear();
		
		if(datas != null && columnNames != null){
			for(Iterator iter = datas.iterator(); iter.hasNext();){
				updateRow(null, iter.next());
			}			
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setDatas(Set datas){
		List<Object> ds  = new ArrayList<Object>();
		if(datas != null){
			for(Iterator iter = datas.iterator(); iter.hasNext();){
				ds.add(iter.next());
			}
		}
		
		setDatas(ds);
	}
	
	@SuppressWarnings("unchecked")
	public TreeItem updateRow(TreeItem treeItem, String[] colNames, Object data,  boolean createNew){
		int col = 0;
		
		if(treeItem == null){
			treeItem = new TreeItem(this, SWT.NONE);
		}else{				
			if(createNew){
				treeItem = new TreeItem(treeItem, SWT.NONE);
			}
		}
		
		treeItem.setData(data);
		
		for(int i=0; i<colNames.length; i++){
			col = i;
			Object value = null;
			boolean valueSetted = false;
			try {
				if(colNames[i] == null || "".equals(colNames[i])){
					continue;
				}
				value = OgnlUtil.getValue(colNames[i], data);
				
				if(treeThing != null){
					//获得列定义
					Thing tColumn = treeThing.getThing("TreeColumn@" + i);
					if(tColumn != null && tColumn.get("scripts@0/@setValueAt") != null){
						//调用TreeIem的处理数据的方法
						ActionContext newBinding = new ActionContext();
						newBinding.put("colIndex", new Integer(col));
						newBinding.put("datas", this.datas);
						newBinding.put("value", value);
						newBinding.put("tree", this);
						newBinding.put("treeItem", treeItem);
						newBinding.put("binding", binding);
						
						//String setValue = (String) tColumn.exec("setValueAt", newBinding);
						//treeItem.setText(col, setValue);
						
						valueSetted = true;
					}
				}											
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
			if(!valueSetted){
				if(value != null){
					String setValue = value.toString();
					treeItem.setText(col, setValue);
				}
			}		
		}
		
		try {
			Object childs = OgnlUtil.getValue("childs", data);
			if(childs != null && childs instanceof List){
				List cls = (List) childs;
				for(Iterator iter = cls.iterator(); iter.hasNext();){
					Object child = iter.next();
					
					TreeItem childItem = new TreeItem(treeItem, SWT.NONE);
					updateRow(childItem, child);
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return treeItem;
	}
	
	/**
	 * 更新一个节点。
	 * 
	 * @param treeItem
	 * @param data
	 */
	public TreeItem updateRow(TreeItem treeItem, Object data){
		return updateRow(treeItem, columnNames, data, false);
	}
	
	public TreeItem updateRow(TreeItem treeItem, Object data, boolean createNew){
		return updateRow(treeItem, columnNames, data, createNew);
	}
	/**
	 * 更新数据的操作。
	 * 
	 * @param rowIndex
	 * @param colIndex
	 * @param treeItem
	 * @param value
	 */
	public void updatedValueAt(int rowIndex, int colIndex, TreeItem treeItem, Object value){
		//通过Ognl向原始数据赋值
		if(rowIndex < datas.size()){
			Object data = datas.get(rowIndex);
			if(colIndex < columnNames.length){
				String columnName = columnNames[colIndex];
				try {
					OgnlUtil.setValue(columnName, data, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		boolean setted = false;
		if(treeThing != null){
			//获得列定义
			Thing tColumn = treeThing.getThing("TreeColumn@" + colIndex);
			if(tColumn != null && tColumn.get("scripts@0/@setValueAt") != null){
				//调用TreeIem的处理数据的方法
				ActionContext newBinding = new ActionContext();
				newBinding.put("rowIndex", new Integer(rowIndex));
				newBinding.put("colIndex", new Integer(colIndex));
				newBinding.put("datas", this.datas);
				newBinding.put("value", value);
				newBinding.put("tree", this);
				newBinding.put("treeItem", treeItem);
				newBinding.put("binding", binding);
				
				//String setValue = (String) tColumn.exec("setValueAt", newBinding);
				//treeItem.setText(colIndex, setValue);	
				setted = true;
			}
		}
		
		if(!setted){
			String setValue = "";
			if(value != null){
				setValue = value.toString();
			}
			
			treeItem.setText(colIndex, setValue);
		}
		
		addRowStatus(rowIndex, value, 2);
	}
	
	/**
	 * 删除一行。
	 * 
	 * @param rowIndex
	 */
	public void removeRow(int rowIndex){
		TreeItem item = this.getItem(rowIndex);
		item.dispose();
		
		if(rowIndex < datas.size()){
			Object value = datas.get(rowIndex);
			datas.remove(rowIndex);
			addRowStatus(rowIndex, value, 3);
		}
	}
	
	/**
	 * 增加一行在指定的位置。
	 */
	public void addNewRow(){
		//如果要能够创建新的行，那么Tree必须有createRowData的方法。
		/*Object value = treeThing.exec("createRowData", binding);
		if(value != null){
			datas.add(value);
			updateRow(null, value);
		}*/
	}
	
	public void addNewRow(TreeItem parent, Object value){
		TreeItem item = new TreeItem(parent, SWT.NONE);
		updateRow(item, value);
	}
	
	/**
	 * 更新行的状态。
	 * 
	 * @param rowIndex
	 * @param value
	 * @param status
	 */
	private void addRowStatus(int rowIndex, Object value, int status){
		boolean have = false;
		for(int i=0; i<rowStatus.size(); i++){
			RowStatus rows = rowStatus.get(i);
			if(rows.getData() == value){
				have = true;
				if(rows.getStatus() == 1 && status == 3){
					//删除新增的行
					rowStatus.remove(i);
					break;
				}
				
				if(rows.getStatus() == 2 && status == 3){
					rows.setStatus(3);
				}
			}
		}
		
		if(!have){
			RowStatus rstatus = new RowStatus(value, status);
			rowStatus.add(rstatus);
		}
	}
	
	public void setBinding(ActionContext binding){
		this.binding = binding;
	}
	
	public List<Object> getDatas(){
		return this.datas;
	}
	
	public void setColumnNames(String[] columnNames){
		this.columnNames = columnNames;
	}
	
	public void setThingPath(String path){
		this.treeThingPath = path;
		
		treeThing = World.getInstance().getThing(treeThingPath);
	}
	
	/**
	 * 覆盖父类的checkSubclass方法，使可以继承，默认情况下swt不允许继承。
	 */
	protected void checkSubclass() {
    }
	
	static int ccheckStyle (int style) {
		/*
		* Feature in Windows.  It is not possible to create
		* a tree that does not have scroll bars.  Therefore,
		* no matter what style bits are specified, set the
		* H_SCROLL and V_SCROLL bits so that the SWT style
		* will match the widget that Windows creates.
		*/
		style |= SWT.H_SCROLL | SWT.V_SCROLL;
		return ccheckBits (style, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0);
	}
	
	static int ccheckBits (int style, int int0, int int1, int int2, int int3, int int4, int int5) {
		int mask = int0 | int1 | int2 | int3 | int4 | int5;
		if ((style & mask) == 0) style |= int0;
		if ((style & int0) != 0) style = (style & ~mask) | int0;
		if ((style & int1) != 0) style = (style & ~mask) | int1;
		if ((style & int2) != 0) style = (style & ~mask) | int2;
		if ((style & int3) != 0) style = (style & ~mask) | int3;
		if ((style & int4) != 0) style = (style & ~mask) | int4;
		if ((style & int5) != 0) style = (style & ~mask) | int5;
		return style;
	}
	
	class RowStatus{
		Object data;
		int status;
		
		public RowStatus(Object data, int status) {
			super();
			this.data = data;
			this.status = status;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}
		
		
	}
}