package com.septanome.util;

import com.septanome.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Some tools to load XML files or create an XML file
 */

public class UtilXML {

    /**
     * @param folder Path to XML file
     */
    public HashMap<Long, Point> loadPoint(String folder) {
        Point point;
        HashMap<Long, Point> points = new HashMap<>();

        try {
            File fXmlFile = new File(folder);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            doc.getDocumentElement().getNodeName();
            NodeList nList = doc.getElementsByTagName("noeud");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    Long id = Long.parseLong(eElement.getAttribute("id"));
                    int x = Integer.parseInt(eElement.getAttribute("x"));
                    int y = Integer.parseInt(eElement.getAttribute("y"));
                    point = new Point(id, x, y);
                    points.put(id, point);
                }
            }
            return points;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param folder Path to XML file
     */
    public HashMap<Long, HashMap<Long, Troncon>> loadTroncon(String folder) {
        Troncon troncon;

        HashMap<Long, HashMap<Long, Troncon>> troncons = new HashMap<Long, HashMap<Long, Troncon>>();

        try {
            File fXmlFile = new File(folder);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            doc.getDocumentElement().getNodeName();
            NodeList nList = doc.getElementsByTagName("troncon");
            //HashMap<Long,Troncon> tronconOriginMap = new ArrayList<>(;)
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    Long dest = Long.parseLong(eElement.getAttribute("destination"));
                    double length = Double.parseDouble(eElement.getAttribute("longueur"));
                    String street = eElement.getAttribute("nomRue");
                    Long origine = Long.parseLong(eElement.getAttribute("origine"));
                    troncon = new Troncon(dest, length, street, origine);
                    //System.out.println(troncon);
//					h.put(dest,troncon);
//					troncons.put(origine, h);
                    HashMap<Long, Troncon> h = new HashMap<Long, Troncon>();
                    h.put(dest, troncon);
                    if (troncons.get(origine) == null) {
                        troncons.put(origine, h);
                    } else {
                        troncons.get(origine).put(dest, troncon);
                    }
                    //tronconList.add(troncon);
                }
            }
            return troncons;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load a command from an XML file
     *
     * @param folder Path to XML file
     * @param plan   list of existing points
     * @return Return a @Commande
     */
    public Commande loadCommande(String folder, Plan plan) {
        HashMap<Long, Point> hash = plan.getPointsMap();
        Commande commande;
        Livraison livraison;
        List<Livraison> liste = new Vector<>();
        Point entrepot = null;
        int heure = -1;

        try {
            File fXmlFile = new File(folder);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            doc.getDocumentElement().getNodeName();

            //Get entrepot attributes
            NodeList nList = doc.getElementsByTagName("entrepot");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
//                    int x = Integer.parseInt(eElement.getAttribute("x"));
//                    int y = Integer.parseInt(eElement.getAttribute("y"));
                    long idEntrepot = Long.parseLong(eElement.getAttribute("adresse"));
                    String heureDepart = eElement.getAttribute("heureDepart");
                    String[] heureDepartArray = heureDepart.split(":");
                    heure = Integer.parseInt(heureDepartArray[0]) * 3600 + Integer.parseInt(heureDepartArray[1]) * 60 + Integer.parseInt(heureDepartArray[2]);
                    //System.out.println(x+" "+y+" "+heure);
//                    entrepot = findPointbyCoords(x,y, hash);
                    //System.out.println(idEntrepot);
                    entrepot = hash.get(idEntrepot);
                    //System.out.println(entrepot);
                }
            }

            if (entrepot == null) {
                System.out.println("Entrepot localisation not found");
                return null;
            }

//			if(heure < 0 || heure > 24) {
//				System.out.println("Hour format incorrect");
//				return null;
//			}

            nList = doc.getElementsByTagName("livraison");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
//                    int x = Integer.parseInt(eElement.getAttribute("x"));
//                    int y = Integer.parseInt(eElement.getAttribute("y"));
                    long idLivraison = Long.parseLong(eElement.getAttribute("adresse"));
                    //long d = Long.parseLong(eElement.getAttribute("d"));
                    Point p = hash.get(idLivraison);

                    if (p != null) {
                        int duree = Integer.parseInt(eElement.getAttribute("duree"));
                        String debutPlage = eElement.getAttribute("debutPlage");
                        String finPlage = eElement.getAttribute("finPlage");
                        //System.out.println(debutPlage);
                        int hd, hf;
                        if (!debutPlage.equals("")) {
                            String[] debutPlageArray = debutPlage.split(":");
                            String[] finPlageArray = finPlage.split(":");
//                            for(String str:debutPlageArray)
//                                System.out.println(str);
                            hd = Integer.parseInt(debutPlageArray[0]) * 3600 + Integer.parseInt(debutPlageArray[1]) * 60 + Integer.parseInt(debutPlageArray[2]);
                            hf = Integer.parseInt(finPlageArray[0]) * 3600 + Integer.parseInt(finPlageArray[1]) * 60 + Integer.parseInt(finPlageArray[2]);
                        } else {
                            hd = 0;
                            hf = Integer.MAX_VALUE;
                        }

                        //int hd = Integer.parseInt(eElement.getAttribute("hd"));
                        //int hf = Integer.parseInt(eElement.getAttribute("hf"));
                        livraison = new Livraison(p.getId(), p.getCoordX(), p.getCoordY(), duree, hd, hf);
                        //System.out.println(livraison);
                        liste.add(livraison);
                    } else {
                        System.out.println("Ce noeud n'existe pas : " +idLivraison);
                    }
                }
            }
            commande = new Commande(heure, entrepot, liste);
            return commande;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Find a point into the HashMap from coordinates
     *
     * @param x    X coordinate
     * @param y    Y coordinate
     * @param hash List of existing @Point
     * @return @Point if it exists, null otherwise
     */

    public Point findPointbyCoords(int x, int y, HashMap<Long, Point> hash) {
        Point point = null;
        for (Map.Entry<Long, Point> entry : hash.entrySet()) {
            Point p = entry.getValue();
            if (p.getCoordX() == x && p.getCoordY() == y) {
                point = p;
            }
        }
        return point;
    }

    public void writeTourneeToFile(String folder) {
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.newDocument();
            Element root = document.createElement("Languages");
            root.setAttribute("cat", "it");
            Element lan = document.createElement("lan");
            lan.setAttribute("id", "1");
            Element name = document.createElement("name");
            name.setTextContent("java");
            Element ide = document.createElement("IDE");
            ide.setTextContent("Eclipse");
            lan.appendChild(name);
            lan.appendChild(ide);
            Element lan2 = document.createElement("lan");
            lan2.setAttribute("id", "2");
            Element name2 = document.createElement("name");
            name2.setTextContent("Swift");
            Element ide2 = document.createElement("ide");
            ide2.setTextContent("XCode");
            lan2.appendChild(name2);
            lan2.appendChild(ide2);

            root.appendChild(lan);
            root.appendChild(lan2);
            document.appendChild(root);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("encoding", "UTF-8");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            System.out.println(writer.toString());

            transformer.transform(new DOMSource(document), new StreamResult(new File(folder)));
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

    }

    public void bubbleSort(int a[]) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1; j++) {
                if (a[j] > a[j + 1]) {
                    int temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                }
            }
        }
    }

    public int getIndex(int a[], int value) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            if (a[i] == value) {
                return i;
            }
        }
        return -1;
    }


}
