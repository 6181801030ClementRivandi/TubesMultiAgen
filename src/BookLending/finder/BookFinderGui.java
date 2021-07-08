/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BookLending.finder;

/**
 *
 * @author user
 */
public interface BookFinderGui {       
  void setAgent(BookFinderAgent a);   
  void show();   
  void hide();   
  void notifyUser(String message);   
  void dispose();   
} 
