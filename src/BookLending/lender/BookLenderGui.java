package BookLending.lender;

public interface BookLenderGui {   
  void setAgent(BookLenderAgent a);   
  void show();   
  void hide();   
  void notifyUser(String message);   
  void dispose();   
} 
