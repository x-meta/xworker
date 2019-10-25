package xworker.game.cocos2d.actions;

public class Cocos2dException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public Cocos2dException(String message){
		super(message);
	}
	
	public Cocos2dException(String message, Throwable t){
		super(message, t);
	}
}
