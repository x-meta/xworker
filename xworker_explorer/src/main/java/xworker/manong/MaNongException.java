package xworker.manong;

public class MaNongException  extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public MaNongException(){
        super();
    }

    public MaNongException(String message){
        super(message);
    }

    public MaNongException(String message, Throwable cause){
        super(message, cause);        
    }
    
    public MaNongException(Throwable cause){
        super(cause);        
    }

}
