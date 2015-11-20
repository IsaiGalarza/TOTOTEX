package bo.buffalo.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="proforma_v2"
    ,schema="public"
)
public class Proforma implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String estado;
	private boolean credito;
	private Date fechaEntrega;
	private Date fechaRegistro;
	private String observacion;
	private String tipoProforma;
	private double total;
	private double totalPagado;
	private double saldo_pendiente;
	private double totalDescuento;
	private String usuarioRegistro;
	private Cliente cliente;


	public Proforma() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	@Column(name="observacion", nullable=true)
	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}


	@Column(name="tipo_proforma")
	public String getTipoProforma() {
		return this.tipoProforma;
	}

	public void setTipoProforma(String tipoProforma) {
		this.tipoProforma = tipoProforma;
	}

	@Column(name="total", precision=10, scale=2)
	public double getTotal() {
		return this.total;
	}

	public void setTotal(double total) {
		this.total = total;
	}


	@Column(name="total_descuento", precision=10, scale=2)
	public double getTotalDescuento() {
		return this.totalDescuento;
	}

	public void setTotalDescuento(double totalDescuento) {
		this.totalDescuento = totalDescuento;
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


	@Column(name="total_pagado", precision=10, scale=2)
	public double getTotalPagado() {
		return totalPagado;
	}

	public void setTotalPagado(double totalPagado) {
		this.totalPagado = totalPagado;
	}

	@Column(name="saldo_pendiente", precision=10, scale=2)
	public double getSaldo_pendiente() {
		return saldo_pendiente;
	}

	public void setSaldo_pendiente(double saldo_pendiente) {
		this.saldo_pendiente = saldo_pendiente;
	}
	
	//bi-directional many-to-one association to DetalleFarmaco

		@Column(name="credito", nullable=true)
		public boolean isCredito() {
			return credito;
		}


		public void setCredito(boolean credito) {
			this.credito = credito;
		}

}