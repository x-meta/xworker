package xworker.util;

import org.xmeta.Thing;

/**
 * 事物分组的相关工具类。
 * 
 * @author zyx
 *
 */
public class ThingGroupUtils {
	/**
	 * 获取模型的默认分组，根据当前的Local选择适合的分组字符串。
	 * 
	 * @param thing
	 * @return
	 */
	public static String getGroup(Thing thing) {
		return getGroup(thing, null);
	}
	
	/**
	 * 获取指定所有者的分组，如果没有或所有者为null则返回默认分组。
	 * 
	 * 一个事物可能会注册到多个事物下，在不同的事物下分组可能是不一样的，为解决这个问题
	 * 引入分组的所有者。如果一个分组是path$group的格式，那么group是针对path对应的
	 * 事物的。
	 * 
	 * @param thing 从该事物上获取分组
	 * @param owner 分组的所有者
	 * 
	 * @return 分组
	 */
	public static String getGroup(Thing thing, Thing owner) {
		String registPath = null;
		if(owner != null) {
			registPath = owner.getMetadata().getPath() + "$";
		}
		String group = thing.getMetadata().getGroup();
		if(group == null || "".equals(group)) {
			return group;
		}else {
			String defaultGroup = null;
			String ownerGroup = null;
			for(String g : group.split("[,]")) {
				if(registPath != null && g.startsWith(registPath)) {
					ownerGroup = append(ownerGroup,  g.substring(registPath.length(), g.length()));
				}else {
					defaultGroup = append(defaultGroup, g);
				}
			}
			
			if(ownerGroup != null) {
				return ownerGroup;
			}else {
				return group;
			}
		}
	}
	
	public static String append(String first, String second) {
		if(first == null) {
			return second;
		}else {
			return first + "," + second;
		}
	}
	
	/**
	 * 设置所有者的分组。注意该方法并不会保存事物模型。
	 * 
	 * 注意，如果owner下有多个gorup，那么会清除掉只剩新的那个。该方法只能保留一个ownder的分组。
	 * 
	 * @param thing
	 * @param owner
	 * @param groupAttrName
	 * @param group
	 */
	public static void setGroup(Thing thing, Thing owner, String groupAttrName, String group) {
		String ogroup = owner.getMetadata().getPath() + "$" + group;
		String oldGroup = thing.getStringBlankAsNull(groupAttrName);
		if(oldGroup == null) {
			thing.set(groupAttrName, ogroup);
		}else {
			String newGroup = null;
			//过滤掉owner原有的group
			for(String g : oldGroup.split("[,]")) {
				if(g.startsWith(owner.getMetadata().getPath() + "$")) {
					continue;
				}
				
				if(newGroup == null) {
					newGroup = g;
				}else {
					newGroup= newGroup + "," + g;
				}
			}
			
			//把owner的group加到末尾
			if(newGroup == null) {
				newGroup = ogroup;
			}else {
				newGroup= newGroup + "," + ogroup;
			}
			thing.set(groupAttrName, newGroup);
		}
	}
}
