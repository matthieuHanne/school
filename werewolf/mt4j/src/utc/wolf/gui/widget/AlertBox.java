package utc.wolf.gui.widget;

import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;

import utc.wolf.gui.Theme;
import utc.wolf.gui.Theme.StyleID;
import utc.wolf.model.Constants;
import utc.wolf.util.PropertyManager;


public class AlertBox extends MTRectangle {
	public static int RESIZE_MARGIN = 14;
	private MTTextArea submitButton;
	
	public static AlertBox alert( AbstractScene applet, String txt){
		return new AlertBox(applet, txt);
	}

	public AlertBox(final AbstractScene scene, String txt) {
		super(scene.getMTApplication(), 0, 0, 200, 200);
		
		String fontname = PropertyManager.getInstance().getProperty(
				PropertyManager.LIST_FONT);
		IFont font = FontManager.getInstance().createFont(getRenderer(),
				fontname, 20, MTColor.BLACK, true);
		
		MTTextArea content = new MTTextArea(scene.getMTApplication(),10,10,180,120,font);
		content.setText(txt);
		content.setStyleInfo(Theme.getTheme().getStyle(StyleID.TRANSPARENT));
		content.unregisterAllInputProcessors();
		content.removeAllGestureEventListeners();
		addChild(content);
				
		submitButton = new MTTextArea(scene.getMTApplication(),10,130,180,50, font);
		submitButton.setStyleInfo(Theme.getTheme().getStyle(Constants.BUTTON_STYLE));
		submitButton.setText("ok");
		submitButton.unregisterAllInputProcessors();
		submitButton.removeAllGestureEventListeners();		
		
		submitButton.registerInputProcessor(new TapProcessor(getRenderer()));
		submitButton.addGestureListener(TapProcessor.class, new IGestureEventListener()  {
			public boolean processGestureEvent(MTGestureEvent ge) {
				if (ge instanceof TapEvent) {
					TapEvent tapEvent = (TapEvent) ge;

					if (tapEvent.isTapped()) {
						AlertBox.this.destroy();
					}
				}

				return false;
			}
		});	
		addChild(submitButton);
		
		setStyleInfo(Theme.getTheme().getStyle(Constants.MENU_STYLE));
		setPositionGlobal(new Vector3D(scene.getMTApplication().width/2f, scene.getMTApplication().height/2f));
	}

	public void setSubmit(String text) {
		submitButton.setText(text);
		submitButton.removeAllGestureEventListeners(TapProcessor.class);
		submitButton.addGestureListener(TapProcessor.class, new IGestureEventListener()  {
			public boolean processGestureEvent(MTGestureEvent ge) {
				if (ge instanceof TapEvent) {
					TapEvent tapEvent = (TapEvent) ge;

					if (tapEvent.isTapped()) {
						AlertBox.this.destroy();
					}
				}

				return false;
			}
		});	
	}

}
