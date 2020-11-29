package Practica1;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.io.FileNotFoundException;
//import org.json.simple.JSONObject;
//import org.json.simple.JSONArray;
import java.io.FileWriter;
import java.util.*;
import org.json.*;


public class Principal {

	private final static int TAMANIO_MAXIMO=1000;
	private final static int TAMANIO_MINIMO=2;

	final static Scanner TECLADO = new Scanner(System.in);
	/**
	 *  Este es el metodo Principal que lo que hara es llamar al menu() para mostrar las opciones disponibles y con 
	 *  el metodo elegirOpcion() para que el usuario introduzca el numero de la opcion que desea elegir y entrar al
	 *  case correspondiente.
	 * @param args
	 * @throws FileNotFoundException
	 * @throws NoSuchElementException
	 * @throws InputMismatchException
	 */
	public static void main(String[] args) throws FileNotFoundException, NoSuchElementException, InputMismatchException {

		int opcion= 0;
		boolean exit= false;
		boolean opcionCorrecta=false; //Se utiliza para volver a mostrar el menu si se producen excepciones en elegirOpcion()
		System.out.println("\t ##########\t Bienvenido al programa \t########## \t ");
		String rutaGeneral=pedirDirectorioEscritura("Introduzca el directorio donde desea que se realicen las operaciones");
		do {
			do {
				menu();
				opcion= elegirOpcion("Elija la opcion entre 0 y 3",0,3);
				opcionCorrecta=true;
				switch(opcion) {
				case 0:		
					System.out.println("Hasta pronto, gracias por confiar en nosotros");
					exit=true;
					break;
				case 1:
					creacionLaberinto(rutaGeneral);
					break;
				case 2:
					generarImagen(rutaGeneral);
					break; 
				case 3:
					resolverLaberinto(rutaGeneral);
					break;
				}
			}while (opcionCorrecta==false);
		}while (exit==false);
	}
	/**
	 * El metodo menu muestra por consola las diferentes opciones para elegir
	 */
	public static void menu() {
		System.out.println("\n0. Abandonar");			
		System.out.println("1. Crear un laberinto y exportarlo a imagen a traves de JSON");
		System.out.println("2. Exportar laberinto a imagen a traves de JSON");
		System.out.println("3. Resolver laberinto");
	}
	/**
	 * En este metodo el usuario introduce el nombre del problema, selecciona la heuristica del algoritmo resolvedor
	 * y con esos datos se procede a resolver el laberinto
	 * @param rutaGeneral
	 */
	public static void resolverLaberinto(String rutaGeneral) {
		String rutaJSONProblema=pedirArchivo("Introduzca el nombre del archivo json del problema", rutaGeneral);
		String heuristica=pedirHeuristica();
		Resolvedor resolvedorLaberinto= new Resolvedor(rutaJSONProblema,rutaGeneral, heuristica);
		resolvedorLaberinto.resolverLaberinto();
	}
	/**
	 * En este metodo el usuario introduce la ruta para encontrar el archivo json que quiere leer y crea el objeto 
	 * LectorDibujador para generar el png
	 * @param rutaGeneral
	 */
	public static void generarImagen(String rutaGeneral) {
		String rutaJSONLaberinto = pedirArchivo("Introduzca el nombre del archivo json que contiene el laberinto", rutaGeneral);
		LectorDibujadorLaberinto dibujadorLaberinto= new LectorDibujadorLaberinto(rutaJSONLaberinto);	
		if(dibujadorLaberinto.comprobarInconsistencias()==0) {
			dibujadorLaberinto.dibujarLaberinto();
		}
	}
	/**
	 * Este metodo tiene como proposito recoger todos los datos para crear el laberinto mediante el algortimo de Wilson y
	 * ademas crear sus correspondientes archivos json
	 * @param rutaGeneral
	 */
	public static void creacionLaberinto(String rutaGeneral) {

		int filas;
		int columnas;
		List<Celda> celdas = new ArrayList<Celda>();
		int[][] mov = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
		String[] id_mov = { "N", "E", "S", "O" };
		int max_n= id_mov.length; //Numero maximo de vecinos


		//Pedimos por teclado el numero de filas y de columnas teniendo en cuenta que tiene que ser mayor que 2 y menor que 1000
		filas = elegirOpcion("Introduce las filas. Debe ser un valor numerico positivo mayor que "+(TAMANIO_MINIMO-1),TAMANIO_MINIMO,TAMANIO_MAXIMO);
		columnas = elegirOpcion("Introduce las columnas. Debe ser un valor numerico positivo mayor que "+(TAMANIO_MINIMO-1),TAMANIO_MINIMO,TAMANIO_MAXIMO);
		celdas = crearCeldas(filas, columnas);
		List<Celda> celdasNoVisitadas = new ArrayList<Celda>(celdas); 

		//Ejecutamos el algoritmo de Wilson
		algoritmoWilson(celdas, mov, filas, columnas, id_mov, celdasNoVisitadas);

		//Con el laberinto ya creado generamos el Json para pasarselo al LectorDibujadorLaberinto y que genere la imagen
		generadorJson(celdas,filas,columnas,max_n,mov,id_mov,rutaGeneral);
	}
	/**
	 * Este metodo tiene como cometido generar los archivos .json y comprobar las inconsistencias para posteriormente
	 * generar la imagen
	 * @param celdas 
	 * @param filas	
	 * @param columnas 
	 * @param max_n	
	 * @param mov 
	 * @param id_mov 
	 * @param ruta 
	 */
	public static void generadorJson(List<Celda> celdas, int filas, int columnas, int max_n, int[][] mov, String[] id_mov, String ruta) {

		generarJSONLaberinto(celdas, filas, columnas, max_n, mov, id_mov, ruta);

		generarJSONProblema(filas, columnas, ruta);

		//Despues de crear el archivo .json se lo pasamos al LectorDibujadorLaberinto para que cree la imagen
		LectorDibujadorLaberinto lectorDibujadorLaberinto = new LectorDibujadorLaberinto(ruta.concat("problema_"+filas+"X"+columnas+"_maze.json"));
		if(lectorDibujadorLaberinto.comprobarInconsistencias()==0) {
			lectorDibujadorLaberinto.dibujarLaberinto();
		}

	}
	/**
	 * Este metodo genera el json correspondiente al laberinto introduciendo diversos atributos como las filas,
	 * las columnas, las celdas...
	 * @param celdas
	 * @param filas
	 * @param columnas
	 * @param max_n
	 * @param mov
	 * @param id_mov
	 * @param ruta
	 */
	public static void generarJSONLaberinto(List<Celda> celdas, int filas, int columnas, int max_n, int[][] mov, String[] id_mov, String ruta) {
		JSONObject JSONLaberinto = new JSONObject();
		JSONObject cells = new JSONObject();
		//En este for iteramos la lista de celdas para introducirle el valor, el array de los vecinos y la posicion al objeto JSon declarado como cells
		for(Celda aux: celdas) {
			JSONObject JSONCelda = new JSONObject();
			JSONCelda.put("value", aux.getValor());
			JSONCelda.put("neighbors", aux.getVecinos());
			cells.put("("+aux.getPosicionX()+", "+aux.getPosicionY()+")",JSONCelda);
		}
		//Para terminar de aadir las variables introducimos todos los elementos del .json incluyendo el objeto cells
		JSONLaberinto.put("rows", filas);
		JSONLaberinto.put("cols", columnas);
		JSONLaberinto.put("max_n", max_n);
		JSONLaberinto.put("mov", mov);
		JSONLaberinto.put("id_mov", id_mov);
		JSONLaberinto.put("cells", cells);

		//Creamos y escribimos el archivo .json y lo guardamos en la ruta que nos han pasado
		try{
			FileWriter file = new FileWriter(ruta.concat("problema_"+filas+"X"+columnas+"_maze.json"));
			file.write(JSONLaberinto.toString());
			file.flush();
			file.close();
		}catch(Exception ex){
			System.err.println("Error escribiendo el archivo json del laberinto: "+ex.toString());
		}
		System.out.println("\nJson del laberinto creado con exito: problema_"+filas+"X"+columnas+"_maze.json");
	}
	/**
	 * Este metodo genera el archivo json correspondiente al problema con la posicion inicial, la objetivo y el json del
	 * laberinto
	 * @param filas
	 * @param columnas
	 * @param ruta
	 */
	public static void generarJSONProblema(int filas, int columnas, String ruta) {
		JSONObject JSONProblema = new JSONObject();
		Random posicionesRandom=new Random();
		int[]posInicial=new int[2];
		int[]posObjetivo=new int[2];

		posInicial[0]=posicionesRandom.nextInt(filas);
		posInicial[1]=posicionesRandom.nextInt(columnas);
		do {
			posObjetivo[0]=posicionesRandom.nextInt(filas);
			posObjetivo[1]=posicionesRandom.nextInt(columnas);
		}while(posInicial[0]==posObjetivo[0] && posInicial[1]==posObjetivo[1]);

		JSONProblema.put("INITIAL","("+posInicial[0]+", "+posInicial[1]+")");
		JSONProblema.put("OBJETIVE","("+posObjetivo[0]+", "+posObjetivo[1]+")");
		JSONProblema.put("MAZE","problema_"+filas+"x"+columnas+"_maze.json");
		
		//Creamos y escribimos el archivo .json del problema y lo guardamos en la ruta que nos han pasado
		try{
			FileWriter file = new FileWriter(ruta.concat("problema_"+filas+"X"+columnas+".json"));
			file.write(JSONProblema.toString());
			file.flush();
			file.close();
		}catch(Exception ex){
			System.err.println("Error escribiendo el archivo json del problema: "+ex.toString());
		}
		System.out.println("\nJson del problema creado con exito: problema_"+filas+"X"+columnas+".json");
	}

