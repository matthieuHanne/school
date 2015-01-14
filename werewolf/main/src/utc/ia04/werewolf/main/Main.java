/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utc.ia04.werewolf.main;

import utc.ia04.werewolf.jade.Environment.EnvironmentAgent;
import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Profile;
import jade.core.ProfileImpl;
/**
 *
 * @author aliaume
 */
public class Main {

    public static String MAIN_PROPERTIES_FILE = "res/maincontainer";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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