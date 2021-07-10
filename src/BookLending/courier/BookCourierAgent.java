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
  
    // Create and show the GUI  
    myGui = new BookCourierGuiImpl();  
    myGui.setAgent(this);  
    myGui.show();  
      
//    addBehaviour(new CallForOfferServer());  
   
    DFAgentDescription dfd = new DFAgentDescription();  
    dfd.setName(getAID());  
    ServiceDescription sd = new ServiceDescription();  
    sd.setType("Book-selling");  
    sd.setName(getLocalName()+"-Book-selling");  
    dfd.addServices(sd);  
    try {  
      DFService.register(this, dfd);  
    }  
    catch (FIPAException fe) {  
      fe.printStackTrace();  
    }  
  }  
  protected void takeDown() {  
    if (myGui != null) {  
      myGui.dispose();  
    }  
   
    System.out.println("Courier-agent "+getAID().getName()+"terminating.");  
  
    try {  
      DFService.deregister(this);  
    }  
    catch (FIPAException fe) {  
      fe.printStackTrace();  
    }  
  }
}
  
//  private class PriceManager extends TickerBehaviour {  
//    private String title;  
//    private int minPrice, currentPrice, initPrice, deltaP;  
//    private long initTime, deadline, deltaT;  
//  
//    private PriceManager(Agent a, String t, int ip, int mp, Date d) {  
//      super(a, 60000); // tick every minute  
//      title = t;  
//      initPrice = ip;  
//      currentPrice = initPrice;  
//      deltaP = initPrice - mp;  
//      deadline = d.getTime();  
//      initTime = System.currentTimeMillis();  
//      deltaT = ((deadline - initTime) > 0 ? (deadline - initTime) : 60000);  
//    }  
//  
//    public void onStart() {   
//      super.onStart();  
//    }   
//  
//    public int getCurrentPrice() {  
//      return currentPrice;  
//    }  
//  }  
//  
//  /** Section 4.3.3 , page 67. 
//   * Inner class CallForOfferServer. 
//   * This is the behaviour used by Book-seller agents to serve 
//   * incoming call for offer from buyer agents. 
//   * If the indicated book is in the local catalogue, the seller agent 
//   * replies with a PROPOSE message specifying the price. Otherwise 
//   * a REFUSE message is sent back. 
//  **/  
//  private class CallForOfferServer extends CyclicBehaviour {  
//    private MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);  
//  
//    public void action() {  
//      ACLMessage msg = myAgent.receive(mt);  
//      if (msg != null) {  
//        // CFP Message received. Process it  
//        String title = msg.getContent();  
//        myGui.notifyUser("Received Proposal to buy "+title);  
//        ACLMessage reply = msg.createReply();  
//        PriceManager pm = (PriceManager) catalogue.get(title);  
//        if (pm != null) {  
//          // The requested book is available for sale. Reply with the price  
//          reply.setPerformative(ACLMessage.PROPOSE);  
//          reply.setContent(String.valueOf(pm.getCurrentPrice()));  
//        }  
//        else {  
//          // The requested book is NOT available for sale.  
//          reply.setPerformative(ACLMessage.REFUSE);  
//        }  
//        myAgent.send(reply);  
//        myGui.notifyUser(pm != null ? "Sent Proposal to sell at "+reply.getContent() : "Refused Proposal as the book is not for sale");  
//      }  
//      else {  
//        block();  
//      }  
//    }  
//  } // End of inner class CallForOfferServer  
//  
//  
//      
//  
//  
//}  
