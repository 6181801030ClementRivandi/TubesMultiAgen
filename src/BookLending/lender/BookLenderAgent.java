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
  //katalog buku
  private Map BookList = new HashMap();  
  
  //Gui 
  private BookLenderGui myGui;  
  
  protected void setup() {  

    System.out.println("Lender-agent "+getAID().getName()+" is ready.");  
    // membuat dan menampilkan GUI  
    myGui = new BookLenderGuiImpl();  
    myGui.setAgent(this);  
    myGui.show();   
    // menambah behaviour
    addBehaviour(new CallForOfferServer());  
  
    DFAgentDescription LenderAgentList = new DFAgentDescription();  
    LenderAgentList.setName(getAID());  
    ServiceDescription sd = new ServiceDescription();  
    sd.setType("Book-lending");  
    sd.setName(getLocalName()+"-Book-lending");  
    LenderAgentList.addServices(sd);  
    try {  
      DFService.register(this, LenderAgentList);  
    }  
    catch (FIPAException fe) {  
      fe.printStackTrace();  
    }  
  }  
  
  public void putForSale(String title, int maxPrice, int minPrice, Date deadline) {  
    addBehaviour(new PriceManager(this, title, maxPrice, minPrice, deadline));  
  }  
  
  private class PriceManager extends TickerBehaviour {  
    private String title;  
    private int minPrice, currentPrice, maxPrice, deltaP;  
    private long initTime, deadline, deltaT;  
  
    private PriceManager(Agent a, String t, int ip, int mp, Date d) {  
      super(a, 15000); 
      title = t;  
      maxPrice = ip;
      currentPrice = maxPrice;  
      deltaP = maxPrice - mp;//range harga dari maksimum dan minimum
      deadline = d.getTime();  
      initTime = System.currentTimeMillis();  
      deltaT = ((deadline - initTime) > 0 ? (deadline - initTime) : 15000);  
    }  
  
    public void onStart() {  
      BookList.put(title, this);  
      super.onStart();  
    }  
  
    public void onTick() {  
      long currentTime = System.currentTimeMillis();  
      if (currentTime > deadline) {    
        myGui.notifyUser("Timeout book: "+title);  
        BookList.remove(title);  
        stop();  
      }  
      else {   
        long elapsedTime = currentTime - initTime;  
        currentPrice = (int)Math.round(maxPrice - 1.0 * deltaP * (1.0 * elapsedTime / deltaT));  
      }  
    }  
  
    public int getCurrentPrice() {  
      return currentPrice;  
    }  
  }  
 
  private class CallForOfferServer extends CyclicBehaviour {  
    private MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);  
    private MessageTemplate st = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
    public void action() {  
      ACLMessage msg = myAgent.receive(mt);  
      ACLMessage successMsg = myAgent.receive(st);
      
      if (successMsg != null ){
            ACLMessage replySuccess = successMsg.createReply();
            replySuccess.setPerformative(ACLMessage.INFORM);        
            myGui.notifyUser("Order accepted.");
            successMsg = null;
            msg = null;
            block();
        }
      else if (msg != null) {  
        String title = msg.getContent();  
        myGui.notifyUser("Received Proposal to rent "+title);  
        ACLMessage reply = msg.createReply(); 
        
        PriceManager pm = (PriceManager) BookList.get(title);  
        if (pm != null) {  

        reply.setPerformative(ACLMessage.PROPOSE);  
        reply.setContent(String.valueOf(pm.getCurrentPrice())); 
                            
        }  
        else {    
          reply.setPerformative(ACLMessage.REFUSE);  
        }  
        myAgent.send(reply);  
        myGui.notifyUser(pm != null ? "Sent Proposal to rent at "+reply.getContent() : "Refused Proposal as the book is not for sale");  
      }else{  
        block();  
      }  
    }  
  }
}  