	/**
	 * Introduce un valor que debe ser mayor que el limite inferior y no puede ser negativo y menos que el limite superior.
	 * @param mensaje 
	 * @return int
	 * @throws InputMismatchException
	 */
	public static int elegirOpcion(String mensaje, int limiteInf,int limiteSup) {
		int opcion = 0;
		boolean correcto=false;
		do {
			try{

				do {
					System.out.println(mensaje);
					opcion = TECLADO.nextInt();
					TECLADO.nextLine();
				} while (opcion < limiteInf || opcion > limiteSup);
				correcto=true;

			}catch(InputMismatchException i) {
				System.err.println("Debe introducir un valor numerico positivo");
				TECLADO.nextLine();
			}
		}while(correcto==false);
		return opcion;
	}
	/**
	 * Crea tantas celdas como posiciones tenga el laberinto (filas*columnas)
	 * @param filas 
	 * @param columnas 
	 * @return List<> 
	 */
	public static List<Celda> crearCeldas(int filas, int columnas) {
		Random tipoTerreno= new Random();
		List<Celda> celdas = new ArrayList<Celda>();
		for (int i = 0; i < filas; i++) {
			for (int j = 0; j < columnas; j++) {
				int[] posicion = { i, j };
				celdas.add(new Celda(posicion, tipoTerreno.nextInt(4)));
			}
		}
		return celdas;
	}
	/**
	 * Establecemos una lista (caminoParcial) que va a contener cada una de las celdas de un camino. En primer lugar se escogen al azar la celda inicial y
	 * final del primer camino y se realiza el camino hasta alcanzar la inicial (se empieza desde la final). Una vez se tiene el camino, se expande 
	 * (cambiando los valores de las paredes de las celdas y ponindolas como visitadas) y se eliminan las celdas visitadas de la lista celdasNoVisitadas.
	 * Posteriormente, se realiza exactamente lo mismo pero con una celda inicial no visitada y una celda final visitada. Esto se realiza hasta que todas
	 * las celdas estn visitadas
	 * @param celdas
	 * @param mov
	 * @param filas 
	 * @param columnas 
	 * @param id_mov 
	 * @param celdasNoVisitadas 
	 */
	public static void algoritmoWilson(List<Celda> celdas, int[][] mov, int filas, int columnas, String [] id_mov, List<Celda> celdasNoVisitadas) {
		List<Celda> caminoParcial = new ArrayList<Celda>();
		Celda[] posicionesInicialFinal = elegirPosiciones(filas, columnas); 
		//El calculo: Posicion X de la Celda * Columnas del Laberinto + Posicion Y de la Celda, nos permite obtener la posicin de esa celda en la Lista celdas.
		int indicePosInicial= (posicionesInicialFinal[0].getPosicionX()* columnas) + posicionesInicialFinal[0].getPosicionY(); 
		int indicePosFinal= (posicionesInicialFinal[1].getPosicionX()* columnas) + posicionesInicialFinal[1].getPosicionY(); 

		Celda celdaFinal = celdas.get(indicePosInicial); 
		Celda celdaActual = celdas.get(indicePosFinal);
		caminoParcial.add(celdaActual); 
		Celda celdaSiguiente = null;	

		//Realizamos el primer camino hasta alcanzar la casilla Inicial
		do{ 
			celdaSiguiente= obtenerCeldaSiguiente(celdaActual, mov, filas, columnas, id_mov, celdaSiguiente,celdas);
			celdaActual= realizarMovimiento(celdaSiguiente, celdaActual, caminoParcial);	
		}while (! ((celdaSiguiente.getPosicionX() == celdaFinal.getPosicionX()) && (celdaSiguiente.getPosicionY() == celdaFinal.getPosicionY()) )) ; 

		//Una vez tengo el camino, expando (cambiar paredes de false a true). Para ello tenemos en cuenta el ultimo movimiento de cada una de las celdas
		expandirCamino(caminoParcial);
		celdasNoVisitadas=eliminarCeldasVisitadas(caminoParcial, celdasNoVisitadas);

		//Ahora caminos desde celdas no visitadas hasta encontrar una visitada
		do {
			caminoParcial.clear();
			celdaActual=null;
			celdaSiguiente=null;
			Random celdaInicioRandom= new Random();
			celdaActual= celdasNoVisitadas.get(celdaInicioRandom.nextInt(celdasNoVisitadas.size())); 
			caminoParcial.add(celdaActual);

			//Realizamos el camino hasta alcanzar cualquier visitada
			do {
				celdaSiguiente= obtenerCeldaSiguiente(celdaActual, mov, filas, columnas, id_mov, celdaSiguiente,celdas);		
				celdaActual= realizarMovimiento(celdaSiguiente, celdaActual, caminoParcial);	
			}while (celdaSiguiente.getVisitado() == false) ;

			//Expando el camino
			expandirCamino(caminoParcial);
			celdasNoVisitadas=eliminarCeldasVisitadas(caminoParcial, celdasNoVisitadas);
		}while(celdasNoVisitadas.size()!=0);
	}
	/**
	 * Se crea un array de celdas llamado posiciones donde se almacenara la celda origen y la destino. Establecemos las coordenadas de forma aleatoria tanto
	 * la celda origen como la destino. La celda final debe tener unas coordenadas distintas a la origen
	 * @param filas 
	 * @param columnas 
	 * @return Celda[] 
	 */
	public static Celda[] elegirPosiciones(int filas, int columnas) {
		Celda[] posiciones = new Celda[2];
		Random randomFila = new Random();
		Random randomColumna = new Random();
		int[] posicionInicial = new int[2];
		int[] posicionFinal = new int[2];
		posicionInicial[0] = randomFila.nextInt(filas);
		posicionInicial[1] = randomColumna.nextInt(columnas);
		do {
			posicionFinal[0] = randomFila.nextInt(filas);
			posicionFinal[1] = randomColumna.nextInt(columnas);
		} while ((posicionInicial[0] == posicionFinal[0]) && (posicionInicial[1] == posicionFinal[1]));
		Celda celdaInicial = new Celda(posicionInicial, 0);
		Celda celdaFinal = new Celda(posicionFinal, 0);
		posiciones[0] = celdaInicial;
		posiciones[1] = celdaFinal;
		return posiciones;
	}

