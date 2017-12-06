package com.septanome.ihm;

import com.septanome.model.Commande;
import com.septanome.model.Livraison;
import com.septanome.model.Point;
import com.septanome.service.ServiceMetier;
import com.septanome.util.UtilXML;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Dashboard extends JFrame implements ActionListener{
    ServiceMetier serviceMetier = new ServiceMetier();
    long focusedPointId;
    int focusedPointNumber=0;
    Commande commandeForUndo;
    Commande commandeForRedo;

    int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
    int xmin;
    int ymin;
    int scale;
    double ratio = 1.5;
    DeliveryMap myMap = new DeliveryMap(serviceMetier,0,2,-1);
    JPanel panelGlobal = new JPanel();
    JPanel panelChooseFile = new JPanel();
    JPanel panelFocusedPoint =new JPanel();
    JPanel panelPointDetail = new JPanel();
    JPanel panelSelectPoint = new JPanel();
    JPanel panelAddPoint = new JPanel();
    JPanel panelRemovePoint = new JPanel();
    JPanel panelEditPlageHoraire = new JPanel();
    JPanel panelUndo = new JPanel();
    JButton buttonChooseMap=new JButton("...");
    JButton buttonChooseCommand=new JButton("...");
    JButton buttonChargeMap = new JButton("Charger le plan");
    JButton buttonPreviousPoint = new JButton("Precedent");
    JButton buttonNextPoint = new JButton("Prochain");
    JButton buttonAddPoint = new JButton("Valider");
    JButton buttonRemovePoint = new JButton("Valider");
    JButton buttonEditPlageHoraire = new JButton("Valider");
    JButton buttonUndo = new JButton("Undo");
    JButton buttonRedo = new JButton("Redo");
    JButton buttonGenerateFile = new JButton("Generer un fichier");
    JLabel labelPointDetail = new JLabel("");
    JTextField textImportMapFile=new JTextField("fichiersXML/planLyonGrand.xml");
    JTextField textImportCommandFile=new JTextField("fichiersXML/DLgrand20TW.xml");
    JTextField textAddPointId = new JTextField();
    JTextField textAddPointHeureDebut = new JTextField();
    JTextField textAddPointHeureFin = new JTextField();
    JTextField textAddPointDuration = new JTextField();
    JTextField textRemovePointID = new JTextField();
    JTextField textEditPointID = new JTextField();
    JTextField textEditPointHeureDebut = new JTextField();
    JTextField textEditPointHeureFin = new JTextField();

    public Dashboard(ServiceMetier serviceMetier){
        this.setTitle("TSPTW");
        this.setLayout(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Pour empêcher le redimensionnement de la fenêtre
        this.setResizable(true);
        // Pour rendre la fenêtre visible
        this.setVisible(true);
        // Pour permettre la fermeture de la fenêtre lors de l'appui sur la croix rouge
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

//        myMap.setBounds(10,10,900,900);
//        myMap.setBackground(Color.cyan);

        panelChooseFile.setBounds((int)((screenHeight-180)*ratio/1.5)+100,10,500,250);
        panelChooseFile.setLayout(null);
        buttonChooseMap.addActionListener(this);
        buttonChooseCommand.addActionListener(this);
        buttonChargeMap.addActionListener(this);
        JLabel labelImportMap = new JLabel("Selectionnez un plan");
        JLabel labelImportCommand = new JLabel("Selectionnez une commande");
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

        panelSelectPoint.setBounds(10,screenHeight-160,(int)((screenHeight-180)*ratio/1.5),70);
        panelSelectPoint.setLayout(null);
        panelSelectPoint.setBackground(Color.blue);
        buttonPreviousPoint.addActionListener(this);
        buttonPreviousPoint.setBounds(10,10,100,50);
        buttonNextPoint.addActionListener(this);
        buttonNextPoint.setBounds(panelSelectPoint.getWidth()-110,10,100,50);
        panelSelectPoint.add(buttonPreviousPoint);
        panelSelectPoint.add(buttonNextPoint);
        panelSelectPoint.setVisible(false);

        panelFocusedPoint.setBounds(0,0,0,0);
        panelFocusedPoint.setLayout(null);

        panelPointDetail.setBounds((int)((screenHeight-180)*ratio/1.5)+100,250,500,150);
        panelPointDetail.setLayout(null);
        panelPointDetail.setBackground(Color.ORANGE);
        labelPointDetail.setBounds(10,10,450,150);
        panelPointDetail.add(labelPointDetail);
        panelPointDetail.setVisible(false);

        panelAddPoint.setBounds((int)((screenHeight-180)*ratio/1.5)+100,400,500,180);
        panelAddPoint.setLayout(null);
        panelAddPoint.setBackground(Color.green);
        JLabel labelAddPointTitle = new JLabel("Ajouter une nouvelle livraison:");
        JLabel labelAddPointID = new JLabel("Point Id:                                                                                                   *");
        JLabel labelAddPointPlageHoraire = new JLabel("Plage horaire:  De                                       A                                          (format: hh:mm:ss)");
        JLabel labelAddPointDuration = new JLabel("Duree:                                                                                                      * (minute)");
        labelAddPointID.setBounds(10,40,500,30);
        labelAddPointPlageHoraire.setBounds(10,70,500,30);
        labelAddPointDuration.setBounds(10,100,500,30);
        labelAddPointTitle.setBounds(50,10,200,30);
        textAddPointId.setBounds(90,40,260,30);
        textAddPointHeureDebut.setBounds(120,70,100,30);
        textAddPointHeureFin.setBounds(250,70,100,30);
        textAddPointDuration.setBounds(90,100,260,30);
        buttonAddPoint.setBounds(300,140,100,30);
        buttonAddPoint.addActionListener(this);
        panelAddPoint.add(labelAddPointTitle);
        panelAddPoint.add(labelAddPointID);
        panelAddPoint.add(labelAddPointPlageHoraire);
        panelAddPoint.add(labelAddPointDuration);
        panelAddPoint.add(textAddPointId);
        panelAddPoint.add(textAddPointHeureDebut);
        panelAddPoint.add(textAddPointHeureFin);
        panelAddPoint.add(textAddPointDuration);
        panelAddPoint.add(buttonAddPoint);
        panelAddPoint.setVisible(false);

        panelRemovePoint.setBounds((int)((screenHeight-180)*ratio/1.5)+100,580,500,130);
        panelRemovePoint.setLayout(null);
        panelRemovePoint.setBackground(Color.MAGENTA);
        JLabel labelRemovePointTitle = new JLabel("Enlever une livraison:");
        JLabel labelRemovePointID = new JLabel("Point Id:                                                                                                   *");
        labelRemovePointTitle.setBounds(50,10,200,30);
        labelRemovePointID.setBounds(10,40,500,30);
        textRemovePointID.setBounds(90,40,260,30);
        buttonRemovePoint.setBounds(300,80,100,30);
        buttonRemovePoint.addActionListener(this);
        panelRemovePoint.add(labelRemovePointTitle);
        panelRemovePoint.add(labelRemovePointID);
        panelRemovePoint.add(textRemovePointID);
        panelRemovePoint.add(buttonRemovePoint);
        panelRemovePoint.setVisible(false);

        panelEditPlageHoraire.setBounds((int)((screenHeight-180)*ratio/1.5)+100,710,500,180);
        panelEditPlageHoraire.setLayout(null);
        panelEditPlageHoraire.setBackground(Color.LIGHT_GRAY);
        JLabel labelEditPointID = new JLabel("Point Id:                                                                                                   *");
        JLabel labelEditPointPlageHoraire = new JLabel("Plage horaire:  De                                       A                                          (format: hh:mm:ss)");
        JLabel labelEditPointTitle = new JLabel("Modifier une livraison:");
        labelEditPointTitle.setBounds(50,10,500,30);
        labelEditPointID.setBounds(10,40,500,30);
        labelEditPointPlageHoraire.setBounds(10,70,500,30);
        textEditPointID.setBounds(90,40,260,30);
        textEditPointHeureDebut.setBounds(120,70,100,30);
        textEditPointHeureFin.setBounds(250,70,100,30);
        buttonEditPlageHoraire.setBounds(300,110,100,30);
        buttonEditPlageHoraire.addActionListener(this);
        panelEditPlageHoraire.add(labelEditPointTitle);
        panelEditPlageHoraire.add(labelEditPointID);
        panelEditPlageHoraire.add(labelEditPointPlageHoraire);
        panelEditPlageHoraire.add(textEditPointID);
        panelEditPlageHoraire.add(textEditPointHeureDebut);
        panelEditPlageHoraire.add(textEditPointHeureFin);
        panelEditPlageHoraire.add(buttonEditPlageHoraire);
        panelEditPlageHoraire.setVisible(false);

        panelUndo.setBounds((int)((screenHeight-180)*ratio/1.5)+100,screenHeight-160,500,70);
        panelUndo.setLayout(null);
        buttonUndo.setBounds(10,10,100,50);
        buttonRedo.setBounds(120,10,100,50);
        buttonGenerateFile.setBounds(panelUndo.getWidth()-160,10,150,50);
        buttonUndo.setVisible(false);
        buttonRedo.setVisible(false);
        buttonGenerateFile.setVisible(false);
        buttonUndo.addActionListener(this);
        buttonRedo.addActionListener(this);
        buttonGenerateFile.addActionListener(this);
        panelUndo.add(buttonUndo);
        panelUndo.add(buttonRedo);
        panelUndo.add(buttonGenerateFile);

        panelGlobal.setBounds(0,0,Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height);
        panelGlobal.setLayout(null);
        panelGlobal.add(panelChooseFile);
        panelGlobal.add(panelSelectPoint);
        panelGlobal.add(panelFocusedPoint);
        panelGlobal.add(panelPointDetail);
        panelGlobal.add(panelAddPoint);
        panelGlobal.add(panelRemovePoint);
        panelGlobal.add(panelEditPlageHoraire);
        panelGlobal.add(panelUndo);

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
            try {
                String plan = textImportMapFile.getText();
                String commande = textImportCommandFile.getText();
                serviceMetier.initPlan(plan);
                serviceMetier.initCommande(commande);
                serviceMetier.initPlanLivraison();
                commandeForUndo = new Commande(serviceMetier.getCommande());
            }catch (Exception e){
                JOptionPane.showMessageDialog(null, "Veuillez verifier les fichiers", "Message", JOptionPane.PLAIN_MESSAGE);
                System.out.println(e);
                return;
            }
            try {
                serviceMetier.calculerTournee(true);
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            if(serviceMetier.getTournee()!=null) {
                focusedPointNumber = 0;
                panelGlobal.remove(myMap);
                myMap = new DeliveryMap(serviceMetier,screenHeight-180,ratio,-1);
                myMap.setBounds(10,10,Toolkit.getDefaultToolkit().getScreenSize().width/4*3,screenHeight-180);
                myMap.setLayout(null);
                panelGlobal.add(myMap);
                repaint();
                xmin = myMap.getXmin();
                ymin = myMap.getYmin();
                scale = myMap.getScale();
                panelFocusedPoint.setBounds((int) ((((double) serviceMetier.getCommande().getEntrepot().getCoordX()) - xmin) / scale * (screenHeight-180 )*ratio +6), (int) ((((double) serviceMetier.getCommande().getEntrepot().getCoordY()) - ymin) / scale * (screenHeight-180))+6, 15, 15);
                panelFocusedPoint.setBackground(Color.GREEN);
                panelGlobal.add(panelFocusedPoint);
                panelSelectPoint.setVisible(true);
                panelPointDetail.setVisible(true);
                panelAddPoint.setVisible(true);
                panelRemovePoint.setVisible(true);
                panelEditPlageHoraire.setVisible(true);
                buttonGenerateFile.setVisible(true);
                repaint();
                refreshPanelPointDetail();
            }else{
                JOptionPane.showMessageDialog(null, "Ne pas pouvoir trouver une solution", "Message", JOptionPane.PLAIN_MESSAGE);
            }
        }else if (event.getSource() == buttonNextPoint){
            if (focusedPointNumber<serviceMetier.getCommande().getListLivraison().size()) {
                focusedPointNumber++;
                focusedPointId = serviceMetier.getTournee().getChemins().get(focusedPointNumber).getOriginePointID();
                Point tmpPoint = serviceMetier.getPlan().getPointsMap().get(focusedPointId);
                panelFocusedPoint.setBounds((int) ((((double) tmpPoint.getCoordX()) - xmin) / scale * (screenHeight-180 )*ratio +6), (int) ((((double) tmpPoint.getCoordY()) - ymin) / scale * (screenHeight-180))+6, 15, 15);
                panelFocusedPoint.setBackground(Color.RED);
                panelGlobal.add(panelFocusedPoint);
                panelGlobal.remove(myMap);
                myMap = new DeliveryMap(serviceMetier,screenHeight-180,ratio,focusedPointNumber);
                myMap.setBounds(10,10,Toolkit.getDefaultToolkit().getScreenSize().width/4*3,screenHeight-180);
                myMap.setLayout(null);
                panelGlobal.add(myMap);
                repaint();
                refreshPanelPointDetail();
            }else{
                JOptionPane.showMessageDialog(null, "C'est deja le dernier livraison", "Message", JOptionPane.PLAIN_MESSAGE);
            }
        }else if(event.getSource()==buttonPreviousPoint){
            if (focusedPointNumber>0) {
                focusedPointNumber--;
                focusedPointId = serviceMetier.getTournee().getChemins().get(focusedPointNumber).getOriginePointID();
                Point tmpPoint = serviceMetier.getPlan().getPointsMap().get(focusedPointId);
                panelFocusedPoint.setBounds((int) ((((double) tmpPoint.getCoordX()) - xmin) / scale * (screenHeight-180 )*ratio +6), (int) ((((double) tmpPoint.getCoordY()) - ymin) / scale * (screenHeight-180))+6, 15, 15);
                panelFocusedPoint.setBackground(Color.RED);
                panelGlobal.add(panelFocusedPoint);
                repaint();
                if(focusedPointNumber>0) {
                    panelGlobal.remove(myMap);
                    myMap = new DeliveryMap(serviceMetier, screenHeight - 180, ratio, focusedPointNumber);
                    myMap.setBounds(10, 10, Toolkit.getDefaultToolkit().getScreenSize().width / 4 * 3, screenHeight - 180);
                    myMap.setLayout(null);
                    panelGlobal.add(myMap);
                }
                refreshPanelPointDetail();
            }else{
                JOptionPane.showMessageDialog(null, "C'est deja le premier livraison", "Message", JOptionPane.PLAIN_MESSAGE);
            }
        }else if(event.getSource()==buttonAddPoint){
            try{
                Long pointID= Long.valueOf(textAddPointId.getText());
                int heureDebut = 0;
                int heureFin = Integer.MAX_VALUE;
                int duration;
                if(textAddPointDuration.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Veillez saisir une duree", "Message", JOptionPane.PLAIN_MESSAGE);
                    return;
                }else{
                    duration = 60 * Integer.valueOf(textAddPointDuration.getText());
                }
                if(!textAddPointHeureDebut.getText().equals("")) {
                    heureDebut = 3600 * Integer.valueOf(textAddPointHeureDebut.getText().substring(0, 2)) + 60 * Integer.valueOf(textAddPointHeureDebut.getText().substring(3, 5)) + Integer.valueOf(textAddPointHeureDebut.getText().substring(6, 8));
                }
                if(!textAddPointHeureFin.getText().equals("")) {
                    heureFin = 3600 * Integer.valueOf(textAddPointHeureFin.getText().substring(0, 2)) + 60 * Integer.valueOf(textAddPointHeureFin.getText().substring(3, 5)) + Integer.valueOf(textAddPointHeureFin.getText().substring(6, 8));
                }
                if(heureDebut>heureFin){
                    JOptionPane.showMessageDialog(null, "Le debut de la plage horaire doit etre inferieur a la fin", "Message", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                Livraison l = new Livraison(pointID,serviceMetier.getPlan().getPointsMap().get(pointID).getCoordX(),serviceMetier.getPlan().getPointsMap().get(pointID).getCoordY(),duration,heureDebut,heureFin);
                commandeForUndo = new Commande(serviceMetier.getCommande());
                serviceMetier.ajouterNouveauLivraison(l);
                serviceMetier.initPlanLivraison();
                try {
                    serviceMetier.calculerTournee(true);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                if(serviceMetier.getTournee()!=null){
                    focusedPointNumber = 0;
                    panelGlobal.remove(myMap);
                    myMap = new DeliveryMap(serviceMetier,screenHeight-180,ratio,-1);
                    myMap.setBounds(10,10,Toolkit.getDefaultToolkit().getScreenSize().width/4*3,screenHeight-180);
                    myMap.setLayout(null);
                    panelGlobal.add(myMap);
                    panelFocusedPoint.setBounds((int) ((((double) serviceMetier.getCommande().getEntrepot().getCoordX()) - xmin) / scale * (screenHeight-180 )*ratio +6), (int) ((((double) serviceMetier.getCommande().getEntrepot().getCoordY()) - ymin) / scale * (screenHeight-180))+6, 15, 15);
                    panelFocusedPoint.setBackground(Color.GREEN);
                    panelGlobal.add(panelFocusedPoint);
                    buttonUndo.setVisible(true);
                    repaint();
                    refreshPanelPointDetail();
                    JOptionPane.showMessageDialog(null, "Reussi!", "Message", JOptionPane.PLAIN_MESSAGE);
                }else{
                    serviceMetier.setCommande(commandeForUndo);
                    serviceMetier.initPlanLivraison();
                    try {
                        serviceMetier.calculerTournee(true);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(null, "Impossible d'ajouter cette livraison", "Message", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Erreur pendant l'ajout", "Message", JOptionPane.PLAIN_MESSAGE);
            }
        }else if(event.getSource()==buttonRemovePoint){
            try{
                Long pointID= Long.valueOf(textRemovePointID.getText());
                boolean found = false;
                for(Livraison l:serviceMetier.getCommande().getListLivraison()){
                    if(l.getId()==pointID){
                        commandeForUndo = new Commande(serviceMetier.getCommande());
                        serviceMetier.getCommande().getListLivraison().remove(l);
                        found = true;
                        break;
                    }
                }
                if(!found){
                    JOptionPane.showMessageDialog(null, "Veuillez verifier l'ID du point", "Message", JOptionPane.PLAIN_MESSAGE);
                }else {
                    serviceMetier.initPlanLivraison();
                    try {
                        do{
                            serviceMetier.calculerTournee(true);
                        }while(serviceMetier.getTournee()==null);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                    focusedPointNumber = 0;
                    panelGlobal.remove(myMap);
                    myMap = new DeliveryMap(serviceMetier,screenHeight-180,ratio,-1);
                    myMap.setBounds(10,10,Toolkit.getDefaultToolkit().getScreenSize().width/4*3,screenHeight-180);
                    myMap.setLayout(null);
                    panelGlobal.add(myMap);
                    panelFocusedPoint.setBounds((int) ((((double) serviceMetier.getCommande().getEntrepot().getCoordX()) - xmin) / scale * (screenHeight-180 )*ratio +6), (int) ((((double) serviceMetier.getCommande().getEntrepot().getCoordY()) - ymin) / scale * (screenHeight-180))+6, 15, 15);
                    panelFocusedPoint.setBackground(Color.GREEN);
                    panelGlobal.add(panelFocusedPoint);
                    buttonUndo.setVisible(true);
                    repaint();
                    refreshPanelPointDetail();
                    JOptionPane.showMessageDialog(null, "Reussi", "Message", JOptionPane.PLAIN_MESSAGE);
                }
            }catch (Exception e){
                JOptionPane.showMessageDialog(null, "Erreur pendant la suppression", "Message", JOptionPane.PLAIN_MESSAGE);
            }

        }else if(event.getSource()==buttonEditPlageHoraire){
            try{
                Long pointID= Long.valueOf(textEditPointID.getText());
                int heureDebutOrigine = 0;
                int heureFinOrigine = Integer.MAX_VALUE;
                boolean found = false;
                for(Livraison l:serviceMetier.getCommande().getListLivraison()){
                    if(l.getId()==pointID){
                        found = true;
                        commandeForUndo = new Commande(serviceMetier.getCommande());
                        int d = 0;
                        int f = Integer.MAX_VALUE;
                        if(!textEditPointHeureDebut.getText().equals("")) d = 3600 * Integer.valueOf(textEditPointHeureDebut.getText().substring(0, 2)) + 60 * Integer.valueOf(textEditPointHeureDebut.getText().substring(3, 5)) + Integer.valueOf(textEditPointHeureDebut.getText().substring(6, 8));
                        if(!textEditPointHeureFin.getText().equals("")) f = 3600 * Integer.valueOf(textEditPointHeureFin.getText().substring(0, 2)) + 60 * Integer.valueOf(textEditPointHeureFin.getText().substring(3, 5)) + Integer.valueOf(textEditPointHeureFin.getText().substring(6, 8));
                        if(d<f) {
                            heureDebutOrigine = l.getHeureDeDebut();
                            heureFinOrigine = l.getHeureDeFin();
                            l.setHeureDeDebut(d);
                            l.setHeureDeFin(f);
                        }else{
                            JOptionPane.showMessageDialog(null, "Le debut de la plage horaire doit etre inferieur a la fin", "Message", JOptionPane.PLAIN_MESSAGE);
                            return;
                        }
                        break;
                    }
                }
                if(!found){
                    JOptionPane.showMessageDialog(null, "Veuillez verifier l'ID du point", "Message", JOptionPane.PLAIN_MESSAGE);
                    return;
                }else{
                    serviceMetier.initPlanLivraison();
                    try {
                        serviceMetier.calculerTournee(true);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                    if(serviceMetier.getTournee()!=null) {
                        focusedPointNumber = 0;
                        panelGlobal.remove(myMap);
                        myMap = new DeliveryMap(serviceMetier,screenHeight-180,ratio,-1);
                        myMap.setBounds(10,10,Toolkit.getDefaultToolkit().getScreenSize().width/4*3,screenHeight-180);
                        myMap.setLayout(null);
                        panelGlobal.add(myMap);
                        panelFocusedPoint.setBounds((int) ((((double) serviceMetier.getCommande().getEntrepot().getCoordX()) - xmin) / scale * (screenHeight-180 )*ratio +6), (int) ((((double) serviceMetier.getCommande().getEntrepot().getCoordY()) - ymin) / scale * (screenHeight-180))+6, 15, 15);
                        panelFocusedPoint.setBackground(Color.GREEN);
                        panelGlobal.add(panelFocusedPoint);
                        buttonUndo.setVisible(true);
                        repaint();
                        refreshPanelPointDetail();
                        JOptionPane.showMessageDialog(null, "Reussi!", "Message", JOptionPane.PLAIN_MESSAGE);
                    }else{
                        for(Livraison l:serviceMetier.getCommande().getListLivraison()){
                            if(l.getId()==pointID) {
                                l.setHeureDeDebut(heureDebutOrigine);
                                l.setHeureDeFin(heureFinOrigine);
                            }
                        }
                        serviceMetier.initPlanLivraison();
                        JOptionPane.showMessageDialog(null, "Impossible d'effectuer la modification", "Message", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Erreur pendant la modification", "Message", JOptionPane.PLAIN_MESSAGE);
            }
        }else if(event.getSource()==buttonUndo){
            try {
                commandeForRedo = new Commande(serviceMetier.getCommande());
                serviceMetier.setCommande(commandeForUndo);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            serviceMetier.initPlanLivraison();
            try {
                do{
                    serviceMetier.calculerTournee(true);
                }while(serviceMetier.getTournee()==null);
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            focusedPointNumber = 0;
            panelGlobal.remove(myMap);
            myMap = new DeliveryMap(serviceMetier,screenHeight-180,ratio,-1);
            myMap.setBounds(10,10,Toolkit.getDefaultToolkit().getScreenSize().width/4*3,screenHeight-180);
            myMap.setLayout(null);
            panelGlobal.add(myMap);
            panelFocusedPoint.setBounds((int) ((((double) serviceMetier.getCommande().getEntrepot().getCoordX()) - xmin) / scale * (screenHeight-180 )*ratio +6), (int) ((((double) serviceMetier.getCommande().getEntrepot().getCoordY()) - ymin) / scale * (screenHeight-180))+6, 15, 15);
            panelFocusedPoint.setBackground(Color.GREEN);
            panelGlobal.add(panelFocusedPoint);
            buttonUndo.setVisible(false);
            buttonRedo.setVisible(true);
            repaint();
            refreshPanelPointDetail();
            JOptionPane.showMessageDialog(null, "Undo Reussi", "Message", JOptionPane.PLAIN_MESSAGE);
        }else if(event.getSource()==buttonRedo){
            try {
                serviceMetier.setCommande(commandeForRedo);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            serviceMetier.initPlanLivraison();
            try {
                do{
                    serviceMetier.calculerTournee(true);
                }while(serviceMetier.getTournee()==null);
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            focusedPointNumber = 0;
            panelGlobal.remove(myMap);
            myMap = new DeliveryMap(serviceMetier,screenHeight-180,ratio,-1);
            myMap.setBounds(10,10,Toolkit.getDefaultToolkit().getScreenSize().width/4*3,screenHeight-180);
            myMap.setLayout(null);
            panelGlobal.add(myMap);
            panelFocusedPoint.setBounds((int) ((((double) serviceMetier.getCommande().getEntrepot().getCoordX()) - xmin) / scale * (screenHeight-180 )*ratio +6), (int) ((((double) serviceMetier.getCommande().getEntrepot().getCoordY()) - ymin) / scale * (screenHeight-180))+6, 15, 15);
            panelFocusedPoint.setBackground(Color.GREEN);
            panelGlobal.add(panelFocusedPoint);
            buttonUndo.setVisible(true);
            buttonRedo.setVisible(false);
            repaint();
            refreshPanelPointDetail();
            JOptionPane.showMessageDialog(null, "Redo Reussi", "Message", JOptionPane.PLAIN_MESSAGE);
        }else if(event.getSource()==buttonGenerateFile){
            try {
                UtilXML myUtil = new UtilXML();
                myUtil.writeTourneeToFile("fichiersXML/output.txt", serviceMetier);
                JOptionPane.showMessageDialog(null, "Fichier genere!", "Message", JOptionPane.PLAIN_MESSAGE);
            }catch (Exception e){
                JOptionPane.showMessageDialog(null, "Erreur pendant l'ecriture", "Message", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }
    public void refreshPanelPointDetail(){
        if(focusedPointNumber==0) {
            int heureDepartint = serviceMetier.getCommande().getHeureDeDepart();
            String heureDepart = String.valueOf((heureDepartint - heureDepartint % 3600) / 3600) + ":" + String.valueOf(((heureDepartint - heureDepartint % 60) % 3600) / 60) + ":" + String.valueOf(heureDepartint % 60);
            labelPointDetail.setText("<html>Entrepot:<br>Coordonne X:" + serviceMetier.getCommande().getEntrepot().getCoordX() + "<br>" +
                    "Coordonne Y:" + serviceMetier.getCommande().getEntrepot().getCoordY() + "<br>" +
                    "Heure de depart:" + heureDepart + "</html>");
            labelPointDetail.repaint();
        }else{
            String heureDeDebut= "N/A";
            String heureDeFin = "N/A";
            String duree = "N/A";
            int arriveeint = (int)serviceMetier.calculerArrivalTime()[focusedPointNumber];
            String arrivee = String.valueOf((arriveeint - arriveeint % 3600) / 3600) + ":" + String.valueOf(((arriveeint - arriveeint % 60) % 3600) / 60) + ":" + String.valueOf(arriveeint % 60);
            for (Livraison l : serviceMetier.getCommande().getListLivraison()) {
                if (l.getId() == serviceMetier.getPlan().getPointsMap().get(focusedPointId).getId()) {
                    int d = l.getHeureDeDebut();
                    int f = l.getHeureDeFin();
                    if (d > 0) {
                        heureDeDebut = String.valueOf((d - d % 3600) / 3600) + ":" + String.valueOf(((d - d % 60) % 3600) / 60) + ":" + String.valueOf(d % 60);
                    }
                    if (f <= 3600 * 24) {
                        heureDeFin = String.valueOf((f - f % 3600) / 3600) + ":" + String.valueOf(((f - f % 60) % 3600) / 60) + ":" + String.valueOf(f % 60);
                    }
                    duree = String.valueOf(l.getDuree());
                    break;
                }
            }
            labelPointDetail.setText("<html>" +"Point ID: "+serviceMetier.getTournee().getChemins().get(focusedPointNumber).getOriginePointID()+"<br>"+
                    "Coordonne X: " + serviceMetier.getPlan().getPointsMap().get(focusedPointId).getCoordX() + "<br>" +
                    "Coordonne Y: " + serviceMetier.getPlan().getPointsMap().get(focusedPointId).getCoordY() + "<br>" +
                    "Heure de debut: " + heureDeDebut + "<br>" +
                    "Heure de fin: " + heureDeFin + "<br>" +
                    "Duree: " + duree + "<br>" +
                    "Arrivee: " + arrivee+"</html>");
            labelPointDetail.repaint();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServiceMetier sm = new ServiceMetier();
        Dashboard myDashboard = new Dashboard(sm);



    }
}

