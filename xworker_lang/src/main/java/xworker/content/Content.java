package xworker.content;

import org.xmeta.Thing;

public interface Content <T>{
    String getType();

    T getContent();

    /**
     * 返回用于定义内容的模型。
     */
    Thing getThing();
}
