package utc.wolf.main;

import utc.ia04.werewolf.jade.Environment.EnvironmentAgent;
import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainBoot {
	public static String MAIN_PROPERTIES_FILE = "rsc/config/main.properties";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boot_gui();
	}

	public static void boot_gui() {
		Runtime rt = Runtime.instance();
        Profile p = null;
        AgentController ac;
        try{
                p = new ProfileImpl(MAIN_PROPERTIES_FILE);
                AgentContainer mc = rt.createMainContainer(p);

                ac = mc.createNewAgent("GameMasterAgent", GameMasterAgent.class.getName(),null);
                ac.start();
                ac = mc.createNewAgent("EnvAgent", EnvironmentAgent.class.getName(),null);
                ac.start();

        }
        catch(Exception ex) {
                ex.printStackTrace();
        }

	}
}
