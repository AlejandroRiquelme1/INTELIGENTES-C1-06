package Practica1;

public class Nodo implements Comparable<Nodo>{
	private int id;
	private Estado estadoActual;
	private int valor;
	private Nodo nodoPadre;
	private int profundidad;
	private int costo;
	private int heuristica;
	private String accion;
	public Nodo(int id,Estado estadoActual, int valor, Nodo nodoPadre, int profundidad, int costo, int heuristica) {
		//this.id++;
		this.id=id;
		this.estadoActual = estadoActual;
		this.valor = valor;//no sabemos si hay que cogerlo de estadoActual o hay que pasarselo
		this.nodoPadre = nodoPadre;
		this.profundidad = profundidad;
		this.costo = costo;
		this.heuristica = heuristica;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String toString() {
		return "["+id+"] [costo: " +costo+ ", estado actual:(" +estadoActual.getId_estado()[0]+ ","+estadoActual.getId_estado()[1]+") accion:" + accion
				+ ", profundidad: " + profundidad + ", heuristica: " + heuristica + ", valor: " + valor + "]";
	}
	/*public String toString() {
		return "["+id+"] [" +costo+ ", " + nodoPadre.getId() + ", " + accion
				+ ", " + profundidad + ", " + heuristica + ", " + valor + "]";
	}*/
	public int getHeuristica() {
		return heuristica;
	}
	public void setHeuristica(int heuristica) {
		this.heuristica = heuristica;
	}
	public Estado getEstadoActual() {
		return estadoActual;
	}
	public void setEstadoActual(Estado estadoActual) {
		this.estadoActual = estadoActual;
	}
	public int getValor() {
		return valor;
	}
	public void setValor(int valor) {
		this.valor = valor;
	}
	public Nodo getNodoPadre() {
		return nodoPadre;
	}
	public void setNodoPadre(Nodo nodoPadre) {
		this.nodoPadre = nodoPadre;
	}
	public int getProfundidad() {
		return profundidad;
	}
	public void setProfundidad(int profundidad) {
		this.profundidad = profundidad;
	}
	public int getCosto() {
		return costo;
	}
	public void setCosto(int costo) {
		this.costo = costo;
	}
	public int compareTo(Nodo nodo) {
		int r=0;
		if(this.getValor()<nodo.getValor()) {
			r=-1;
		}else if(this.getValor()>nodo.getValor()){
			r=1;
		}else {
			if(this.getEstadoActual().getId_estado()[0]<nodo.getEstadoActual().getId_estado()[0]) {
				r=-1;
			}else if(this.getEstadoActual().getId_estado()[0]>nodo.getEstadoActual().getId_estado()[0]){
				r=1;
			}else {
				if(this.getEstadoActual().getId_estado()[1]<nodo.getEstadoActual().getId_estado()[1]) {
					r=-1;
				}else if(this.getEstadoActual().getId_estado()[1]>nodo.getEstadoActual().getId_estado()[1]){
					r=1;
				}
			}
		}
		return r;
	}
}
