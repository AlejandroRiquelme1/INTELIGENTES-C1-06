package Practica1;

import java.util.ArrayList;
import java.util.List;

public class Laberinto {
	private int filas;
	private int columnas;
	private List<Celda> celdas = new ArrayList<Celda>();
	private int[][] mov = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
	private String[] id_mov = { "N", "E", "S", "O" };
	private int max_n= id_mov.length;
	public Laberinto() {
		
	}
	public int getFilas() {
		return filas;
	}
	public void setFilas(int filas) {
		this.filas = filas;
	}
	public int getColumnas() {
		return columnas;
	}
	public void setColumnas(int columnas) {
		this.columnas = columnas;
	}
	public List<Celda> getCeldas() {
		return celdas;
	}
	public void setCeldas(List<Celda> celdas) {
		this.celdas = celdas;
	}
	public int[][] getMov() {
		return mov;
	}
	public void setMov(int[][] mov) {
		this.mov = mov;
	}
	public String[] getId_mov() {
		return id_mov;
	}
	public void setId_mov(String[] id_mov) {
		this.id_mov = id_mov;
	}
	public int getMax_n() {
		return max_n;
	}
	public void setMax_n(int max_n) {
		this.max_n = max_n;
	} 
}