	/**
	 * Utilizamos una celda auxiliar que nos dara las coordenadas de la siguiente celda y el ultimo movimiento de la celda actual
	 * @param celdaActual 
	 * @param mov 
	 * @param filas 
	 * @param columnas 
	 * @param id_mov 
	 * @param celdaSiguiente 
	 * @param celdas 
	 * @return Celda 
	 */
	public static Celda obtenerCeldaSiguiente(Celda celdaActual, int[][]mov, int filas, int columnas, String [] id_mov, Celda celdaSiguiente, List<Celda> celdas) {
		//Se establece el siguiente movimiento y se guarda dicho movimiento en la celda actual
		Celda celdaAuxiliarMovimiento = siguienteMovimientoAleatorio(celdaActual, mov, filas, columnas, id_mov); 
		celdaActual.setUltimoMovimiento(celdaAuxiliarMovimiento.getultimoMovimiento()); 
		//Se obtiene la celda siguiente
		int indicePosSiguiente= (celdaAuxiliarMovimiento.getPosicionX()* columnas) + celdaAuxiliarMovimiento.getPosicionY(); 
		celdaSiguiente = celdas.get(indicePosSiguiente);
		return celdaSiguiente;
	}

	/**
	 * Se elige de forma aleatoria el siguiente movimiento a realizar. Si ese siguiente movimiento esta dentro de los limites del laberinto, se confirman las
	 * siguientes coordenadas. Tambien se aprovecha y se devuelve el ultimo movimiento que ha hecho la celda actual.
	 * @param celdaActual
	 * @param mov
	 * @param filas 
	 * @param columnas 
	 * @param id_mov 
	 * @return Celda
	 */

