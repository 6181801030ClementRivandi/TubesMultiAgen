package BookLending.courier;

public interface BookCourierGui {       
  void setAgent(BookCourierAgent a);   
  void show();   
  void hide();   
  void notifyUser(String message);   
  void dispose();   
} 
