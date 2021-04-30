package xworker.dataObject.annotation;

@BeanDataObject
public class User {
    @BeanDataObjectField(label="名字",sortable=true)
    String name;
}
