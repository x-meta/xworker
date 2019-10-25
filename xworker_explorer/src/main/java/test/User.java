package test;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class User {
	String name;
	int age;
	
	public static Object create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		User user = new User();
		user.name = self.getString("name");
		user.age = self.getInt("age");
		
		actionContext.g().put(user.name, user);
		return user;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", age=" + age + "]";
	}
	
	
}
