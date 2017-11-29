package com.septanome.ihm;

import com.septanome.model.Plan;
import com.septanome.service.ServiceMetier;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class Dashboard extends JFrame implements ActionListener{
    ServiceMetier serviceMetier = new ServiceMetier();
    JPanel myMap = new JPanel();
    JPanel panelGlobal = new JPanel();
    JButton buttonChooseMap=new JButton("...");
    JButton buttonChooseCommand=new JButton("...");
    JButton buttonChargeMap = new JButton("Charger le plan");
    JLabel labelImportMap = new JLabel("Selectionnez un plan");
    JLabel labelImportCommand = new JLabel("Selectionnez une commande");
    JTextField textImportMapFile=new JTextField("D:\\My Documents\\Intellji Program\\TSPTW\\intellji\\fichiersXML\\planLyonGrand.xml");
    JTextField textImportCommandFile=new JTextField("D:\\My Documents\\Intellji Program\\TSPTW\\intellji\\fichiersXML\\DLgrand10TW2.xml");

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


        myMap.setBounds(10,10,400,600);
        myMap.setLayout(null);
        myMap.setBackground(Color.cyan);

        JPanel panelChooseFile = new JPanel();
        panelChooseFile.setBounds(600,10,500,380);
        panelChooseFile.setLayout(null);
        panelChooseFile.setBackground(Color.green);
        buttonChooseMap.addActionListener(this);
        buttonChooseCommand.addActionListener(this);
        buttonChargeMap.addActionListener(this);
        textImportMapFile.setBounds(10,10,250,40);
        textImportCommandFile.setBounds(10,100,250,40);
        buttonChooseMap.setBounds(300,10,120,40);
        buttonChooseCommand.setBounds(300,100,120,40);
        buttonChargeMap.setBounds(300,200,120,40);
        labelImportMap.setBounds(10,50,120,40);
        panelChooseFile.add(textImportMapFile);
        panelChooseFile.add(textImportCommandFile);
        panelChooseFile.add(buttonChooseMap);
        panelChooseFile.add(buttonChooseCommand);
        panelChooseFile.add(buttonChargeMap);
        panelChooseFile.add(labelImportMap);


        panelGlobal = new JPanel();
        panelGlobal.setBounds(0,0,500,500);
        panelGlobal.setLayout(null);
        panelGlobal.add(panelChooseFile);
        panelGlobal.add(myMap);
        panelGlobal.setBackground(Color.yellow);

        this.setContentPane(panelGlobal);
    }


    public void actionPerformed(ActionEvent event){
        if(event.getSource() == buttonChooseMap){
            JFileChooser jfc=new JFileChooser();
            int result = jfc.showOpenDialog(this);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Fichier XML", "xml");
            jfc.setFileFilter(filter);
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
            File file=jfc.getSelectedFile();

            if (result == JFileChooser.APPROVE_OPTION) {
                textImportMapFile.setText(file.getAbsolutePath());
            }

        }else if(event.getSource() == buttonChooseCommand){
            JFileChooser jfc=new JFileChooser();
            int result = jfc.showOpenDialog(this);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Fichier XML", "xml");
            jfc.setFileFilter(filter);
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
            File file=jfc.getSelectedFile();

            if (result == JFileChooser.APPROVE_OPTION) {
                textImportCommandFile.setText(file.getAbsolutePath());
            }

        }else if (event.getSource() == buttonChargeMap){
            String plan=textImportMapFile.getText();
            String commande=textImportCommandFile.getText();
            serviceMetier.initPlan(plan);
            serviceMetier.initCommande(commande);
            serviceMetier.initPlanLivraison();
            try {
                serviceMetier.calculerTournee(true);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            myMap = new DeliveryMap(serviceMetier,600);


//            EventQueue.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("排序");
//
//                    myMap.removeAll();
//                    myMap.add(new JLabel("tianjia"));
//                    panelGlobal.add(myMap);
//                    myMap.updateUI();
//                    myMap.repaint();
//                }
//            });

//            myMap.removeAll();
            JLabel tht = new JLabel("adfadsf");
            tht.setBounds(0,0,50,50);
            myMap.add(tht);

            panelGlobal.add(myMap);
            myMap.updateUI();
            myMap.repaint();

            System.out.println("finished!!!!");
        }

    }
    public static void main(String[] args){
        ServiceMetier sm = new ServiceMetier();
        Dashboard myDashboard = new Dashboard(sm);
    }
}

