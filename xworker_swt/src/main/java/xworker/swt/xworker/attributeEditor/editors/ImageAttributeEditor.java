package xworker.swt.xworker.attributeEditor.editors;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.form.FormModifyListener;
import xworker.swt.form.GridData;
import xworker.swt.util.SwtUtils;
import xworker.swt.xworker.attributeEditor.AttributeEditor;

public class ImageAttributeEditor extends AttributeEditor implements PaintListener{
	Canvas canvas; 
	Object value;
	
	public ImageAttributeEditor(Thing formThing, Thing attribute, GridData gridData, ActionContext actionContext) {
		super(formThing, attribute, gridData, actionContext);
	}

	@Override
	public Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);
		canvas.addPaintListener(this);
		
		Map<String, String> params = this.getParams();
		if(params.size() > 0) {
			//布局，通过设置的参数
			Thing gridThing = new Thing("xworker.swt.layout.GridData");
			for(String key : params.keySet()) {
				gridThing.put(key, params.get(key));
			}
			
			ActionContext ac = new ActionContext();
			ac.put("parent", canvas);
			gridThing.doAction("create", ac);
		}else {
			org.eclipse.swt.layout.GridData gridData = new org.eclipse.swt.layout.GridData();
			gridData.horizontalSpan = getColspan(xgridData.colspan);
			gridData.verticalSpan = this.xgridData.rowspan;
			canvas.setLayoutData(gridData);
		}
		
		this.context.g().put(inputName, this);
		return canvas;
	}


	/**
	 * 重新绘制图片。
	 */
	public void redraw() {
		canvas.redraw();
	}
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value, String viewPattern, String editPattern) {
		this.value = value;
		redraw();
	}

	@Override
	public void paintControl(PaintEvent e) {
		Image image = getImage();
		if(image == null) {
			//没有图像可以绘制。
			return;
		}
		
		int x = 0;
		int y = 0;
		int width ;
		int height;
		int imageWidth = image.getImageData().width;
		int imageHeight = image.getImageData().height;
		GC gc = e.gc;
		Rectangle canvasRect = canvas.getClientArea();
		width = canvasRect.width;
		height = canvasRect.height;
		Map<String, String> params = getParams();
		String imageStyle = params.get("imageStyle");
	
		if(params.size() == 0 || "FILL".equals(imageStyle)) {
			//默认样式是按宽或按高适配
			
			double rw = 1d * width / imageWidth;
			double rh = 1d * height / imageHeight;
			if(rw < rh) {
				imageWidth = width;
				imageHeight = (int) (imageHeight * rw);
				y = (height - imageHeight) / 2;
			}else {
				imageHeight = height;
				imageWidth = (int) (imageWidth * rh);
				x = (width - imageWidth) / 2;
			}						
		}else if("FILL_BOTH".equals(imageStyle)) {
			imageWidth = width;
			imageHeight = height;
		}
		
		gc.drawImage(image, 0, 0, image.getImageData().width, image.getImageData().height, 
				x, y, imageWidth, imageHeight);
	}
	
	public Image getImage() {
		if(value instanceof Image) {
			return (Image) value;
		}else if(value instanceof String) {
			return SwtUtils.createImage(canvas, (String) value, context); 
		}else {
			return null;
		}
	}

	@Override
	public Control getControl() {
		return canvas;
	}

}