	public static Celda siguienteMovimientoAleatorio(Celda celdaActual, int[][] mov, int filas, int columnas, String [] id_mov) {
		Random siguienteMov = new Random();
		int[] siguienteMovimiento;
		int [] coordenadasActuales= {celdaActual.getPosicionX(), celdaActual.getPosicionY()};
		Celda celdaAuxiliar= new Celda(coordenadasActuales,0) ;
		int numeroAleatorio;

		do {
			numeroAleatorio = siguienteMov.nextInt(4);
			siguienteMovimiento = mov[numeroAleatorio];
		} while (dentroLimites(celdaActual, siguienteMovimiento, filas, columnas)== false);

		celdaAuxiliar.setPosicionX(celdaActual.getPosicionX() + siguienteMovimiento[0]);
		celdaAuxiliar.setPosicionY(celdaActual.getPosicionY() + siguienteMovimiento[1]);
		celdaAuxiliar.setUltimoMovimiento(id_mov[numeroAleatorio]); 
		return celdaAuxiliar;
	}

	/**
	 * Conociendo el siguiente movimiento que se va a realizar, comprueba si esas nuevas coordenadas estan dentro de los limites del laberinto
	 * @param celdaActual 
	 * @param siguienteMovimiento 
	 * @param filas 
	 * @param columnas 
	 * @return boolean 
	 */
	public static boolean dentroLimites(Celda celdaActual, int[] siguienteMovimiento, int filas, int columnas) {
		if (celdaActual.getPosicionX() + siguienteMovimiento[0] < 0 || celdaActual.getPosicionX() + siguienteMovimiento[0] > (filas-1) || celdaActual.getPosicionY() + siguienteMovimiento[1] < 0
				|| celdaActual.getPosicionY() + siguienteMovimiento[1] > (columnas-1 )) {
			return false;
		}
		return true;
	}
	/**
	 * Se comprueba si se forma un bucle. Si se forma, se obtiene la posicion en la que la celda que forma el bucle aparece por  primera vez. Desde la 
	 * posicion siguiente a esa, se eliminan las celdas del recorrido. La celda actual cambiaria a la celda que ha formado el bucle pero la primera vez 
	 * que aparece. Por otra parte, si no se formara bucle, se incluye en el recorrido y la celda actual pasa a ser esa.
	 * @param celdaSiguiente 
	 * @param celdaActual 
	 * @param caminoParcial 
	 * @return Celda
	 */
	public static Celda realizarMovimiento(Celda celdaSiguiente, Celda celdaActual, List<Celda> caminoParcial) {
		if (comprobarBucle(celdaSiguiente, caminoParcial)== true) { 			
			int indiceBorrado = caminoParcial.indexOf(celdaSiguiente); 	
			int longitud=caminoParcial.size(); 
			for (int repeticiones = 0; repeticiones < (longitud - indiceBorrado - 1); repeticiones++) { 
				caminoParcial.remove(indiceBorrado+1);
			}
			celdaActual=null;
			celdaActual= caminoParcial.get(caminoParcial.size() - 1); 
		}else { 
			caminoParcial.add(celdaSiguiente);
			celdaActual=null;
			celdaActual=celdaSiguiente;
		}
		return celdaActual;
	}

