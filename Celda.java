
package Practica1;


import java.util.Arrays;

public class Celda {
	private int [] posicion;
	private int valor;
	String ultimo_movimiento;
	boolean visitado;
	private boolean [] vecinos= new boolean[4];
	public Celda(int [] pos, int valor) {
		posicion = new int[2];
		visitado= false;
		System.arraycopy(pos,0,posicion,0,pos.length);
		this.valor=valor;
		InicializarVecinos();
		//System.out.println(pos.toString());
	}
	public int getPosicionX() {
		return posicion[0];
	}
	public int getPosicionY() {
		return posicion[1];
	}
	
	public boolean getVisitado() {
		return visitado;
	}
	public void setVisitado(boolean visitado) {
		this.visitado = visitado;
	}
	private void InicializarVecinos() {
		for(int i=0; i<vecinos.length;i++) {
			vecinos[i]=false;
		}
	}
	public int[] getPosicion() {
		return posicion;
	}
	public void setPosicion(int[] posicion) {
		this.posicion = posicion;
	}
	public void setPosicionX(int posicion) {
		this.posicion[0]=posicion;
	}
	
	public void setPosicionY(int posicion) {
		this.posicion[1]=posicion;
	}
	
	public int getValor() {
		return valor;
	}
	public void setValor(int valor) {
		this.valor = valor;
	}
	public boolean[] getVecinos() {
		return vecinos;
	}
	public void setVecinos(int index, boolean pared) {
		this.vecinos[index] = pared;
	}
	
	public void setUltimoMovimiento(String ultimo_movimiento) {
		this.ultimo_movimiento = ultimo_movimiento;
	}
	
	public String getultimoMovimiento() {
		return ultimo_movimiento;
	}

	public String toString() {
		return "Celda [posicion=" + Arrays.toString(posicion) + ", valor=" + valor + ", ultimo_movimiento="
				+ ultimo_movimiento + ", visitado=" + visitado + ", vecinos=" + Arrays.toString(vecinos) + "]";
	}
	
	


}
