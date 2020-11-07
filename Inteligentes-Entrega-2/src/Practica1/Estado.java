package Practica1;

public class Estado {
	private String mov;
	private int[] id_estado;
	private int costo;
	public Estado(String mov, int[] id_estado, int costo) {
		this.mov = mov;
		this.id_estado = id_estado;
		this.costo = costo;
	}
	public String getMov() {
		return mov;
	}
	public void setMov(String mov) {
		this.mov = mov;
	}
	public int[] getId_estado() {
		return id_estado;
	}
	public void setId_estado(int[] id_estado) {
		this.id_estado = id_estado;
	}
	public int getCosto() {
		return costo;
	}
	public void setCosto(int costo) {
		this.costo = costo;
	}
}
