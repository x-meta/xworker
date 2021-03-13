package xworker.javafx.examples;

import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.Button;
import xworker.javafx.application.ThingApplication;

import java.util.HashMap;

public class Test {
    public static void main(String[] args){
        try{
            /*
            ThingApplication.checkStart();

            Button button = new Button();
            button.setText("hello world");
            ObservableMap<Object,Object> properties = button.getProperties();
            System.out.println(properties);
            System.out.println(button.textProperty());
            for(Object key : properties.keySet()){
                System.out.println(key + "=" + properties.get(key));
            }
            System.out.print("Finished");*/

            SimpleMapProperty<String, Object> map = new SimpleMapProperty<String, Object>(FXCollections.observableMap(new HashMap<>()));
            map.put("name", new SimpleStringProperty());
            System.out.println(map.get("name"));
            map.put("name", "abc");
            System.out.println(map.get("name"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
