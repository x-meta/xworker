package xworker.ide;

import org.xmeta.World;

public class Test {
	public static void main(String[] args){
		try{
			World world = World.getInstance();			
			world.init("./xworker/");
			
			if(args.length > 0) {
				System.out.println ("Thing path is : " + args[0]);	
			}else {
				System.out.println("Please input thing path");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
