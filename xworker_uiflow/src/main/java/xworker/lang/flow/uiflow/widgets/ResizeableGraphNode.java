package xworker.lang.flow.uiflow.widgets;

import org.eclipse.draw2d.IFigure;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.core.widgets.internal.GraphLabel;

public class ResizeableGraphNode extends GraphNode{

	public ResizeableGraphNode(IContainer graphModel, int style, String text) {
		super(graphModel, style, text);
	}

	protected IFigure createFigureForModel() {
		GraphNode node = this;
		boolean cacheLabel = (this).cacheLabel();
		GraphLabel label = new ResizeableGraphLabel(this, node.getText(), node.getImage(), cacheLabel);
		label.setFont(this.getFont());
		if (checkStyle(ZestStyles.NODES_HIDE_TEXT)) {
			label.setText("");
		}
		updateFigureForModel(label);
		return label;
	}
}
