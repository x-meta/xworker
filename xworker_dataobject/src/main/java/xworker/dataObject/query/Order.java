package xworker.dataObject.query;

public class Order {
    public static final String ASC = "asc";
    public static final String DESC = "desc";

    String name;
    String dir;

    public Order(String name, String dir){
        this.name = name;
        this.dir = dir;
    }

    public String toSql(){
        return name + " " + dir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
