package bo.buffalo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="proforma_venta_v2"
    ,schema="public"
)
public class ProformaVenta implements Serializable {

	private static final long serialVersionUID = 7968542469145391549L;

	private Integer id;
	private Boolean comisionMedico;
	private Boolean credito;
	private String estado;
	private Date fechaEntrega;
	private Date fechaRegistro;
	private String observacion;
	private double saldoPendiente;
	private double totalDescuento;
	private double totalPagado;
	private double totalPagar;
	private String usuarioRegistro;
	private Cliente cliente;
	private Proforma proforma;
	

	public ProformaVenta() {
		proforma= new Proforma();
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(name="comision_medico")
	public Boolean getComisionMedico() {
		return this.comisionMedico;
	}

	public void setComisionMedico(Boolean comisionMedico) {
		this.comisionMedico = comisionMedico;
	}


	public Boolean getCredito() {
		return this.credito;
	}

	public void setCredito(Boolean credito) {
		this.credito = credito;
	}


	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}


	@Column(name="fecha_entrega")
	public Date getFechaEntrega() {
		return this.fechaEntrega;
	}

	public void setFechaEntrega(Date fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}


	@Column(name="fecha_registro")
	public Date getFechaRegistro() {
		return this.fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}


	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}


	@Column(name="saldo_pendiente")
	public double getSaldoPendiente() {
		return this.saldoPendiente;
	}

	public void setSaldoPendiente(double saldoPendiente) {
		this.saldoPendiente = saldoPendiente;
	}


	@Column(name="total_descuento")
	public double getTotalDescuento() {
		return this.totalDescuento;
	}

	public void setTotalDescuento(double totalDescuento) {
		this.totalDescuento = totalDescuento;
	}


	@Column(name="total_pagado")
	public double getTotalPagado() {
		return this.totalPagado;
	}

	public void setTotalPagado(double totalPagado) {
		this.totalPagado = totalPagado;
	}


	@Column(name="total_pagar")
	public double getTotalPagar() {
		return this.totalPagar;
	}

	public void setTotalPagar(double totalPagar) {
		this.totalPagar = totalPagar;
	}


	@Column(name="usuario_registro")
	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}


	//bi-directional many-to-one association to Cliente
	@ManyToOne
	@JoinColumn(name="id_cliente")
	public Cliente getCliente() {
		return this.cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}





	//bi-directional many-to-one association to Proforma
	@ManyToOne
	@JoinColumn(name="id_proforma")
	public Proforma getProforma() {
		return this.proforma;
	}

	public void setProforma(Proforma proforma) {
		this.proforma = proforma;
	}
	

}
