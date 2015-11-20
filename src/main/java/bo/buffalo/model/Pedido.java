package bo.buffalo.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="pedido"
    ,schema="public"
)
public class Pedido implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Cliente cliente;
	private Factura factura;
	private Boolean credito;
	private int numeroCuotas;
	private Date fechaEntrega;
	private String observacion;
	private double totalDescuento;
	private double totalSaldo;
	private double totalPagado;
	private double importeTotal;
	private double importeTotalDescuento;
	private String usuarioRegistro;
	private Date fechaRegistro;
	private String estado;

	public Pedido() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	@Column(name="usuario_registro")
	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}


	//bi-directional many-to-one association to Cliente
	@ManyToOne
	@JoinColumn(name="id_cliente", nullable=true)
	public Cliente getCliente() {
		return this.cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@Column(name="numero_cuotas")
	public int getNumeroCuotas() {
		return numeroCuotas;
	}


	public void setNumeroCuotas(int numeroCuotas) {
		this.numeroCuotas = numeroCuotas;
	}

	@Column(name="total_saldo")
	public double getTotalSaldo() {
		return totalSaldo;
	}


	public void setTotalSaldo(double totalSaldo) {
		this.totalSaldo = totalSaldo;
	}

	@Column(name="importe_total")
	public double getImporteTotal() {
		return importeTotal;
	}


	public void setImporteTotal(double importeTotal) {
		this.importeTotal = importeTotal;
	}

	@Column(name="importe_total_descuento")
	public double getImporteTotalDescuento() {
		return importeTotalDescuento;
	}

	public void setImporteTotalDescuento(double importeTotalDescuento) {
		this.importeTotalDescuento = importeTotalDescuento;
	}


	@ManyToOne
	@JoinColumn(name="id_factura", nullable=true)
	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}
	

}
