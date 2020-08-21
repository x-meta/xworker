package xworker.examples;

import org.xmeta.ActionContext;
import org.xmeta.World;
import org.xmeta.annotation.ActionParams;

public class ParamsAnnotation {
	@ActionParams(names="world")
	public static void printlnWorld1(World world) {
		System.out.println(world);
	}
	
	@ActionParams(names="world")
	public static void printlnWorld2(World world, ActionContext actionContext) {
		System.out.println(world);
	}
}
