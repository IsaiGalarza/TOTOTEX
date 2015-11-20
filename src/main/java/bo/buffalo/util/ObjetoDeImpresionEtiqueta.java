package bo.buffalo.util;

import java.awt.*;
import java.awt.print.*;

public class ObjetoDeImpresionEtiqueta implements Printable{
	
	private String nombreCliente;
	private String codigoElaborado;
	private String nombrePreparado;
	private String presentacionPreparado;
	private String doctorPreparado;
	private String usoPreparado;
	private String conservacionPreparado;
	private String fechaElaboracion;
	private String fechaVencimiento;
	
	public int print(Graphics g, PageFormat f, int pageIndex){
		
		Font font = new Font("Arial", Font.PLAIN, 8);
		
		if(pageIndex == 0){
			g.setFont(font);
//			g.create(2, 2, 100, 30);
			g.drawString(nombreCliente, 10, 15);
			g.drawString(codigoElaborado, 170, 15);
			g.drawString(nombrePreparado, 10, 25);
			g.drawString(presentacionPreparado, 170, 25);
			g.drawString(doctorPreparado, 10, 35);
			g.drawString(usoPreparado, 10, 45);
			g.drawString(conservacionPreparado, 170, 45);
			g.drawString(fechaElaboracion, 170, 55);
			g.drawString(fechaVencimiento, 170, 65);
			return PAGE_EXISTS;
		}else{
			return NO_SUCH_PAGE;
		}
	}
	
	//get and set
	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}

	public String getCodigoElaborado() {
		return codigoElaborado;
	}

	public void setCodigoElaborado(String codigoElaborado) {
		this.codigoElaborado = codigoElaborado;
	}

	public String getNombrePreparado() {
		return nombrePreparado;
	}

	public void setNombrePreparado(String nombrePreparado) {
		this.nombrePreparado = nombrePreparado;
	}

	public String getPresentacionPreparado() {
		return presentacionPreparado;
	}

	public void setPresentacionPreparado(String presentacionPreparado) {
		this.presentacionPreparado = presentacionPreparado;
	}

	public String getDoctorPreparado() {
		return doctorPreparado;
	}

	public void setDoctorPreparado(String doctorPreparado) {
		this.doctorPreparado = doctorPreparado;
	}

	public String getUsoPreparado() {
		return usoPreparado;
	}

	public void setUsoPreparado(String usoPreparado) {
		this.usoPreparado = usoPreparado;
	}

	public String getConservacionPreparado() {
		return conservacionPreparado;
	}

	public void setConservacionPreparado(String conservacionPreparado) {
		this.conservacionPreparado = conservacionPreparado;
	}

	public String getFechaElaboracion() {
		return fechaElaboracion;
	}

	public void setFechaElaboracion(String fechaElaboracion) {
		this.fechaElaboracion = fechaElaboracion;
	}

	public String getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(String fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
}
