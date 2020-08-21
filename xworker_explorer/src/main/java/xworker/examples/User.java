package xworker.examples;

import org.xmeta.ActionContext;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;

@ActionClass(creator="getUser")
public class User {
	@ActionField
	public String name;
	
	int age;
	
	public void println() {
		System.out.println(name + " age is " + age);
	}
	
	public static User getUser(ActionContext actionContext) {
		User user = new User();
		user.age = 20;
		return user;
	}
}
