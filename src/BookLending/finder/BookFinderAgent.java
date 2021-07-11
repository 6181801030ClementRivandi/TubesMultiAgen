package BookLending.finder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this templateMessage file, choose Tools | Templates
 * and open the templateMessage in the editor.
 */
import jade.core.*;   
import jade.core.behaviours.*;   
import jade.lang.acl.*;   
   
import jade.domain.*;   
import jade.domain.FIPAAgentManagement.*;   
   
import java.util.Vector;   
import java.util.Date;   
/**
 *
 * @author user
 */
public class BookFinderAgent extends Agent{  
    // list dari lender agen 
  private Vector lenderAgents = new Vector(); 
  private Vector courierAgents = new Vector();
   
  // Gui 
  private BookFinderGui myGui;   
   
  /**  
   * inisialisasi agen 
   **/   
  protected void setup() {   
 
    System.out.println("Finder-agent "+getAID().getName()+" is ready.");   
       
    // Get names of seller agents as arguments   
    Object[] args = getArguments();   
    if (args != null && args.length > 0) {   
      for (int i = 0; i < args.length; ++i) {   
        AID lender = new AID((String) args[i], AID.ISLOCALNAME);   
        lenderAgents.addElement(lender);   
        AID courier = new AID((String) args[i], AID.ISLOCALNAME);   
        courierAgents.addElement(courier); 
      }   
    }    
    // menampilkan GUI 
    myGui = new BookFinderGuiImpl();   
    myGui.setAgent(this);   
    myGui.show();   
    // Update lender agen tiap 30 detik
    addBehaviour(new TickerBehaviour(this, 15000) {   
      protected void onTick() {   
        // Update lender agen
        DFAgentDescription templateMessage = new DFAgentDescription();   
        DFAgentDescription templateMessageCourier = new DFAgentDescription();
        
        ServiceDescription sd = new ServiceDescription();   
        sd.setType("Book-lending");   
        
        ServiceDescription sdCour = new ServiceDescription();
        sdCour.setType("Book-lending-courier");
        
        templateMessage.addServices(sd);   
        try {   
          DFAgentDescription[] resultLenderList = DFService.search(myAgent, templateMessage);   
          lenderAgents.clear(); 
          DFAgentDescription[] resultCourieList = DFService.search(myAgent, templateMessageCourier);   
          courierAgents.clear(); 
          for (int i = 0; i < resultLenderList.length; ++i) {   
            lenderAgents.addElement(resultLenderList[i].getName());   
          }   
          for (int i = 0; i < resultCourieList.length; ++i) {   
            courierAgents.addElement(resultCourieList[i].getName());   
          } 
        }   
        catch (FIPAException fe) {   
          fe.printStackTrace();   
        }   
      }   
    } );   
  }
    
    public void rentBook(String title, int maxPrice, Date deadline, int rentTime,String address){
        addBehaviour(new RentManager(this, title, maxPrice, deadline, rentTime,address)); 
    }
    
    private class RentManager extends TickerBehaviour {   
        private String title, address;   
        private int maxPrice, rentTime;   
        private long deadline, initTime, deltaT;  

        private RentManager(Agent a, String t, int mp, Date d, int r, String address) {   
          super(a, 15000); // tick every minute   
          title = t;   
          maxPrice = mp;   
          deadline = d.getTime();   
          initTime = System.currentTimeMillis();   
          deltaT = deadline - initTime; 
          rentTime = r;
          this.address = address;
        }   

        public void onTick() {   
          long currentTime = System.currentTimeMillis();   
          if (currentTime > deadline) {   
            
            //gui message time expired
            myGui.notifyUser("Timeout book: "+title); 
            stop();   
          }   
          else {   
              
            long elapsedTime = currentTime - initTime;   
            int acceptablePrice = (int)Math.round(1.0 * maxPrice * (1.0 * elapsedTime / deltaT));
            //int acceptablePrice = (int)Math.ceil(maxPrice * 1.1);
            myAgent.addBehaviour(new BookNegotiator(title, acceptablePrice, this,this.address, rentTime));   
          }   
        }   
      }
    
    private class BookNegotiator extends Behaviour {   
    private String title;   
    private int maxPrice;   
    private RentManager manager;   
    private AID bestSeller; // The seller agent who provides the best offer   
    private int bestPrice; // The best offered price   
    private int repliesCnt = 0; // The counter of replies from seller agents   
    private MessageTemplate mt; // The template to receive replies   
    private int step = 0;   
    private String address;
    private int rentTime;
   
    public BookNegotiator(String t, int p, RentManager m,String a, int rentTime) {   
      super(null);   
      title = t;   
      maxPrice = p;   
      manager = m; 
      this.address = a;
      this.rentTime = rentTime;
    }   
   
