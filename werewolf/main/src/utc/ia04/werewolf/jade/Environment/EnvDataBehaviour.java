package utc.ia04.werewolf.jade.Environment;

import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.PlayerInfo;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;

public class EnvDataBehaviour extends CyclicBehaviour {

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
		/*
		 * RECEIVING QUERY_REF
		 */
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
		ACLMessage msg = myAgent.receive(mt);
		if(msg != null){
			MessageFormat content = MessageFormat.read(msg.getContent());
			System.err.println("ENVDATA -- RECEIVED QUERY: "+msg.getContent());
			/*
			 * IN CASE IT IS A getAID REQUEST
			 */
			if(content.getAction().equals("getAID")){
				PlayerInfo.Role playerRole = PlayerInfo.Role.valueOf((String)content.getArg("roleFilter"));
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM_REF);
				MessageFormat replyContent = new MessageFormat();
				replyContent.setAction("AIDplayers");
				replyContent.addArg("AIDplayers", ((EnvironmentAgent)myAgent).getAIDplayers(playerRole));
				System.out.println(replyContent.toJSON());
				reply.setContent(replyContent.toJSON());
				myAgent.send(reply);
				
			/*
			 * IN CASE IT IS A getnicknames REQUEST 
			 */
			} else if(content.getAction().equals("getnicknames")){
				PlayerInfo.Role playerRole = PlayerInfo.Role.valueOf((String)content.getArg("roleFilter"));
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM_REF);
				MessageFormat replyContent = new MessageFormat();
				replyContent.setAction("nicknames");
				replyContent.addArg("nicknames", ((EnvironmentAgent)myAgent).getPlayersNick(playerRole));
				replyContent.addArg("rolePlayer", playerRole);
				System.out.println(replyContent.toJSON());
				reply.setContent(replyContent.toJSON());
				myAgent.send(reply);
				
			/*
			 * RETRIEVIENG ALL PLAYERS NICKNAMES + AIDs
			 */
			} else if(content.getAction().equals("getAllPlayers")){
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM_REF);
				MessageFormat replyContent = new MessageFormat();
				replyContent.setAction("sendAllPlayers");
				HashMap<String, AID> players = new HashMap<String, AID>();
				System.out.println("NR of players : "+((EnvironmentAgent)myAgent).players.size());
				HashMap<String, PlayerInfo> list = ((EnvironmentAgent)myAgent).players;
				for(String nick : list.keySet()){
					if(list.get(nick).isAlive)
						players.put(nick, list.get(nick).agentID);
				}
				replyContent.addArg("players", players);
				System.out.println(replyContent.toJSON());
				reply.setContent(replyContent.toJSON());
				myAgent.send(reply);
				
			/*
			 * IN CASE IT IS A getLovers REQUEST 
			 */
			} else if(content.getAction().equals("getLovers")){
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM_REF);
				MessageFormat mf = new MessageFormat();
				mf.setAction("sendLovers");
				mf.addArg("lovers", ((EnvironmentAgent)myAgent).getLoversNick());
				reply.setContent(mf.toJSON());
				myAgent.send(reply);
			}
		}
	}

}
