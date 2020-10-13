package Practica1;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Lector {
	public Lector(String ruta) {
		realizarLectura(ruta);
	}
	
	
	public static void realizarLectura(String ruta) {
		BufferedImage imagen = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);  
		Graphics2D g2d = imagen.createGraphics();
		g2d.setBackground(Color.white);
		g2d.setColor(Color.black);
		g2d.clearRect(0, 0, 500, 500);
		
		JSONParser parser= new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(ruta));
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject cells= (JSONObject)jsonObject.get("cells");
			Object[] coordenadas=cells.keySet().toArray();	
			for(Object aux: coordenadas) {
				JSONObject celda= (JSONObject)cells.get(aux);
				JSONArray vecinos= (JSONArray)celda.get("neighbors");	
				String coord=aux.toString();
				coord=	coord.substring(1, coord.length()-1);
				StringTokenizer tokens=new StringTokenizer(coord, ",");
				int x=Integer.parseInt(tokens.nextToken());
				int y=Integer.parseInt(tokens.nextToken().substring(1));
				System.out.println(vecinos.size());
				for(int i=0; i<vecinos.size();i++) {
					if((boolean)vecinos.get(i)==false) {
			    		switch(i) {
			    			case 0:
			    				g2d.drawLine(y*11,x*11,y*11+10,x*11);
			    				break;
			    			case 1:
			    				g2d.drawLine(y*11+10,x*11,y*11+10,x*11+10);
			    				break;
			    			case 2:
			    				g2d.drawLine(y*11,x*11+10,y*11+10,x*11+10);
			    				break;
			    			case 3:
			    				g2d.drawLine(y*11,x*11,y*11,x*11+10);
			    				break;
			    			
			    		}
			    	}
				}
				
			}
		}catch(Exception ex){
			System.err.println("Error: "+ex.toString());
		}
		try{ImageIO.write(imagen,"png",new File("C:\\Users\\aleja\\Desktop\\LABERINTO.png"));}catch (Exception e) {}
	}
}