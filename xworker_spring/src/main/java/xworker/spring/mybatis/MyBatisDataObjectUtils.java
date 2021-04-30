package xworker.spring.mybatis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.xmeta.ActionException;
import xworker.dataObject.query.Condition;

import java.util.function.Consumer;

public class MyBatisDataObjectUtils {
    public static Wrapper<Object> createWrapperFromCondition(Condition condition) {
        QueryWrapper<Object> wrapper = new QueryWrapper<Object>();

        initWrapperFromCondition(condition, wrapper);
        return wrapper;
    }


    public static void initWrapperFromCondition(Condition condition, QueryWrapper<Object> wrapper){
        if(!condition.isActive()){
            return;
        }

        if(condition.getChilds().size() == 0){
            String join = condition.getJoin();
            if(Condition.OR.equals(join)){
                wrapper.or();
            }

            Object conditionValue = condition.getConditionValue();
            String column = condition.getAttributeName();
            String sqlColumn = condition.getConditionThing().getStringBlankAsNull("sqlColumn");
            if(sqlColumn != null){
                column = sqlColumn;
            }

            if(conditionValue != null || !condition.getConditionThing().getBoolean("ignoreNull")){
                byte operator = condition.getOperator();
                switch(operator){
                    case Condition.eq:
                    case Condition.equals:
                        wrapper.eq(column, conditionValue);
                        break;
                    case Condition.between:
                        if(conditionValue instanceof Object[]){
                            Object[] values = (Object[]) conditionValue;
                            wrapper.between(column, values[0], values[1]);
                        }else{
                            throw new ActionException("Between must has tow values.");
                        }
                        break;
                    case Condition.gt:
                        wrapper.gt(column, conditionValue);
                        break;
                    case Condition.in:
                        if(conditionValue instanceof Object[]){
                            Object[] values = (Object[]) conditionValue;
                            wrapper.in(column, values);
                        }else if(conditionValue instanceof String){
                            wrapper.in(column, (String) conditionValue);
                        }else{
                            wrapper.in(column, new Object[]{conditionValue});
                        }
                        break;
                    case Condition.uneq:
                        wrapper.ne(column, conditionValue);
                        break;
                    case Condition.gteq:
                        wrapper.ge(column, conditionValue);
                        break;
                    case Condition.lt:
                        wrapper.lt(column, conditionValue);
                        break;
                    case Condition.lteq:
                        wrapper.le(column, conditionValue);
                        break;
                    case Condition.like:
                        wrapper.like(column, String.valueOf(conditionValue));
                        break;
                    case Condition.llike:
                        String value = String.valueOf(conditionValue);
                        wrapper.like(column, "%" + value);
                        break;
                    case Condition.rlike:
                        wrapper.like(column, String.valueOf(conditionValue) + "%");
                        break;
                    case Condition.lrlike:
                        wrapper.like(column, "%" + String.valueOf(conditionValue) + "%");
                        break;
                    case Condition.regex:
                        wrapper.like(column, String.valueOf(conditionValue));
                        break;
                    case Condition.isnull:
                        wrapper.isNull(column);
                        break;
                    case Condition.notnull:
                        wrapper.isNotNull(column);
                        break;
                    case Condition.notin:
                        if(conditionValue instanceof Object[]){
                            Object[] values = (Object[]) conditionValue;
                            wrapper.notIn(column, values);
                        }else if(conditionValue instanceof String){
                            wrapper.notIn(column, (String) conditionValue);
                        }else{
                            wrapper.notIn(column, new Object[]{conditionValue});
                        }
                        break;
                }
            }
        }else if(condition.getChilds().size() > 0){
            Wrapper<Object> childWrapper = null;
            String join = condition.getJoin();
            if(Condition.OR.equals(join)){
                wrapper.or(new Consumer<QueryWrapper<Object>>() {
                    @Override
                    public void accept(QueryWrapper<Object> objectQueryWrapper) {
                        for(Condition child : condition.getChilds()){
                            initWrapperFromCondition(child, objectQueryWrapper);
                        }
                    }
                });
            }else{
                wrapper.and(new Consumer<QueryWrapper<Object>>() {
                    @Override
                    public void accept(QueryWrapper<Object> objectQueryWrapper) {
                        for(Condition child : condition.getChilds()){
                            initWrapperFromCondition(child, objectQueryWrapper);
                        }
                    }
                });
            }

        }
    }
}
