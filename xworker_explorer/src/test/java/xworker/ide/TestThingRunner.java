package xworker.ide;

import org.xmeta.util.ThingRunner;

public class TestThingRunner {
	public static void main(String args[]){
		try{
			ThingRunner.run(new String[]{"xworker", "f:\\work\\docs\\Server.dml"});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
