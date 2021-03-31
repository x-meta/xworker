package xworker.util.codeassist.textassists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.codeassist.CodeAssitContent;
import xworker.util.codeassist.TextAssistor;

public class DataReactorTextAssistor implements TextAssistor {
	List<CodeAssitContent> keyWords = new ArrayList<CodeAssitContent>();
	
	public DataReactorTextAssistor() {
		keyWords.add(new CodeAssitContent("fire", "params:fire", CodeAssitContent.IMAGE_NORMAL).setDocument("事件"));
		keyWords.add(new CodeAssitContent("monitorStatus", "params:monitorStatus", CodeAssitContent.IMAGE_NORMAL).setDocument("是否监控状态"));
		keyWords.add(new CodeAssitContent("modify", "params:modify", CodeAssitContent.IMAGE_NORMAL).setDocument("是否监听修改事件"));
		keyWords.add(new CodeAssitContent("defaultSelection", "params:defaultSelection", CodeAssitContent.IMAGE_NORMAL).setDocument("是否监听默认选择事件"));
		keyWords.add(new CodeAssitContent("thing", "filter:thing", CodeAssitContent.IMAGE_NORMAL).setDocument("模型过滤器"));
		keyWords.add(new CodeAssitContent("file", "filter:file", CodeAssitContent.IMAGE_NORMAL).setDocument("文件过滤器"));
		keyWords.add(new CodeAssitContent("source", "filter:source", CodeAssitContent.IMAGE_NORMAL).setDocument("源过滤器"));
		keyWords.add(new CodeAssitContent("selected", "fire:selected", CodeAssitContent.IMAGE_NORMAL).setDocument("事件-已选择"));
		keyWords.add(new CodeAssitContent("added", "fire:added", CodeAssitContent.IMAGE_NORMAL).setDocument("事件-已增加"));
		keyWords.add(new CodeAssitContent("updated", "fire:updated", CodeAssitContent.IMAGE_NORMAL).setDocument("事件-已修改"));
		keyWords.add(new CodeAssitContent("removed", "fire:removed", CodeAssitContent.IMAGE_NORMAL).setDocument("事件-已移除"));
		keyWords.add(new CodeAssitContent("loaded", "fire:loaded", CodeAssitContent.IMAGE_NORMAL).setDocument("事件-已加载"));
		keyWords.add(new CodeAssitContent("unselected", "fire:unselected", CodeAssitContent.IMAGE_NORMAL).setDocument("事件-已取消选择"));
	}
	
	@Override
	public List<CodeAssitContent> getContents(String codeType, String code, int cursorIndex, Thing thing,
			ActionContext actionContext) {
		if(codeType == null || !"reactors".equals(codeType.toLowerCase())) {
			return Collections.emptyList();
		}
		
		
		return keyWords;
	}

}
