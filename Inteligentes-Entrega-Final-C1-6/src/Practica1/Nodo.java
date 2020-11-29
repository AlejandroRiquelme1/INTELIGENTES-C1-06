package Practica1;

import java.text.DecimalFormat;

public class Nodo implements Comparable<Nodo>{
	private long id;
	private Estado estadoActual;
	private float valor;
	private Nodo nodoPadre;
	private int profundidad;
	private int costo;
	private int heuristica;
	private String accion;
	public Nodo(long id,Estado estadoActual, float valor, Nodo nodoPadre, int profundidad, int costo, int heuristica) {
		this.id=id;
		this.estadoActual = estadoActual;
		this.valor = valor;//no sabemos si hay que cogerlo de estadoActual o hay que pasarselo
		this.nodoPadre = nodoPadre;
		this.profundidad = profundidad;
		this.costo = costo;
		this.heuristica = heuristica;
	}
	public Nodo() {

	}
	public String getAccion() {
		return accion;
	}
	public void setAccion(String accion) {
		this.accion = accion;
	}
	public long getId() {
		return id;
	}
	public void setId(long id2) {
		this.id = id2;
	}
	public String toString() {
		if(id==0) {
			return "["+id+"] [costo: " +costo+ ", estado actual:(" +estadoActual.getId_estado()[0]+ 
					","+estadoActual.getId_estado()[1]+"), idPadre: None, accion: None, profundidad: " + profundidad + ", heuristica: " + heuristica + ", valor: " + valor + "]";
		}else {
			return "["+id+"] [costo: " +costo+ ", estado actual:(" +estadoActual.getId_estado()[0]+ 
					","+estadoActual.getId_estado()[1]+"), idPadre:"+nodoPadre.getId()+", accion:" + accion
					+ ", profundidad: " + profundidad + ", heuristica: " + heuristica + ", valor: " + valor + "]";
		}
	}
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
	public float getValor() {
		return valor;
	}
	public void setValor(float f) {
		this.valor = f;
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
				}else {
					if(this.getId()<nodo.getId()) {
						r=-1;
					}else {
						r=1;
					}
				}
			}
		}
		return r;
	}
}
