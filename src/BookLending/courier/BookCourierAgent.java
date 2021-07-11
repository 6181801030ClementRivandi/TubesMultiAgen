package BookLending.courier;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
import jade.core.Agent;  
import jade.core.behaviours.*;  
import jade.lang.acl.ACLMessage;  
import jade.lang.acl.MessageTemplate;  
import jade.domain.*;  
import jade.domain.FIPAAgentManagement.*;  
  
import java.util.*;  
  
public class BookCourierAgent extends Agent {  
    
  private BookCourierGui myGui;  
    
  protected void setup() {  
      
    System.out.println("courier-agent "+getAID().getName()+" is ready.");  

    myGui = new BookCourierGuiImpl();  
    myGui.setAgent(this);  
    myGui.show();  
      
    addBehaviour(new StartCourierJob()); 
    
    DFAgentDescription CourierAgentList = new DFAgentDescription();  
    CourierAgentList.setName(getAID());  
    ServiceDescription sd = new ServiceDescription();  
    sd.setType("Book-lending");  
    sd.setName(getLocalName()+"-Book-lending");  
    CourierAgentList.addServices(sd);  
    try {  
      DFService.register(this, CourierAgentList);  
    }  
    catch (FIPAException fe) {  
      fe.printStackTrace();  
    }
    
  }
  
  private class StartCourierJob extends CyclicBehaviour {  
    private MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.AGREE);  
    
    public void action() {  
      ACLMessage msg = myAgent.receive(mt);  
      
      if (msg != null) {  
         String address = msg.getContent();  
         myGui.notifyUser("Received Address "+address);
         myGui.notifyUser("Sending Book...");
         ACLMessage reply = msg.createReply();  
      }  
      else {  
        block();  
      }  
    }  
  }
}
  
