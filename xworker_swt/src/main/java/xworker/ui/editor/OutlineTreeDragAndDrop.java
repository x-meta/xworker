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
package xworker.ui.editor;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class OutlineTreeDragAndDrop implements DragSourceListener ,DropTargetListener {
	Tree dragTree;
	TextTransfer textTransfer;
	ActionContext actionContext;

	public static void attach(Tree tree){
		attach(tree, null);
	}
	
	public static void attach(Tree tree, ActionContext actionContext){
		new OutlineTreeDragAndDrop(tree, actionContext );
	}
	
	public OutlineTreeDragAndDrop(Tree tree, ActionContext actionContext){
		this.dragTree = tree;
		this.actionContext = actionContext;
		textTransfer = TextTransfer.getInstance();

		DragSource source = new DragSource(dragTree, DND.DROP_MOVE
				| DND.DROP_COPY);
		source.setTransfer(new Transfer[] { textTransfer }); // 指定允许的传输类型
		source.addDragListener(this);

		// 将dropTable指定为Drop Target,
		DropTarget target = new DropTarget(dragTree, DND.DROP_MOVE
				| DND.DROP_COPY | DND.DROP_DEFAULT);
		target.setTransfer(new Transfer[] { textTransfer });
		target.addDropListener(this);
	}
	
	public void dragFinished(DragSourceEvent event) {
	}

	public void dragSetData(DragSourceEvent event) {
		if (TextTransfer.getInstance().isSupportedType(event.dataType)){
           if (((DragSource) event.widget).getControl()  instanceof  Tree){
               TreeItem selection  =  dragTree.getSelection()[ 0 ];
               Thing thing = (Thing) selection.getData();
               event.data = thing.getMetadata().getPath();
           }
       }
	}

	public void dragStart(DragSourceEvent event) {	

	}

	public void dragEnter(DropTargetEvent arg0) {
	}

	public void dragLeave(DropTargetEvent arg0) {
	}

	public void dragOperationChanged(DropTargetEvent arg0) {
	}

	public void dragOver(DropTargetEvent event) {
		TreeItem item = (TreeItem) event.item;
		item.setExpanded(true);
	}

	public void drop(DropTargetEvent event) {
		if (textTransfer.isSupportedType(event.currentDataType)){
            String text = (String) event.data;
                        
            if(event.item ==  dragTree.getSelection()[0]){
            	return;
            }
            
            Thing shellThing = World.getInstance().getThing("ui:worldExplorer:swt.dataExplorerParts.OutlineTreeDragDropDialog:/@shell");
            ActionContext context = new ActionContext();
            context.put("targetItem", event.item);
            context.put("sourceItem", dragTree.getSelection()[0]);
            context.put("sourcePath", text);
            context.put("refreshMenuSelection", actionContext.get("refreshMenuSelection"));
            
            Shell shell = (Shell) shellThing.doAction("create", context);
            shell.setLocation(event.x, event.y);
            
            TreeItem targetItem = (TreeItem) event.item;
            if(targetItem.getParentItem() == null){
            	//根节点
            	Link link1 = (Link) context.get("movePre");
            	Link link2 = (Link) context.get("moveNext");
            	link1.setEnabled(false);
            	link2.setEnabled(false);
            }
            
            TreeItem targetParent = targetItem;
            while((targetParent = targetParent.getParentItem()) != null){
            	if(targetParent == dragTree.getSelection()[0]){
            		//父节点往子节点移动时不能移动
            		Link link1 = (Link) context.get("movePre");
                	Link link2 = (Link) context.get("moveNext");
                	link1.setEnabled(false);
                	link2.setEnabled(false);
                	link1 = (Link) context.get("movePaste");
                	link2 = (Link) context.get("movePasteAsChild");
                	link1.setEnabled(false);
                	link2.setEnabled(false);
            	}
            }
            shell.open();            
		}
	}

	public void dropAccept(DropTargetEvent arg0) {
	}

}