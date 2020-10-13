package Practica1;
import java.io.File;
import java.io.FileNotFoundException;
//import org.json.simple.JSONObject;
//import org.json.simple.JSONArray;
import java.io.FileWriter;
import java.util.*;
import com.google.gson.*;
import org.json.*;


public class Principal2 {

	final static Scanner TECLADO = new Scanner(System.in);

	public static void main(String[] args) throws FileNotFoundException, NoSuchElementException, InputMismatchException {
		menu();
		int opcion= 0;
		boolean opcion_correcta=false;
		do {
			try {
				opcion= elegirOpcion("Elija la opción entre 1 y 2");
				opcion_correcta=true;
				switch(opcion) {
					case 1:
						CreacionLaberinto();
						break;
					case 2:
						GenerarImagen();
						break;
					}
					
			} catch (InputMismatchException e) {
				System.out.println("Debe introducir un valor numerico positivo");
			}
		}while (opcion_correcta==false);
	}
	
	public static void menu() {
		System.out.println("Bienvenido al menú. Elija la operación que desea realizar: ");
		System.out.println("1. Crear un laberinto y exportarlo a imagen a través de JSON");
		System.out.println("2. Exportar laberinto a imagen a través de JSON");
	}

	public static void GenerarImagen() {
		// COMPLETAR
		
	}

	public static void CreacionLaberinto() {
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
		TECLADO.nextLine();
		int filas;
		int columnas;
		List<Celda> celdas = new ArrayList<Celda>();
		int[][] mov = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
		String[] id_mov = { "N", "E", "S", "O" };
		int max_n= id_mov.length; //Numero maximo de vecinos

		
		
		filas = elegirOpcion("Introduce las filas. Debe ser un valor numerico positivo");
		columnas = elegirOpcion("Introduce las columnas. Debe ser un valor numerico positivo");
		celdas = crearCeldas(filas, columnas);
		List<Celda> celdasNoVisitadas = new ArrayList<Celda>(celdas); 

		
		algoritmoWilson(celdas, mov, filas, columnas, id_mov, celdasNoVisitadas);
		System.out.println(celdas);
		JSONObject obj = new JSONObject();
		JSONObject cells = new JSONObject();
		for(Celda aux: celdas) {
			JSONObject json2 = new JSONObject();
			json2.put("value", aux.getValor());
			json2.put("neighbors", aux.getVecinos());
			cells.put("("+aux.getPosicionX()+", "+aux.getPosicionY()+")",json2);
		}

		obj.put("filas", filas);
		obj.put("columnas", columnas);
		obj.put("max_n", max_n);
		obj.put("mov", mov);
		obj.put("id_mov", id_mov);
		obj.put("cells", cells);
		System.out.println(obj.toString());
		
		try{
			FileWriter file = new FileWriter(ruta.concat("\\LABERINTO.json"));
			file.write(obj.toString());
			file.flush();
			file.close();
		}catch(Exception ex){
			System.out.println("Error: "+ex.toString());
		}

		Lector l = new Lector(ruta.concat("\\LABERINTO.json"));
		
	}

	//Introduce un valor que debe ser mayor que 0 y no puede ser negativo.
	/**
	 * 
	 * @param mensaje
	 * @return
	 * @throws InputMismatchException
	 */
	public static int elegirOpcion(String mensaje) throws InputMismatchException {
		int opcion = 0;
		try{
			do {
				System.out.println(mensaje);
				opcion = TECLADO.nextInt();
				TECLADO.nextLine();
			} while (opcion <= 0);
			
		}catch(InputMismatchException i) {
			System.out.println("Debe introducir un valor numerico positivo");
			TECLADO.nextLine();
			opcion=elegirOpcion(mensaje);
		}
		return opcion;
	}
	
	//Crea tantas celdas como posiciones tenga el laberinto (filas*columnas)
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
	
