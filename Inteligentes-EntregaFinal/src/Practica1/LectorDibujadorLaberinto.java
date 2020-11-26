package Practica1;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import org.json.simple.parser.JSONParser;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LectorDibujadorLaberinto {
	private static String ruta;
	/**
	 * Constructor del LectorDibujadorLaberinto que inicializa la ruta donde se encontrara el json 
	 * del laberinto para comprobar inconsistencias y generar su respectivo jpeg
	 * @param ruta donde se encuentra el archivo .json
	 */
	public LectorDibujadorLaberinto(String ruta) {
		this.ruta=ruta;
	}
	/**
	 * Este metodo tiene como objetivo principal analizar todo el .json que le pasemos buscando si existen alguna incosistencia o error en sus variables
	 * para esto lo que hace el metodo es recorrer cada celda y va comprobando en su array de vecinos si alguna posicin est a true, primero comprueba si 
	 * esta en un borde y si no esta comprueba con su celda vecina dependiendo si el case del swtich, que puede sumar o restar la posicion X o Y 
	 * dependiendo de en que posicion este del array de vecinos (Que serian los movimientos N,E,S,O) si sus correspondientes paredes estan definidas
	 * correctamente
	 * return int
	 */
	public static int comprobarInconsistencias() {
		JSONParser parser= new JSONParser();
		int inconsistente=0;
		try {
			JSONObject JSONLaberinto = (JSONObject) parser.parse(new FileReader(ruta));
			if(JSONLaberinto.get("rows")==null) {
				System.out.println("\nEl archivo introducido no es el esperado");
				return 1;
			}
			//Cogemos las filas y las columnas del JSon
			long rows= (long)JSONLaberinto.get("rows");
			long cols= (long)JSONLaberinto.get("cols");

			//Cogemos los movimientos posibles y sus respectivos identificadores
			JSONArray id_mov= (JSONArray) JSONLaberinto.get("id_mov");
			JSONArray mov= (JSONArray) JSONLaberinto.get("mov");

			//cogemos las Celdas y con keySet obtenemos las coordenadas de las celdas
			JSONObject cells= (JSONObject)JSONLaberinto.get("cells");
			Object[] coordenadas=cells.keySet().toArray();

			//Comenzamos a iterar las posciones de las celdas que hemos sacado antes
			for(Object coordenadaAux: coordenadas) {

				int coordenadasCelda[]=convertirStringAArrayPosiciones(coordenadaAux);
				//Obtenemos las celdas a partir de las coordendas que vamos sacando segun iteramos y con estas sacamos su array de vecinos
				JSONObject celda= (JSONObject)cells.get(coordenadaAux);
				JSONArray vecinos= (JSONArray)celda.get("neighbors");

				//Este for itera del 0 al tamanio del array de vecinos
				for(int posicionVecino=0; posicionVecino<vecinos.size();posicionVecino++) {

					//Segun el numero en el que este iterando el for entra en un case u otro, para comprobar primero si la celda es un borde o si no lo es
					//que compruebe si la celda contigua correspodiente al movimiento del array corresponde al moviemiento contrario de la celda anterior
					switch(posicionVecino) {
					case 0:
						if((coordenadasCelda[0]-1)<0) {
							inconsistente+= errorParedLimite(vecinos,posicionVecino,coordenadasCelda[0], coordenadasCelda[1], id_mov);
						}else {
							inconsistente+= errorParedesCeldasVecinas(cells, vecinos, posicionVecino, coordenadasCelda[0], coordenadasCelda[1], id_mov,  mov);
						}
						break;
					case 1:
						if((coordenadasCelda[1]+1)>=cols) {
							inconsistente+=errorParedLimite(vecinos,posicionVecino,coordenadasCelda[0], coordenadasCelda[1], id_mov);
						}else {
							inconsistente+= errorParedesCeldasVecinas(cells, vecinos, posicionVecino, coordenadasCelda[0], coordenadasCelda[1], id_mov,  mov);
						}
						break;
					case 2:
						if((coordenadasCelda[0]+1)>=rows) {
							inconsistente+=errorParedLimite(vecinos,posicionVecino,coordenadasCelda[0], coordenadasCelda[1], id_mov);
						}else {
							inconsistente+= errorParedesCeldasVecinas(cells, vecinos, posicionVecino, coordenadasCelda[0], coordenadasCelda[1], id_mov,  mov);
						}
						break;
					case 3:
						if((coordenadasCelda[1]-1)<0) {
							inconsistente+= errorParedLimite(vecinos,posicionVecino,coordenadasCelda[0], coordenadasCelda[1], id_mov);
						}else {
							inconsistente+= errorParedesCeldasVecinas(cells, vecinos, posicionVecino, coordenadasCelda[0], coordenadasCelda[1], id_mov,  mov);
						}
						break;
					}
				}		
			}
		}catch(Exception ex){
			System.err.println("Error comprobando inconsistencias "+ex.toString());
			inconsistente=1;
		}
		return inconsistente;
	}
	/**
	 * Este método comprueba si las paredes de las celdas que corresponden al borde son true, en cuyo caso
	 * es inconsistente
	 * @param vecinos
	 * @param i
	 * @param coordenadaX
	 * @param coordenadaY
	 * @param id_mov
	 * @return int
	 */
	public static int errorParedLimite(JSONArray vecinos, int i, int coordenadaX, int coordenadaY, JSONArray id_mov) {
		int inconsistente=0;
		if((boolean)vecinos.get(i)==true) {
			System.out.println("Celda: "+coordenadaX+","+coordenadaY+" tiene un error en la pared " + id_mov.get(i));
			inconsistente=1;
		}
		return inconsistente;
	}
	/**
	 * Este método comprueba que las paredes de dos celdas contiguas no concuerdan
	 * @param cells
	 * @param vecinos
	 * @param i
	 * @param coordenadaX
	 * @param coordenadaY
	 * @param id_mov
	 * @param mov
	 * @return int
	 */
	public static int errorParedesCeldasVecinas(JSONObject cells, JSONArray vecinos, int i, int coordenadaX, int coordenadaY, JSONArray id_mov, JSONArray mov) {
		int inconsistente=0;
		List<Long> movimiento= (List<Long>)mov.get(i);
		//Obtenemos la celda contigua a partir de la posicion de la celda actual aplicandole el movimiento realizado 
		JSONObject celdaContigua=(JSONObject)cells.get("("+(coordenadaX+(Math.toIntExact(movimiento.get(0))))+", "+(coordenadaY+(Math.toIntExact(movimiento.get(1))))+")");
		JSONArray vecinosCeldaContigua=(JSONArray)celdaContigua.get("neighbors");
		//Este if se utiliza para diferenciar entre [norte, este] y [sur, oeste] dado que por ejemplo
		//si comparamos el norte con el sur esta ultima esta dos posiciones a la derecha de la primera.
		//Sin embargo, si comparamos el sur con el norte, esta ultima esta a dos posiciones a la izquierda
		//de la primera. Por tanto, a veces se utiliza i+2 y en otras i-2
		if(i<=1) {
			if(!vecinosCeldaContigua.get(i+2).equals(vecinos.get(i))) {
				System.out.println("Celda: "+coordenadaX+","+coordenadaY+" no corresponde su pared "+ id_mov.get(i) +" con la pared "+ id_mov.get(i+2) +" de su vecino "+ id_mov.get(i));
				inconsistente=1;
			}
		}else {
			if(!vecinosCeldaContigua.get(i-2).equals(vecinos.get(i))) {
				System.out.println("Celda: "+coordenadaX+","+coordenadaY+" no corresponde su pared "+ id_mov.get(i) +" con la pared "+ id_mov.get(i-2) +" de su vecino "+ id_mov.get(i));

				inconsistente=1;
			}
		}

		return inconsistente;
	}
	/**
	 * Este metodo dibuja el laberinto con sus celdas de diferentes colores segun el terreno(asfalto, tierra...) 
	 */
	public static void dibujarLaberinto() {
		int[]posicionCelda=new int[2];

		JSONParser parser= new JSONParser();
		try {
			//Creamos el objeto para leer el archivo .json con la ruta que le pasamos
			JSONObject JSONLaberinto = (JSONObject) parser.parse(new FileReader(ruta));
			//Sacamos las filas y las columnas del objeto JSON
			long rows= (long)JSONLaberinto.get("rows");
			long cols= (long)JSONLaberinto.get("cols");

			//Damos el tamanio y formato de la imagen donde dibujaremos el laberinto 
			BufferedImage imagen = new BufferedImage((int) cols*11+1,(int) rows *11+1, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = imagen.createGraphics();

			//Ponemos el fondo blanco, las lineas de color negro y por ultimo rellenamos el fondo de color balnco
			g2d.setBackground(Color.white);
			g2d.setColor(Color.black);
			g2d.clearRect(0, 0, (int)cols*11,(int)rows*11);

			//cogemos las Celdas y con keySet obtenemos las coordenadas de las celdas
			JSONObject cells= (JSONObject)JSONLaberinto.get("cells");
			Object[] coordenadas=cells.keySet().toArray();

			dibujarCeldasLaberinto(coordenadas, cells, g2d);

			pintarParedesCelda(coordenadas, cells, g2d);

			//Con la ruta que le pasamos del .json quitamos la extension .json y la ponemos como .jpeg para crear el laberinto y que lo guarde en la misma ruta
			//que el archivo .json
			int posicion=ruta.indexOf(".");
			try{
				ImageIO.write(imagen,"jpeg",new File(ruta.substring(0,posicion).concat(".jpeg")));
				System.out.println("\nImagen generada con exito\n");
			}catch (Exception e) {
				System.out.println("Error generando la imagen ");
			}
		}catch(Exception ex){
			System.err.println("Error leyendo el .json "+ex.toString());
		}
	}
	/**
	 * Este metodo dibuja el laberinto con sus celdas ademas de el camino solucion, la frontera y los visitados
	 * @param frontera
	 * @param visitado
	 * @param caminoSolucion
	 */
	public static void dibujarSolucion(Frontera frontera,ArrayList<Estado>visitado, ArrayList<Nodo>caminoSolucion, String heuristica) {

		JSONParser parser= new JSONParser();
		try {
			//Creamos el objeto para leer el archivo .json con la ruta que le pasamos
			JSONObject JSONLaberinto = (JSONObject) parser.parse(new FileReader(ruta));
			//Sacamos las filas y las columnas del objeto JSON
			long rows= (long)JSONLaberinto.get("rows");
			long cols= (long)JSONLaberinto.get("cols");

			//Damos el tamao y formato de la imagen donde dibujaremos el laberinto 
			BufferedImage imagen = new BufferedImage((int) cols*11+1,(int) rows *11+1, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = imagen.createGraphics();
			//Ponemos el fondo blanco, las lineas de color negro y por ultimo rellenamos el fondo de color balnco
			g2d.setBackground(Color.white);
			g2d.setColor(Color.black);
			g2d.clearRect(0, 0, (int)cols*11,(int)rows*11);

			//cogemos las Celdas y con keySet obtenemos las coordenadas de las celdas
			JSONObject cells= (JSONObject)JSONLaberinto.get("cells");
			Object[] coordenadas=cells.keySet().toArray();

			dibujarCeldasLaberinto(coordenadas, cells, g2d);

			dibujarFrontera(frontera, g2d);

			dibujarArbolInterior(visitado, g2d);

			dibujarCaminoSolucion(caminoSolucion, g2d);

			pintarParedesCelda(coordenadas,cells, g2d);
			
			//Con la ruta que le pasamos del .json quitamos la extension .json y la ponemos como .jpeg para crear el laberinto y que lo guarde en la misma ruta
			//que el archivo .json
			int posicion=ruta.indexOf(".");
			try{
				ImageIO.write(imagen,"jpeg",new File(ruta.substring(0,posicion).concat("_"+heuristica+".jpeg")));
				System.out.println("\nImagen generada con exito\n");
			}catch (Exception e) {
				System.out.println("Error generando la imagen ");
			}
		}catch(Exception ex){
			System.err.println("Error leyendo el .json en lector"+ex.toString());
		}
	}
	/**
	 * Este metodo pinta una celda con el color introducido por parametros
	 * @param posicionCelda
	 * @param g2d
	 * @param r
	 * @param g
	 * @param b
	 */
	public static void pintarCelda(int[]posicionCelda, Graphics2D g2d, int r, int g, int b) {
		float[] HSB=new float[3];
		int x=posicionCelda[0];
		int y=posicionCelda[1];
		Color.RGBtoHSB(r,g,b,HSB);
		g2d.setColor(Color.getHSBColor(HSB[0], HSB[1], HSB[2]));
		g2d.drawRect(y*11, x*11, 10, 10);
		g2d.fillRect(y*11, x*11, 10, 10);
	}
	/**
	 * Como su propio nombre indica convierte un string en un array de posiciones
	 * @param aux
	 * @return
	 */
	public static int[] convertirStringAArrayPosiciones(Object aux) {
		int[]posicionCelda=new int[2];
		int x, y;
		String coord=aux.toString();
		coord=	coord.substring(1, coord.length()-1);
		StringTokenizer tokens=new StringTokenizer(coord, ",");
		x=Integer.parseInt(tokens.nextToken());
		y=Integer.parseInt(tokens.nextToken().substring(1));
		posicionCelda[0]= x;
		posicionCelda[1]= y;
		return posicionCelda;
	}
	/**
	 * Este metodo pinta las paredes de cada celda segun su array de vecinos
	 * @param coordenadas
	 * @param cells
	 * @param g2d
	 */
	public static void pintarParedesCelda(Object[] coordenadas, JSONObject cells,Graphics2D g2d) {
		//pintamos las paredes de cada celda
		for(Object coordenadaAux: coordenadas) {
			//Sacamos las celdas con las coordenas y despues obtenemos su array de vecinos
			JSONObject celda= (JSONObject)cells.get(coordenadaAux);
			int[] posicionCelda=convertirStringAArrayPosiciones(coordenadaAux);
			int x=posicionCelda[0];
			int y=posicionCelda[1];
			//Sacamos las celdas con las coordenas y despues obtenemos su array de vecinos
			JSONArray vecinos= (JSONArray)celda.get("neighbors");
			//En este for lo que haremos es iterar con las paredes de cada celda y si es igual a false se dibuja
			for(int i=0; i<vecinos.size();i++) {
				g2d.setColor(Color.black);
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
	}
	/**
	 * Este metodo dibuja las celdas del laberinto segun el valor del terreno(tierra, asfalto...)
	 * @param coordenadas
	 * @param cells
	 * @param g2d
	 */
	public static void dibujarCeldasLaberinto(Object[] coordenadas, JSONObject cells, Graphics2D g2d) {
		int[]posicionCelda=new int[2];
		//Comenzamos a iterar las posiciones de las celdas que hemos sacado antes
		for(Object coordenadaAux: coordenadas) {
			//Sacamos las celdas con las coordenas y despues obtenemos su array de vecinos
			JSONObject celda= (JSONObject)cells.get(coordenadaAux);
			long valor=(long)celda.get("value");
			posicionCelda=convertirStringAArrayPosiciones(coordenadaAux);
			switch((int)valor) {
			case 0:
				pintarCelda(posicionCelda, g2d,255,255,255);
				break;
			case 1:
				pintarCelda(posicionCelda, g2d,245,222,179);
				break;
			case 2:
				pintarCelda(posicionCelda, g2d,152,251,152);
				break;
			case 3:
				pintarCelda(posicionCelda, g2d,135,206,250);
				break;
			}
		}
	}
	/**
	 * Este metodo dibuja la frontera de la solucion
	 * @param frontera
	 * @param g2d
	 */
	public static void dibujarFrontera(Frontera frontera, Graphics2D g2d) {
		//pintamos la frontera
		//se guarda el tamano en una variable ya que cada vez que se hace pop el tamano disminuye
		int tamano=frontera.getSize();
		for(int i=0;i<tamano;i++) {
			Nodo nodo=frontera.pop();
			int[]estadoNodo=nodo.getEstadoActual().getId_estado();
			pintarCelda(estadoNodo, g2d,0,0,255);
		}
	}
	/**
	 * Este metodo dibuja el arbol interior de la solucion
	 * @param visitado
	 * @param g2d
	 */
	public static void dibujarArbolInterior(ArrayList<Estado>visitado, Graphics2D g2d) {
		//pintamos el arbol interior
		for(Estado nodo:visitado) {
			int[]estadoNodo=nodo.getId_estado();
			pintarCelda(estadoNodo, g2d,0,255,0);
		}
	}
	/**
	 * Este metodo dibuja el camino solucion de la solucion
	 * @param caminoSolucion
	 * @param g2d
	 */
	public static void dibujarCaminoSolucion(ArrayList<Nodo> caminoSolucion, Graphics2D g2d){
		//pintamos el camino solucion
		for(Nodo nodoSolucion:caminoSolucion) {
			int[]estadoNodo=nodoSolucion.getEstadoActual().getId_estado();
			pintarCelda(estadoNodo, g2d,255,0,0);

		}
	}
}