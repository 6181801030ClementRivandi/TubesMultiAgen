/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BookLending.lender;

import jade.gui.TimeChooser;   
   
import java.awt.*;   
import java.awt.event.*;   
import javax.swing.*;   
import javax.swing.border.*;   
   
import java.util.Date;   
    
public class BookLenderGuiImpl extends JFrame implements BookLenderGui {   
    private BookLenderAgent myAgent;   
       
    private JTextField titleTF, maxCostTF, minCostTF, deadlineTF;   
    private JButton setDeadlineB, setExec;   
    private JTextArea logTA;   
    private Date deadline;   
       
    public BookLenderGuiImpl() {   
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
           
    ///////////   
    // Line 0   
    ///////////   
        JLabel l = new JLabel("Judul");   
    l.setHorizontalAlignment(SwingConstants.LEFT);   
    GridBagConstraints gridBagConstraints = new GridBagConstraints();   
    gridBagConstraints.gridx = 0;   
    gridBagConstraints.gridy = 0;   
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;   
    gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);   
    rootPanel.add(l, gridBagConstraints);   
   
    titleTF = new JTextField(64);   
    titleTF.setMinimumSize(new Dimension(210, 20));   
    titleTF.setPreferredSize(new Dimension(210, 20));   
    gridBagConstraints = new GridBagConstraints();   
    gridBagConstraints.gridx = 1;   
    gridBagConstraints.gridy = 0;   
    gridBagConstraints.gridwidth = 3;   
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;   
    gridBagConstraints.insets = new Insets(5, 3, 0, 3);   
    rootPanel.add(titleTF, gridBagConstraints);   
   