	public static void algoritmoWilson(List<Celda> celdas, int[][] mov, int filas, int columnas, String [] id_mov, List<Celda> celdasNoVisitadas) {
		List<Celda> CAMINO_PARCIAL = new ArrayList<Celda>();
		Celda[] posiciones = elegirPosiciones(filas, columnas); //Posiciones origen y destino del primer camino
		//El calculo: Posicion X de la Celda * Columnas del Laberinto + Posicion Y de la Celda, nos permite obtener la posicion de esa celda en la Lista celdas.
		int indicePosInicial= (posiciones[0].getPosicionX()* columnas) + posiciones[0].getPosicionY(); 
		int indicePosFinal= (posiciones[1].getPosicionX()* columnas) + posiciones[1].getPosicionY(); 
		
		Celda celdaFinal = celdas.get(indicePosInicial); //La celda a la que queremos llegar es a la Inicial
		Celda celdaActual = celdas.get(indicePosFinal);
		CAMINO_PARCIAL.add(celdaActual); 
		Celda celdaSiguiente = null;	
		
		//Realizamos el primer camino hasta alcanzar la casilla Inicial
		do{ 
			celdaSiguiente= obtenerCeldaSiguiente(celdaActual, mov, filas, columnas, id_mov, celdaSiguiente,celdas);
			celdaActual= realizarMovimiento(celdaSiguiente, celdaActual, CAMINO_PARCIAL);	
		}while (! ((celdaSiguiente.getPosicionX() == celdaFinal.getPosicionX()) && (celdaSiguiente.getPosicionY() == celdaFinal.getPosicionY()) )) ; 
		
		//Una vez tengo el camino, expando (cambiar paredes de false a true). Para ello tenemos en cuenta el ultimo movimiento de cada una de las celdas
		expandirCamino(CAMINO_PARCIAL);
		System.out.println("CAMINO PARCIAL FINAL CON PAREDES: " + CAMINO_PARCIAL.toString() + "\n\n");	
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
			System.out.println("CAMINO PARCIAL 2 FINAL CON PAREDES: " + CAMINO_PARCIAL.toString());
			celdasNoVisitadas=eliminarCeldasVisitadas(CAMINO_PARCIAL, celdasNoVisitadas);
		}while(celdasNoVisitadas.size()!=0);
	}
	//Se crea un array de celdas llamado posiciones donde se almacenara la celda origen y la destino. Establecemos las coordenadas de forma aleatoria tanto
	//la celda origen como la destino. La celda final debe tener unas coordenadas distintas a la origen
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
		Celda inicial = new Celda(posicionInicial, 0);
		Celda Final = new Celda(posicionFinal, 0);
		posiciones[0] = inicial;
		System.out.println("\nInicial: " + posiciones[0].toString());
		posiciones[1] = Final;
		System.out.println("Final: " + posiciones[1].toString()+"\n");
		return posiciones;
	}
	
	//Utilizamos una celda auxiliar que nos dara las coordenadas de la siguiente celda y el ultimo movimiento de la celda actual
	public static Celda obtenerCeldaSiguiente(Celda celdaActual, int[][]mov, int filas, int columnas, String [] id_mov, Celda celdaSiguiente, List<Celda> celdas) {
		Celda celdaAuxiliarMovimiento = siguientePaso(celdaActual, mov, filas, columnas, id_mov); //HACIA QUE COORDENADAS ME HE MOVIDO Y HACIA QUE POSICION(NORTE,SUR,ETC)
		celdaActual.setUltimoMovimiento(celdaAuxiliarMovimiento.getultimoMovimiento()); //guardo el ultimo movimiento de la actual
		int indicePosSiguiente= (celdaAuxiliarMovimiento.getPosicionX()* columnas) + celdaAuxiliarMovimiento.getPosicionY(); //PATRON CALCULADO. Busco siguiente celda con coordenadas de celdaAuxiliarMovimiento
		celdaSiguiente = celdas.get(indicePosSiguiente);
		return celdaSiguiente;
	}
	
	//Se elige de forma aleatoria el siguiente movimiento a realizar. Si ese siguiente movimiento esta dentro de los limites del laberinto, se confirman las
	//siguientes coordenadas. Tambien se aprovecha y se devuelve el ultimo movimiento que ha hecho la celda actual.
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
		System.out.println("Me he movido desde la celda: " + celdaActual.getPosicionX() + celdaActual.getPosicionY() + " hacia la celda: " + celdaAuxiliar.getPosicionX() + celdaAuxiliar.getPosicionY());
		celdaAuxiliar.setUltimoMovimiento(id_mov[numeroAleatorio]); //aqui deja la letra del ultimo movimiento que ha hecho la ultima celda
		return celdaAuxiliar;
	}
	
	//Conociendo el siguiente movimiento que se va a realizar, comprueba si esas nuevas coordenadas estan dentro de los limites del laberinto
	public static boolean dentroLimites(Celda celdaActual, int[] siguienteMovimiento, int filas, int columnas) {
		if (celdaActual.getPosicionX() + siguienteMovimiento[0] < 0 || celdaActual.getPosicionX() + siguienteMovimiento[0] > (filas-1) || celdaActual.getPosicionY() + siguienteMovimiento[1] < 0
				|| celdaActual.getPosicionY() + siguienteMovimiento[1] > (columnas-1 )) {
			return false;
		}
		return true;
	}
	

	/**
	 * Se comprueba si se forma un bucle. Si se forma, se obtiene la posición en la que la celda que forma el bucle aparece por  primera vez. Desde la 
	 * posición siguiente a esa, se eliminan las celdas del recorrido. La celda actual cambiaría a la celda que ha formado el bucle pero la primera vez 
	 * que aparece. Por otra parte, si no se formara bucle, se incluye en el recorrido y la celda actual pasa a ser esa.
	 * @param celdaSiguiente Celda después de realizar el movimiento
	 * @param celdaActual Celda antes de realizar el movimiento
	 * @param CAMINO_PARCIAL Conjunto de celdas que forman un camino (no tiene por qué estar completado)
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
	 * @param CAMINO_PARCIAL Conjunto de celdas que forman un camino (no tiene por qué estar completado)
	 * @return boolean Comprobación de si finalmente se ha hecho un bucle o no
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
	 * Dependiendo del último movimiento realizado cambiaremos a true las paredes de ambas celdas (celdaActual y celdaSiguiente)
	 * @param actual Celda antes de realizar el movimiento
	 * @param siguiente Celda después de realizar el movimiento
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
	 * @param celdasNoVisitadas Conjunto de las celdas que todavía no pertenecen a ningún camino 
	 * @return List<> Devuelve actualizadas las celdas que todavía no pertenecen a ningún camino 
	 */
	public static List<Celda> eliminarCeldasVisitadas(List<Celda> CAMINO_PARCIAL, List<Celda> celdasNoVisitadas) {
		for(int i =0; i< CAMINO_PARCIAL.size();i++ ) {
			celdasNoVisitadas.remove(CAMINO_PARCIAL.get(i));
		}
		return celdasNoVisitadas;
	}
}
