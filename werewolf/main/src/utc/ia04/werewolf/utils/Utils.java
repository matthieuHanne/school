package utc.ia04.werewolf.utils;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Utils {
	public static AID getMaster(Agent agent){
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd  = new ServiceDescription();        sd.setType("GM");
        dfd.addServices(sd);
        
        try {
			DFAgentDescription[] result = DFService.search(agent, dfd);
			if(result.length > 0){
				return result[0].getName();
			}
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
        
	}
        
        public static AID  getTable(Agent agent){

	        DFAgentDescription dfd = new DFAgentDescription();
	        ServiceDescription sd  = new ServiceDescription();
	        sd.setType("Gui");
	        dfd.addServices(sd);
	        
	        try {
				DFAgentDescription[] result = DFService.search(agent, dfd);
				if(result.length > 0){
					return result[0].getName();
				}
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return null;
		}
			
			
			
		
		public static AID getEnv(Agent agent){
	        DFAgentDescription dfd = new DFAgentDescription();
	        ServiceDescription sd  = new ServiceDescription();	        
	        sd.setType("ENV");
	        dfd.addServices(sd);
	        
	        try {
				DFAgentDescription[] result = DFService.search(agent, dfd);
				if(result.length > 0){
					return result[0].getName();
				}
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return null;
		}
        
        
        
        
	}
	
	
	
	
	
	
	
	
	
	