    ///////////   
    // Line 1   
    ///////////    
        l = new JLabel("Harga Maksimal");   
    l.setHorizontalAlignment(SwingConstants.LEFT);   
    l.setMinimumSize(new Dimension(100, 20));   
    l.setPreferredSize(new Dimension(100, 20));   
    gridBagConstraints = new GridBagConstraints();   
    gridBagConstraints.gridx = 0;   
    gridBagConstraints.gridy = 1;   
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;   
    gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);   
    rootPanel.add(l, gridBagConstraints); 
    
    maxCostTF = new JTextField(64);   
    maxCostTF.setMinimumSize(new Dimension(80, 20));   
    maxCostTF.setPreferredSize(new Dimension(80, 20));   
    gridBagConstraints = new GridBagConstraints();   
    gridBagConstraints.gridx = 1;   
    gridBagConstraints.gridy = 1;   
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;   
    gridBagConstraints.insets = new Insets(5, 3, 0, 3);   
    rootPanel.add(maxCostTF, gridBagConstraints);
    
    ///////////   
    // Line 2   
    ///////////    
        l = new JLabel("Harga Minimal");   
    l.setHorizontalAlignment(SwingConstants.LEFT);   
    l.setMinimumSize(new Dimension(100, 20));   
    l.setPreferredSize(new Dimension(100, 20));   
    gridBagConstraints = new GridBagConstraints();   
    gridBagConstraints.gridx = 0;   
    gridBagConstraints.gridy = 2;   
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;   
    gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);   
    rootPanel.add(l, gridBagConstraints); 
    
    minCostTF = new JTextField(64);   
    minCostTF.setMinimumSize(new Dimension(80, 20));   
    minCostTF.setPreferredSize(new Dimension(80, 20));   
    gridBagConstraints = new GridBagConstraints();   
    gridBagConstraints.gridx = 1;   
    gridBagConstraints.gridy = 2;   
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;   
    gridBagConstraints.insets = new Insets(5, 3, 0, 3);   
    rootPanel.add(minCostTF, gridBagConstraints); 
    
    ///////////   
    // Line 3  
    ///////////   
        l = new JLabel("Batas waktu");   
    l.setHorizontalAlignment(SwingConstants.LEFT);   
    gridBagConstraints = new GridBagConstraints();   
    gridBagConstraints.gridx = 0;   
    gridBagConstraints.gridy = 3;   
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;   
    gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);   
    rootPanel.add(l, gridBagConstraints);   
   
    deadlineTF = new JTextField(64);   
    deadlineTF.setMinimumSize(new Dimension(146, 20));   
    deadlineTF.setPreferredSize(new Dimension(146, 20));   
    deadlineTF.setEnabled(false);   
    gridBagConstraints = new GridBagConstraints();   
    gridBagConstraints.gridx = 1;   
    gridBagConstraints.gridy = 3;   
    gridBagConstraints.gridwidth = 2;   
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;   
    gridBagConstraints.insets = new Insets(5, 3, 0, 3);   
    rootPanel.add(deadlineTF, gridBagConstraints);   
   
    setDeadlineB = new JButton("Set");   
    setDeadlineB.setMinimumSize(new Dimension(70, 20));   
    setDeadlineB.setPreferredSize(new Dimension(70, 20));   
        setDeadlineB.addActionListener(new ActionListener(){   
        public void actionPerformed(ActionEvent e) {   
            Date d = deadline;   
            if (d == null) {   
                d = new Date();   
            }   
            TimeChooser tc = new TimeChooser(d);   
            if (tc.showEditTimeDlg(BookLenderGuiImpl.this) == TimeChooser.OK) {   
                deadline = tc.getDate();   
                deadlineTF.setText(deadline.toString());   
            }   
        }   
        } );   
    gridBagConstraints = new GridBagConstraints();   
    gridBagConstraints.gridx = 3;   
    gridBagConstraints.gridy = 3;   
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;   
    gridBagConstraints.insets = new Insets(5, 3, 0, 3);   
    rootPanel.add(setDeadlineB, gridBagConstraints);     
           
    rootPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));   
       
    getContentPane().add(rootPanel, BorderLayout.NORTH);   
       
       
    logTA = new JTextArea();   
    logTA.setEnabled(false);   
    JScrollPane jsp = new JScrollPane(logTA);   
    jsp.setMinimumSize(new Dimension(300, 180));   
    jsp.setPreferredSize(new Dimension(300, 180));   
    JPanel p = new JPanel();   
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));   
    p.add(jsp);   
    getContentPane().add(p, BorderLayout.CENTER);   
     
       
    p = new JPanel();   
    setExec = new JButton("Execute Order");   
        setExec.addActionListener(new ActionListener(){   
        public void actionPerformed(ActionEvent e) {   
            String title = titleTF.getText();    
            int maxPrice = -1;              
            int minCost = -1;
            if (title != null && title.length() > 0) {   
                if (deadline != null && deadline.getTime() > System.currentTimeMillis()) {   
                    try {   
                        //desiredCost = Integer.parseInt(desiredCostTF.getText());   
                        try {   
                            maxPrice = Integer.parseInt(maxCostTF.getText());  
                            minCost = Integer.parseInt(minCostTF.getText());
                            // if (maxPrice >= desiredCost) {   
                                // myAgent.purchase(title, desiredCost, maxPrice, deadline.getTime());   
                                myAgent.putForSale(title, maxPrice, minCost,deadline);   
                  notifyUser("PUT FOR LEND: "+title+" at max "+maxPrice+" by "+deadline);    
                            //}   
                            //else {   
                                // Max cost < desiredCost   
                                //JOptionPane.showMessageDialog(BookBuyerGuiImpl.this, "Max cost must be greater than best cost", "WARNING", JOptionPane.WARNING_MESSAGE);   
                            //}                                
                        }   
                        catch (Exception ex1) {   
                            // Invalid max cost   
                            JOptionPane.showMessageDialog(BookLenderGuiImpl.this, "Invalid max cost", "WARNING", JOptionPane.WARNING_MESSAGE);   
                        }   
                    }   
                    catch (Exception ex2) {   
                        // No deadline specified   
                        JOptionPane.showMessageDialog(BookLenderGuiImpl.this, "Invalid deadline", "WARNING", JOptionPane.WARNING_MESSAGE); 
                    }   
                }   
            }   
            else {   
                // No book title specified   
                JOptionPane.showMessageDialog(BookLenderGuiImpl.this, "No book title specified", "WARNING", JOptionPane.WARNING_MESSAGE);   
            }   
        }   
        } );    
       
    p.add(setExec);    
       
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));   
    getContentPane().add(p, BorderLayout.SOUTH);   
       
    pack();   
       
    setResizable(false);   
    }   
   
    public void setAgent(BookLenderAgent a) {   
        myAgent = a;   
        setTitle(myAgent.getName());   
    }   
           
    public void notifyUser(String message) {   
        logTA.append(message+"\n");   
    }   
}     
