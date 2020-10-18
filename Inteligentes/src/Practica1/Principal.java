package Practica1;

import java.io.File;
import java.io.FileNotFoundException;
//import org.json.simple.JSONObject;
//import org.json.simple.JSONArray;
import java.io.FileWriter;
import java.util.*;
import com.google.gson.*;
import org.json.*;


public class Principal {

	final static Scanner TECLADO = new Scanner(System.in);
	/**
	 * Este es el metodo Principal que lo que hara es llamar al menu() para mostrarte las opciones y con el metodo elegirOpcion() para que
	 * introduzcas el n�mero de la opcion que quieras elegir, con ese numero entrara en un case del switch
	 * @param args
	 * @throws FileNotFoundException
	 * @throws NoSuchElementException
	 * @throws InputMismatchException
	 */
	public static void main(String[] args) throws FileNotFoundException, NoSuchElementException, InputMismatchException {
		int opcion= 0;
		boolean exit= false;
		boolean opcionCorrecta=false;
		do {
			do {
					menu();
					opcion= elegirOpcion("Elija la opcion entre 0 y 2",0,2);
					opcionCorrecta=true;
					switch(opcion) {
					case 0:		
						exit=true;
						break;
					case 1:
						CreacionLaberinto();
						break;
					case 2:
						GenerarImagen();
						break;
					} 
				}while (opcionCorrecta==false);
			}while (exit==false);
		}
		/**
		 * El metodo menu nos muestra por consola las diferentes opciones para elegir
		 */
		public static void menu() {
			System.out.println("\t ##########\t Bienvenido al menu. Elija la operacion que desea realizar: \t########## \t ");
			System.out.println("0. Abandonar");			
			System.out.println("1. Crear un laberinto y exportarlo a imagen a traves de JSON");
			System.out.println("2. Exportar laberinto a imagen a traves de JSON");

		}
		/**
		 * En este metodo el usuario introduce la ruta para encontrar el archivo json que quiere leer y crea el objeto lector para generar el png
		 */
		public static void GenerarImagen() {
			System.out.println("Introduce la ruta donde se encuentra el archivo JSON");
			String ruta = TECLADO.nextLine();
			Lector l= new Lector(ruta);		
		}
		/**
		 * Este metodo tiene como proposito recoger todos los datos para crear el laberinto mediante el algortimo de Wilson
		 */
		public static void CreacionLaberinto() {
			//Pedimos por teclado la ruta y comprobamos si es valida, si no lo es la pedira de nuevo
			boolean ruta_valida= false;
			String ruta="";
			System.out.println("Introduce la ruta donde desea guardar el archivo JSON y generar la imagen PNG");
			while (ruta_valida == false) {
				ruta = TECLADO.nextLine();
				File carpeta = new File(ruta);
				if(carpeta.isDirectory()) {
					ruta_valida=true;
				}else {
					System.out.println("Introduce una ruta valida");
				}

			}
			
			int filas;
			int columnas;
			List<Celda> celdas = new ArrayList<Celda>();
			int[][] mov = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
			String[] id_mov = { "N", "E", "S", "O" };
			int max_n= id_mov.length; //Numero maximo de vecinos


			//Pedimos por teclado el numero de filas y de columnas teniendo en cuenta que tiene que ser mayor que 2 y menor que 1000
			filas = elegirOpcion("Introduce las filas. Debe ser un valor numerico positivo mayor que 1",2,1000);
			columnas = elegirOpcion("Introduce las columnas. Debe ser un valor numerico positivo mayor que 1",2,1000);
			celdas = crearCeldas(filas, columnas);
			List<Celda> celdasNoVisitadas = new ArrayList<Celda>(celdas); 

			//Ejecutamos el algoritmo de Wilson
			algoritmoWilson(celdas, mov, filas, columnas, id_mov, celdasNoVisitadas);
				
			//Con el laberinto ya creado generamos el Json para pasarselo al lector y que genere la imagen
			generadorJson(celdas,filas,columnas,max_n,mov,id_mov,ruta);
		}
		/**
		 * Este metodo tiene como cometido generar el archivo .json y se lo pasa al lector que crea
		 * @param celdas la lista que contiene todas las celdas
		 * @param filas	numero de filas del laberinto
		 * @param columnas numero de columnas del laberinto
		 * @param max_n	maximo numero de vecinos
		 * @param mov array de movimientos 
		 * @param id_mov array de string de movimientos
		 * @param ruta donde queremos que se guarde nuestro .json y .png
		 */
		public static void generadorJson(List<Celda> celdas, int filas, int columnas, int max_n, int[][] mov, String[] id_mov, String ruta) {
			JSONObject obj = new JSONObject();
			JSONObject cells = new JSONObject();
			//En este for iteramos la lista de celdas para introducirle el valor, el array de los vecinos y la posicion al objeto JSon declarado como cells
			for(Celda aux: celdas) {
				JSONObject json2 = new JSONObject();
				json2.put("value", aux.getValor());
				json2.put("neighbors", aux.getVecinos());
				cells.put("("+aux.getPosicionX()+", "+aux.getPosicionY()+")",json2);
			}
			//Para terminar de a�adir las variables introducimos todos los elementos del .json incluyendo el objeto cells 
			obj.put("rows", filas);
			obj.put("cols", columnas);
			obj.put("max_n", max_n);
			obj.put("mov", mov);
			obj.put("id_mov", id_mov);
			obj.put("cells", cells);
			
			//Creamos y escribimos el archivo .json y lo guardamos en la ruta que nos han pasado
			try{
				FileWriter file = new FileWriter(ruta.concat("\\LABERINTO.json"));
				file.write(obj.toString());
				file.flush();
				file.close();
			}catch(Exception ex){
				System.out.println("Error: "+ex.toString());
			}
			//Despues de crear el archivo .json se lo pasamos al lector para que cree la imagen
			Lector l = new Lector(ruta.concat("\\LABERINTO.json"));
		}

