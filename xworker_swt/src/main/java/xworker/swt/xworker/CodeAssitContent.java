package xworker.swt.xworker;

public class CodeAssitContent implements Comparable<CodeAssitContent>{
	public String value;
	public String url;
	public String label;
	public String image;
	
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
}
