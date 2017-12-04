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

    public DeliveryMap(ServiceMetier sm, int h){
        this.sm =sm;
        screenHeigth=h;

        p.putAll(sm.getPlan().getPointsMap());

        r.putAll(sm.getPlan().getTronconsMap());
        commande=sm.getCommande();
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
        xmin= Integer.MAX_VALUE;
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

        System.out.println("xmin="+xmin);
        System.out.println("ymin="+ymin);
        System.out.println("scale="+scale);
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(3.0f));
        for(Map.Entry<Long,Point> entry:p.entrySet()){
            g.fillOval((int)((((double)entry.getValue().getCoordX())-xmin)/scale*(screenHeigth-12)),
                    (int)((((double)entry.getValue().getCoordY())-ymin)/scale*(screenHeigth-37)), 4, 4);
        }

        g.drawLine(screenHeigth,0,screenHeigth,screenHeigth);


        for(Map.Entry<Long,HashMap<Long,Troncon>> entry:r.entrySet()){
            Long idOri=entry.getKey();
            HashMap<Long,Troncon> h= entry.getValue();
            for(Map.Entry<Long,Troncon> e:h.entrySet()){
                Long idDest=e.getKey();
                g2.drawLine((int)((((double)p.get(idOri).getCoordX())-xmin)/scale*(screenHeigth-12))+5,
                        (int)((((double)p.get(idOri).getCoordY())-ymin)/scale*(screenHeigth-37))+5,
                        (int)((((double)p.get(idDest).getCoordX())-xmin)/scale*(screenHeigth-12))+5,
                        (int)((((double)p.get(idDest).getCoordY())-ymin)/scale*(screenHeigth-37))+5);
            }
        }

        g.setColor(Color.RED);
        System.out.println(sm.getTournee());

        for(Chemin c : sm.getTournee().getChemins()){
            for (Troncon tr:c.getTroncons()){
                double startX = p.get(tr.getOrigineID()).getCoordX();
                //System.out.println(startX);
                double startY = p.get(tr.getOrigineID()).getCoordY();
                //System.out.println(startY);
                double endX = p.get(tr.getDestinationID()).getCoordX();
                double endY = p.get(tr.getDestinationID()).getCoordY();
                g.drawLine((int)(((startX-xmin)/scale*(screenHeigth-12)))+5,
                        (int)((startY-ymin)/scale*(screenHeigth-37))+5,
                        (int)((endX-xmin)/scale*(screenHeigth-12))+5,
                        (int)((endY-ymin)/scale*(screenHeigth-37))+5);
            }
        }
        g.setColor(Color.green);
        int CoordX =(int)((((double)commande.getEntrepot().getCoordX())-xmin)/scale*(screenHeigth-12));
        int CoordY = (int)((((double)commande.getEntrepot().getCoordY())-ymin)/scale*(screenHeigth-37));
        g.fillOval(CoordX, CoordY, 8, 8);
        g.setColor(Color.magenta);

        for(Livraison l:commande.getListLivraison()){
            CoordX =(int)((((double)l.getCoordX())-xmin)/scale*(screenHeigth-12));
            CoordY = (int)((((double)l.getCoordY())-ymin)/scale*(screenHeigth-37));
            g.fillOval(CoordX, CoordY, 8, 8);
        }


    }

}

