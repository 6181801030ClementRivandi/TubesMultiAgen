package BookLending.finder;

import jade.gui.TimeChooser;   
   
import java.awt.*;   
import java.awt.event.*;   
import javax.swing.*;   
import javax.swing.border.*;   
   
import java.util.Date;   
    
public class BookFinderGuiImpl extends JFrame implements BookFinderGui {   
    private BookFinderAgent myAgent;   
       
    private JTextField titleTF, maxCostTF, lamaPinjamTF, deadlineTF, alamatTF;   
    private JButton setDeadlineB, setExec;   
    private JTextArea logTA;   
    private Date deadline;   
       
    public BookFinderGuiImpl() {   
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
             
    // Line 0    
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
  
    // Line 1     
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
    
    // Line 2    
        l = new JLabel("Lama pinjam");   
        l.setHorizontalAlignment(SwingConstants.LEFT);   
        l.setMinimumSize(new Dimension(100, 20));   
        l.setPreferredSize(new Dimension(100, 20));   
        gridBagConstraints = new GridBagConstraints();   
        gridBagConstraints.gridx = 0;   
        gridBagConstraints.gridy = 2;   
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;   
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);   
        rootPanel.add(l, gridBagConstraints); 

        lamaPinjamTF = new JTextField(64);   
        lamaPinjamTF.setMinimumSize(new Dimension(80, 20));   
        lamaPinjamTF.setPreferredSize(new Dimension(80, 20));   
        gridBagConstraints = new GridBagConstraints();   
        gridBagConstraints.gridx = 1;   
        gridBagConstraints.gridy = 2;   
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;   
        gridBagConstraints.insets = new Insets(5, 3, 0, 3);   
        rootPanel.add(lamaPinjamTF, gridBagConstraints); 
       
    // Line 3   
        l = new JLabel("Alamat");   
        l.setHorizontalAlignment(SwingConstants.LEFT);   
        l.setMinimumSize(new Dimension(100, 20));   
        l.setPreferredSize(new Dimension(100, 20));   
        gridBagConstraints = new GridBagConstraints();   
        gridBagConstraints.gridx = 0;   
        gridBagConstraints.gridy = 3;   
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;   
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);   
        rootPanel.add(l, gridBagConstraints); 

        alamatTF = new JTextField(64);   
        alamatTF.setMinimumSize(new Dimension(120, 20));   
        alamatTF.setPreferredSize(new Dimension(120, 20));   
        gridBagConstraints = new GridBagConstraints();   
        gridBagConstraints.gridx = 1;   
        gridBagConstraints.gridy = 3;   
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;   
        gridBagConstraints.insets = new Insets(5, 3, 0, 3);   
        rootPanel.add(alamatTF, gridBagConstraints); 
  
    // Line 4  
        l = new JLabel("Batas waktu");   
        l.setHorizontalAlignment(SwingConstants.LEFT);   
        gridBagConstraints = new GridBagConstraints();   
        gridBagConstraints.gridx = 0;   
        gridBagConstraints.gridy = 4;   
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;   
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);   
        rootPanel.add(l, gridBagConstraints);   

        deadlineTF = new JTextField(64);   
        deadlineTF.setMinimumSize(new Dimension(146, 20));   
        deadlineTF.setPreferredSize(new Dimension(146, 20));   
        deadlineTF.setEnabled(false);   
        gridBagConstraints = new GridBagConstraints();   
        gridBagConstraints.gridx = 1;   
        gridBagConstraints.gridy = 4;   
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
                if (tc.showEditTimeDlg(BookFinderGuiImpl.this) == TimeChooser.OK) {   
                    deadline = tc.getDate();   
                    deadlineTF.setText(deadline.toString());   
                }   
            }   
            } );   
        gridBagConstraints = new GridBagConstraints();   
        gridBagConstraints.gridx = 3;   
        gridBagConstraints.gridy = 4;   
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
                int maxCost = -1;
                int rentTime = -1;
                String address = null;

                if (title != null && title.length() > 0) {   
                    if (deadline != null && deadline.getTime() > System.currentTimeMillis()) {   
                        try {      
                            try {   
                                maxCost = Integer.parseInt(maxCostTF.getText());   
                                rentTime = Integer.parseInt(lamaPinjamTF.getText());
                                address = String.valueOf(alamatTF.getText());
                                    myAgent.rentBook(title, maxCost, deadline,rentTime,address);   
                                    notifyUser("PUT FOR FIND: "+title+" at max "+maxCost+" by "+deadline);                                
                            }   
                            catch (Exception ex1) {   
                                JOptionPane.showMessageDialog(BookFinderGuiImpl.this, "Invalid max cost", "WARNING", JOptionPane.WARNING_MESSAGE);   
                            }   
                        }   
                        catch (Exception ex2) {   
                            JOptionPane.showMessageDialog(BookFinderGuiImpl.this, "Invalid deadline", "WARNING", JOptionPane.WARNING_MESSAGE); 
                        }   
                    }   
                }   
                else {   
                    JOptionPane.showMessageDialog(BookFinderGuiImpl.this, "No book title specified", "WARNING", JOptionPane.WARNING_MESSAGE);   
                }   
            }   
            } );    

        p.add(setExec);    

        p.setBorder(new BevelBorder(BevelBorder.LOWERED));   
        getContentPane().add(p, BorderLayout.SOUTH);   

        pack();   

        setResizable(false);   
    }   
   
    public void setAgent(BookFinderAgent a) {   
        myAgent = a;   
        setTitle(myAgent.getName());   
    }   
           
    public void notifyUser(String message) {   
        logTA.append(message+"\n");   
    }   
}     
