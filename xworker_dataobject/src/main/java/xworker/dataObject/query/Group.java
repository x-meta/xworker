package xworker.dataObject.query;

public class Group {
    String name;

    public Group(String name){
        this.name = name;
    }

    public String toSql(){
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
