package utc.ia04.werewolf.jade.Environment;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utc.ia04.werewolf.utils.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 09/06/2014
 * Time: 21:38
 * To change this template use File | Settings | File Templates.
 */
public class KillPlayerBehaviour extends Behaviour {
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage message = myAgent.receive(mt);
        if(message != null){
            System.out.println("Environment Agent -  Received message "+message.getContent());
            MessageFormat content = MessageFormat.read(message.getContent());
            if(content.getAction().equals("killPlayer")){
                System.out.println("Environment Agent - Player deletion in process");
                String nickname = (String)content.getArg("nickname");
                boolean success = ((EnvironmentAgent)myAgent).killPlayer(nickname);

                if(success){
                    System.out.println("Environment Agent - Player killed on Env");
                    ACLMessage reply = message.createReply();
                    reply.setPerformative(ACLMessage.CONFIRM);

                    MessageFormat replyContent = new MessageFormat();
                    replyContent.setAction("killPlayerConfirm");
                    replyContent.addArg("nickname", nickname);

                    reply.setContent(replyContent.toJSON());
                    myAgent.send(reply);
                } else {
                    System.out.println("Environment Agent - Failed killing player player!");
                    ACLMessage reply = message.createReply();
                    reply.setPerformative(ACLMessage.FAILURE);

                    MessageFormat replyContent = new MessageFormat();
                    replyContent.setAction("killPlayerFailure");
                    replyContent.addArg("nickname", nickname);

                    reply.setContent(replyContent.toJSON());
                    myAgent.send(reply);
                }
            }
        }
    }

    @Override
    public boolean done() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
