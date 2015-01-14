package utc.ia04.werewolf.jade.Player;

import java.beans.PropertyChangeListener;

import jade.gui.GuiEvent;

public interface PlayerAgentInterface  {
	public void fireOnGuiEvent(GuiEvent ev);
	public void fireOnChange(String propName, Object value);
	public void addNewPropertyChangeListener(PropertyChangeListener pcl);

}
