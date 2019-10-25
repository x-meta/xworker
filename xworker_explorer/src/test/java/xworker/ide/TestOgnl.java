package xworker.ide;

import java.util.Random;

import org.xmeta.World;

public class TestOgnl {
	public static void main(String[] args){
		try{
			World world = World.getInstance();			
			world.init("./xworker/");
			
			//启动编辑器			
			//Thing thing = World.getInstance().getThing("_local.test.uifunction.TestUiFunction");
			
			//ActionContext actionContext = new ActionContext();
			//actionContext.put("thing", thing);
			
			//System.out.println(OgnlUtil.getValue("thing.doAction(\"run\", actionContext, null)", actionContext));
			
			
			Random r = new Random();
			for(int i=0; i<1000; i++){
				long t = 2222l;
				t  = t << 32;
				t = t  | (r.nextInt() & 0xFFFFFFF);
				//System.out.println(t);
				//System.out.println(t + " = " + Long.toString(t, 32));
				//System.out.println(t >>> 32);
				if(t >>> 32 != 2222){
					System.out.println("error");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
