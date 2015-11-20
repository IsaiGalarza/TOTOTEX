package bo.buffalo.util;

public class EstructuraImpresora {
	
	private int id;
	private String nombre;
	
	//constructor
	public EstructuraImpresora(int id, String nombre) {
		super();
		this.id = id;
		this.nombre = nombre;
	}
	
	//get and set
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
