package xworker.swt.xworker.codeassist.textassists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xworker.CodeAssitContent;
import xworker.swt.xworker.codeassist.TextAssistor;

public class DataBinderTextAssistor implements TextAssistor{
	List<CodeAssitContent> keyWords = new ArrayList<CodeAssitContent>();
	
	public DataBinderTextAssistor() {
		keyWords.add(new CodeAssitContent("backgroundImage", "backgroundImage", CodeAssitContent.IMAGE_NORMAL).setDocument("背景图像"));
		keyWords.add(new CodeAssitContent("background", "background", CodeAssitContent.IMAGE_NORMAL).setDocument("背景色"));
		keyWords.add(new CodeAssitContent("cursor", "cursor", CodeAssitContent.IMAGE_NORMAL).setDocument("图标"));
		keyWords.add(new CodeAssitContent("enabled", "enabled", CodeAssitContent.IMAGE_NORMAL).setDocument("激活"));
		keyWords.add(new CodeAssitContent("font", "font", CodeAssitContent.IMAGE_NORMAL).setDocument("字体"));
		keyWords.add(new CodeAssitContent("foreground", "foreground", CodeAssitContent.IMAGE_NORMAL).setDocument("前景色"));
		keyWords.add(new CodeAssitContent("toolTip", "toolTip", CodeAssitContent.IMAGE_NORMAL).setDocument("提示"));
		keyWords.add(new CodeAssitContent("touchEnabled", "touchEnabled", CodeAssitContent.IMAGE_NORMAL).setDocument("是否激活触摸"));
		keyWords.add(new CodeAssitContent("visible", "visible", CodeAssitContent.IMAGE_NORMAL).setDocument("是否可见"));
		keyWords.add(new CodeAssitContent("alignment", "alignment", CodeAssitContent.IMAGE_NORMAL).setDocument("Alignment"));
		keyWords.add(new CodeAssitContent("append", "append", CodeAssitContent.IMAGE_NORMAL).setDocument("是否追加"));
		keyWords.add(new CodeAssitContent("backgroundMode", "backgroundMode", CodeAssitContent.IMAGE_NORMAL).setDocument("背景模式"));
		keyWords.add(new CodeAssitContent("doubleClickEnabled", "doubleClickEnabled", CodeAssitContent.IMAGE_NORMAL).setDocument("是否可双击"));
		keyWords.add(new CodeAssitContent("editable", "editable", CodeAssitContent.IMAGE_NORMAL).setDocument("是否可编辑"));
		keyWords.add(new CodeAssitContent("grayed", "grayed", CodeAssitContent.IMAGE_NORMAL).setDocument("是否变灰"));
		keyWords.add(new CodeAssitContent("image", "image", CodeAssitContent.IMAGE_NORMAL).setDocument("图片"));
		keyWords.add(new CodeAssitContent("increment", "increment", CodeAssitContent.IMAGE_NORMAL).setDocument("增加量"));
		keyWords.add(new CodeAssitContent("maximum", "maximum", CodeAssitContent.IMAGE_NORMAL).setDocument("最大值"));
		keyWords.add(new CodeAssitContent("minimum", "minimum", CodeAssitContent.IMAGE_NORMAL).setDocument("最小值"));
		keyWords.add(new CodeAssitContent("orientation", "orientation", CodeAssitContent.IMAGE_NORMAL).setDocument("orientation"));
		keyWords.add(new CodeAssitContent("pareIncrement", "pareIncrement", CodeAssitContent.IMAGE_NORMAL).setDocument("pareIncrement"));
		keyWords.add(new CodeAssitContent("selection", "selection", CodeAssitContent.IMAGE_NORMAL).setDocument("选择"));
		keyWords.add(new CodeAssitContent("selectionBoolean", "selectionBoolean", CodeAssitContent.IMAGE_NORMAL).setDocument("选择"));
		keyWords.add(new CodeAssitContent("state", "state", CodeAssitContent.IMAGE_NORMAL).setDocument("状态"));
		keyWords.add(new CodeAssitContent("text", "text", CodeAssitContent.IMAGE_NORMAL).setDocument("文本"));
		keyWords.add(new CodeAssitContent("thumb", "thumb", CodeAssitContent.IMAGE_NORMAL).setDocument("thumb"));
		keyWords.add(new CodeAssitContent("topIndex", "topIndex", CodeAssitContent.IMAGE_NORMAL).setDocument("topIndex"));
		keyWords.add(new CodeAssitContent("message", "message", CodeAssitContent.IMAGE_NORMAL).setDocument("消息"));
	}
	
	@Override
	public List<CodeAssitContent> getContents(String codeType, String code, int cursorIndex, Thing thing,
			ActionContext actionContext) {
		if(codeType == null || !"databind".equals(codeType.toLowerCase())) {
			return Collections.emptyList();
		}
		
		
		return keyWords;
	}

}