		/**
		 * Introduce un valor que debe ser mayor que el limite inferior y no puede ser negativo y menos que el limite superior.
		 * @param mensaje Mensaje indicando que se debe introducir
		 * @return Valor de lo que se pide introducir
		 * @throws InputMismatchException
		 */
		public static int elegirOpcion(String mensaje, int limiteInf,int limiteSup) throws InputMismatchException {
			int opcion = 0;
			try{
				do {
					System.out.println(mensaje);
					opcion = TECLADO.nextInt();
					TECLADO.nextLine();
				} while (opcion < limiteInf || opcion > limiteSup);


			}catch(InputMismatchException i) {
				System.out.println("Debe introducir un valor numerico positivo");
				TECLADO.nextLine();
				opcion=elegirOpcion(mensaje,limiteInf,limiteSup);
			}
			return opcion;
		}

		/**
		 * Crea tantas celdas como posiciones tenga el laberinto (filas*columnas)
		 * @param filas N�mero de filas que contiene el laberinto
		 * @param columnas N�mero de columnas que contiene el laberinto
		 * @return List<> Celdas que contendr� el laberinto
		 */
		public static List<Celda> crearCeldas(int filas, int columnas) {
			List<Celda> celdas = new ArrayList<Celda>();
			for (int i = 0; i < filas; i++) {
				for (int j = 0; j < columnas; j++) {
					int[] posicion = { i, j };
					celdas.add(new Celda(posicion, 0));
				}
			}
			return celdas;
		}
		/**
		 * Establecemos una lista (CAMINO_PARCIAL) que va a contener cada una de las celdas de un camino. En primer lugar se escogen al azar la celda inicial y
		 * final del primer camino y se realiza el camino hasta alcanzar la inicial (se empieza desde la final). Una vez se tiene el camino, se expande 
		 * (cambiando los valores de las paredes de las celdas y poni�ndolas como visitadas) y se eliminan las celdas visitadas de la lista celdasNoVisitadas.
		 * Posteriormente, se realiza exactamente lo mismo pero con una celda inicial no visitada y una celda final visitada. Esto se realiza hasta que todas
		 * las celdas est�n visitadas
		 * @param celdas Lista con todas las celdas del laberinto
		 * @param mov Posibles movimientos que realizar (num�rico)
		 * @param filas N�mero de filas que contiene el laberinto
		 * @param columnas N�mero de columnas que contiene el laberinto
		 * @param id_mov Posibles movimientos que realizar (textual)
		 * @param celdasNoVisitadas Lista con las celdas no visitadas del laberinto
		 */
		public static void algoritmoWilson(List<Celda> celdas, int[][] mov, int filas, int columnas, String [] id_mov, List<Celda> celdasNoVisitadas) {
			List<Celda> CAMINO_PARCIAL = new ArrayList<Celda>();
			Celda[] posiciones = elegirPosiciones(filas, columnas); 
			//El calculo: Posicion X de la Celda * Columnas del Laberinto + Posicion Y de la Celda, nos permite obtener la posici�n de esa celda en la Lista celdas.
			int indicePosInicial= (posiciones[0].getPosicionX()* columnas) + posiciones[0].getPosicionY(); 
			int indicePosFinal= (posiciones[1].getPosicionX()* columnas) + posiciones[1].getPosicionY(); 

			Celda celdaFinal = celdas.get(indicePosInicial); 
			Celda celdaActual = celdas.get(indicePosFinal);
			CAMINO_PARCIAL.add(celdaActual); 
			Celda celdaSiguiente = null;	

			//Realizamos el primer camino hasta alcanzar la casilla Inicial
			do{ 
				celdaSiguiente= obtenerCeldaSiguiente(celdaActual, mov, filas, columnas, id_mov, celdaSiguiente,celdas);
				celdaActual= realizarMovimiento(celdaSiguiente, celdaActual, CAMINO_PARCIAL);	
			}while (! ((celdaSiguiente.getPosicionX() == celdaFinal.getPosicionX()) && (celdaSiguiente.getPosicionY() == celdaFinal.getPosicionY()) )) ; 

			//Una vez tengo el camino, expando (cambiar paredes de false a true). Para ello tenemos en cuenta el �ltimo movimiento de cada una de las celdas
			expandirCamino(CAMINO_PARCIAL);
			celdasNoVisitadas=eliminarCeldasVisitadas(CAMINO_PARCIAL, celdasNoVisitadas);

			//Ahora caminos desde celdas no visitadas hasta encontrar una visitada
			do {
				CAMINO_PARCIAL.clear();
				celdaActual=null;
				celdaSiguiente=null;
				Random r = new Random();
				celdaActual= celdasNoVisitadas.get(r.nextInt(celdasNoVisitadas.size())); 
				CAMINO_PARCIAL.add(celdaActual);

				//Realizamos el camino hasta alcanzar cualquier visitada
				do {
					celdaSiguiente= obtenerCeldaSiguiente(celdaActual, mov, filas, columnas, id_mov, celdaSiguiente,celdas);		
					celdaActual= realizarMovimiento(celdaSiguiente, celdaActual, CAMINO_PARCIAL);	
				}while (celdaSiguiente.getVisitado() == false) ;

				//Expando el camino
				expandirCamino(CAMINO_PARCIAL);
				celdasNoVisitadas=eliminarCeldasVisitadas(CAMINO_PARCIAL, celdasNoVisitadas);
			}while(celdasNoVisitadas.size()!=0);
		}
		/**
		 * Se crea un array de celdas llamado posiciones donde se almacenara la celda origen y la destino. Establecemos las coordenadas de forma aleatoria tanto
		 * la celda origen como la destino. La celda final debe tener unas coordenadas distintas a la origen
		 * @param filas Numero de filas que contiene el laberinto
		 * @param columnas Numero de columnas que contiene el laberinto
		 * @return Celda[] Celda inicial y celda final del primer camino
		 */
		public static Celda[] elegirPosiciones(int filas, int columnas) {
			Celda[] posiciones = new Celda[2];
			Random r = new Random();
			Random r1 = new Random();
			int[] posicionInicial = new int[2];
			int[] posicionFinal = new int[2];
			posicionInicial[0] = r.nextInt(filas);
			posicionInicial[1] = r1.nextInt(columnas);
			do {
				posicionFinal[0] = r.nextInt(filas);
				posicionFinal[1] = r1.nextInt(columnas);
			} while ((posicionInicial[0] == posicionFinal[0]) && (posicionInicial[1] == posicionFinal[1]));
			Celda celdaInicial = new Celda(posicionInicial, 0);
			Celda celdaFinal = new Celda(posicionFinal, 0);
			posiciones[0] = celdaInicial;
			posiciones[1] = celdaFinal;
			return posiciones;
		}

