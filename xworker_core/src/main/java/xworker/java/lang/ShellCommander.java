package xworker.java.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellCommander {
	private static ShellCommander instance = new ShellCommander();
	InputStreamReader inr = new InputStreamReader(System.in);
	BufferedReader br = new BufferedReader(inr);
	
	private ShellCommander(){		
	}
	
	public ShellCommander getInstance(){
		return instance;
	}
	
	public String getLine() throws IOException{
		return br.readLine();
	}
	
	public void println(String message){
		System.out.println(message);
	}
	
	public void print(String message){
		System.out.print(message);
	}
	
	public void exit(){
		System.exit(0);
	}
}
