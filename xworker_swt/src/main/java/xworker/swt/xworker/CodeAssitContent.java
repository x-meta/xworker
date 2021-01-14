package xworker.swt.xworker;

/**
 * 代码辅助内容，是代码提示中的列表中的内容。比如输入System.时弹出的java.lang.System相关的方法列表，列表中的内容就是CodeAssitContent。
 * 
 * 选择CodeAssitContent会吧它的value插入到文本编辑器中。
 * 
 * @author zhangyuxiang
 *
 */
public class CodeAssitContent implements Comparable<CodeAssitContent>{
	public static final String IMAGE_NORMAL = "normalImage";
	public static final String IAMGE_METHOD = "methodImage";
	public static final String IAMGE_FIELD = "fieldImage";
	public static final String IMAGE_TABLE = "tableImage";
	public static final String IMAGE_COLUMN = "columnImage";
	
	public String value;
	public String url;
	public String label;
	public String image;
	public String document;
	
	public CodeAssitContent(String value, String url){
		this.value = value;
		this.url = url;
	}

	public CodeAssitContent(String value, String label, String image){
		this.value = value;
		this.label = label;
		this.image = image;
	}
	
	@Override
	public int compareTo(CodeAssitContent o) {
		if(value == null || o == null || o.value == null){
			return 1;
		}
		return value.compareTo(o.value);
	}
		
	
	public String getValue() {
		return value;
	}

	public CodeAssitContent setValue(String value) {
		this.value = value;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public CodeAssitContent setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public CodeAssitContent setLabel(String label) {
		this.label = label;
		return this;
	}

	public String getImage() {
		return image;
	}

	public CodeAssitContent setImage(String image) {
		this.image = image;
		return this;
	}

	/**
	 * 文档如果以url:开头，表明是一个网页。
	 * 
	 * @return
	 */
	public String getDocument() {
		return document;
	}

	/**
	 * 设置文档，可以加url:前缀。
	 * 
	 * @param document
	 * @return
	 */
	public CodeAssitContent setDocument(String document) {
		this.document = document;
		return this;
	}
	
}
