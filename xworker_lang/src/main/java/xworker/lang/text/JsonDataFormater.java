package xworker.lang.text;

public interface JsonDataFormater {
	public String format(Object data, String ident);
	
	public Object parse(String json);
}
