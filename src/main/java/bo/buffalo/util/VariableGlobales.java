package bo.buffalo.util;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import bo.buffalo.model.Usuario;


@Named("variableGlobales")
@ApplicationScoped
public class VariableGlobales implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 87268760946252760L;
	private Date fechaConsulta;

	private boolean diaAperturada = false;
	private boolean cajaAperturada = false;



	public Date getFechaConsulta() {
		return fechaConsulta;
	}

	public void setFechaConsulta(Date fechaConsulta) {
		this.fechaConsulta = fechaConsulta;
	}

	


	public boolean esCajero(Usuario cajeUsuario) {
		if (cajeUsuario.getCargo().getNombre().toUpperCase()
				.startsWith("CAJERO")) {
			System.out.println("CAJERO");
			return true;
		} else {
			System.out.println("NO CAJERO");
			return false;
		}
	}



	public boolean isDiaAperturada() {
		return diaAperturada;
	}

	public void setDiaAperturada(boolean diaAperturada) {
		this.diaAperturada = diaAperturada;
	}

	public boolean isCajaAperturada() {
		return cajaAperturada;
	}

	public void setCajaAperturada(boolean cajaAperturada) {
		this.cajaAperturada = cajaAperturada;
	}

}