package bo.buffalo.structure;

import java.io.Serializable;

import bo.buffalo.model.Roles;

public class EstructuraRoles implements Serializable{

	private static final long serialVersionUID = 4691768718549651388L;
	
	//fields
	private int id;
	private boolean check;
	private Roles rol;
	
	//constructor
	public EstructuraRoles(int id, boolean check, Roles rol) {
		super();
		this.id = id;
		this.check = check;
		this.rol = rol;
	}

	public EstructuraRoles(){
		
	}
	
	//get and set
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public Roles getRol() {
		return rol;
	}
	public void setRol(Roles rol) {
		this.rol = rol;
	}
	
}
