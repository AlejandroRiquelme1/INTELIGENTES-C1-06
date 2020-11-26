package Practica1;

import java.util.Arrays;

public class Estado {
	private String mov;
	private int[] id_estado;
	private int costo;
	public Estado(String mov, int[] id_estado, int costo) {
		this.mov = mov;
		this.id_estado = id_estado;
		this.costo = costo;
	}
	public Estado() {
	}
	@Override
	public String toString() {
		return "Estado [mov=" + mov + ", id_estado=" + Arrays.toString(id_estado) + ", costo=" + costo + "]";
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
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Estado other = (Estado) obj;
		if (!Arrays.equals(id_estado, other.id_estado))
			return false;
		return true;
	}
}