	/**
	 * Comprueba si se realiza un bucle dependiendo de si la celda a la que queremos llegar se encuentra en caminoParcial
	 * @param celdaSiguiente 
	 * @param caminoParcial 
	 * @return boolean 
	 */
	public static boolean comprobarBucle(Celda celdaSiguiente, List<Celda> caminoParcial) {
		if(caminoParcial.contains(celdaSiguiente)) {
			return true;
		}
		return false;
	}

	/**
	 * Una vez obtenido el camino, vamos obteniendo cada celda para ponerla visitada y cambiar sus paredes a true dependiendo de sus movimientos
	 * @param caminoParcial 
	 */
	public static void expandirCamino(List<Celda> caminoParcial) {
		Celda celdaActual=null;
		Celda celdaSiguiente =null;
		for(int indice=0; indice<caminoParcial.size() - 1; indice++) {
			celdaActual=caminoParcial.get(indice);
			celdaSiguiente=caminoParcial.get(indice+1);
			cambiarParedes(celdaActual, celdaSiguiente);
			celdaActual.setVisitado(true);
			celdaSiguiente.setVisitado(true);
		}
	}

	/**
	 * Dependiendo del ultimo movimiento realizado cambiaremos a true las paredes de ambas celdas (celdaActual y celdaSiguiente)
	 * @param actual 
	 * @param siguiente 
	 */
	public static void cambiarParedes(Celda actual, Celda siguiente) {
		switch(actual.getultimoMovimiento()) { 
		case "N":
			actual.setVecinos(0,true);
			siguiente.setVecinos(2,true);
			break;
		case "E":
			actual.setVecinos(1,true);
			siguiente.setVecinos(3,true);	
			break;
		case "S":
			actual.setVecinos(2,true);
			siguiente.setVecinos(0,true);
			break;
		case "O":
			actual.setVecinos(3,true);
			siguiente.setVecinos(1,true);
			break;
		}
	}