		/**
		 * Utilizamos una celda auxiliar que nos dar� las coordenadas de la siguiente celda y el �ltimo movimiento de la celda actual
		 * @param celdaActual Celda antes de realizar el movimiento
		 * @param mov Posibles movimientos que realizar (num�rico)
		 * @param filas N�mero de filas que contiene el laberinto
		 * @param columnas N�mero de columnas que contiene el laberinto
		 * @param id_mov Posibles movimientos que realizar (textual)
		 * @param celdaSiguiente Celda despu�s de realizar el movimiento
		 * @param celdas Lista de todas las celdas del laberinto
		 * @return Celda Celda a la que nos hemos desplazado
		 */
		public static Celda obtenerCeldaSiguiente(Celda celdaActual, int[][]mov, int filas, int columnas, String [] id_mov, Celda celdaSiguiente, List<Celda> celdas) {
			Celda celdaAuxiliarMovimiento = siguientePaso(celdaActual, mov, filas, columnas, id_mov); 
			celdaActual.setUltimoMovimiento(celdaAuxiliarMovimiento.getultimoMovimiento()); 
			int indicePosSiguiente= (celdaAuxiliarMovimiento.getPosicionX()* columnas) + celdaAuxiliarMovimiento.getPosicionY(); 
			celdaSiguiente = celdas.get(indicePosSiguiente);
			return celdaSiguiente;
		}

