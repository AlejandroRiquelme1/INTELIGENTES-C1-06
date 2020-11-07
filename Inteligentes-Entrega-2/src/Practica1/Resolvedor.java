package Practica1;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;

public class Resolvedor {
	private static String rutaArchivoSucesores;
	private static String rutaGeneral;
	public Resolvedor(String rutaArchivoSucesores, String rutaGeneral) {
		this.rutaArchivoSucesores=rutaArchivoSucesores;
		this.rutaGeneral=rutaGeneral;
	}
	/**
	 * Este metodo lee el archivo json de sucesores obteniendo las posiciones inicial y objetivo y el laberinto
	 * en forma de arraylist gracias al metodo obtenerLaberintoJSON 
	 * @return ArrayList<Celda>
	 */
	public static ArrayList<Celda> resolverLaberinto() {
		ArrayList<Celda>laberinto=new ArrayList<Celda>();
		String nombreLaberinto="";
		int[][] posicionesInicialObjetivo;
		JSONParser parserJSON= new JSONParser();

		try {
			//castea a JSONObject el JSON del archivo con la ruta rutaJSONSucesores parseado
			JSONObject JSONSucesores = (JSONObject) parserJSON.parse(new FileReader(rutaArchivoSucesores));
			posicionesInicialObjetivo=obtenerPosicionInicialObjetivo(rutaArchivoSucesores);
			if(posicionesInicialObjetivo==null) {
				return null;
			}

			nombreLaberinto=(String)JSONSucesores.get("MAZE");

			JSONObject JSONLaberinto = (JSONObject) parserJSON.parse(new FileReader(rutaGeneral.concat(nombreLaberinto)));
			if(posicionesDentroLimites(JSONLaberinto, posicionesInicialObjetivo)==false) {
				return null;
			}
			laberinto=obtenerLaberintoJSON(nombreLaberinto, rutaGeneral);
			if(laberinto==null) {
				return null;
			}
		}catch(Exception ex) {
			System.err.println("Error leyendo el archivo json que contiene los datos para resolver el laberinto ");
		}	
		return laberinto;
	}
	/**
	 * Este metodo obtiene las posiciones inicial y objetivo del archivo json de sucesores como string
	 * para despues convertirlo en arraylist por el metodo convertirString_A_ArrayPosiciones que nos ayuda a comprobar
	 * si son iguales o no entre otras cosas
	 * @param rutaJSONSucesores
	 * @return int[][]
	 */
	public static int[][]obtenerPosicionInicialObjetivo(String rutaJSONSucesores) {
		JSONParser parserJSON= new JSONParser();
		String posicionInicial="";
		String posicionObjetivo="";
		try {
			JSONObject JSONSucesores = (JSONObject) parserJSON.parse(new FileReader(rutaJSONSucesores));
			posicionInicial= (String)JSONSucesores.get("INITIAL");
			posicionObjetivo= (String)JSONSucesores.get("OBJETIVE");
		}catch(Exception ex){
			System.out.println("Error leyendo el archivo json que contiene los datos para resolver el laberinto para obtener las posiciones inicial y objetivo "+ex.toString());
		}
		int[]posInicial=convertirString_A_ArrayPosiciones(posicionInicial);
		int[]posObjetivo=convertirString_A_ArrayPosiciones(posicionObjetivo);
		if(posInicial[0]==posObjetivo[0] && posInicial[1]==posObjetivo[1]) {
			System.err.println("La posicion inicial y la objetivo coinciden");
			return null;
		}
		int[][]posiciones= {posInicial, posObjetivo};
		return posiciones;
	}
	/**
	 * Como su propio nombre indica convierte un string en un array de posiciones
	 * @param coordenadas
	 * @return int[]
	 */
	public static int[] convertirString_A_ArrayPosiciones(String coordenadas) {
		//se cogen las coordenadas de cada celda quitando los parentesis con el substring
		String posicion=coordenadas.substring(1 , coordenadas.length()-1);
		StringTokenizer tokens=new StringTokenizer(posicion, ",");
		int coordenadaX=Integer.parseInt(tokens.nextToken());
		//substring(1) debido a que hay un espacio entre la coma y el segundo numero
		int coordenadaY=Integer.parseInt(tokens.nextToken().substring(1));
		int[] posicionXY= {coordenadaX,coordenadaY};
		return posicionXY;
	}
	/**
	 * Este metodo lee un archivo json que contiene el laberinto, despues, comprueba que ese laberinto no tenga
	 * inconsistenicias para almacenar cada celda en un arraylist con su correspondiente valor y vecinos
	 * @param nombreLaberinto
	 * @param rutaGeneral
	 * @return ArrayList<Celda>
	 */
	public static ArrayList<Celda> obtenerLaberintoJSON(String nombreLaberinto, String rutaGeneral) {
		ArrayList<Celda>laberinto=new ArrayList<Celda>();
		String rutaCompleta=rutaGeneral.concat(nombreLaberinto);
		JSONParser parserJSON= new JSONParser();
		try {
			JSONObject JSONLaberinto = (JSONObject) parserJSON.parse(new FileReader(rutaCompleta));
			Lector l= new Lector(rutaGeneral.concat(nombreLaberinto));
			if(l.comprobarInconsistencias()==true) {
				JSONObject cells= (JSONObject)JSONLaberinto.get("cells");
				Object[] coordenadasCeldas=cells.keySet().toArray();	

				for(int i=0;i<coordenadasCeldas.length;i++) {
					//se cogen las coordenadas de cada celda
					int [] coordenadasCelda=convertirString_A_ArrayPosiciones(coordenadasCeldas[i].toString());
					JSONObject JSONCelda = (JSONObject) cells.get(coordenadasCeldas[i]);
					long value= (long)JSONCelda.get("value");
					JSONArray neighbors= (JSONArray)JSONCelda.get("neighbors");
					Celda celdaAux=new Celda(coordenadasCelda, (int)value);
					for(int posicionMov=0;posicionMov<neighbors.size();posicionMov++) {
						celdaAux.setVecinos(posicionMov,(boolean)neighbors.get(posicionMov));
					}
					laberinto.add(celdaAux);
				}	
			}

		}catch(Exception ex){
			System.err.println("Error leyendo el archivo .json del laberinto "+ ex.toString());
		}
		return laberinto;
	}
	/**
	 * Este metodo comprueba si las posiciones inicial y objetivo estan dentro de los limites especificados por 
	 * el json del laberinto que recibe por parametro
	 * @param JSONLaberinto
	 * @param posicionesInicialObjetivo
	 * @return boolean
	 */
	private static boolean posicionesDentroLimites(JSONObject JSONLaberinto, int[][]posicionesInicialObjetivo) {
		long rows= (long) JSONLaberinto.get("rows");
		long cols= (long) JSONLaberinto.get("cols");
		boolean seguir=true;
		if(posicionesInicialObjetivo[0][0]>rows || posicionesInicialObjetivo[0][0]<0
				|| posicionesInicialObjetivo[0][1]>cols || posicionesInicialObjetivo[0][1]<0) {
			System.err.println("La celda inicial no esta dentro de los limites del laberinto");
			seguir=false;
		}
		if(posicionesInicialObjetivo[1][0]>rows || posicionesInicialObjetivo[1][0]<0
				|| posicionesInicialObjetivo[1][1]>cols || posicionesInicialObjetivo[1][1]<0) {
			System.err.println("La celda objetivo no esta dentro de los limites del laberinto");
			seguir=false;
		}
		return seguir;
	}
}