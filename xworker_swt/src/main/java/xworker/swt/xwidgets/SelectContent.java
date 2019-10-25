package xworker.swt.xwidgets;


public class SelectContent implements Comparable<SelectContent>{
	public String value;
	public String label;
	public String image;
	public Object object;
	
	public SelectContent(String value){
		this(value, null, null);
	}
	
	public SelectContent(String value, Object object){
		this(value, null, null, object);
	}
	
	public SelectContent(String value, String label){
		this(value, label, null);
	}
	
	public SelectContent(String value, String label, Object object){
		this(value, label, null, object);
	}

	public SelectContent(String value, String label, String image){
		this(value, label, image, null);
	}
	
	public SelectContent(String value, String label, String image, Object object){
		this.value = value;
		this.label = label;
		this.image = image;
		this.object = object;
	}
	
	@Override
	public int compareTo(SelectContent o) {
		if(label != null && o.label != null){
			return label.compareTo(o.label);
		}else{
			return value.compareTo(o.value);
		}
	}

	@Override
	public String toString() {
		return "SelectContent [value=" + value + ", label=" + label
				+ ", image=" + image + "]";
	}
	
}