		/**
		 * Se elige de forma aleatoria el siguiente movimiento a realizar. Si ese siguiente movimiento esta dentro de los limites del laberinto, se confirman las
		 * siguientes coordenadas. Tambien se aprovecha y se devuelve el ultimo movimiento que ha hecho la celda actual.
		 * @param celdaActual Celda en la que nos encontramos antes de realizar el movimiento
		 * @param mov Posibles movimientos que realizar (num�rico)
		 * @param filas N�mero de filas que contiene el laberinto
		 * @param columnas N�mero de columnas que contiene el laberinto
		 * @param id_mov Posibles movimientos que realizar (textual)
		 * @return Celda Celda que se va a utilizar para establecer las coordenadas de la siguiente celda y para establecer el �ltimo movimiento de la celda Actual
		 */

		public static Celda siguientePaso(Celda celdaActual, int[][] mov, int filas, int columnas, String [] id_mov) {
			Random r = new Random();
			int[] siguienteMovimiento;
			int [] coordenadasActuales= {celdaActual.getPosicionX(), celdaActual.getPosicionY()};
			Celda celdaAuxiliar= new Celda(coordenadasActuales,0) ;
			int numeroAleatorio;

			do {
				numeroAleatorio = r.nextInt(4);
				siguienteMovimiento = mov[numeroAleatorio];
			} while (dentroLimites(celdaActual, siguienteMovimiento, filas, columnas)== false);

			celdaAuxiliar.setPosicionX(celdaActual.getPosicionX() + siguienteMovimiento[0]);
			celdaAuxiliar.setPosicionY(celdaActual.getPosicionY() + siguienteMovimiento[1]);
			celdaAuxiliar.setUltimoMovimiento(id_mov[numeroAleatorio]); 
			return celdaAuxiliar;
		}

		/**
		 * Conociendo el siguiente movimiento que se va a realizar, comprueba si esas nuevas coordenadas estan dentro de los limites del laberinto
		 * @param celdaActual Celda en la que nos encontramos
		 * @param siguienteMovimiento Valor de la posici�n X e Y que debemos aplicar a la celda actual
		 * @param filas N�mero de filas que contiene el laberinto
		 * @param columnas N�mero de columnas que contiene el laberinto
		 * @return boolean Especifica si la celda a la que nos queremos mover est� dentro o fuera de los l�mites del laberinto
		 */
		public static boolean dentroLimites(Celda celdaActual, int[] siguienteMovimiento, int filas, int columnas) {
			if (celdaActual.getPosicionX() + siguienteMovimiento[0] < 0 || celdaActual.getPosicionX() + siguienteMovimiento[0] > (filas-1) || celdaActual.getPosicionY() + siguienteMovimiento[1] < 0
					|| celdaActual.getPosicionY() + siguienteMovimiento[1] > (columnas-1 )) {
				return false;
			}
			return true;
		}


