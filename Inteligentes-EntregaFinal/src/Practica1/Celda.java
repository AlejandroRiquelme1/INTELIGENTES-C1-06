package Practica1;


import java.util.Arrays;

public class Celda {
	private int [] posicion;
	private float valor;
	private String ultimo_movimiento;
	private boolean visitado;
	private boolean [] vecinos= new boolean[4];
	
	public Celda(int [] pos, float valor) {
		posicion = new int[2];
		visitado= false;
		System.arraycopy(pos,0,posicion,0,pos.length);
		this.valor=valor;
		InicializarVecinos();
	}
	public Celda() {
		posicion = new int[2];
		visitado= false;
		InicializarVecinos();
	}
	
	public int[] getPosicion() {
		return posicion;
	}
	
	public void setPosicion(int[] posicion) {
		this.posicion = posicion;
	}
	
	public float getValor() {
		return valor;
	}
	
	public void setValor(float valor) {
		this.valor = valor;
	}
	
	public String getultimoMovimiento() {
		return ultimo_movimiento;
	}
	
	public void setUltimoMovimiento(String ultimo_movimiento) {
		this.ultimo_movimiento = ultimo_movimiento;
	}
	
	public boolean getVisitado() {
		return visitado;
	}
	
	public void setVisitado(boolean visitado) {
		this.visitado = visitado;
	}
	
	public boolean[] getVecinos() {
		return vecinos;
	}
	public void setVecinos(int index, boolean pared) {
		this.vecinos[index] = pared;
	}
	
	public int getPosicionX() {
		return posicion[0];
	}
	
	public void setPosicionX(int posicion) {
		this.posicion[0]=posicion;
	}
	
	public int getPosicionY() {
		return posicion[1];
	}
	
	public void setPosicionY(int posicion) {
		this.posicion[1]=posicion;
	}
	
	public void InicializarVecinos() {
		for(int i=0; i<vecinos.length;i++) {
			vecinos[i]=false;
		}
	}	

	public String toString() {
		return "Celda [posicion=" + Arrays.toString(posicion) + ", valor=" + valor + ", ultimo_movimiento="
				+ ultimo_movimiento + ", visitado=" + visitado + ", vecinos=" + Arrays.toString(vecinos) + "]";
	}
	
}
