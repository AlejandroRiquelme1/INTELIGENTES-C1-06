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
	/**
	 * Constructor del Lector que llamama a los metodos para comprobar el json y generar el png
	 * @param ruta donde se encuentra el archivo .json
	 */
	public Lector(String ruta) {

		comprobarInconsistencias(ruta);
		realizarLectura(ruta);
	}
	/**
	 * Este metodo tiene como objetivo principal analizar todo el .json que le pasemos buscando si existen alguna incosistencia o error en sus variables
	 * para esto lo que hace el metodo es recorrer cada celda y va comprobando en su array de vecinos si alguna posici�n est� a true, primero comprueba si 
	 * esta en un borde y si no esta comprueba con su celda vecina dependiendo si el case del swtich, que puede sumar o restar la posicion X o Y 
	 * dependiendo de en que posicion este del array de vecinos (Que serian los movimientos N,E,S,O) si sus correspondientes paredes estan definidas
	 * correctamente
	 * 
	 * @param ruta donde se encuentra el archivo .json
	 */
	public static void comprobarInconsistencias(String ruta) {
		JSONParser parser= new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(ruta));
			JSONObject jsonObject = (JSONObject) obj;
			//Cogemos las filas y las columnas del JSon
			long rows= (long)jsonObject.get("rows");
			long cols= (long)jsonObject.get("cols");

			//cogemos las Celdas y con keySet obtenemos las coordenadas de las celdas
			JSONObject cells= (JSONObject)jsonObject.get("cells");
			Object[] coordenadas=cells.keySet().toArray();	

			//Comenzamos a iterar las posciones de las celdas que hemos sacado antes
			for(Object aux: coordenadas) {
				//El siguiente fragemento se utiliza para obtener las coordenadas X e Y
				String coord=aux.toString();

				//Este substring lo que har� es coger las posiciones X e Y sin parentesis
				coord=	coord.substring(1 , coord.length()-1);

				//El StringTokenizer nos separa los dos numero de la coma que hay entre estos, poniendo como token la coma
				StringTokenizer tokens=new StringTokenizer(coord, ",");
				int coordenadaX=Integer.parseInt(tokens.nextToken());
				int coordenadaY=Integer.parseInt(tokens.nextToken().substring(1));

				//Obtenemos las celdas a partir de las coordendas que vamos sacando segun iteramos y con estas sacamos su array de vecinos
				JSONObject celda= (JSONObject)cells.get(aux);
				JSONArray vecinos= (JSONArray)celda.get("neighbors");

				//Este for itera del 0 al tama�o del array de vecinos
				for(int i=0; i<vecinos.size();i++) {

					//Segun el n�mero en el que este iterando el for entra en un case u otro, para comprobar primero si la celda es un borde o si no lo es
					//que compruebe si la celda contigua correspodiente al movimiento del array corresponde al moviemiento contrario de la celda anterior
					switch(i) {
					case 0:
						if((coordenadaX-1)<0) {
							if((boolean)vecinos.get(i)==true) {
								System.out.println("Celda: "+coordenadaX+","+coordenadaY+" tiene un error en la pared del norte");
								System.exit(-1);
							}
						}else {
							JSONObject celdaAuxiliar=(JSONObject)cells.get("("+(coordenadaX-1)+", "+coordenadaY+")");
							JSONArray vecinosAuxiliares=(JSONArray)celdaAuxiliar.get("neighbors");
							if(!vecinosAuxiliares.get(2).equals(vecinos.get(0))) {
								System.out.println("Celda: "+coordenadaX+","+coordenadaY+" no corresponde su pared norte con la pared sur de su vecino norte");
								System.exit(-1);
							}
						}
						break;
					case 1:
						if((coordenadaY+1)>=cols) {
							if((boolean)vecinos.get(i)==true) {
								System.out.println("Celda: "+coordenadaX+","+coordenadaY+" tiene un error en la pared del este");
								System.exit(-1);
							}
						}else {
							JSONObject celdaAuxiliar=(JSONObject)cells.get("("+coordenadaX+", "+(coordenadaY+1)+")");
							JSONArray vecinosAuxiliares=(JSONArray)celdaAuxiliar.get("neighbors");
							if(!vecinosAuxiliares.get(3).equals(vecinos.get(1))) {
								System.out.println("Celda: "+coordenadaX+","+coordenadaY+" no corresponde su pared este con la pared oeste de su vecino este");
								System.exit(-1);
							}
						}
						break;
					case 2:
						if((coordenadaX+1)>=rows) {
							if( (boolean)vecinos.get(i)==true) {
								System.out.println("Celda: "+coordenadaX+","+coordenadaY+" tiene un error en la pared del sur");
								System.exit(-1);
							}
						}else {
							JSONObject celdaAuxiliar=(JSONObject)cells.get("("+(coordenadaX+1)+", "+coordenadaY+")");
							JSONArray vecinosAuxiliares=(JSONArray)celdaAuxiliar.get("neighbors");
							if(!vecinosAuxiliares.get(0).equals(vecinos.get(2))) {
								System.out.println("Celda: "+coordenadaX+","+coordenadaY+" no corresponde su pared sur con la pared norte de su vecino sur");
								System.exit(-1);
							}
						}
						break;
					case 3:
						if((coordenadaY-1)<0) {
							if((boolean)vecinos.get(i)==true) {
								System.out.println("Celda: "+coordenadaX+","+coordenadaY+" tiene un error en la pared del oeste");
								System.exit(-1);
							}
						}else {
							JSONObject celdaAuxiliar=(JSONObject)cells.get("("+coordenadaX+", "+(coordenadaY-1)+")");
							JSONArray vecinosAuxiliares=(JSONArray)celdaAuxiliar.get("neighbors");
							if(!vecinosAuxiliares.get(1).equals(vecinos.get(3))) {
								System.out.println("Celda: "+coordenadaX+","+coordenadaY+" no corresponde su pared oeste con la pared este de su vecino oeste");
								System.exit(-1);
							}
						}
						break;
					}
				}		
			}
		}catch(Exception ex){
			System.err.println("Error comprobando incosistencias ");
		}
	}
	/**
	 * El metodo actual tiene como funcion objetivo generar la imagen del laberinto (leido del .json) y lo almacena en un archivo .png
	 * @param ruta donde se encuentra el archivo .json
	 */
	public static void realizarLectura(String ruta) {


		JSONParser parser= new JSONParser();
		try {
			//Creamos el objeto para leer el archivo .json con la ruta que le pasamos
			Object obj = parser.parse(new FileReader(ruta));
			JSONObject jsonObject = (JSONObject) obj;
			//Sacamos las filas y las columnas del objeto JSON
			long rows= (long)jsonObject.get("rows");
			long cols= (long)jsonObject.get("cols");

			//Damos el tama�o y formato de la imagen donde dibujaremos el laberinto 
			BufferedImage imagen = new BufferedImage((int) cols*11+1,(int) rows *11+1, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = imagen.createGraphics();
			//Ponemos el fondo blanco, las lineas de color negro y por ultimo rellenamos el fondo de color balnco
			g2d.setBackground(Color.white);
			g2d.setColor(Color.black);
			g2d.clearRect(0, 0, (int)cols*11,(int)rows*11);



			//cogemos las Celdas y con keySet obtenemos las coordenadas de las celdas
			JSONObject cells= (JSONObject)jsonObject.get("cells");
			Object[] coordenadas=cells.keySet().toArray();	

			//Comenzamos a iterar las posciones de las celdas que hemos sacado antes
			for(Object aux: coordenadas) {
				//Sacamos las celdas con las coordenas y despues obtenemos su array de vecinos
				JSONObject celda= (JSONObject)cells.get(aux);
				JSONArray vecinos= (JSONArray)celda.get("neighbors");	
				//Sacamos la coordenada X e Y exactamente como en el metodo anterior
				String coord=aux.toString();
				coord=	coord.substring(1, coord.length()-1);
				StringTokenizer tokens=new StringTokenizer(coord, ",");
				int x=Integer.parseInt(tokens.nextToken());
				int y=Integer.parseInt(tokens.nextToken().substring(1));
				//En este for lo que haremos es iterar con las paredes de cada celda y si es igual a false se dibuja
				for(int i=0; i<vecinos.size();i++) {
					if((boolean)vecinos.get(i)==false) {
						switch(i) {
						case 0:
							g2d.drawLine(y*11,x*11,y*11+11,x*11);
							break;
						case 1:
							g2d.drawLine(y*11+11,x*11,y*11+11,x*11+11);
							break;
						case 2:
							g2d.drawLine(y*11,x*11+11,y*11+11,x*11+11);
							break;
						case 3:
							g2d.drawLine(y*11,x*11,y*11,x*11+11);
							break;
						}
					}
				}

			}
			//Con la ruta que le pasamos del .json quitamos la extension .json y la ponemos como .png para crear el laberinto y que lo guarde en la misma ruta
			//que el archivo .json
			int posicion=ruta.indexOf(".");
			try{
				ImageIO.write(imagen,"jpeg",new File(ruta.substring(0,posicion).concat(".jpeg")));
				System.out.println("\nImagen generada con exito\n");
			}catch (Exception e) {
				System.out.println("Error generando la imagen ");
			}
		}catch(Exception ex){
			System.err.println("Error leyendo el .json ");
		}


	}
}