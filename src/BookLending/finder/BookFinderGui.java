package BookLending.finder;

public interface BookFinderGui {       
  void setAgent(BookFinderAgent a);   
  void show();   
  void hide();   
  void notifyUser(String message);   
  void dispose();   
} 
