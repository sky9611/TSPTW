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


        myMap.setBounds(10,10,800,900);
        myMap.setBackground(Color.cyan);

        JPanel panelChooseFile = new JPanel();
        panelChooseFile.setBounds(900,10,500,380);
        panelChooseFile.setLayout(null);
        panelChooseFile.setBackground(Color.green);
        buttonChooseMap.addActionListener(this);
        buttonChooseCommand.addActionListener(this);
        buttonChargeMap.addActionListener(this);
        labelImportMap.setBounds(10,10,200,30);
        textImportMapFile.setBounds(10,50,400,30);
        buttonChooseMap.setBounds(450,50,40,30);
        labelImportCommand.setBounds(10,100,200,30);
        textImportCommandFile.setBounds(10,140,400,30);
        buttonChooseCommand.setBounds(450,140,40,30);
        buttonChargeMap.setBounds(300,200,120,30);
        panelChooseFile.add(textImportMapFile);
        panelChooseFile.add(textImportCommandFile);
        panelChooseFile.add(buttonChooseMap);
        panelChooseFile.add(buttonChooseCommand);
        panelChooseFile.add(buttonChargeMap);
        panelChooseFile.add(labelImportMap);
        panelChooseFile.add(labelImportCommand);




        panelGlobal.setBounds(0,0,1600,1000);
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
            panelGlobal.remove(myMap);
            myMap = new DeliveryMap(serviceMetier,900);
            myMap.setBounds(10,10,800,900);
            myMap.setLayout(null);
            panelGlobal.add(myMap);
            //myMap.updateUI();
            repaint();


            System.out.println("finished!!!!");
        }

    }
    public static void main(String[] args){
        ServiceMetier sm = new ServiceMetier();
        Dashboard myDashboard = new Dashboard(sm);
    }
}

