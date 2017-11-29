package com.septanome.model;

import java.util.*;

public class Plan {
    private HashMap<Long,Point> pointsMap;
    private HashMap<Long,HashMap<Long,Troncon>> tronconsMap;


    //1ere Long est le debut, 2eme Long est la fin
    public Plan(HashMap<Long,Point> pointsMap,HashMap<Long,HashMap<Long,Troncon>> tronconsMap) {
        this.pointsMap = pointsMap;
        this.tronconsMap = tronconsMap;
    }

    public Plan() {
        // TODO Auto-generated constructor stub
        this.pointsMap = new HashMap<Long,Point>();
        this.tronconsMap =  new HashMap<Long,HashMap<Long,Troncon>>();
    }

    public HashMap<Long,Point> getPointsMap(){
        return pointsMap;
    }

    public void setPointMap(HashMap<Long,Point> pointsMap){
        this.pointsMap = pointsMap;
    }

    public HashMap<Long,HashMap<Long,Troncon>> getTronconsMap(){
        return tronconsMap;
    }

    public void setTronconsMap(HashMap<Long,HashMap<Long,Troncon>> tronconsMap) {
        this.tronconsMap = tronconsMap;
    }

}