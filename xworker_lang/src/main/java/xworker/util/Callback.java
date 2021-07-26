package xworker.util;

public interface Callback <P,R>{
    public R call(P p);
}
