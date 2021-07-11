/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BookLending.courier;

import BookLending.lender.*;
import jade.gui.TimeChooser;   
   
import java.awt.*;   
import java.awt.event.*;   
import javax.swing.*;   
import javax.swing.border.*;   
   
import java.util.Date;   
    
public class BookCourierGuiImpl extends JFrame implements BookCourierGui {   
    private BookCourierAgent myAgent;   
          
    
    private JTextArea logTA;    
       
    public BookCourierGuiImpl() {   
        super();   
           
        addWindowListener(new   WindowAdapter() {   
            public void windowClosing(WindowEvent e) {   
                myAgent.doDelete();   
            }   
        } );   

        JPanel rootPanel = new JPanel();   
        rootPanel.setLayout(new GridBagLayout());   
    rootPanel.setMinimumSize(new Dimension(330, 125));   
    rootPanel.setPreferredSize(new Dimension(330, 125));     
    
    logTA = new JTextArea();   
    logTA.setEnabled(false);   
    JScrollPane jsp = new JScrollPane(logTA);   
    jsp.setMinimumSize(new Dimension(300, 180));   
    jsp.setPreferredSize(new Dimension(300, 180));   
    JPanel p = new JPanel();   
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));   
    p.add(jsp);   
    getContentPane().add(p, BorderLayout.CENTER);   
       
    pack();   
       
    setResizable(false);   
    }   
   
    public void setAgent(BookCourierAgent a) {   
        myAgent = a;   
        setTitle(myAgent.getName());   
    }   
           
    public void notifyUser(String message) {   
        logTA.append(message+"\n");   
    }   
}     
