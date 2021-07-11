package BookLending.courier;

import jade.core.Agent;  
import jade.core.behaviours.*;  
import jade.lang.acl.ACLMessage;  
import jade.lang.acl.MessageTemplate;  
import jade.domain.*;  
import jade.domain.FIPAAgentManagement.*;   
  
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
    sd.setType("Book-lending-courier");  
    sd.setName(getLocalName()+"-Book-lending-courier");  
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
    private MessageTemplate mtp = MessageTemplate.MatchPerformative(ACLMessage.PROXY);
    
    public void action() {  
      ACLMessage msg = myAgent.receive(mt);  
      ACLMessage msgMtp = myAgent.receive(mtp);
      
      if (msg != null) {  
         String address = msg.getContent();  
         myGui.notifyUser("Received Address "+address);
      }  
      else {  
        block();  
      }
      if(msgMtp != null){
          String duration = msgMtp.getContent();
          myGui.notifyUser("Duration: " + duration + " day");
          myGui.notifyUser("Sending Book...");
      }
    }  
  }
}
  
