package Practica1;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;

public class Resolvedor {
	private static String rutaArchivoProblema;
	private static String rutaGeneral;
	private static int cotaProfundidad;
	private static long id;
	private static String heuristica;
	public Resolvedor(String rutaArchivoProblema, String rutaGeneral, String heuristica) {
		this.rutaArchivoProblema=rutaArchivoProblema;
		this.rutaGeneral=rutaGeneral;
		this.cotaProfundidad=1000000;
		this.id=0;
		this.heuristica=heuristica;
	}
	/**
	 * Este metodo lee el archivo json del problema obteniendo las posiciones inicial y objetivo y el laberinto
	 * en forma de arraylist gracias al metodo obtenerLaberintoJSON. Posteriormente, resuelve el laberinto de 
	 * acuerdo a una heuristica
	 */
	public static void resolverLaberinto() {
		ArrayList<Celda>laberinto=new ArrayList<Celda>();
		ArrayList<Nodo>solucionLaberinto=new ArrayList<Nodo>();
		String nombreLaberinto="";
		int[][] posicionesInicialObjetivo;
		JSONParser parserJSON= new JSONParser();
		float tiempoInicio=0, tiempoFin=0;


		try {
			//castea a JSONObject el JSON del archivo del problema con la ruta rutaArchivoProblema parseado
			JSONObject JSONProblema = (JSONObject) parserJSON.parse(new FileReader(rutaArchivoProblema));
			//comprueba que el archivo es el correcto
			if(JSONProblema.get("INITIAL")==null) {
				System.out.println("\nEl archivo introducido no es el esperado");
				return;
			}
			//obtiene las posiciones INITIAL y OBJETIVE del archivo json
			posicionesInicialObjetivo=obtenerPosicionInicialObjetivo(JSONProblema);
			if(posicionesInicialObjetivo==null) {
				return;
			}
			int[]estadoInicial= {posicionesInicialObjetivo[0][0],posicionesInicialObjetivo[0][1]};
			int[]estadoObjetivo= {posicionesInicialObjetivo[1][0],posicionesInicialObjetivo[1][1]};

			nombreLaberinto=(String)JSONProblema.get("MAZE");
			//se lee el archivo json del laberinto
			JSONObject JSONLaberinto = (JSONObject) parserJSON.parse(new FileReader(rutaGeneral.concat(nombreLaberinto)));
			if(posicionesDentroLimites(JSONLaberinto, posicionesInicialObjetivo)==false) {
				return;
			}
			
			laberinto=obtenerLaberintoJSON(JSONLaberinto, rutaGeneral, nombreLaberinto);
			if(laberinto.size()==0) {
				return;
			}

			tiempoInicio=System.nanoTime();
			solucionLaberinto=algoritmoBusqueda(JSONLaberinto,heuristica, estadoInicial,estadoObjetivo,nombreLaberinto);
			tiempoFin=System.nanoTime();
			imprimirSolucion(solucionLaberinto);
			System.out.println("\nEl algoritmo de busqueda con la heuristica "+heuristica+" se ha llevado a cabo en : "+(tiempoFin-tiempoInicio)/Math.pow(10, 9)+" segundos");


		}catch(NumberFormatException numforex) {
			System.err.println("El formato de la celda debe ser numerico "+numforex.toString());
		}catch(Exception ex) {
			System.err.println("Error leyendo el archivo json que contiene los datos para resolver el laberinto "+ex.toString());
		}
	}
	/**
	 * Este metodo obtiene las posiciones inicial y objetivo del archivo json de sucesores como string
	 * para despues convertirlo en arraylist por el metodo convertirString_A_ArrayPosiciones que nos ayuda a comprobar
	 * si son iguales o no entre otras cosas
	 * @param rutaJSONProblema
	 * @return int[][]
	 */
	public static int[][]obtenerPosicionInicialObjetivo(JSONObject JSONProblema) {
		String posicionInicial="";
		String posicionObjetivo="";

		try {
			posicionInicial= (String)JSONProblema.get("INITIAL");
			posicionObjetivo= (String)JSONProblema.get("OBJETIVE");
		}catch(Exception ex){
			System.err.println("Error leyendo el archivo JSON para obtener las posiciones inicial y objetivo "+ex.toString());
		}

		int[]posInicial=convertirString_A_ArrayPosiciones(posicionInicial);
		int[]posObjetivo=convertirString_A_ArrayPosiciones(posicionObjetivo);
		if(posInicial[0]==posObjetivo[0] && posInicial[1]==posObjetivo[1]) {
			System.out.println("La posicion inicial y la objetivo coinciden");
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
	public static ArrayList<Celda> obtenerLaberintoJSON(JSONObject JSONLaberinto, String rutaGeneral, String nombreLaberinto) {
		ArrayList<Celda>laberinto=new ArrayList<Celda>();
		String rutaLaberinto=rutaGeneral.concat(nombreLaberinto);

		try {
			LectorDibujadorLaberinto lectorLaberinto= new LectorDibujadorLaberinto(rutaLaberinto);
			//si no hay inconsistencias se obtiene el laberinto del json
			if(lectorLaberinto.comprobarInconsistencias()==0) {
				JSONObject cells= (JSONObject)JSONLaberinto.get("cells");
				Object[] coordenadasCeldas=cells.keySet().toArray();

				for(int i=0;i<coordenadasCeldas.length;i++) {
					//se cogen las coordenadas de cada celda para obtener los objetos json de dichas celdas
					int [] coordenadasCelda=convertirString_A_ArrayPosiciones(coordenadasCeldas[i].toString());
					JSONObject JSONCelda = (JSONObject) cells.get(coordenadasCeldas[i]);
					//recogemos los atributos de la celda para posteriormente crearla y asignarselos
					long value= (long)JSONCelda.get("value");
					JSONArray neighbors= (JSONArray)JSONCelda.get("neighbors");
					Celda celdaAux=new Celda(coordenadasCelda, (float)value);
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
		if(posicionesInicialObjetivo[0][0]>=rows || posicionesInicialObjetivo[0][0]<0
				|| posicionesInicialObjetivo[0][1]>=cols || posicionesInicialObjetivo[0][1]<0) {
			System.out.println("La celda inicial no esta dentro de los limites del laberinto");
			seguir=false;
		}

		if(posicionesInicialObjetivo[1][0]>=rows || posicionesInicialObjetivo[1][0]<0
				|| posicionesInicialObjetivo[1][1]>=cols || posicionesInicialObjetivo[1][1]<0) {
			System.out.println("La celda objetivo no esta dentro de los limites del laberinto");
			seguir=false;
		}
		return seguir;
	}
	/**
	 * Funcion empleada en el algoritmo de busqueda para obtener los sucesores de un nodo pasado por parametro. Para ello,
	 * se obtiene el estado (posicion de la celda) a partir del nodo y se busca ese estado en el json del laberinto para obtener
	 * su array de vecinos. Si no existe pared, quiere decir que esa celda contigua es su sucesor
	 * @param nodo
	 * @param JSONLaberinto
	 * @return ArrayList<Estado>
	 */
	public static ArrayList<Sucesor> funcionSucesor(Nodo nodo, JSONObject JSONLaberinto) {
		ArrayList<Sucesor> sucesoresNodo=new ArrayList<Sucesor>();
		JSONObject cells;
		JSONArray id_mov=(JSONArray)JSONLaberinto.get("id_mov");
		JSONArray mov=(JSONArray)JSONLaberinto.get("mov");	
		int[] idEstado=nodo.getEstadoActual().getId_estado();

		cells=(JSONObject)JSONLaberinto.get("cells");
		JSONObject celda=(JSONObject)cells.get("("+idEstado[0]+", "+idEstado[1]+")");

		JSONArray vecinos=(JSONArray)celda.get("neighbors");
		for(int posicionVecino=0;posicionVecino<vecinos.size();posicionVecino++) {
			if((boolean)vecinos.get(posicionVecino)==true) {
				sucesoresNodo.add(obtenerEstadoSucesor(mov, idEstado, cells,posicionVecino, id_mov));
			}
		}

		return sucesoresNodo;
	}
	/**
	 * Este método obtiene un estado sucesor a partir del actual. Para ello, en primer lugar se suma al idEstado del estado actual
	 * dependiendo del movimiento realizado. Posteriormente, se obtiene la celda del laberinto de ese nuevo estado a partir de
	 * cells(celdas del json del laberinto) que se utilizara para adquirir el valor de la celda (asfalto, tierra...). Finalmente,
	 * se crea un nuevo estado otorgandole su idEstado, su costo y su movimiento
	 * @param mov
	 * @param idEstado
	 * @param cells
	 * @param posicionVecino
	 * @param id_mov
	 * @return Estado
	 */
	public static Sucesor obtenerEstadoSucesor(JSONArray mov,int[]idEstado, JSONObject cells, int posicionVecino, JSONArray id_mov) {
		int idEstadoSucesor[]=new int[2];
		JSONObject celdaDestino;
		Estado estadoSucesor=new Estado();
		ArrayList<Long> movAux=new ArrayList<Long>();
		JSONArray movArray=(JSONArray)mov.get(posicionVecino);
		//en movAux se guarda un movimiento segun posicionVecino ([-1,0] o [1,0]...)
		movAux=(ArrayList<Long>)movArray;
		idEstadoSucesor[0]=idEstado[0]+Math.toIntExact(movAux.get(0));
		idEstadoSucesor[1]=idEstado[1]+Math.toIntExact(movAux.get(1));
		celdaDestino=(JSONObject)cells.get("("+idEstadoSucesor[0]+", "+idEstadoSucesor[1]+")");

		estadoSucesor.setId_estado(idEstadoSucesor);
		//se obtiene "N", "E", "S", "O" segun posicionVecino del array id_mov
		estadoSucesor.setMov((String)id_mov.get(posicionVecino));
		estadoSucesor.setCosto(Math.toIntExact((long)celdaDestino.get("value"))+1);
		
		return new Sucesor(estadoSucesor.getMov(),estadoSucesor,estadoSucesor.getCosto());
	}
	/**
	 * Este metodo comprueba si el estadoActual pasado por parametro es igual a la celda objetivo
	 * @param estadoActual
	 * @param celdaObjetivo
	 * @return boolean
	 */
	public static boolean funcionObjetivo(Estado estadoActual, int[]celdaObjetivo) {
		int [] estadoCeldaActual=estadoActual.getId_estado();
		boolean esObjetivo=false;

		if(estadoCeldaActual[0]==celdaObjetivo[0]&&estadoCeldaActual[1]==celdaObjetivo[1]) {
			esObjetivo= true;
		}

		return esObjetivo;
	}
	/**
	 * Este metodo calcula la heuristica a partir del estadoActual aplicando la distancia de Manhattan a la celda objetivo
	 * @param celdaObjetivo
	 * @param estadoActual
	 * @return int
	 */
	public static int calcularHeuristica(int[]celdaObjetivo, Estado estadoActual) {
		int [] estadoCeldaActual=estadoActual.getId_estado();

		return (Math.abs(estadoCeldaActual[0]-celdaObjetivo[0])+ Math.abs(estadoCeldaActual[1]-celdaObjetivo[1]));
	}
	/**
	 * Este metodo implementa el algoritmo de busqueda estudiado en clase aplicado a la resolucion del laberinto
	 * @param laberinto
	 * @param estrategia
	 * @param estadoInicial
	 * @param estadoObjetivo
	 * @param nombreLaberinto
	 * @return ArrayList<Nodo>
	 */
	public static ArrayList<Nodo> algoritmoBusqueda(JSONObject laberinto, String estrategia, int[]estadoInicial, int[]estadoObjetivo, String nombreLaberinto) {
		ArrayList<Estado> visitado=new ArrayList<Estado>();
		ArrayList<Nodo> nodosHijo=new ArrayList<Nodo>();
		Frontera frontera=new Frontera();	
		boolean solucion;
		Nodo nodo=new Nodo();
		Estado estadoInicialAux=new Estado("",estadoInicial,0);

		//Nodo inicial
		Nodo nodoInicial=new Nodo();
		nodoInicial=asignarValoresNodo(nodoInicial, null, estadoInicialAux, estadoObjetivo, estrategia);
		frontera.push(nodoInicial);
		solucion=false;

		while(frontera.getSize()!=0 && solucion==false) {
			nodo=frontera.pop();
			if(funcionObjetivo(nodo.getEstadoActual(),estadoObjetivo)) {
				solucion=true;
			}else if(visitado.contains(nodo.getEstadoActual())==false && nodo.getProfundidad()<cotaProfundidad){
				visitado.add(nodo.getEstadoActual());
				nodosHijo=expandirNodo(laberinto,nodo, estrategia, estadoObjetivo);
				for(Nodo nodoHijo:nodosHijo) {
					frontera.push(nodoHijo);
				}
			}
		}

		if(solucion) {
			//se dibuja la imagen de la solucion
			LectorDibujadorLaberinto dibujadorLaberinto=new LectorDibujadorLaberinto(rutaGeneral.concat(nombreLaberinto));
			dibujadorLaberinto.dibujarSolucion(frontera,visitado,caminoSolucion(nodo), heuristica);
			//se devuelve la solucion ordenada de la inicial a la objetivo gracias a caminoSolucion
			return caminoSolucion(nodo); 
		}else {
			return null;
		}
	}
	/**
	 * Este metodo calcula el valor de un nodo pasado por parametro segun la estrategia seleccionada
	 * @param estrategia
	 * @param nodo
	 * @return float
	 */
	public static float calcularValorNodo(String estrategia, Nodo nodo) {
		float valorNodo=0;
		switch(estrategia) {
		case "BREADTH":
			valorNodo=nodo.getProfundidad();
			break;
		case "DEPTH":
			valorNodo=(float)(1.0/(nodo.getProfundidad()+1.0));
			break;
		case "UNIFORM":
			valorNodo=nodo.getCosto();
			break;
		case "GREEDY":
			valorNodo=nodo.getHeuristica();
			break;
		case "A":
			valorNodo=nodo.getCosto()+nodo.getHeuristica();
			break;
		}
		return valorNodo;
	}
	/**
	 * Este metodo crea tantos nodos como sucesores tenga el nodo pasado por parametro y les otorga valores a cada uno de sus
	 * atributos
	 * @param laberinto
	 * @param nodo
	 * @param estrategia
	 * @param estadoObjetivo
	 * @return ArrayList<Nodo>
	 */
	public static ArrayList<Nodo> expandirNodo(JSONObject laberinto, Nodo nodo, String estrategia, int[]estadoObjetivo) {
		ArrayList<Nodo> nodosHijo=new ArrayList<Nodo>();

		for(Sucesor sucesor: funcionSucesor(nodo,laberinto)) {
			Nodo nodoHijo=new Nodo();
			nodoHijo=asignarValoresNodo(nodoHijo, nodo, sucesor.getEstado(), estadoObjetivo, estrategia);
			nodosHijo.add(nodoHijo);
		}

		return nodosHijo;
	}
	/**
	 * Este metodo recorre el camino hacia atras desde el nodo pasado por parametro hasta alcanzar el nodo raiz
	 * @param nodo
	 * @return ArrayList<Nodo>
	 */
	public static ArrayList<Nodo> caminoSolucion(Nodo nodo) {
		Stack<Nodo> nodosSolucion=new Stack<Nodo>();
		ArrayList<Nodo> caminoOrdenado=new ArrayList<Nodo>();
		nodosSolucion.push(nodo);
		//se introducen los nodos en la pila desde el ultimo (objetivo) hasta la raiz
		while(nodo.getNodoPadre()!=null) {
			nodosSolucion.push(nodo.getNodoPadre());
			nodo=nodo.getNodoPadre();
		}
		//se van extrayendo los nodos de la pila, el primero que sale es el raiz y el ultimo, el objetivo, dando el camino ordenado
		while(nodosSolucion.size()!=0) {
			caminoOrdenado.add(nodosSolucion.pop());
		}

		return caminoOrdenado;
	}
	/**
	 * Este metodo imprime la solucion del laberinto recorriendo el arraylist con el conjunto de nodos solucion
	 * @param solucionLaberinto
	 */
	public static void imprimirSolucion(ArrayList<Nodo>solucionLaberinto) {
		for(Nodo nodo: solucionLaberinto) {
			System.out.println(nodo.toString());
		}
	}
	/**
	 * Este metodo asigna todos los valores a un nodo pasado por parametro
	 * @param nodo
	 * @param nodoPadre
	 * @param sucesor
	 * @param estadoObjetivo
	 * @param estrategia
	 * @return Nodo
	 */
	public static Nodo asignarValoresNodo(Nodo nodoActual, Nodo nodoPadre, Estado sucesor, int[] estadoObjetivo, String estrategia) {
		nodoActual.setId(id++);
		nodoActual.setNodoPadre(nodoPadre);
		//sucesor es el objeto estado calculada a partir del estado del padre y sumandole NESO
		nodoActual.setEstadoActual(sucesor);
		//dependiendo si se estan asignando valores al nodo raiz o a los demas
		if(null!=nodoPadre) {
			nodoActual.setCosto(nodoPadre.getCosto()+sucesor.getCosto());
			nodoActual.setProfundidad(nodoPadre.getProfundidad()+1);
		}else {
			nodoActual.setCosto(0);
			nodoActual.setProfundidad(0);
		}
		nodoActual.setAccion(sucesor.getMov());
		nodoActual.setHeuristica(calcularHeuristica(estadoObjetivo,sucesor));
		nodoActual.setValor(calcularValorNodo(estrategia,nodoActual));
		return nodoActual;
	}
}