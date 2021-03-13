package xworker.javafx.event;

import javafx.scene.control.*;
import javafx.scene.media.MediaView;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import java.util.Locale;

public class ThingEventHandler implements EventHandler<Event> {
	Thing thing;
	ActionContext actionContext;

	public ThingEventHandler(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	@Override
	public void handle(Event event) {
		for (Thing child : thing.getChilds()) {
			child.getAction().run(actionContext, "event", event);
		}

		String ref = thing.getStringBlankAsNull("ref");
		if(ref != null){
			int index = ref.indexOf(":");
			if(index != -1){
				ActionContainer actions = actionContext.getObject(ref.substring(0, index).trim());
				String name = ref.substring(index + 1, ref.length()).trim();
				if(actions != null){
					actions.doAction(name, actionContext, "event", event);
				}
			}else{
				Thing refThing = World.getInstance().getThing(ref);
				if(refThing != null){
					for (Thing child : refThing.getChilds()) {
						child.getAction().run(actionContext, "event", event);
					}
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");

		Object parent = actionContext.getObject("parent");
		String eventName = self.getString("type");
		if(eventName == null || "".equals(eventName)){
			return;
		}

		// EventType eventType = new EventType(self.getMetadata().getName());
		EventHandler eventHandler = new ThingEventHandler(self, actionContext);

		if (parent instanceof Window) {
			Window window = (Window) parent;
			if ("onCloseRequest".equals(eventName)) {
				window.setOnCloseRequest(eventHandler);
			} else if ("onHidden".equals(eventName)) {
				window.setOnHidden(eventHandler);
			} else if ("onHiding".equals(eventName)) {
				window.setOnHiding(eventHandler);
			} else if ("onShowing".equals(eventName)) {
				window.setOnShowing(eventHandler);
			} else if ("onShown".equals(eventName)) {
				window.setOnShown(eventHandler);
			}
		}

		if (parent instanceof Node) {
			Node obj = (Node) parent;
			if ("onContextMenuRequested".equals(eventName)) {
				obj.setOnContextMenuRequested(eventHandler);
			} else if ("onDragDetected".equals(eventName)) {
				obj.setOnDragDetected(eventHandler);
			} else if ("onDragDone".equals(eventName)) {
				obj.setOnDragDone(eventHandler);
			} else if ("onDragDropped".equals(eventName)) {
				obj.setOnDragDropped(eventHandler);
			} else if ("onDragEntered".equals(eventName)) {
				obj.setOnDragEntered(eventHandler);
			} else if ("onDragExited".equals(eventName)) {
				obj.setOnDragExited(eventHandler);
			} else if ("onDragOver".equals(eventName)) {
				obj.setOnDragOver(eventHandler);
			} else if ("onInputMethodTextChanged".equals(eventName)) {
				obj.setOnInputMethodTextChanged(eventHandler);
			} else if ("onKeyPressed".equals(eventName)) {
				obj.setOnKeyPressed(eventHandler);
			} else if ("onKeyReleased".equals(eventName)) {
				obj.setOnKeyReleased(eventHandler);
			} else if ("onKeyTyped".equals(eventName)) {
				obj.setOnKeyTyped(eventHandler);
			} else if ("onMouseClicked".equals(eventName)) {
				obj.setOnMouseClicked(eventHandler);
			} else if ("onMouseDragEntered".equals(eventName)) {
				obj.setOnMouseDragEntered(eventHandler);
			} else if ("onMouseDragExited".equals(eventName)) {
				obj.setOnMouseDragExited(eventHandler);
			} else if ("onMouseDragged".equals(eventName)) {
				obj.setOnMouseDragged(eventHandler);
			} else if ("onMouseDragOver".equals(eventName)) {
				obj.setOnMouseDragOver(eventHandler);
			} else if ("onMouseDragReleased".equals(eventName)) {
				obj.setOnMouseDragReleased(eventHandler);
			} else if ("onMouseEntered".equals(eventName)) {
				obj.setOnMouseEntered(eventHandler);
			} else if ("onMouseExited".equals(eventName)) {
				obj.setOnMouseExited(eventHandler);
			} else if ("onMouseMoved".equals(eventName)) {
				obj.setOnMouseMoved(eventHandler);
			} else if ("onMousePressed".equals(eventName)) {
				obj.setOnMousePressed(eventHandler);
			} else if ("onMouseReleased".equals(eventName)) {
				obj.setOnMouseReleased(eventHandler);
			} else if ("onRotate".equals(eventName)) {
				obj.setOnRotate(eventHandler);
			} else if ("onRotationFinished".equals(eventName)) {
				obj.setOnRotationFinished(eventHandler);
			} else if ("onRotationStarted".equals(eventName)) {
				obj.setOnRotationStarted(eventHandler);
			} else if ("onScroll".equals(eventName)) {
				obj.setOnScroll(eventHandler);
			} else if ("onScrollFinished".equals(eventName)) {
				obj.setOnScrollFinished(eventHandler);
			} else if ("onScrollStarted".equals(eventName)) {
				obj.setOnScrollStarted(eventHandler);
			} else if ("onSwipeDown".equals(eventName)) {
				obj.setOnSwipeDown(eventHandler);
			} else if ("onSwipeLeft".equals(eventName)) {
				obj.setOnSwipeLeft(eventHandler);
			} else if ("onSwipeRight".equals(eventName)) {
				obj.setOnSwipeRight(eventHandler);
			} else if ("onSwipeUp".equals(eventName)) {
				obj.setOnSwipeUp(eventHandler);
			} else if ("onTouchMoved".equals(eventName)) {
				obj.setOnTouchMoved(eventHandler);
			} else if ("onTouchPressed".equals(eventName)) {
				obj.setOnTouchPressed(eventHandler);
			} else if ("onTouchReleased".equals(eventName)) {
				obj.setOnTouchReleased(eventHandler);
			} else if ("onTouchStationary".equals(eventName)) {
				obj.setOnTouchStationary(eventHandler);
			} else if ("onZoom".equals(eventName)) {
				obj.setOnZoom(eventHandler);
			} else if ("onZoomFinished".equals(eventName)) {
				obj.setOnZoomFinished(eventHandler);
			} else if ("onZoomStarted".equals(eventName)) {
				obj.setOnZoomStarted(eventHandler);
			}
		}

		if (parent instanceof Scene) {
			Scene obj = (Scene) parent;
			if ("onContextMenuRequested".equals(eventName)) {
				obj.setOnContextMenuRequested(eventHandler);
			} else if ("onDragDetected".equals(eventName)) {
				obj.setOnDragDetected(eventHandler);
			} else if ("onDragDone".equals(eventName)) {
				obj.setOnDragDone(eventHandler);
			} else if ("onDragDropped".equals(eventName)) {
				obj.setOnDragDropped(eventHandler);
			} else if ("onDragEntered".equals(eventName)) {
				obj.setOnDragEntered(eventHandler);
			} else if ("onDragExited".equals(eventName)) {
				obj.setOnDragExited(eventHandler);
			} else if ("onDragOver".equals(eventName)) {
				obj.setOnDragOver(eventHandler);
			} else if ("onInputMethodTextChanged".equals(eventName)) {
				obj.setOnInputMethodTextChanged(eventHandler);
			} else if ("onKeyPressed".equals(eventName)) {
				obj.setOnKeyPressed(eventHandler);
			} else if ("onKeyReleased".equals(eventName)) {
				obj.setOnKeyReleased(eventHandler);
			} else if ("onKeyTyped".equals(eventName)) {
				obj.setOnKeyTyped(eventHandler);
			} else if ("onMouseClicked".equals(eventName)) {
				obj.setOnMouseClicked(eventHandler);
			} else if ("onMouseDragEntered".equals(eventName)) {
				obj.setOnMouseDragEntered(eventHandler);
			} else if ("onMouseDragExited".equals(eventName)) {
				obj.setOnMouseDragExited(eventHandler);
			} else if ("onMouseDragged".equals(eventName)) {
				obj.setOnMouseDragged(eventHandler);
			} else if ("onMouseDragOver".equals(eventName)) {
				obj.setOnMouseDragOver(eventHandler);
			} else if ("onMouseDragReleased".equals(eventName)) {
				obj.setOnMouseDragReleased(eventHandler);
			} else if ("onMouseEntered".equals(eventName)) {
				obj.setOnMouseEntered(eventHandler);
			} else if ("onMouseExited".equals(eventName)) {
				obj.setOnMouseExited(eventHandler);
			} else if ("onMouseMoved".equals(eventName)) {
				obj.setOnMouseMoved(eventHandler);
			} else if ("onMousePressed".equals(eventName)) {
				obj.setOnMousePressed(eventHandler);
			} else if ("onMouseReleased".equals(eventName)) {
				obj.setOnMouseReleased(eventHandler);
			} else if ("onRotate".equals(eventName)) {
				obj.setOnRotate(eventHandler);
			} else if ("onRotationFinished".equals(eventName)) {
				obj.setOnRotationFinished(eventHandler);
			} else if ("onRotationStarted".equals(eventName)) {
				obj.setOnRotationStarted(eventHandler);
			} else if ("onScroll".equals(eventName)) {
				obj.setOnScroll(eventHandler);
			} else if ("onScrollFinished".equals(eventName)) {
				obj.setOnScrollFinished(eventHandler);
			} else if ("onScrollStarted".equals(eventName)) {
				obj.setOnScrollStarted(eventHandler);
			} else if ("onSwipeDown".equals(eventName)) {
				obj.setOnSwipeDown(eventHandler);
			} else if ("onSwipeLeft".equals(eventName)) {
				obj.setOnSwipeLeft(eventHandler);
			} else if ("onSwipeRight".equals(eventName)) {
				obj.setOnSwipeRight(eventHandler);
			} else if ("onSwipeUp".equals(eventName)) {
				obj.setOnSwipeUp(eventHandler);
			} else if ("onTouchMoved".equals(eventName)) {
				obj.setOnTouchMoved(eventHandler);
			} else if ("onTouchPressed".equals(eventName)) {
				obj.setOnTouchPressed(eventHandler);
			} else if ("onTouchReleased".equals(eventName)) {
				obj.setOnTouchReleased(eventHandler);
			} else if ("onTouchStationary".equals(eventName)) {
				obj.setOnTouchStationary(eventHandler);
			} else if ("onZoom".equals(eventName)) {
				obj.setOnZoom(eventHandler);
			} else if ("onZoomFinished".equals(eventName)) {
				obj.setOnZoomFinished(eventHandler);
			} else if ("onZoomStarted".equals(eventName)) {
				obj.setOnZoomStarted(eventHandler);

			}
		}

		if (parent instanceof ComboBoxBase) {
			ComboBoxBase obj = (ComboBoxBase) parent;
			if ("onAction".equals(eventName)) {
				obj.setOnAction(eventHandler);
			} else if ("onHidden".equals(eventName)) {
				obj.setOnHidden(eventHandler);
			} else if ("onHiding".equals(eventName)) {
				obj.setOnHiding(eventHandler);
			} else if ("onShowing".equals(eventName)) {
				obj.setOnShowing(eventHandler);
			} else if ("onShown".equals(eventName)) {
				obj.setOnShown(eventHandler);
			}
		}

		if (parent instanceof ButtonBase) {
			ButtonBase obj = (ButtonBase) parent;
			if ("onAction".equals(eventName)) {
				obj.setOnAction(eventHandler);
			}
		}

		if (parent instanceof MenuItem) {
			MenuItem obj = (MenuItem) parent;
			if ("onAction".equals(eventName)) {
				obj.setOnAction(eventHandler);
			} else if ("onMenuValidation".equals(eventName)) {
				obj.setOnMenuValidation(eventHandler);
			}
		}

		if (parent instanceof Menu) {
			Menu obj = (Menu) parent;
			if ("onHidden".equals(eventName)) {
				obj.setOnHidden(eventHandler);
			} else if ("onHiding".equals(eventName)) {
				obj.setOnHiding(eventHandler);
			} else if ("onShowing".equals(eventName)) {
				obj.setOnShowing(eventHandler);
			} else if ("onShown".equals(eventName)) {
				obj.setOnShown(eventHandler);
			}
		}
		
		if (parent instanceof TableView) {
			TableView<Object> obj = (TableView<Object>) parent;
			if ("onScrollToColumn".equals(eventName)) {
				obj.setOnScrollToColumn(eventHandler);
			} else if ("onScrollTo".equals(eventName)) {
				obj.setOnScrollTo(eventHandler);
			} else if ("onSort".equals(eventName)) {
				obj.setOnSort(eventHandler);
			}
		}

		if(parent instanceof Tab){
			Tab obj = (Tab) parent;
			if("onClosed".equals(eventName)) {
				obj.setOnClosed(eventHandler);
			}else if("onCloseRequest".equals(eventName)) {
				obj.setOnCloseRequest(eventHandler);
			}else if("onSelectionChanged".equals(eventName)) {
				obj.setOnSelectionChanged(eventHandler);
			}
		}

		if(parent instanceof TextField){
			TextField obj = (TextField) parent;
			if("onAction".equals(eventName)) {
				obj.setOnAction(eventHandler);
			}
		}

		if(parent instanceof TreeTableView){
			TreeTableView obj = (TreeTableView) parent;
			if("onScrollTo".equals(eventName)) {
				obj.setOnScrollTo(eventHandler);
			}else if("onScrollToColumn".equals(eventName)) {
				obj.setOnScrollToColumn(eventHandler);
			}else if("onSort".equals(eventName)) {
				obj.setOnSort(eventHandler);
			}
		}

		if(parent instanceof TreeTableColumn){
			TreeTableColumn obj = (TreeTableColumn) parent;
			if("onEditCancel".equals(eventName)) {
				obj.setOnEditCancel(eventHandler);
			}else if("onEditCommit".equals(eventName)) {
				obj.setOnEditCommit(eventHandler);
			}else if("onEditStart".equals(eventName)) {
				obj.setOnEditStart(eventHandler);
			}
		}

		if(parent instanceof  TreeView){
			TreeView obj = (TreeView) parent;
			if("onEditCancel".equals(eventName)) {
				obj.setOnEditCancel(eventHandler);
			}else if("onEditCommit".equals(eventName)) {
				obj.setOnEditCommit(eventHandler);
			}else if("onEditStart".equals(eventName)) {
				obj.setOnEditStart(eventHandler);
			}else if("onScrollTo".equals(eventName)) {
				obj.setOnScrollTo(eventHandler);
			}
		}

		if(parent instanceof MediaView){
			MediaView obj = (MediaView) parent;
			if("onError".equals(eventName)) {
				obj.setOnError(eventHandler);
			}
		}

		if(parent instanceof ContextMenu){
			ContextMenu obj = (ContextMenu) parent;
			if("onAction".equals(eventName)) {
				obj.setOnAction(eventHandler);
			}
		}

		if(parent instanceof ChoiceBox){
			ChoiceBox<?> obj = (ChoiceBox<?>) parent;
			if ("onAction".equals(eventName)) {
				obj.setOnAction(eventHandler);
			}else if ("onHidden".equals(eventName)) {
				obj.setOnHidden(eventHandler);
			} else if ("onHiding".equals(eventName)) {
				obj.setOnHiding(eventHandler);
			} else if ("onShowing".equals(eventName)) {
				obj.setOnShowing(eventHandler);
			} else if ("onShown".equals(eventName)) {
				obj.setOnShown(eventHandler);
			}
		}
	}

}
