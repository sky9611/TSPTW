package com.septanome.ihm;

import com.septanome.model.*;
import com.septanome.model.Point;
import com.septanome.service.ServiceMetier;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class DeliveryMap extends JPanel  {

    HashMap<Long,Point> p=new HashMap<Long,Point>();
    HashMap<Long,HashMap<Long,Troncon>> r=new HashMap<Long,HashMap<Long,Troncon>>();
    HashMap<Long,Integer[]> CoordOnMap =new HashMap<Long,Integer[]>();
    Commande commande;
    int screenHeigth;
    ServiceMetier sm;
    int xmin;
    int xmax;
    int ymin;
    int ymax;
    int scale;
    double ratio;
    int k;

    public DeliveryMap(ServiceMetier sm, int h, double ratio ,int k){
        this.sm =sm;
        this.screenHeigth=h;
        this.ratio = ratio;
        this.k= k;
        p.putAll(sm.getPlan().getPointsMap());

        r.putAll(sm.getPlan().getTronconsMap());
        commande=sm.getCommande();

        xmin=Integer.MAX_VALUE;
        xmax=Integer.MIN_VALUE;
        ymin=Integer.MAX_VALUE;
        ymax=Integer.MIN_VALUE;
        for(Map.Entry<Long,Point> entry:p.entrySet()){
            if(entry.getValue().getCoordX()>xmax){
                xmax=entry.getValue().getCoordX();
            }
            if(entry.getValue().getCoordX()<xmin){
                xmin=entry.getValue().getCoordX();
            }
            if(entry.getValue().getCoordY()>ymax){
                ymax=entry.getValue().getCoordY();
            }
            if(entry.getValue().getCoordY()<ymin){
                ymin=entry.getValue().getCoordY();
            }
        }
        scale = (ymax-ymin>xmax-xmin)? ymax-ymin:xmax-xmin;
    }

    public int getXmin(){
        return xmin;
    }

    public int getYmin(){
        return ymin;
    }

    public int getScale(){
        return scale;
    }

    public void paintComponent(Graphics g){


        for(Map.Entry<Long,Point> entry:p.entrySet()){
            g.fillOval((int)(((((double)entry.getValue().getCoordX())-xmin)/scale*(screenHeigth))*ratio),
                    (int)((((double)entry.getValue().getCoordY())-ymin)/scale*(screenHeigth)), 4, 4);
        }

        //g.drawLine(screenHeigth,0,screenHeigth,screenHeigth);


        for(Map.Entry<Long,HashMap<Long,Troncon>> entry:r.entrySet()){
            Long idOri=entry.getKey();
            HashMap<Long,Troncon> h= entry.getValue();
            for(Map.Entry<Long,Troncon> e:h.entrySet()){
                Long idDest=e.getKey();
                g.drawLine((int)(((((double)p.get(idOri).getCoordX())-xmin)/scale*(screenHeigth))*ratio),
                        (int)((((double)p.get(idOri).getCoordY())-ymin)/scale*(screenHeigth)),
                        (int)(((((double)p.get(idDest).getCoordX())-xmin)/scale*(screenHeigth))*ratio),
                        (int)((((double)p.get(idDest).getCoordY())-ymin)/scale*(screenHeigth)));
            }
        }
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(2.0f));
        g2.setColor(Color.red);
        //System.out.println(sm.getTournee());

        for(Chemin c : sm.getTournee().getChemins()){
            g2.setColor(Color.red);
            for (Troncon tr:c.getTroncons()){
                double startX = p.get(tr.getOrigineID()).getCoordX();
                //System.out.println(startX);
                double startY = p.get(tr.getOrigineID()).getCoordY();
                //System.out.println(startY);
                double endX = p.get(tr.getDestinationID()).getCoordX();
                double endY = p.get(tr.getDestinationID()).getCoordY();
                g2.drawLine((int)((((startX-xmin)/scale*(screenHeigth)))*ratio),
                        (int)((startY-ymin)/scale*(screenHeigth)),
                        (int)(((endX-xmin)/scale*(screenHeigth))*ratio),
                        (int)((endY-ymin)/scale*(screenHeigth)));
            }
        }
        g2.setStroke(new BasicStroke(5.0f));
        for(Chemin c : sm.getTournee().getChemins()){
            g2.setColor(Color.CYAN);
            if(k!=-1){
                if (sm.getTournee().getChemins().get(k-1).equals(c)) {
                    for (Troncon tr:c.getTroncons()){
                            double startX = p.get(tr.getOrigineID()).getCoordX();
                            //System.out.println(startX);
                            double startY = p.get(tr.getOrigineID()).getCoordY();
                            //System.out.println(startY);
                            double endX = p.get(tr.getDestinationID()).getCoordX();
                            double endY = p.get(tr.getDestinationID()).getCoordY();
                            g2.drawLine((int)((((startX-xmin)/scale*(screenHeigth)))*ratio),
                                    (int)((startY-ymin)/scale*(screenHeigth)),
                                    (int)(((endX-xmin)/scale*(screenHeigth))*ratio),
                                    (int)((endY-ymin)/scale*(screenHeigth)));
                        }
                    }
            }
        }

        g.setColor(Color.green);
        int CoordX =(int)(((((double)commande.getEntrepot().getCoordX())-xmin)/scale*(screenHeigth))*ratio);
        int CoordY = (int)((((double)commande.getEntrepot().getCoordY())-ymin)/scale*(screenHeigth));
        g.fillOval(CoordX, CoordY, 10, 10);
        g.setColor(Color.BLUE);

        for(Livraison l:commande.getListLivraison()){
            CoordX =(int)(((((double)l.getCoordX())-xmin)/scale*(screenHeigth))*ratio);
            CoordY = (int)((((double)l.getCoordY())-ymin)/scale*(screenHeigth));
            g.fillOval(CoordX, CoordY, 10, 10);
        }


    }

}

