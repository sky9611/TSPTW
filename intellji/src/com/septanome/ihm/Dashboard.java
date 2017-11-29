package com.septanome.ihm;

import com.septanome.model.Plan;
import com.septanome.service.ServiceMetier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Dashboard extends JFrame implements ActionListener{
    JButton buttonValider=new JButton("valider");;



    JTextField textImportFile=new JTextField("");

    public Dashboard(ServiceMetier sm){
        this.setTitle("IHM Courbe - Selection ");
        this.setLayout(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Pour empêcher le redimensionnement de la fenêtre
        this.setResizable(true);
        // Pour rendre la fenêtre visible
        this.setVisible(true);
        // Pour permettre la fermeture de la fenêtre lors de l'appui sur la croix rouge
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        JPanel panelMap = new JPanel();
//        panelMap.setBounds(10,10,500,380);
//        panelMap.setLayout(null);
//        panelMap.setBackground(Color.BLUE);
//        map myMap = new map(sm,500);
//        myMap.setLayout(new BorderLayout());
//        myMap.setBounds(10,10,200,200);
//        panelMap.add(myMap);

        JPanel panelChooseFile = new JPanel();
        panelChooseFile.setBounds(600,10,500,380);
        panelChooseFile.setLayout(null);
//        panelChooseFile.setBackground(Color.green);
        buttonValider.addActionListener(this);
        textImportFile.setBounds(10,10,80,80);
        buttonValider.setBounds(100,10,80,80);
        panelChooseFile.add(textImportFile);
        panelChooseFile.add(buttonValider);


        JPanel panelGlobal = new JPanel();
        panelGlobal.setBounds(0,0,500,500);
        panelGlobal.setLayout(null);
        panelGlobal.add(panelChooseFile);
//        panelGlobal.add(panelMap);
//        panelGlobal.setBackground(Color.yellow);

        this.setContentPane(panelGlobal);
    }


    public void actionPerformed(ActionEvent event){
        if(event.getSource() == buttonValider){

            String query=textImportFile.getText();
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
        }

    }
    public static void main(String[] args){
        ServiceMetier sm = new ServiceMetier();
        Dashboard myDashboard = new Dashboard(sm);
    }
}