    public void action() {   
        ACLMessage agree = new ACLMessage(ACLMessage.AGREE);
      switch (step) {   
        case 0:   
          // Send the cfp to all sellers   
          ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
          
          //ACLMessage agree = new ACLMessage(ACLMessage.AGREE);
          //ACLMessage cfp_price_offer = new ACLMessage(ACLMessage.CFP);
          
          for (int i = 0; i < lenderAgents.size(); ++i) {   
            cfp.addReceiver((AID)lenderAgents.elementAt(i));  
            //cfp_price_offer.addReceiver((AID)lenderAgents.elementAt(i));  
          }
          
          for(int i = 0; i < courierAgents.size(); ++i){
              agree.addReceiver((AID)courierAgents.elementAt(i));
          }
          
          cfp.setContent(title);   
          cfp.setConversationId("book-trade");   
          cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value 
          
          //cfp_price_offer.setContent(HARGA);
          
          myAgent.send(cfp);   
          myGui.notifyUser("Sent Call for Proposal");   
   
          // Prepare the template to get proposals   
          mt = MessageTemplate.and(   
          MessageTemplate.MatchConversationId("book-trade"),   
          MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));   
          step = 1;   
          break;   
   
        case 1:   
          // Receive all proposals/refusals from seller agents   
            
          ACLMessage reply = myAgent.receive(mt);   
          repliesCnt = repliesCnt + 1; 
          if (reply != null) {   
            // Reply received   
            if (reply.getPerformative() == ACLMessage.PROPOSE) {   
              // This is an offer   
              int price = Integer.parseInt(reply.getContent());   
              myGui.notifyUser("Received Proposal at "+price+" when maximum acceptable price was "+maxPrice);   
              if (bestSeller == null || price < bestPrice) {   
                // This is the best offer at present   
                bestPrice = price;   
                bestSeller = reply.getSender();   
              }   
            }   
            //System.out.println("repliesCn++: " + repliesCnt );
            
            
              //System.out.println("repliesCn++: " + repliesCnt );
              //System.out.println("lenderAgents.size(): " + lenderAgents.size() );
            if (repliesCnt >= lenderAgents.size()) {   
              // We received all replies   
              System.out.println("step = 2");     
              step = 2;
            }             
          }    
          else {   
            block();   
          }
          
          break;   
        case 2:   
          if (bestSeller != null && bestPrice <= maxPrice) {  
            step = 3; 
            System.out.println("step: 2");
            // Send the purchase order to the seller that provided the best offer   
            MessageTemplate mtCour =  MessageTemplate.MatchPerformative(ACLMessage.AGREE);
            
            ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);   
            order.addReceiver(bestSeller);   
            order.setContent(title);   
            order.setConversationId("book-trade");   
            order.setReplyWith("order"+System.currentTimeMillis());   
            
            myAgent.send(order);  
            myGui.notifyUser("sent Accept Proposal"); 
            step = 3; 
            
            // with address of the order 
            ACLMessage orderAddress = new ACLMessage(ACLMessage.AGREE);
            ACLMessage orderRentTime = new ACLMessage(ACLMessage.PROXY);
            orderAddress.setContent(address);
            orderRentTime.setContent(String.valueOf(rentTime));
            //AID testAID = agree.getSender();
            orderRentTime.addReceiver((new AID("Courier", AID.ISLOCALNAME)));
            orderAddress.addReceiver((new AID("Courier", AID.ISLOCALNAME)));
            orderAddress.setConversationId("book-trade-address");
            orderAddress.setReplyWith("order"+System.currentTimeMillis());
            myAgent.send(orderAddress);
            myAgent.send(orderRentTime);
            myGui.notifyUser("sent Address to Courier");
            
            // Prepare the template to get the purchase order reply   
            //mt = MessageTemplate.and(   
              //MessageTemplate.MatchConversationId("book-trade"),   
              //MessageTemplate.MatchInReplyTo(order.getReplyWith()));   
              mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            step = 3;  
            
            if(step == 3){
                reply = myAgent.receive(mt);   
                if (reply != null) {     
                    myGui.notifyUser("Book "+title+" successfully rented. Price = " + bestPrice);   
                    manager.stop();   
                     
                    step = 4;   
                }   
                else {   
                  block();   
                }   
                break; 
            }
            
             
          }   
          else {   
            // If we received no acceptable proposals, terminate   
            step = 4;   
          }   
          break;   
        case 3:   
          // Receive the purchase order reply   
          reply = myAgent.receive(mt);   
          if (reply != null) {   
            // Purchase order reply received   
            if (reply.getPerformative() == ACLMessage.INFORM) {   
              // Purchase successful. We can terminate   
              myGui.notifyUser("Book "+title+" successfully rented. Price = " + bestPrice);   
              manager.stop();   
            }   
            step = 4;   
          }   
          else {   
            block();   
          }   
          break;   
      } // end of switch   
    }   
   
    public boolean done() {   
      return step == 4;   
    }   
  }
}
