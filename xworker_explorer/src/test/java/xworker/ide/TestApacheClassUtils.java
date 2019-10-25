package xworker.ide;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.xmeta.Thing;
import org.xmeta.World;

public class TestApacheClassUtils {
	public static void main(String args[]){
		try{
			//测试性能
			World world = World.getInstance();			
			world.init("./xworker/");
			
			int count = 10000;
			long start = System.currentTimeMillis();
			for(int i=0; i<count; i++){
				new Thing("xworker.swt.widgets.Shell");
			}
			
			start = System.currentTimeMillis();
			for(int i=0; i<count; i++){
				new Thing("xworker.swt.widgets.Shell");
			}
			System.out.println("创建" + count + "个事物耗时：" + (System.currentTimeMillis() - start));
			
			start = System.currentTimeMillis();
			for(int i=0; i<count; i++){
				Thing thing = new Thing("xworker.swt.widgets.Shell");
				thing.put("name", "abcd");
			}
			System.out.println("创建" + count + "个事物并设置属性耗时：" + (System.currentTimeMillis() - start));
			
			start = System.currentTimeMillis();
			for(int i=0; i<count; i++){
				ConstructorUtils.invokeConstructor(Thing.class, "xworker.swt.widgets.Shell");
			}
			System.out.println("ConstructorUtils创建" + count + "个事物耗时：" + (System.currentTimeMillis() - start));
			
			start = System.currentTimeMillis();
			for(int i=0; i<count; i++){
				ConstructorUtils.invokeExactConstructor(Thing.class, "xworker.swt.widgets.Shell");
			}
			System.out.println("ConstructorUtils精确创建" + count + "个事物耗时：" + (System.currentTimeMillis() - start));
			
			start = System.currentTimeMillis();
			for(int i=0; i<count; i++){
				Thing thing = (Thing) ConstructorUtils.invokeConstructor(Thing.class, "xworker.swt.widgets.Shell");
				MethodUtils.invokeMethod(thing, "put", new String[]{"name", "abcd"});
			}
			System.out.println("ConstructorUtils创建" + count + "个事物并设置属性耗时：" + (System.currentTimeMillis() - start));
			
			start = System.currentTimeMillis();
			for(int i=0; i<count; i++){
				Thing thing = (Thing) ConstructorUtils.invokeExactConstructor(Thing.class, "xworker.swt.widgets.Shell");
				MethodUtils.invokeExactMethod(thing, "put", new Object[]{"name", "abcd"});
			}
			System.out.println("ConstructorUtils精确创建" + count + "个事物并设置属性耗时：" + (System.currentTimeMillis() - start));
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