	/**
	 * Una vez obtenido un camino parcial, eliminamos las celdas de ese camino en la lista de celdas no visitadas
	 * @param caminoParcial
	 * @param celdasNoVisitadas 
	 * @return List<> 
	 */
	public static List<Celda> eliminarCeldasVisitadas(List<Celda> caminoParcial, List<Celda> celdasNoVisitadas) {
		for(int i =0; i< caminoParcial.size();i++ ) {
			celdasNoVisitadas.remove(caminoParcial.get(i));
		}
		return celdasNoVisitadas;
	}
	/**
	 * Este metodo se encarga de pedir al usuario el directorio de trabajo del programa comprobando que este es un 
	 * directorio y no un archivo e introduciendole la ultima barra en el caso de que no la haya introducido el usuario
	 * @param mensaje
	 * @return String
	 */
	public static String pedirDirectorioEscritura(String mensaje) {
		boolean ruta_valida=false;
		String rutaDirectorio="";
		System.out.println(mensaje);
		while (ruta_valida == false) {
			rutaDirectorio = TECLADO.nextLine();
			File directorio = new File(rutaDirectorio);
			if(directorio.isDirectory()) {
				ruta_valida=true;
			}else {
				System.out.println("Introduce un directorio valido");
			}
		}
		if(!rutaDirectorio.endsWith("\\")) {
			rutaDirectorio=rutaDirectorio.concat("\\");	
		}
		return rutaDirectorio;
	}
	/**
	 * Este metodo se encarga de pedir al usuario el archivo donde se quiere trabajar comprobando que este no es un 
	 * directorio
	 * @param mensaje
	 * @param rutaGeneral
	 * @return String
	 */
	public static String pedirArchivo(String mensaje, String rutaGeneral) {
		boolean ruta_valida=false;
		String nombreArchivo="";
		String rutaCompletaArchivo="";
		System.out.println(mensaje);
		while (ruta_valida == false) {
			nombreArchivo = TECLADO.nextLine();
			rutaCompletaArchivo=rutaGeneral.concat(nombreArchivo);
			File file = new File(rutaCompletaArchivo);
			if(file.isFile()) {
				ruta_valida=true;
			}else {
				System.out.println("Introduce una ruta valida que contenga el archivo");
			}
		}
		return rutaCompletaArchivo;
	}
	/**
	 * Este metodo se encarga de pedir al usuario la heuristica de forma numerica
	 * @return String
	 */
	public static String pedirHeuristica() {
		String heuristica="";
		menuHeuristica();
		int opcion=elegirOpcion("Elija la heuristica entre 1 y 5",1,5);
		switch(opcion) {
		case 1:
			heuristica="BREADTH";
			break;
		case 2:
			heuristica="DEPTH";
			break;
		case 3:
			heuristica="UNIFORM";
			break;
		case 4:
			heuristica="GREEDY";
			break;
		case 5:
			heuristica="A";
			break;
		}
		return heuristica;
	}
	/**
	 * Este metodo se encarga de mostrar al usuario las diferentes heuristicas disponibles
	 */
	public static void menuHeuristica() {
		System.out.println("Heuristicas:");
		System.out.println("1.- BREADTH(ANCHURA)");
		System.out.println("2.- DEPTH(PROFUNDIDAD)");
		System.out.println("3.- UNIFORM(COSTE UNIFORME)");
		System.out.println("4.- GREEDY(VORAZ)");
		System.out.println("5.- A(A*)");
	}
}
