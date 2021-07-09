package BookLending.lender;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookLenderAgent extends Agent {  
  // katalog buku
  private Map catalogue = new HashMap();  
  
  // Gui 
  private BookLenderGui myGui;  
  
  protected void setup() {  

    System.out.println("Lender-agent "+getAID().getName()+" is ready.");  
  
    // membuat dan menampilkan GUI  
    myGui = new BookLenderGuiImpl();  
    myGui.setAgent(this);  
    myGui.show();  
      
    // menambah behaviour
    addBehaviour(new CallForOfferServer());  
  
    DFAgentDescription dfd = new DFAgentDescription();  
    dfd.setName(getAID());  
    ServiceDescription sd = new ServiceDescription();  
    sd.setType("Book-lending");  
    sd.setName(getLocalName()+"-Book-lending");  
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
  
    System.out.println("lender-agent "+getAID().getName()+"terminating.");  
  
    try {  
      DFService.deregister(this);  
    }  
    catch (FIPAException fe) {  
      fe.printStackTrace();  
    }  
  }  

  public void putForSale(String title, int initPrice, int minPrice, Date deadline) {  
    addBehaviour(new PriceManager(this, title, initPrice, minPrice, deadline));  
  }  
  
  
  
  private class PriceManager extends TickerBehaviour {  
    private String title;  
    private int minPrice, currentPrice, initPrice, deltaP;  
    private long initTime, deadline, deltaT;  
  
    private PriceManager(Agent a, String t, int ip, int mp, Date d) {  
      super(a, 60000); // tick every minute  
      title = t;  
      initPrice = ip;  
      currentPrice = initPrice;  
      deltaP = initPrice - mp;  
      deadline = d.getTime();  
      initTime = System.currentTimeMillis();  
      deltaT = ((deadline - initTime) > 0 ? (deadline - initTime) : 60000);  
    }  
  
    public void onStart() {  
      // Insert the book in the catalogue of books available for sale  
      catalogue.put(title, this);  
      super.onStart();  
    }  
  
    public void onTick() {  
      long currentTime = System.currentTimeMillis();  
      if (currentTime > deadline) {  
        // Deadline expired  
        myGui.notifyUser("Cannot sell book "+title);  
        catalogue.remove(title);  
        stop();  
      }  
      else {  
        // Compute the current price  
        long elapsedTime = currentTime - initTime;  
        currentPrice = (int)Math.round(initPrice - 1.0 * deltaP * (1.0 * elapsedTime / deltaT));  
      }  
    }  
  
    public int getCurrentPrice() {  
      return currentPrice;  
    }  
  }  
 
  private class CallForOfferServer extends CyclicBehaviour {  
    private MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);  
  
    public void action() {  
      ACLMessage msg = myAgent.receive(mt);  
      if (msg != null) {  
        // CFP Message received. Process it  
        String title = msg.getContent();  
        myGui.notifyUser("Received Proposal to buy "+title);  
        ACLMessage reply = msg.createReply();  
        PriceManager pm = (PriceManager) catalogue.get(title);  
        if (pm != null) {  
          // The requested book is available for sale. Reply with the price  
          reply.setPerformative(ACLMessage.PROPOSE);  
          reply.setContent(String.valueOf(pm.getCurrentPrice()));  
        }  
        else {  
          // The requested book is NOT available for sale.  
          reply.setPerformative(ACLMessage.REFUSE);  
        }  
        myAgent.send(reply);  
        myGui.notifyUser(pm != null ? "Sent Proposal to sell at "+reply.getContent() : "Refused Proposal as the book is not for sale");  
      }  
      else {  
        block();  
      }  
    }  
  } // End of inner class CallForOfferServer  
  
  
      
  
  
}  
