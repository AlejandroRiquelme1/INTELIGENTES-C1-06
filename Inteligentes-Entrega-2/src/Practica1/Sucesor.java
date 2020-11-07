package Practica1;

public class Sucesor {
	private String mov;
	private int costo_mov;
	private Estado estado;
	public Sucesor(String mov, int costo_mov, Estado estado) {
		super();
		this.mov = mov;
		this.costo_mov = costo_mov;
		this.estado = estado;
	}
	public String getMov() {
		return mov;
	}
	public void setMov(String mov) {
		this.mov = mov;
	}
	public int getCosto_mov() {
		return costo_mov;
	}
	public void setCosto_mov(int costo_mov) {
		this.costo_mov = costo_mov;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
}
