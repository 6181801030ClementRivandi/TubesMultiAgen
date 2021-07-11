package BookLending.finder;

import jade.core.*;   
import jade.core.behaviours.*;   
import jade.lang.acl.*;     
import jade.domain.*;   
import jade.domain.FIPAAgentManagement.*;      
import java.util.Vector;   
import java.util.Date;   

public class BookFinderAgent extends Agent{  
  // list dari agen 
  private Vector lenderAgents = new Vector(); 
  private Vector courierAgents = new Vector();
   
  //Gui 
  private BookFinderGui myGui;   
   
  //inisialisasi agen 
   
  protected void setup() {   
 
    System.out.println("Finder-agent "+getAID().getName()+" is ready.");   
        
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
        // Update agen
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
          super(a, 15000); 
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
            myGui.notifyUser("Timeout book: "+title); 
            stop();   
          }   
          else{               
            long elapsedTime = currentTime - initTime;   
            int acceptablePrice = (int)Math.round(1.0 * maxPrice * (1.0 * elapsedTime / deltaT));
            myAgent.addBehaviour(new BookNegotiator(title, acceptablePrice, this,this.address, rentTime));   
          }   
        }   
      }
    
    private class BookNegotiator extends Behaviour {   
    private String title;   
    private int maxPrice;   
    private RentManager manager;   
    private AID bestSeller;
    private int bestPrice;  
    private int repliesCnt = 0;  
    private MessageTemplate mt; 
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
          ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
         
          for (int i = 0; i < lenderAgents.size(); ++i) {   
            cfp.addReceiver((AID)lenderAgents.elementAt(i));  
          }
          
          for(int i = 0; i < courierAgents.size(); ++i){
              agree.addReceiver((AID)courierAgents.elementAt(i));
          }
          
          cfp.setContent(title);   
          cfp.setConversationId("book-trade");   
          cfp.setReplyWith("cfp"+System.currentTimeMillis());
          
          myAgent.send(cfp);   
          myGui.notifyUser("Sent Call for Proposal");   
     
          mt = MessageTemplate.and(   
          MessageTemplate.MatchConversationId("book-trade"),   
          MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));   
          step = 1;   
          break;   
   
        case 1:     
          ACLMessage reply = myAgent.receive(mt);   
          repliesCnt = repliesCnt + 1; 
          if (reply != null) {   
            if (reply.getPerformative() == ACLMessage.PROPOSE) {    
              int price = Integer.parseInt(reply.getContent());   
              myGui.notifyUser("Received Proposal at "+price+" when maximum acceptable price was "+maxPrice);   
              if (bestSeller == null || price < bestPrice) {    
                bestPrice = price;   
                bestSeller = reply.getSender();   
              }   
            }   
            if (repliesCnt >= lenderAgents.size()) {     
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
            MessageTemplate mtCour =  MessageTemplate.MatchPerformative(ACLMessage.AGREE);
            ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);   
            order.addReceiver(bestSeller);   
            order.setContent(title);   
            order.setConversationId("book-trade");   
            order.setReplyWith("order"+System.currentTimeMillis());   
            
            myAgent.send(order);  
            myGui.notifyUser("sent Accept Proposal"); 
            step = 3; 
            
            ACLMessage orderAddress = new ACLMessage(ACLMessage.AGREE);
            ACLMessage orderRentTime = new ACLMessage(ACLMessage.PROXY);
            orderAddress.setContent(address);
            orderRentTime.setContent(String.valueOf(rentTime));
            orderRentTime.addReceiver((new AID("Courier", AID.ISLOCALNAME)));
            orderAddress.addReceiver((new AID("Courier", AID.ISLOCALNAME)));
            orderAddress.setConversationId("book-trade-address");
            orderAddress.setReplyWith("order"+System.currentTimeMillis());
            myAgent.send(orderAddress);
            myAgent.send(orderRentTime);
            myGui.notifyUser("sent Address to Courier");
            
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
            step = 4;   
          }   
          break;   
        case 3:    
          reply = myAgent.receive(mt);   
          if (reply != null) {   
            if (reply.getPerformative() == ACLMessage.INFORM) {     
              myGui.notifyUser("Book "+title+" successfully rented. Price = " + bestPrice);   
              manager.stop();   
            }   
            step = 4;   
          }   
          else {   
            block();   
          }   
          break;   
      } 
    }   
   
    public boolean done() {   
      return step == 4;   
    }   
  }
}
