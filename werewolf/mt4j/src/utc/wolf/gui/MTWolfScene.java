package utc.wolf.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.components.visibleComponents.widgets.MTBackgroundImage;
import org.mt4j.input.gestureAction.DefaultPanAction;
import org.mt4j.input.gestureAction.TapAndHoldVisualizer;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.panProcessor.PanProcessorTwoFingers;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import utc.wolf.gui.menu.OptionChooser;
import utc.wolf.gui.widget.AlertBox;
import utc.wolf.main.StartWolf;
import utc.wolf.model.Constants;
import utc.wolf.model.menu.DefaultMenuModel;
import utc.wolf.model.menu.IMenuModel;
import utc.wolf.util.ImageManager;

public class MTWolfScene extends AbstractScene implements
		PropertyChangeListener {
	private AbstractMTApplication app;
	OptionChooser chooser;
	IMenuModel menumodel;
	MTBackgroundImage nightImage;
	MTBackgroundImage daylightImage;

	public MTWolfScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		// this.setClearColor(new MTColor(126, 130, 168, 255));
		this.setClearColor(new MTColor(228, 240, 240, 0));
		// this.setClearColor(new MTColor(140, 210, 210, 240));
		this.registerGlobalInputProcessor(new CursorTracer(app, this));

		// 2 finger pan gesture
		getCanvas().registerInputProcessor(new PanProcessorTwoFingers(app));
		getCanvas().addGestureListener(PanProcessorTwoFingers.class,
				new DefaultPanAction());
		menumodel = new DefaultMenuModel();
		initManager();
		addStyle();
		createImages();
		addOptionChooser();
		playOptionChooser(new Vector3D(500, 200));
	}

	private void initManager() {
		ImageManager.getInstance().init(getMTApplication());
	}

	private void addOptionChooser() {
		getCanvas().registerInputProcessor(new TapAndHoldProcessor(app, 1000));
		getCanvas().addGestureListener(TapAndHoldProcessor.class,
				new TapAndHoldVisualizer(app, getCanvas()));

		getCanvas().addGestureListener(TapAndHoldProcessor.class,
				new IGestureEventListener() {
					public boolean processGestureEvent(MTGestureEvent ge) {
						TapAndHoldEvent th = (TapAndHoldEvent) ge;
						switch (th.getId()) {
						case TapAndHoldEvent.GESTURE_STARTED:
							break;
						case TapAndHoldEvent.GESTURE_UPDATED:
							break;
						case TapAndHoldEvent.GESTURE_ENDED:
							if (th.isHoldComplete()) {
								playOptionChooser(th.getLocationOnScreen());
							}
							break;
						default:
							break;
						}
						return false;
					}
				});

	}

	private void createImages() {
		long start = System.currentTimeMillis();
		nightImage = new MTBackgroundImage(StartWolf.getInstance(),
				ImageManager.getInstance().load("night.jpg"), false);
		daylightImage = new MTBackgroundImage(StartWolf.getInstance(),
				ImageManager.getInstance().load("day.jpg"), false);
		System.out.println(System.currentTimeMillis() - start);
	}

	private void addStyle() {
		StyleInfo defaultStyle = new StyleInfo();
		defaultStyle.setFillColor(200, 200, 200, 180);
		defaultStyle.setStrokeColor(MTColor.BLACK);
		defaultStyle.setDrawSmooth(false);
		//Theme theme = new Theme(defaultStyle);
		Theme theme = Theme.getTheme();
		StyleInfo menu = new StyleInfo();
		menu.setFillColor(new MTColor(255, 255, 255, 255));
		menu.setStrokeColor(MTColor.BLACK);
		menu.setStrokeWeight(1);
		theme.addStyle(Constants.MENU_STYLE, menu);
		
		StyleInfo button = new StyleInfo();
		button.setFillColor(new MTColor(0, 150, 255, 150));
		button.setStrokeColor(MTColor.BLACK);
		button.setStrokeWeight(1);
		theme.addStyle(Constants.BUTTON_STYLE, button);
		Theme.setTheme(theme);
	}

	public void playOptionChooser(Vector3D location) {
		if (chooser != null) {
			getCanvas().removeChild(chooser);
		}

		chooser = new OptionChooser(getMTApplication());
		chooser.setMustBeDestroy(false);
		chooser.setModel(menumodel);
		location.translate(new Vector3D(-100, 0));
		chooser.translate(location, TransformSpace.GLOBAL);
		getCanvas().addChild(chooser);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Constants.OPEN_OPTION)) {
			menumodel = (IMenuModel) evt.getNewValue();
			playOptionChooser(new Vector3D(500, 200));
		}
		if (evt.getPropertyName().equals(Constants.IMAGE)) {
			getCanvas().removeAllChildren();
			if (evt.getNewValue().equals(Constants.DAYLIGHT_IMAGE)) {
				getCanvas().addChild(daylightImage);
			} else {
				getCanvas().addChild(nightImage);
			}
		}
		if (evt.getPropertyName().equals(Constants.HINT)) {
			AlertBox alert = AlertBox.alert(this, evt.getNewValue().toString());
			getCanvas().addChild(alert);
		}
	}
}
