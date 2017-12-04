package com.septanome.ihm;

import com.septanome.model.Livraison;
import com.septanome.model.Point;
import com.septanome.service.ServiceMetier;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class Dashboard extends JFrame implements ActionListener{
    ServiceMetier serviceMetier = new ServiceMetier();
    long focusedPointId;
    int focusedPointNumber=0;

    int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
    int xmin;
    int ymin;
    int scale;

    DeliveryMap myMap = new DeliveryMap(serviceMetier,0);
    JPanel panelGlobal = new JPanel();
    JPanel panelChooseFile = new JPanel();
    JPanel panelFocusedPoint =new JPanel();
    JPanel panelPointDetail = new JPanel();
    JPanel panelSelectPoint = new JPanel();
    JPanel panelAddPoint = new JPanel();
    JPanel panelRemovePoint = new JPanel();
    JPanel panelEditPlageHoraire = new JPanel();
    JButton buttonChooseMap=new JButton("...");
    JButton buttonChooseCommand=new JButton("...");
    JButton buttonChargeMap = new JButton("Charger le plan");
    JButton buttonPreviousPoint = new JButton("Precedent");
    JButton buttonNextPoint = new JButton("Prochain");
    JButton buttonAddPoint = new JButton("Valider");
    JButton buttonRemovePoint = new JButton("Valider");
    JButton buttonEditPlageHoraire = new JButton("Valider");
    JLabel labelPointDetail = new JLabel("X");
    JTextField textImportMapFile=new JTextField("D:\\My Documents\\Intellji Program\\TSPTW\\intellji\\fichiersXML\\planLyonGrand.xml");
    JTextField textImportCommandFile=new JTextField("D:\\My Documents\\Intellji Program\\TSPTW\\intellji\\fichiersXML\\DLgrand10TW2.xml");
    JTextField textAddPointId = new JTextField();
    JTextField textAddPointHeureDebut = new JTextField();
    JTextField textAddPointHeureFin = new JTextField();
    JTextField textAddPointDuration = new JTextField();
    JTextField textRemovePointID = new JTextField();
    JTextField textEditPointID = new JTextField();
    JTextField textEditPointHeureDebut = new JTextField();
    JTextField textEditPointHeureFin = new JTextField();

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

        panelChooseFile.setBounds(900,10,500,380);
        panelChooseFile.setLayout(null);
        panelChooseFile.setBackground(Color.green);
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


        panelSelectPoint.setBounds(10,920,800,70);
        panelSelectPoint.setLayout(null);
        panelSelectPoint.setBackground(Color.blue);
        buttonPreviousPoint.addActionListener(this);
        buttonPreviousPoint.setBounds(10,10,100,50);
        buttonNextPoint.addActionListener(this);
        buttonNextPoint.setBounds(600,10,100,50);
        panelSelectPoint.add(buttonPreviousPoint);
        panelSelectPoint.add(buttonNextPoint);


        panelFocusedPoint.setBounds(myMap.getBounds());
        panelFocusedPoint.setLayout(null);
        panelFocusedPoint.setOpaque(false);

        panelPointDetail.setBounds(900,400,500,200);
        panelPointDetail.setLayout(null);
        panelPointDetail.setBackground(Color.ORANGE);
        labelPointDetail.setBounds(10,10,450,150);
        panelPointDetail.add(labelPointDetail);

        panelAddPoint.setBounds(900,600,500,150);
        panelAddPoint.setLayout(null);
        panelAddPoint.setBackground(Color.green);
        JLabel labelAddPointID = new JLabel("Point Id:");
        JLabel labelAddPointPlageHoraire = new JLabel("Plage horaire:  De                                       A");
        JLabel labelAddPointDuration = new JLabel("Duree:");
        labelAddPointID.setBounds(10,10,200,30);
        labelAddPointPlageHoraire.setBounds(10,40,300,30);
        labelAddPointDuration.setBounds(10,70,200,30);
        textAddPointId.setBounds(90,10,260,30);
        textAddPointHeureDebut.setBounds(120,40,100,30);
        textAddPointHeureFin.setBounds(250,40,100,30);
        textAddPointDuration.setBounds(90,70,260,30);
        buttonAddPoint.setBounds(100,110,100,30);
        buttonAddPoint.addActionListener(this);
        panelAddPoint.add(labelAddPointID);
        panelAddPoint.add(labelAddPointPlageHoraire);
        panelAddPoint.add(labelAddPointDuration);
        panelAddPoint.add(textAddPointId);
        panelAddPoint.add(textAddPointHeureDebut);
        panelAddPoint.add(textAddPointHeureFin);
        panelAddPoint.add(textAddPointDuration);
        panelAddPoint.add(buttonAddPoint);

        panelRemovePoint.setBounds(900,750,500,100);
        panelRemovePoint.setLayout(null);
        panelRemovePoint.setBackground(Color.MAGENTA);
        JLabel labelRemovePointID = new JLabel("Point Id:");
        labelRemovePointID.setBounds(10,10,100,30);
        textRemovePointID.setBounds(90,10,200,30);
        buttonRemovePoint.setBounds(100,50,100,30);
        buttonRemovePoint.addActionListener(this);
        panelRemovePoint.add(labelRemovePointID);
        panelRemovePoint.add(textRemovePointID);
        panelRemovePoint.add(buttonRemovePoint);

        panelEditPlageHoraire.setBounds(900,850,500,150);
        panelEditPlageHoraire.setLayout(null);
        panelEditPlageHoraire.setBackground(Color.LIGHT_GRAY);
        JLabel labelEditPointID = new JLabel("Point Id:");
        JLabel labelEditPointPlageHoraire = new JLabel("Plage horaire:  De                                       A");
        labelEditPointID.setBounds(10,10,200,30);
        labelEditPointPlageHoraire.setBounds(10,40,300,30);
        textEditPointID.setBounds(90,10,260,30);
        textEditPointHeureDebut.setBounds(120,40,100,30);
        textEditPointHeureFin.setBounds(250,40,100,30);
        buttonEditPlageHoraire.setBounds(100,80,100,30);
        buttonEditPlageHoraire.addActionListener(this);
        panelEditPlageHoraire.add(labelEditPointID);
        panelEditPlageHoraire.add(labelEditPointPlageHoraire);
        panelEditPlageHoraire.add(textEditPointID);
        panelEditPlageHoraire.add(textEditPointHeureDebut);
        panelEditPlageHoraire.add(textEditPointHeureFin);
        panelEditPlageHoraire.add(buttonEditPlageHoraire);

        panelGlobal.setBounds(0,0,1600,1000);
        panelGlobal.setLayout(null);
        panelGlobal.add(panelChooseFile);
        //panelGlobal.add(myMap);
        panelGlobal.add(panelSelectPoint);
        panelGlobal.add(panelFocusedPoint);
        panelGlobal.add(panelPointDetail);
        panelGlobal.add(panelAddPoint);
        panelGlobal.add(panelRemovePoint);
        panelGlobal.add(panelEditPlageHoraire);
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
            if(serviceMetier.getTournee()!=null) {
                focusedPointNumber = 0;
                panelGlobal.remove(myMap);
                myMap = new DeliveryMap(serviceMetier, screenHeight - 180);
                myMap.setBounds(10, 10, 800, 900);
                myMap.setLayout(null);
                panelGlobal.add(myMap);
                //myMap.updateUI();
                repaint();
                xmin = myMap.getXmin();
                ymin = myMap.getYmin();
                scale = myMap.getScale();
            }
        }else if (event.getSource() == buttonNextPoint){
            if(focusedPointNumber<serviceMetier.getCommande().getListLivraison().size()-1) {
                focusedPointNumber++;
                focusedPointId = serviceMetier.getTournee().getChemins().get(focusedPointNumber).getOriginePointID();

                Point tmpPoint = serviceMetier.getPlan().getPointsMap().get(focusedPointId);
                System.out.println((int) ((((double) tmpPoint.getCoordX()) - xmin) / scale * (screenHeight-180 - 12)));

                panelFocusedPoint.getGraphics().fillOval((int) ((((double) tmpPoint.getCoordX()) - xmin) / scale * (screenHeight-180 - 12)), (int) ((((double) tmpPoint.getCoordY()) - ymin) / scale * (screenHeight-180 - 37)), 15, 15);
                panelFocusedPoint.repaint();
                refreshPanelPointDetail();
            }
        }else if(event.getSource()==buttonPreviousPoint){
            //to do
        }else if(event.getSource()==buttonAddPoint){
            try{
                Long pointID= Long.valueOf(textAddPointId.getText());
                int heureDebut = -1;
                int heureFin = -1;
                if(!textAddPointHeureDebut.getText().equals("")) {
                    heureDebut = 3600 * Integer.valueOf(textAddPointHeureDebut.getText().substring(0, 1)) + 60 * Integer.valueOf(textAddPointHeureDebut.getText().substring(2, 3)) + Integer.valueOf(textAddPointHeureDebut.getText().substring(5, 6));
                }
                if(!textAddPointHeureFin.getText().equals("")) {
                    heureFin = 3600 * Integer.valueOf(textAddPointHeureFin.getText().substring(0, 1)) + 60 * Integer.valueOf(textAddPointHeureFin.getText().substring(2, 3)) + Integer.valueOf(textAddPointHeureFin.getText().substring(5, 6));
                }
                int duration = 60*Integer.valueOf(textAddPointDuration.getText());
                Livraison l = new Livraison(pointID,serviceMetier.getPlan().getPointsMap().get(pointID).getCoordX(),serviceMetier.getPlan().getPointsMap().get(pointID).getCoordY(),duration,heureDebut,heureFin);
                serviceMetier.getCommande().getListLivraison().add(l);

                serviceMetier.initPlanLivraison();
                try {
                    serviceMetier.calculerTournee(true);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                focusedPointNumber = 0;
                panelGlobal.remove(myMap);
                myMap = new DeliveryMap(serviceMetier,screenHeight-180);
                myMap.setBounds(10,10,800,900);
                myMap.setLayout(null);
                panelGlobal.add(myMap);
                //myMap.updateUI();
                repaint();
            }catch(Exception e){
                System.out.println("failed to add!!!");
            }

        }else if(event.getSource()==buttonRemovePoint){
            try{
                Long pointID= Long.valueOf(textRemovePointID.getText());
                boolean found = false;
                for(Livraison l:serviceMetier.getCommande().getListLivraison()){
                    if(l.getId()==pointID){
                        serviceMetier.getCommande().getListLivraison().remove(l);
                        found = true;
                        break;
                    }
                }
                if(!found){
                    System.out.println("Point not found!!!");
                }else {
                    serviceMetier.initPlanLivraison();
                    try {
                        serviceMetier.calculerTournee(true);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    focusedPointNumber = 0;
                    panelGlobal.remove(myMap);
                    myMap = new DeliveryMap(serviceMetier, screenHeight - 180);
                    myMap.setBounds(10, 10, 800, 900);
                    myMap.setLayout(null);
                    panelGlobal.add(myMap);
                    //myMap.updateUI();
                    repaint();
                }
            }catch (Exception e){
                System.out.println("failed to remove!!!");
            }

        }else if(event.getSource()==buttonEditPlageHoraire){
            try{
                Long pointID= Long.valueOf(textEditPointID.getText());
                boolean found = false;
                for(Livraison l:serviceMetier.getCommande().getListLivraison()){
                    if(l.getId()==pointID){
                        l.setHeureDeDebut(3600 * Integer.valueOf(textEditPointHeureDebut.getText().substring(0, 1)) + 60 * Integer.valueOf(textEditPointHeureDebut.getText().substring(2, 3)) + Integer.valueOf(textEditPointHeureDebut.getText().substring(5, 6)));
                        l.setHeureDeFin(3600 * Integer.valueOf(textEditPointHeureFin.getText().substring(0, 1)) + 60 * Integer.valueOf(textEditPointHeureFin.getText().substring(2, 3)) + Integer.valueOf(textEditPointHeureFin.getText().substring(5, 6)));
                        found = true;
                        break;
                    }
                }
                if(!found){
                    System.out.println("Point not found!!!");
                }else{
                    serviceMetier.initPlanLivraison();
                    try {
                        serviceMetier.calculerTournee(true);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    focusedPointNumber = 0;
                    panelGlobal.remove(myMap);
                    myMap = new DeliveryMap(serviceMetier,screenHeight-180);
                    myMap.setBounds(10,10,800,900);
                    myMap.setLayout(null);
                    panelGlobal.add(myMap);
                    //myMap.updateUI();
                    repaint();
                }
            }catch(Exception e){
                System.out.println("failed to edit time slot!!!");
            }
        }

    }
    public void refreshPanelPointDetail(){
        String heureDeDebut = "N/A";
        String heureDeFin = "N/A";
        String duree = "N/A";
        String arrivee = null;
        for(Livraison l:serviceMetier.getCommande().getListLivraison()){
            if(l.getId() == serviceMetier.getPlan().getPointsMap().get(focusedPointId).getId()){
                int d=l.getHeureDeDebut();
                int f=l.getHeureDeFin();
                if(d >= 0) {
                    heureDeDebut = String.valueOf((d - d % 3600) / 3600) + ":" + String.valueOf(((d - d % 60) % 3600) / 60) + ":" + String.valueOf(d % 60);
                }
                if (f<=3600*24 && f!=-1) {
                    heureDeFin = String.valueOf((f - f % 3600) / 3600) + ":" + String.valueOf(((f - f % 60) % 3600) / 60) + ":" + String.valueOf(f % 60);
                }
                duree = String.valueOf(l.getDuree());
            }
        }
        labelPointDetail.setText("<html>Coordonne X:"+serviceMetier.getPlan().getPointsMap().get(focusedPointId).getCoordX()+"<br>" +
                                            "Coordonne Y:"+serviceMetier.getPlan().getPointsMap().get(focusedPointId).getCoordY()+"<br>" +
                                            "Heure de debut:"+heureDeDebut+"<br>"+
                                            "Heure de fin:"+heureDeFin+"<br>"+
                                            "Duree:"+duree+"<br>"+
                                            "Arrivee:"+"</html>");
        labelPointDetail.repaint();
    }

    public static void main(String[] args){
        ServiceMetier sm = new ServiceMetier();
        Dashboard myDashboard = new Dashboard(sm);
    }
}

