package xworker.content;

public class StringContent implements Content<String>{
    String type;
    String content;

    public StringContent(String type, String content){
        this.type = type;
        this.content = content;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getContent() {
        return content;
    }
}
