package BookLending.finder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
      }   
    }    
    // menampilkan GUI 
    myGui = new BookFinderGuiImpl();   
    myGui.setAgent(this);   
    myGui.show();   
    // Update lender agen tiap 30 detik
    addBehaviour(new TickerBehaviour(this, 30000) {   
      protected void onTick() {   
        // Update lender agen
        DFAgentDescription template = new DFAgentDescription();   
        ServiceDescription sd = new ServiceDescription();   
        sd.setType("Book-lending");   
        template.addServices(sd);   
        try {   
          DFAgentDescription[] result = DFService.search(myAgent, template);   
          lenderAgents.clear();   
          for (int i = 0; i < result.length; ++i) {   
            lenderAgents.addElement(result[i].getName());   
          }   
        }   
        catch (FIPAException fe) {   
          fe.printStackTrace();   
        }   
      }   
    } );   
  }
    
    public void rentBook(String title, int maxPrice, Date deadline){
        addBehaviour(new PurchaseManager(this, title, maxPrice, deadline)); 
    }
    
    private class PurchaseManager extends TickerBehaviour {   
        private String title;   
        private int maxPrice, startPrice;   
        private long deadline, initTime, deltaT;   

        private PurchaseManager(Agent a, String t, int mp, Date d) {   
          super(a, 60000); // tick every minute   
          title = t;   
          maxPrice = mp;   
          deadline = d.getTime();   
          initTime = System.currentTimeMillis();   
          deltaT = deadline - initTime;   
        }   

        public void onTick() {   
          long currentTime = System.currentTimeMillis();   
          if (currentTime > deadline) {   
            
            //gui message time expired
            
            stop();   
          }   
          else {   
              
            long elapsedTime = currentTime - initTime;   
            int acceptablePrice = (int)Math.round(1.0 * maxPrice * (1.0 * elapsedTime / deltaT));   

          }   
        }   
      }
}
