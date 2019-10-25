package xworker.libdgx.functions.scenes.scene2d.actions;

import org.xmeta.ActionContext;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class ActionsFunctions {
	public static Object addAction_action(ActionContext actionContext){
		Action action = (Action) actionContext.get("action");
		return Actions.addAction(action);
	}
	
	public static Object addAction_action_targetActor(ActionContext actionContext){
		Action action = (Action) actionContext.get("action");
		Actor targetActor = (Actor) actionContext.get("targetActor");
		return Actions.addAction(action, targetActor);
	}
	
	public static Object addListener_listener_capture(ActionContext actionContext){
		EventListener listener = (EventListener) actionContext.get("listener");
		Boolean capture = (Boolean) actionContext.get("capture");
		return Actions.addListener(listener, capture);
	}
	
	public static Object addListener_listener_capture_targetActor(ActionContext actionContext){
		EventListener listener = (EventListener) actionContext.get("listener");
		Boolean capture = (Boolean) actionContext.get("capture");
		Actor targetActor = (Actor) actionContext.get("targetActor");
		return Actions.addListener(listener, capture, targetActor);
	}
	
	public static Object after_action(ActionContext actionContext){
		Action action = (Action) actionContext.get("action");
		return Actions.after(action);
	}
	
	public static Object alpha_a(ActionContext actionContext){
		Number a = (Number) actionContext.get("a");
		return Actions.alpha(a.floatValue());
	}
	
	public static Object alpha_a_duration(ActionContext actionContext){
		Number a = (Number) actionContext.get("a");
		Number duration = (Number) actionContext.get("duration");
		return Actions.alpha(a.floatValue(), duration.floatValue());
	}
	
	public static Object alpha_a_duration_interpolation(ActionContext actionContext){
		Number a = (Number) actionContext.get("a");
		Number duration = (Number) actionContext.get("duration");
		Interpolation interpolation = (Interpolation) actionContext.get("interpolation");
		
		return Actions.alpha(a.floatValue(), duration.floatValue(), interpolation);
	}
	
	public static Object color_color(ActionContext actionContext){
		Color color = (Color) actionContext.get("color");
		return Actions.color(color);
	}
	
	public static Object color_color_duration(ActionContext actionContext){
		Color color = (Color) actionContext.get("color");
		Number duration = (Number) actionContext.get("duration");
		
		return Actions.color(color, duration.floatValue());
	}
	
	public static Object color_color_duration_interpolation(ActionContext actionContext){
		Color color = (Color) actionContext.get("color");
		Number duration = (Number) actionContext.get("duration");
		Interpolation interpolation = (Interpolation) actionContext.get("interpolation");
		
		return Actions.color(color, duration.floatValue(), interpolation);
	}
	
	public static Object delay_duration(ActionContext actionContext){
		Number duration = (Number) actionContext.get("duration");
		
		return Actions.delay(duration.floatValue());
	}
	
	public static Object delay_duration_delayedAction(ActionContext actionContext){
		Number duration = (Number) actionContext.get("duration");
		Action delayedAction = (Action) actionContext.get("delayedAction");
		
		return Actions.delay(duration.floatValue(), delayedAction);
	}
	
	public static Object fadeIn_duration(ActionContext actionContext){
		Number duration = (Number) actionContext.get("duration");
		
		return Actions.fadeIn(duration.floatValue());
	}
	
	public static Object fadeIn_duration_interpolation(ActionContext actionContext){
		Number duration = (Number) actionContext.get("duration");
		Interpolation interpolation = (Interpolation) actionContext.get("interpolation");
		
		return Actions.fadeIn(duration.floatValue(), interpolation);
	}
	
	public static Object fadeOut_duration(ActionContext actionContext){
		Number duration = (Number) actionContext.get("duration");
		
		return Actions.fadeOut(duration.floatValue());
	}
	
	public static Object fadeOut_duration_interpolation(ActionContext actionContext){
		Number duration = (Number) actionContext.get("duration");
		Interpolation interpolation = (Interpolation) actionContext.get("interpolation");
		
		return Actions.fadeOut(duration.floatValue(), interpolation);
	}
	
	public static Object forever_repeatedAction(ActionContext actionContext){
		Action repeatedAction = (Action) actionContext.get("repeatedAction");
		
		return Actions.forever(repeatedAction);
	}
	
	public static Object hide(ActionContext actionContext){
		return Actions.hide();
	}
	
	public static Object layout_enabled(ActionContext actionContext){
		Boolean enabled = (Boolean) actionContext.get("enabled");
		
		return Actions.layout(enabled);
	}
	
	public static Object moveBy_amountX_amountY(ActionContext actionContext){
		Number amountX = (Number) actionContext.get("amountX");
		Number amountY = (Number) actionContext.get("amountY");
		
		return Actions.moveBy(amountX.floatValue(), amountY.floatValue());
	}
	
	public static Object moveBy_amountX_amountY_duration(ActionContext actionContext){
		Number amountX = (Number) actionContext.get("amountX");
		Number amountY = (Number) actionContext.get("amountY");
		Number duration = (Number) actionContext.get("duration");
		
		return Actions.moveBy(amountX.floatValue(), amountY.floatValue(), duration.floatValue());
	}
	
	public static Object moveBy_amountX_amountY_duration_interpolation(ActionContext actionContext){
		Number amountX = (Number) actionContext.get("amountX");
		Number amountY = (Number) actionContext.get("amountY");
		Number duration = (Number) actionContext.get("duration");
		Interpolation interpolation = (Interpolation) actionContext.get("interpolation");
		
		return Actions.moveBy(amountX.floatValue(), amountY.floatValue(), duration.floatValue(), interpolation);
	}
	
	public static Object moveTo_x_y(ActionContext actionContext){
		Number x = (Number) actionContext.get("x");
		Number y = (Number) actionContext.get("y");
		
		return Actions.moveTo(x.floatValue(), y.floatValue());
	}
	
	public static Object moveTo_x_y_duration(ActionContext actionContext){
		Number x = (Number) actionContext.get("x");
		Number y = (Number) actionContext.get("y");
		Number duration = (Number) actionContext.get("duration");
		
		return Actions.moveTo(x.floatValue(), y.floatValue(), duration.floatValue());
	}
	
	public static Object moveTo_x_y_duration_interpolation(ActionContext actionContext){
		Number x = (Number) actionContext.get("x");
		Number y = (Number) actionContext.get("y");
		Number duration = (Number) actionContext.get("duration");
		Interpolation interpolation = (Interpolation) actionContext.get("interpolation");
		
		return Actions.moveTo(x.floatValue(), y.floatValue(), duration.floatValue(), interpolation);
	}
	
	public static Object parallel(ActionContext actionContext){
		return Actions.parallel();
	}
	
	public static Object parallel_actions(ActionContext actionContext){
		Action[] actions = (Action[]) actionContext.get("actions");
		return Actions.parallel(actions);
	}
	
	public static Object parallel_action1(ActionContext actionContext){
		Action action1 = (Action) actionContext.get("action1");
		return Actions.parallel(action1);
	}
	
	public static Object parallel_action1_action2(ActionContext actionContext){
		Action action1 = (Action) actionContext.get("action1");
		Action action2 = (Action) actionContext.get("action2");
		
		return Actions.parallel(action1, action2);
	}
	
	public static Object parallel_action1_action2_action3(ActionContext actionContext){
		Action action1 = (Action) actionContext.get("action1");
		Action action2 = (Action) actionContext.get("action2");
		Action action3 = (Action) actionContext.get("action3");
		
		return Actions.parallel(action1, action2, action3);
	}
	
	public static Object parallel_action1_action2_action3_action4(ActionContext actionContext){
		Action action1 = (Action) actionContext.get("action1");
		Action action2 = (Action) actionContext.get("action2");
		Action action3 = (Action) actionContext.get("action3");
		Action action4 = (Action) actionContext.get("action4");
		
		return Actions.parallel(action1, action2, action3, action4);
	}
	
	public static Object parallel_action1_action2_action3_action4_action5(ActionContext actionContext){
		Action action1 = (Action) actionContext.get("action1");
		Action action2 = (Action) actionContext.get("action2");
		Action action3 = (Action) actionContext.get("action3");
		Action action4 = (Action) actionContext.get("action4");
		Action action5 = (Action) actionContext.get("action5");
		
		return Actions.parallel(action1, action2, action3, action4, action5);
	}
	
	public static Object removeAction_action(ActionContext actionContext){
		Action action = (Action) actionContext.get("action");
		return Actions.removeAction(action);
	}
	
	public static Object removeAction_action_targetActor(ActionContext actionContext){
		Action action = (Action) actionContext.get("action");
		Actor targetActor = (Actor) actionContext.get("targetActor");
		
		return Actions.removeAction(action, targetActor);
	}
	
	public static Object removeActor(ActionContext actionContext){
		return Actions.removeActor();
	}
	
	public static Object removeActor_removeActor(ActionContext actionContext){
		Actor removeActor = (Actor) actionContext.get("removeActor");
		
		return Actions.removeActor(removeActor);
	}
	
	public static Object removeListener_listener_capture(ActionContext actionContext){
		EventListener listener = (EventListener) actionContext.get("listener");
		Boolean capture = (Boolean) actionContext.get("capture");
		
		return Actions.removeListener(listener, capture);
	}
	
	public static Object removeListener_listener_capture_targetActor(ActionContext actionContext){
		EventListener listener = (EventListener) actionContext.get("listener");
		Boolean capture = (Boolean) actionContext.get("capture");
		Actor targetActor = (Actor) actionContext.get("targetActor");
		
		return Actions.removeListener(listener, capture, targetActor);
	}
	
	public static Object repeat_count_repeatedAction(ActionContext actionContext){
		Number count = (Number) actionContext.get("count");
		Action repeatedAction = (Action) actionContext.get("repeatedAction");
		
		return Actions.repeat(count.intValue(), repeatedAction);
	}
	
	public static Object rotateBy_rotationAmount(ActionContext actionContext){
		Number rotationAmount = (Number) actionContext.get("rotationAmount");
		
		return Actions.rotateBy(rotationAmount.floatValue());
	}
	
	public static Object rotateBy_rotationAmount_duration(ActionContext actionContext){
		Number rotationAmount = (Number) actionContext.get("rotationAmount");
		Number duration = (Number) actionContext.get("duration");
		
		return Actions.rotateBy(rotationAmount.floatValue(), duration.floatValue());
	}
	
	public static Object rotateBy_rotationAmount_duration_interpolation(ActionContext actionContext){
		Number rotationAmount = (Number) actionContext.get("rotationAmount");
		Number duration = (Number) actionContext.get("duration");
		Interpolation interpolation = (Interpolation) actionContext.get("interpolation");
		
		return Actions.rotateBy(rotationAmount.floatValue(), duration.floatValue(), interpolation);
	}
	
	public static Object rotateTo_rotation(ActionContext actionContext){
		Number rotation = (Number) actionContext.get("rotation");
		
		return Actions.rotateTo(rotation.floatValue());
	}
	
	public static Object rotateTo_rotation_duration(ActionContext actionContext){
		Number rotation = (Number) actionContext.get("rotation");
		Number duration = (Number) actionContext.get("duration");
		
		return Actions.rotateTo(rotation.floatValue(), duration.floatValue());
	}
	
	public static Object rotateTo_rotation_duration_interpolation(ActionContext actionContext){
		Number rotation = (Number) actionContext.get("rotation");
		Number duration = (Number) actionContext.get("duration");
		Interpolation interpolation = (Interpolation) actionContext.get("interpolation");
		
		return Actions.rotateTo(rotation.floatValue(), duration.floatValue(), interpolation);
	}
	
	public static Object run_runnable(ActionContext actionContext){
		Runnable runnable = (Runnable) actionContext.get("runnable");
		
		return Actions.run(runnable);
	}
	
	public static Object scaleBy_amountX_amountY(ActionContext actionContext){
		Number amountX = (Number) actionContext.get("amountX");
		Number amountY = (Number) actionContext.get("amountY");
		
		return Actions.scaleBy(amountX.floatValue(), amountY.floatValue());
	}
	
	public static Object scaleBy_amountX_amountY_duration(ActionContext actionContext){
		Number amountX = (Number) actionContext.get("amountX");
		Number amountY = (Number) actionContext.get("amountY");
		Number duration = (Number) actionContext.get("duration");
		
		return Actions.scaleBy(amountX.floatValue(), amountY.floatValue(), duration.floatValue());
	}
	
	public static Object scaleBy_amountX_amountY_duration_interpolation(ActionContext actionContext){
		Number amountX = (Number) actionContext.get("amountX");
		Number amountY = (Number) actionContext.get("amountY");
		Number duration = (Number) actionContext.get("duration");
		Interpolation interpolation = (Interpolation) actionContext.get("interpolation");
		
		return Actions.scaleBy(amountX.floatValue(), amountY.floatValue(), duration.floatValue(), interpolation);
	}
	
	public static Object scaleTo_x_y(ActionContext actionContext){
		Number x = (Number) actionContext.get("x");
		Number y = (Number) actionContext.get("y");
		
		return Actions.scaleTo(x.floatValue(), y.floatValue());
	}
	
	public static Object scaleTo_x_y_duration(ActionContext actionContext){
		Number x = (Number) actionContext.get("x");
		Number y = (Number) actionContext.get("y");
		Number duration = (Number) actionContext.get("duration");
		
		return Actions.scaleTo(x.floatValue(), y.floatValue(), duration.floatValue());
	}
	
	public static Object scaleTo_x_y_duration_interpolation(ActionContext actionContext){
		Number x = (Number) actionContext.get("x");
		Number y = (Number) actionContext.get("y");
		Number duration = (Number) actionContext.get("duration");
		Interpolation interpolation = (Interpolation) actionContext.get("interpolation");
		
		return Actions.scaleTo(x.floatValue(), y.floatValue(), duration.floatValue(), interpolation);
	}
	
	public static Object sequence(ActionContext actionContext){
		return Actions.sequence();
	}
	
	public static Object sequence_actions(ActionContext actionContext){
		Action[] actions = (Action[]) actionContext.get("actions");
		return Actions.sequence(actions);
	}
	
	public static Object sequence_action1(ActionContext actionContext){
		Action action1 = (Action) actionContext.get("action1");
		return Actions.sequence(action1);
	}
	
	public static Object sequence_action1_action2(ActionContext actionContext){
		Action action1 = (Action) actionContext.get("action1");
		Action action2 = (Action) actionContext.get("action2");
		
		return Actions.sequence(action1, action2);
	}
	
	public static Object sequence_action1_action2_action3(ActionContext actionContext){
		Action action1 = (Action) actionContext.get("action1");
		Action action2 = (Action) actionContext.get("action2");
		Action action3 = (Action) actionContext.get("action3");
		
		return Actions.sequence(action1, action2, action3);
	}
	
	public static Object sequence_action1_action2_action3_action4(ActionContext actionContext){
		Action action1 = (Action) actionContext.get("action1");
		Action action2 = (Action) actionContext.get("action2");
		Action action3 = (Action) actionContext.get("action4");
		Action action4 = (Action) actionContext.get("action3");
		
		return Actions.sequence(action1, action2, action3, action4);
	}
	
	public static Object sequence_action1_action2_action3_action4_action5(ActionContext actionContext){
		Action action1 = (Action) actionContext.get("action1");
		Action action2 = (Action) actionContext.get("action2");
		Action action3 = (Action) actionContext.get("action4");
		Action action4 = (Action) actionContext.get("action3");
		Action action5 = (Action) actionContext.get("action5");
		
		return Actions.sequence(action1, action2, action3, action4, action5);
	}
	
	public static Object show(ActionContext actionContext){
		return Actions.show();
	}
	
	public static Object sizeBy_amountX_amountY(ActionContext actionContext){
		Number amountX = (Number) actionContext.get("amountX");
		Number amountY = (Number) actionContext.get("amountY");
		
		return Actions.sizeBy(amountX.floatValue(), amountY.floatValue());
	}
	
	public static Object sizeBy_amountX_amountY_duration(ActionContext actionContext){
		Number amountX = (Number) actionContext.get("amountX");
		Number amountY = (Number) actionContext.get("amountY");
		Number duration = (Number) actionContext.get("duration");
		
		return Actions.sizeBy(amountX.floatValue(), amountY.floatValue(), duration.floatValue());
	}
	
	public static Object sizeBy_amountX_amountY_duration_interpolation(ActionContext actionContext){
		Number amountX = (Number) actionContext.get("amountX");
		Number amountY = (Number) actionContext.get("amountY");
		Number duration = (Number) actionContext.get("duration");
		Interpolation interpolation = (Interpolation) actionContext.get("interpolation");
		
		return Actions.sizeBy(amountX.floatValue(), amountY.floatValue(), duration.floatValue(), interpolation);
	}
	
	public static Object sizeTo_x_y(ActionContext actionContext){
		Number x = (Number) actionContext.get("x");
		Number y = (Number) actionContext.get("y");
		
		return Actions.sizeTo(x.floatValue(), y.floatValue());
	}
	
	public static Object sizeTo_x_y_duration(ActionContext actionContext){
		Number x = (Number) actionContext.get("x");
		Number y = (Number) actionContext.get("y");
		Number duration = (Number) actionContext.get("duration");
		
		return Actions.sizeTo(x.floatValue(), y.floatValue(), duration.floatValue());
	}
	
	public static Object sizeTo_x_y_duration_interpolation(ActionContext actionContext){
		Number x = (Number) actionContext.get("x");
		Number y = (Number) actionContext.get("y");
		Number duration = (Number) actionContext.get("duration");
		Interpolation interpolation = (Interpolation) actionContext.get("interpolation");
		
		return Actions.sizeTo(x.floatValue(), y.floatValue(), duration.floatValue(), interpolation);
	}
	
	public static Object timeScale_scale_scaledAction(ActionContext actionContext){
		Number scale = (Number) actionContext.get("scale");
		Action scaledAction = (Action) actionContext.get("scaledAction");
		
		return Actions.timeScale(scale.floatValue(), scaledAction);
	}
	
	public static Object touchable_touchable(ActionContext actionContext){
		Touchable touchable = (Touchable) actionContext.get("touchable");
		
		return Actions.touchable(touchable);
	}
	
	public static Object visible_visible(ActionContext actionContext){
		Boolean visible = (Boolean) actionContext.get("visible");
		
		return Actions.visible(visible);
	}
}

