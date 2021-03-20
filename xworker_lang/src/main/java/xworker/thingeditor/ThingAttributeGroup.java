package xworker.thingeditor;

import org.xmeta.Thing;

import java.util.*;

public class ThingAttributeGroup implements Comparable<ThingAttributeGroup>{
    String name;
    int weight;
    List<Thing> attributes = new ArrayList<>();
    List<ThingAttributeGroup> childs = new ArrayList<>();

    private ThingAttributeGroup(String name, int weight){
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public List<Thing> getAttributes() {
        return attributes;
    }

    public List<ThingAttributeGroup> getChilds() {
        return childs;
    }

    public static List<ThingAttributeGroup> parseGroups(List<Thing> attributes){
        List<ThingAttributeGroup> groups = new ArrayList<>();
        for(Thing attribute : attributes){
            String groupName = attribute.getString("group");
            int groupIndex = attribute.getInt("groupIndex", 0);
            if(groupName == null){
                groupName = "";
            }
            groupName = groupName.trim();

            ThingAttributeGroup group = null;
            for(ThingAttributeGroup g : groups){
                if(g.name.equals(groupName)){
                    group = g;
                    break;
                }
            }
            if(group == null){
                group = new ThingAttributeGroup(groupName, groupIndex);
                groups.add(group);
            }

            group.attributes.add(attribute);
        }

        Collections.sort(groups);

        return groups;
    }


    @Override
    public int compareTo(ThingAttributeGroup o) {
        if("".equals(this.name)){
            //默认组总是排在最前面
            return -1;
        }

        if(weight < o.weight){
            return -1;
        }else if(weight == o.weight){
            //效果应该是按照继承的顺序，如果不是可考虑按名字排序
            return 0;
        }else{
            return 1;
        }
    }
}
