package com.septanome.ihm;

import com.septanome.model.Commande;
import com.septanome.model.Plan;
import com.septanome.model.Point;
import com.septanome.model.Troncon;
import com.septanome.service.ServiceMetier;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

public class ihm extends JFrame implements ActionListener {


    JPanel but1=new JPanel();
    JPanel menu=new JPanel();
    JButton valider=new JButton("valider");
    JButton b1     =new JButton("          action 1            ");
    JButton b2     =new JButton("          action 2            ");
    JButton b3     =new JButton("          action 3            ");
    JButton b1o    =new JButton("          action 4            ");
    JButton b2o    =new JButton("          action 5            ");
    JButton b3o    =new JButton("          action 6            ");
    JTextField loadPlan=new JTextField("entrez le chemin");

    public ihm(ServiceMetier sm) {
        super("Fullscreen");
        this.setTitle("Map");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Toolkit toolkit =  Toolkit.getDefaultToolkit ();
        Dimension dim = toolkit.getScreenSize();
        getContentPane().setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        pack();
        setResizable(false);
        show();
        this.setLocationRelativeTo(null);



        //création menu(side bar)
        menu=new JPanel();
        menu.setPreferredSize(new Dimension(dim.width-dim.height, dim.height));
        menu.setLayout(new BoxLayout(menu,BoxLayout.Y_AXIS));

        //création panel de bouton
        JPanel butPan=new JPanel();
        butPan.setLayout(new BoxLayout(butPan,BoxLayout.Y_AXIS));

        JPanel text=new JPanel();
        text.setLayout(new BoxLayout(text,BoxLayout.X_AXIS));
        but1=new JPanel();
        but1.setLayout(new BoxLayout(but1,BoxLayout.X_AXIS));

        JPanel but2=new JPanel();
        but2.setLayout(new BoxLayout(but2,BoxLayout.X_AXIS));

        JPanel tour= new JPanel();
        tour.setLayout(new BoxLayout(tour,BoxLayout.Y_AXIS));
        JPanel tourTitle=new JPanel();


        tourTitle.setPreferredSize(new Dimension(300, 70));
        tourTitle.setMaximumSize(new Dimension(300, 70));
        JLabel j=new JLabel("ordre de la tournee",SwingConstants.CENTER);
        j.setFont(j.getFont().deriveFont(32f));


        tourTitle.add(j);

        b1.addActionListener(this);


        valider.addActionListener(this);


        loadPlan.setPreferredSize(new Dimension(150, 25));
        loadPlan.setMaximumSize(new Dimension(150, 25));
        text.add(loadPlan);
        text.add(Box.createRigidArea(new Dimension(30,0)));
        text.add(valider);

        but1.add(b1);
        but1.add(Box.createRigidArea(new Dimension(30,0)));
        but1.add(b2);
        but1.add(Box.createRigidArea(new Dimension(30,0)));
        but1.add(b3);

        but2.add(b1o);
        but2.add(Box.createRigidArea(new Dimension(30,0)));
        but2.add(b2o);
        but2.add(Box.createRigidArea(new Dimension(30,0)));
        but2.add(b3o);

        butPan.add(Box.createRigidArea(new Dimension(0,20)));
        butPan.add(but1);
        butPan.add(Box.createRigidArea(new Dimension(0,20)));
        butPan.add(but2);

        menu.add(Box.createRigidArea(new Dimension(0,20)));
        menu.add(text);
        menu.add(Box.createRigidArea(new Dimension(0,50)));
        menu.add(butPan);
        menu.add(Box.createRigidArea(new Dimension(0,50)));
        menu.add(tourTitle);
        menu.add(tour);

//        pan.add(menu, BorderLayout.EAST);
//
//        this.getContentPane().add(pan);


        this.setVisible(true);
    }
    public ihm(){
        super("Fullscreen");

        this.setTitle("Map");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Toolkit toolkit =  Toolkit.getDefaultToolkit ();
        Dimension dim = toolkit.getScreenSize();
        getContentPane().setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        pack();
        setResizable(false);
        this.setLocationRelativeTo(null);
        JPanel menu=new JPanel();
        menu.setPreferredSize(new Dimension(dim.width-dim.height, dim.height));
        menu.setLayout(new BoxLayout(menu,BoxLayout.Y_AXIS));

        //création panel de bouton
        JPanel butPan=new JPanel();
        butPan.setLayout(new BoxLayout(butPan,BoxLayout.Y_AXIS));

        JPanel pan=new JPanel();
        pan.setLayout(new BorderLayout());


        JPanel text=new JPanel();
        text.setLayout(new BoxLayout(text,BoxLayout.X_AXIS));
        JPanel but1=new JPanel();
        but1.setLayout(new BoxLayout(but1,BoxLayout.X_AXIS));

        JPanel but2=new JPanel();
        but2.setLayout(new BoxLayout(but2,BoxLayout.X_AXIS));
        b1.addActionListener(this);


        valider.addActionListener(this);


        loadPlan.setPreferredSize(new Dimension(150, 25));
        loadPlan.setMaximumSize(new Dimension(150, 25));
        text.add(loadPlan);
        text.add(Box.createRigidArea(new Dimension(30,0)));
        text.add(valider);

        but1.add(b1);
        but1.add(Box.createRigidArea(new Dimension(30,0)));
        but1.add(b2);
        but1.add(Box.createRigidArea(new Dimension(30,0)));
        but1.add(b3);

        but2.add(b1o);
        but2.add(Box.createRigidArea(new Dimension(30,0)));
        but2.add(b2o);
        but2.add(Box.createRigidArea(new Dimension(30,0)));
        but2.add(b3o);

        butPan.add(Box.createRigidArea(new Dimension(0,20)));
        butPan.add(but1);
        butPan.add(Box.createRigidArea(new Dimension(0,20)));
        butPan.add(but2);

        menu.add(Box.createRigidArea(new Dimension(0,20)));
        menu.add(text);
        menu.add(Box.createRigidArea(new Dimension(0,50)));
        menu.add(butPan);
        menu.add(Box.createRigidArea(new Dimension(0,50)));
        pan.add(menu, BorderLayout.EAST);

        this.getContentPane().add(pan);


        this.setVisible(true);
    }
    public void actionPerformed(ActionEvent arg0){
        if(arg0.getSource() == valider){

            String query=loadPlan.getText();
            ServiceMetier m=new ServiceMetier();
            m.initPlan(query);
            m.initCommande("intellji/fichiersXML/DLgrand10TW2.xml");
            m.initPlanLivraison();

            try {
                m.calculerTournee(true);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Plan p = m.getPlan();
            ihm fenetre = new ihm(m);
        }

    }



    public static void main(String[] args){
        ihm fenetre = new ihm();
    }
}
