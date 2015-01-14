

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utc.ia04.werewolf.main;

import utc.ia04.werewolf.jade.Environment.EnvironmentAgent;
import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import utc.ia04.werewolf.jade.Player.PlayerAgent;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 *
 * @author aliaume yiyan
 */
public class SecondaryMain {
    private static String SECONDARY_PROPERTIES_FILE = "res/secondarycontainer";
	
    public static void main(String[] args) {
            // TODO Auto-generated method stub
            Runtime rt = Runtime.instance();
            Profile p = null;
            AgentController ac;
            try {
                 System.out.println("second");
                p = new ProfileImpl(SECONDARY_PROPERTIES_FILE);
                ContainerController cc = rt.createAgentContainer(p);
                
                Object[] param1 = {"Aliaume"};
                Object[] param2 = {"Yiyan"};
                Object[] param3 = {"Matthieu"};
                ac = cc.createNewAgent("PlayerAgent1", PlayerAgent.class.getName(),param1);
                ac.start();
                ac = cc.createNewAgent("PlayerAgent2", PlayerAgent.class.getName(),param2);
                ac.start();
                ac = cc.createNewAgent("PlayerAgent3", PlayerAgent.class.getName(),param3);
                ac.start();
                ac = cc.createNewAgent("PlayerAgent4", PlayerAgent.class.getName(),new Object[] {"QSDF"});
                ac.start();
                ac = cc.createNewAgent("PlayerAgent5", PlayerAgent.class.getName(),new Object[] {"OoOo"});
                ac.start();
                ac = cc.createNewAgent("PlayerAgent6", PlayerAgent.class.getName(),new Object[] {"Jean"});
                ac.start();
                ac = cc.createNewAgent("PlayerAgent7", PlayerAgent.class.getName(),new Object[] {"Paulo"});
                ac.start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


    }
}