		/**
		 * Se comprueba si se forma un bucle. Si se forma, se obtiene la posici�n en la que la celda que forma el bucle aparece por  primera vez. Desde la 
		 * posici�n siguiente a esa, se eliminan las celdas del recorrido. La celda actual cambiar�a a la celda que ha formado el bucle pero la primera vez 
		 * que aparece. Por otra parte, si no se formara bucle, se incluye en el recorrido y la celda actual pasa a ser esa.
		 * @param celdaSiguiente Celda despu�s de realizar el movimiento
		 * @param celdaActual Celda antes de realizar el movimiento
		 * @param CAMINO_PARCIAL Conjunto de celdas que forman un camino (no tiene por qu� estar completado)
		 * @return Celda Nueva celda actual
		 */
		public static Celda realizarMovimiento(Celda celdaSiguiente, Celda celdaActual, List<Celda> CAMINO_PARCIAL) {
			if (comprobarBucle(celdaSiguiente, CAMINO_PARCIAL)== true) { 					
				int indiceBorrado = CAMINO_PARCIAL.indexOf(celdaSiguiente); 
				int longitud=CAMINO_PARCIAL.size(); 
				for (int repeticiones = 0; repeticiones < (longitud - indiceBorrado - 1); repeticiones++) { 
					CAMINO_PARCIAL.remove(indiceBorrado+1);
				}
				celdaActual=null;
				celdaActual= CAMINO_PARCIAL.get(CAMINO_PARCIAL.size() - 1); 
			}else { 
				CAMINO_PARCIAL.add(celdaSiguiente);
				celdaActual=null;
				celdaActual=celdaSiguiente;
			}
			return celdaActual;
		}

		/**
		 * Comprueba si se realiza un bucle dependiendo de si la celda a la que queremos llegar se encuentra en CAMINO_PARCIAL
		 * @param celdaSiguiente Celda hacia la que queremos avanzar
		 * @param CAMINO_PARCIAL Conjunto de celdas que forman un camino (no tiene por qu� estar completado)
		 * @return boolean Comprobaci�n de si finalmente se ha hecho un bucle o no
		 */
		public static boolean comprobarBucle(Celda celdaSiguiente, List<Celda> CAMINO_PARCIAL) {
			for(int i =0; i< CAMINO_PARCIAL.size();i++ ) {
				if(celdaSiguiente.getPosicionX() == CAMINO_PARCIAL.get(i).getPosicionX() && celdaSiguiente.getPosicionY() == CAMINO_PARCIAL.get(i).getPosicionY()) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Una vez obtenido el camino, vamos obteniendo cada celda para ponerla visitada y cambiar sus paredes a true dependiendo de sus movimientos
		 * @param CAMINO_PARCIAL Conjunto de celdas que forman un camino 
		 */
		public static void expandirCamino(List<Celda> CAMINO_PARCIAL) {
			Celda celdaActual=null;
			Celda celdaSiguiente =null;
			for(int indice=0; indice<CAMINO_PARCIAL.size() - 1; indice++) {
				celdaActual=CAMINO_PARCIAL.get(indice);
				celdaSiguiente=CAMINO_PARCIAL.get(indice+1);
				cambiarParedes(celdaActual, celdaSiguiente);
				celdaActual.setVisitado(true);
				celdaSiguiente.setVisitado(true);
			}
		}

		/**
		 * Dependiendo del �ltimo movimiento realizado cambiaremos a true las paredes de ambas celdas (celdaActual y celdaSiguiente)
		 * @param actual Celda antes de realizar el movimiento
		 * @param siguiente Celda despu�s de realizar el movimiento
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
		 * @param CAMINO_PARCIAL Conjunto de celdas que forman un camino 
		 * @param celdasNoVisitadas Conjunto de las celdas que todav�a no pertenecen a ning�n camino 
		 * @return List<> Devuelve actualizadas las celdas que todav�a no pertenecen a ning�n camino 
		 */
		public static List<Celda> eliminarCeldasVisitadas(List<Celda> CAMINO_PARCIAL, List<Celda> celdasNoVisitadas) {
			for(int i =0; i< CAMINO_PARCIAL.size();i++ ) {
				celdasNoVisitadas.remove(CAMINO_PARCIAL.get(i));
			}
			return celdasNoVisitadas;
		}
	}
