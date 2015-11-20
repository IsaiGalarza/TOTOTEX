/*
 * FacturacionController.java	1.0 2014/09/19
 *
 * Copyright (c) 2014 by QBIT SRL, Inc. All Rights Reserved.
 * 
 * QBIT SRL grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to QBIT SRL.
 * 
 * Autor: Isai Galarza
 * 
 */
package bo.buffalo.controller;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Finishings;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.Sides;
import javax.servlet.http.HttpServletRequest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.ClienteRepository;
import bo.buffalo.data.DetalleFacturaRepository;
import bo.buffalo.data.DetalleServicioRepository;
import bo.buffalo.data.FacturaRepository;
import bo.buffalo.data.NITClienteRepository;
import bo.buffalo.data.NotaVentaRepository;
import bo.buffalo.data.ProductoRepository;
import bo.buffalo.data.ProformaRepository;
import bo.buffalo.data.ProformaVentaRepository;
import bo.buffalo.data.ServicioRepository;
import bo.buffalo.data.SucursalRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Cliente;
import bo.buffalo.model.DetalleFactura;
import bo.buffalo.model.DetalleServicio;
import bo.buffalo.model.Dosificacion;
import bo.buffalo.model.Factura;
import bo.buffalo.model.NitCliente;
import bo.buffalo.model.NotaVenta;
import bo.buffalo.model.Producto;
import bo.buffalo.model.Proforma;
import bo.buffalo.model.Servicio;
import bo.buffalo.model.Sucursal;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.ClienteRegistration;
import bo.buffalo.service.FacturaRegistration;
import bo.buffalo.service.NITClienteRegistration;
import bo.buffalo.service.ProformaRegistration;
import bo.buffalo.service.SucursalRegistration;
import bo.buffalo.util.CodigoControl7;
import bo.buffalo.util.EstructuraConcepto;
import bo.buffalo.util.EstructuraDetallePedido;
import bo.buffalo.util.EstructuraImpresora;
import bo.buffalo.util.EstructuraProducto;
import bo.buffalo.util.EstructuraServicio;
import bo.buffalo.util.MSAccessPedido;
import bo.buffalo.util.NumerosToLetras;
import bo.buffalo.util.UltimosPedidos;

@Named(value = "facturacionController")
@ConversationScoped
public class FacturacionController implements Serializable {

	private static final long serialVersionUID = -870270122853609604L;

	@Inject
	Conversation conversation;

	@Inject
	private FacesContext facesContext;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private EntityManager em;

	@Inject
	private NITClienteRegistration nitClienteRegistration;

	@Inject
	private FacturaRegistration facturaRegistration;

	@Inject
	private ProformaRegistration proformaRegistration;

	@Inject
	private SucursalRegistration sucursalRegistration;

	@Inject
	private ClienteRegistration clienteRegistration;


	@Inject
	private DetalleServicioRepository detalleServicioRepository;


	@Inject
	NITClienteRepository nitClienteRepository;

	@Inject
	ServicioRepository servicioRepository;

	@Inject
	ProductoRepository productoRepository;

	@Inject
	UsuarioRepository usuarioRepository;

	@Inject
	ClienteRepository clienteRepository;

	@Inject
	FacturaRepository facturaRepository;

	@Inject
	UltimosPedidos ultimosPedidos;

	@Inject
	DetalleFacturaRepository detalleFacturaRepository;
	
	private @Inject SucursalRepository sucursalRepository;

	// @Inject
	// UltimosPedidos ultimosPedidosJob;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEvent;

	private String notaVenta;
	private String descripcionProducto;
	private String descripcionServicio;
	private String proformaVentaCliente;

	private int conceptoID;
	private EstructuraConcepto selectedConcepto = new EstructuraConcepto();
	private EstructuraConcepto newConcepto;
	private List<EstructuraConcepto> listaConcepto = new ArrayList<EstructuraConcepto>();

	private List<EstructuraProducto> listaProductoModal = new ArrayList<EstructuraProducto>();
	private List<EstructuraDetallePedido> listaPedido = new ArrayList<EstructuraDetallePedido>();

	private EstructuraDetallePedido selectNotaVenta;
	private List<EstructuraDetallePedido> listaUltimosPedidoModal = new ArrayList<EstructuraDetallePedido>();
	private List<EstructuraDetallePedido> listaPedidoModal = new ArrayList<EstructuraDetallePedido>();
	private List<EstructuraServicio> listaServicioModal = new ArrayList<EstructuraServicio>();

	// Modal Proformas
	@Inject
	private ProformaRepository proformaRepository;
	private Proforma selectProforma;
	private List<Proforma> listaModalProformas = new ArrayList<Proforma>();


	// DETALLE SERVICIO
	private List<DetalleServicio> listaDetalleServicios = new ArrayList<DetalleServicio>();

	// Modal Proformas Venta
	@Inject
	private ProformaVentaRepository proformaVentaRepository;
	private String criterioBusquedaPV = "";
	private List<Proforma> listaModalProformasVenta = new ArrayList<Proforma>();
	private Proforma selectedProformaVenta;
	private double montoPagar;

	// Modal Buscar Factura
	private int facturaID;
	private String criterioBusquedaFactura;
	private Factura selectedFactura;
	private List<Factura> listaUltimasFacturas = new ArrayList<Factura>();
	private List<DetalleFactura> listaDetalleFactura = new ArrayList<DetalleFactura>();

	// Modal Seleccionar Impresora
	private String selectedImpresora;
	private List<EstructuraImpresora> listaImpresora = new ArrayList<EstructuraImpresora>();

	private boolean pagoEfectivo = true;
	private boolean pagoTarjeta = false;
	private double tipoCambio = 6.96;
	private double totalDolares;
	private double totalBolivianos;
	private double totalEntregado;
	private String totalEntregadoString = "";
	private double totalEntregadoDolares;
	private String totalEntregadoDolaresString = "";
	private double totalEntregadoDebito;
	private String totalCambioString;
	private double totalCambio;
	private String cadenaQR;
	private String urlCodeQR;

	private Factura newFactura;
	private Factura factura;
	private Usuario usuarioSession;
	boolean resulConcatenarCodigoRespuestaRapida = false;

	// Impresion
	private boolean showButtonImpresion;

	// Vista Previa Factura
	private String urlFactura;

	private String urlNotaVenta;

	public String getUrlFactura() {
		return urlFactura;
	}

	public void setUrlFactura(String urlFactura) {
		this.urlFactura = urlFactura;
	}

	/**
	 * Define el Contructor del Controlador initFactura(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	@PostConstruct
	public void initFacturacion() {
		try {
			// initConversation();
			beginConversation();

			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			System.out
					.println("initFacturacion*********************************");
			System.out.println("request.getClass().getName():"
					+ request.getClass().getName());
			System.out.println("isVentas:" + request.isUserInRole("ventas"));
			System.out.println("remoteUser:" + request.getRemoteUser());
			System.out.println("userPrincipalName:"
					+ (request.getUserPrincipal() == null ? "null" : request
							.getUserPrincipal().getName()));
			System.out
					.println("initFacturacion*********************************");

			usuarioSession = usuarioRepository.findByLogin(request
					.getUserPrincipal().getName());
			System.out.println("Sucursal Usuario: "
					+ usuarioSession.getSucursal().getNombre());
			newFactura = new Factura();
			listaConcepto.clear();

			pagoEfectivo = true;
			pagoTarjeta = false;

			showButtonImpresion = false;
			criterioBusquedaPV = "";

			// traerUltimosPedidos();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en initFacturacion: " + e.getMessage());
		}
	}

	public void beginConversation() {

		if (conversation.isTransient()) {
			System.out.println("beginning conversation : " + this.conversation);
			conversation.begin();
			System.out.println("---> Init Conversation");
		}
	}

	public void endConversation() {
		if (!conversation.isTransient()) {
			conversation.end();
		}
	}

	/**
	 * Instancia una nueva factura para procesar nuevaFactura(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void nuevaFactura() {
		try {
			System.out
					.println("Ingreso a Limpiar Formulario --- > Nueva Factura");
			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			usuarioSession = usuarioRepository.findByLogin(request
					.getUserPrincipal().getName());
			System.out.println("Sucursal Usuario: "
					+ usuarioSession.getSucursal().getNombre());

			newFactura = new Factura();
			listaConcepto.clear();

			pagoEfectivo = true;
			pagoTarjeta = false;

			showButtonImpresion = false;
			traerUltimosPedidos();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en nuevaFactura: " + e.getMessage());
		}
	}

	/**
	 * Comparamos 2 Fechas
	 * 
	 * @author Isai Galarza
	 * @param fechaLimite
	 *            Emision
	 * @param fechaActual
	 * @return
	 */
	private boolean puedoFacturarFechaLimite(Date fechaLimiteEmision,
			Date fechaActual) {
		System.out.println("Parametro fechaLimiteEmision = "
				+ fechaLimiteEmision + "\n" + "Parametro fechaActual = "
				+ fechaActual + "\n");
		try {
			String DATE_FORMAT = "yyyyMMdd";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			String cadenaFechaLimiteEmision = sdf.format(fechaLimiteEmision);
			String cadenaFechaActual = sdf.format(fechaActual);
			System.out.println("cadenaFechaLimiteEmision: "
					+ cadenaFechaLimiteEmision);
			System.out.println("cadenaFechaActual: " + cadenaFechaActual);

			if (Integer.valueOf(cadenaFechaLimiteEmision) >= Integer
					.valueOf(cadenaFechaActual)) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			System.out
					.println("Se Produjo un Error puedoFacturarFechaLimite:  "
							+ e.getMessage());
		}
		return false;
	}

	/**
	 * Define el registro de la factura y su detalle registrarFactura(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */

	public void registrarFactura() throws Exception {
		try {
			Dosificacion dosificacionActiva=sucursalRepository.dosificacionActiva(usuarioSession
					.getSucursal());
			boolean puedoFacturar=false;
			if(dosificacionActiva!=null){
				puedoFacturar = puedoFacturarFechaLimite(dosificacionActiva.getFechaLimiteEmision(), new Date());
			}
			
			System.out.println("Puedo Facturar: " + puedoFacturar);
			if (puedoFacturar) {

				Date fechaFactura = new Date();
				String CC = obtenerCodigoControl(fechaFactura);
				System.out.println("Codigo Control: " + CC);

				if (CC.length() == 14 || CC.length() == 11) {
					cargarDatosFactura(CC, fechaFactura);

					if (resulConcatenarCodigoRespuestaRapida) {

						// Registrar Factura

						facturaRegistration.register(newFactura, listaConcepto);

						System.out.println("===== FACTURA PROCESADA ====== OK");
						FacesMessage m = new FacesMessage(
								FacesMessage.SEVERITY_INFO, "Factura #: "
										+ dosificacionActiva.getNumeroSecuencia(),
								"Registrada Correctamente!");
						facesContext.addMessage(null, m);

						// Actualizar Datos de la Proforma Venta
						double totalPagado = selectedProformaVenta
								.getTotalPagado()
								+ newFactura.getTotalFacturado();
						double saldoPendiente = selectedProformaVenta
								.getTotal() - totalPagado;
						selectedProformaVenta.setTotalPagado(totalPagado);
						selectedProformaVenta.setSaldo_pendiente(saldoPendiente);
						
						if(saldoPendiente<=0){
							selectedProformaVenta.setEstado("FA");
														
						}
						
						proformaRegistration.updated(selectedProformaVenta);
						System.out.println("===== PROFORMA VENTA ACTUALIZADA ====== OK");

						// increment next numberFactura
						incrementarSecuencialDosificacion();
						showButtonImpresion = true;

					} else {
						FacesMessage msg = new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Error, Codigo Respuesta Rapida.",
								"Consulte con el Administrador.");
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				} else {
					FacesMessage msg = new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Codigo Control Invalido: ", CC);
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			} else {
				if(dosificacionActiva!=null){
				     FacesMessage msg = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Error Dosificacion: ",
						"Fecha Limite de Emision a Caducado: "
								+ dosificacionActiva.getFechaLimiteEmision());
				     FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					FacesMessage msg = new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Error Dosificacion: ",
							"No tiene dosificaciones activas");
					     FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error en registrarFactura: " + e.getMessage());
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registration unsuccessful");
			facesContext.addMessage(null, m);
		} finally {
			// registrar historial transaccion
			registrarHistorialNitCliente(newFactura);
			limpiarValoresCambio();
		}
	}

	public void registrarFacturaConcepto() throws Exception {
		try {

			Dosificacion dosificacionActiva=sucursalRepository.dosificacionActiva(usuarioSession
					.getSucursal());
			boolean puedoFacturar=false;
			if(dosificacionActiva!=null){
				puedoFacturar = puedoFacturarFechaLimite(dosificacionActiva.getFechaLimiteEmision(), new Date());
			}
			
			System.out.println("Puedo Facturar: " + puedoFacturar);
			if (puedoFacturar) {

				Date fechaFactura = new Date();
				String CC = obtenerCodigoControl(fechaFactura);
				System.out.println("Codigo Control: " + CC);
				if (CC.length() == 14 || CC.length() == 11) {
					cargarDatosFactura(CC, fechaFactura);

					if (resulConcatenarCodigoRespuestaRapida) {

						// Registrar Factura
						facturaRegistration.register(newFactura, listaConcepto);

						System.out.println("===== FACTURA PROCESADA ====== OK");
						FacesMessage m = new FacesMessage(
								FacesMessage.SEVERITY_INFO, "Factura #: "
										+ dosificacionActiva.getNumeroSecuencia(),
								"Registrada Correctamente!");
						facesContext.addMessage(null, m);

						// increment next numberFactura
						incrementarSecuencialDosificacion();
						showButtonImpresion = true;

					} else {
						FacesMessage msg = new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Error, Codigo Respuesta Rapida.",
								"Consulte con el Administrador.");
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				} else {
					FacesMessage msg = new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Codigo Control Invalido: ", CC);
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			} else {
				FacesMessage msg = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Error Dosificacion: ",
						"Fecha Limite de Emision a Caducado: "
								+ dosificacionActiva.getFechaLimiteEmision());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error en registrarFactura: " + e.getMessage());
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registration unsuccessful");
			facesContext.addMessage(null, m);
		} finally {
			// registrar historial transaccion
			registrarHistorialNitCliente(newFactura);
			limpiarValoresCambio();
		}
	}

	/**
	 * Metodo para anular una Factura anularFactura(). <br>
	 * 
	 * @param none
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void anularFactura() {
		try {
			System.out.println("Ingreso a Anular Factura...");
			facturaRegistration.anularFactura(newFactura);
			// FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
			// "Factura #: "+usuarioSession.getSucursal().getNumeroSecuencial(),
			// "Anulada Correctamente!");
			// facesContext.addMessage(null, m);
			showButtonImpresion = true;
			System.out.println("Anular Factura OK!");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error en anularFactura: " + e.getMessage());
			// String errorMessage = getRootErrorMessage(e);
			// FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
			// errorMessage, "Error al Anular Factura");
			// facesContext.addMessage(null, m);
		}
	}

	/**
	 * Limpiar Valores de Pago, Cambio, Otro limpiarValoresCambio(). <br>
	 * 
	 * @param none
	 * @returns none
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void limpiarValoresCambio() {
		try {
			System.out.println("Ingreso a limpiarValoresCambio...");
			totalDolares = 0;
			totalBolivianos = 0;
			totalEntregado = 0;
			totalEntregadoDolares = 0;
			totalEntregadoDebito = 0;
			totalCambio = 0;
			totalCambioString = null;
			totalEntregadoString = "";
			totalEntregadoDolaresString = "";

		} catch (Exception e) {
			System.out.println("Error en limpiarValoresCambio: "
					+ e.getMessage());
		}
	}

	/**
	 * Registrar Transaccion NIT Cliente Historial
	 * registrarHistorialNitCliente(). <br>
	 * 
	 * @param Factura
	 * @returns none
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void registrarHistorialNitCliente(Factura factura) {
		try {
			System.out.println("Ingreso a registrarNitCliente...nit: "
					+ factura.getNitCi());
			NitCliente nitCliente = nitClienteRepository.findNit(factura
					.getNitCi());

			if (nitCliente != null) {
				// update nitCliente
				System.out
						.println("Ingreso a Actualizar Historia NIT Cliente..."
								+ factura.getNitCi());
				nitCliente.setNombreFactura(factura.getNombreFactura());
				nitCliente.setNitCi(factura.getNitCi());
				nitCliente.setEstado("AC");
				nitCliente.setFechaRegistro(new Date());
				nitCliente.setUsuarioRegistro(usuarioSession.getLogin());
				nitClienteRegistration.updated(nitCliente);

			} else {
				// register nitCliente
				System.out
						.println("Ingreso a Registrar Historia NIT Cliente..."
								+ factura.getNitCi());
				Cliente newCliente = new Cliente(factura.getNitCi(),
						factura.getNombreFactura());
				clienteRegistration.register(newCliente);

				Cliente cliente = clienteRepository
						.findById(factura.getNitCi());
				if (cliente != null) {
					nitCliente = new NitCliente();
					nitCliente.setNombreFactura(factura.getNombreFactura());
					nitCliente.setNitCi(factura.getNitCi());
					nitCliente.setCliente(cliente);
					nitCliente.setEstado("AC");
					nitCliente.setFechaRegistro(new Date());
					nitCliente.setUsuarioRegistro(usuarioSession.getLogin());

					nitClienteRegistration.register(nitCliente);

				} else {
					System.out
							.println("Cliente no Existe para Registrar Historial NIT Cliente...");
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en registrarNitCliente: "
					+ e.getMessage());
		}
	}

	/**
	 * Buscar Todos los Productos Activos <br>
	 * 
	 * @param none
	 * @returns none
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public List<EstructuraProducto> traerProductos() {
		try {
			List<EstructuraProducto> listaProducto = new ArrayList<EstructuraProducto>();

			// List<Producto> resultadoConsulta =
			// productoRepository.traerProductos();
			// for(Producto producto : resultadoConsulta){
			// listaProducto.add(new
			// EstructuraProducto(producto.getCodigoProducto(), 0, null,
			// producto.getCodigoLinea(), String.valueOf(producto.getId()),
			// producto.getNombreProducto(), producto.getNombreProducto(),
			// producto.getNombreProducto(), null, null, null,
			// String.valueOf(producto.getStockActual()),
			// producto.getStockMinimo(), producto.getStockMaximo(), false,
			// producto.getPrecioUnitarioVenta(),
			// producto.getPrecioUnitarioCompra(), 0,
			// 0, 0, false, producto.getFechaVencimiento().toString(), true,
			// null, producto.getUsuarioRegistro(),
			// producto.getFechaRegistro().toString(),
			// producto.getCodigoBarra(), null, null, 0, 0, 0,
			// 0, null, 0, null, null, null, 1, false));
			//
			// }

			return listaProducto;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en traerProductos: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Remueve un Item Concepto de la lista que esta Seleccionado
	 * quitarSeleccionadoListaConcepto(). <br>
	 * 
	 * @param none
	 * @returns none
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void quitarSeleccionadoListaConcepto() {
		try {
			System.out.println("Ingreso a : quitarSeleccionadoListaConcepto: "
					+ this.getConceptoID());
			System.out.println("Size antes: " + listaConcepto.size());

			if (this.getConceptoID() > 0) {
				for (int i = 0; i < listaConcepto.size(); i++) {
					if (listaConcepto.get(i).getId() == this.getConceptoID()) {
						String descripcionConcepto = listaConcepto.get(i)
								.getConcepto();
						listaConcepto.remove(listaConcepto.get(i));
						this.setConceptoID(0);

						System.out.println("Size despues: "
								+ listaConcepto.size());
						FacesMessage m = new FacesMessage(
								FacesMessage.SEVERITY_INFO, "Item: "
										+ descripcionConcepto,
								"Removido Correctamente!");
						facesContext.addMessage(null, m);

						return;
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en quitarSeleccionadoListaConcepto: "
					+ e.getMessage());
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Error al quitar Item.");
			facesContext.addMessage(null, m);
		}
	}

	/**
	 * Obtiene el formato concatenado del codigo de respuesta rapida para el QR
	 * armarCadenaQR(). <br>
	 * 
	 * @param none
	 * @returns boolean
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public boolean armarCadenaQR() {
		try {
			System.out.println("Ingreso a generar cadena QR....");
			Sucursal sucursal = em.find(Sucursal.class, usuarioSession
					.getSucursal().getId());
			System.out.println("Sucursal: ");
			Dosificacion dosificacionActiva=sucursalRepository.dosificacionActiva(usuarioSession
					.getSucursal());
			cadenaQR = new String();

			// NIT emisor
			cadenaQR = cadenaQR.concat(sucursal.getNit());
			cadenaQR = cadenaQR.concat("|");

			// Numero de Factura
			cadenaQR = cadenaQR.concat(newFactura.getNumeroFactura());
			cadenaQR = cadenaQR.concat("|");

			// Numero de Autorizacion
			cadenaQR = cadenaQR.concat(dosificacionActiva.getNumeroAutorizacion());
			cadenaQR = cadenaQR.concat("|");

			// Fecha de Emision
			cadenaQR = cadenaQR.concat(obtenerFechaEmision(newFactura
					.getFechaFactura()));
			cadenaQR = cadenaQR.concat("|");

			// Total Bs
			cadenaQR = cadenaQR.concat(String.valueOf(newFactura
					.getTotalFacturado()));
			cadenaQR = cadenaQR.concat("|");

			// Importe Base para el Credito Fiscal
			cadenaQR = cadenaQR.concat(String.valueOf(newFactura
					.getTotalFacturado()));
			cadenaQR = cadenaQR.concat("|");

			// Codigo de Control
			cadenaQR = cadenaQR.concat(newFactura.getCodigoControl());
			cadenaQR = cadenaQR.concat("|");

			// NIT / CI del Comprador
			cadenaQR = cadenaQR.concat(newFactura.getNitCi());
			cadenaQR = cadenaQR.concat("|");

			// Importe ICE/IEHD/TASAS [cuando corresponda]
			cadenaQR = cadenaQR.concat("0");
			cadenaQR = cadenaQR.concat("|");

			// Importe por ventas no Gravadas o Gravadas a Tasa Cero [cuando
			// corresponda]
			cadenaQR = cadenaQR.concat("0");
			cadenaQR = cadenaQR.concat("|");

			// Importe no Sujeto a Credito Fiscal [cuando corresponda]
			cadenaQR = cadenaQR.concat("0");
			cadenaQR = cadenaQR.concat("|");

			// Descuentos Bonificaciones y Rebajas Obtenidas [cuando
			// corresponda]
			cadenaQR = cadenaQR.concat("0");

			System.out.println("cadena QR generada: " + cadenaQR);

			return true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en armarCadenaQR: " + e.getMessage());
			cadenaQR = new String();
			return false;
		}
	}

	/**
	 * Obtiene el formado adecuado para la Fecha Emision de Factura
	 * obtenerFechaEmision(). <br>
	 * 
	 * @param Date
	 *            fechaEmision
	 * @returns String dd/MM/yyyy
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public String obtenerFechaEmision(Date fechaEmision) {
		try {
			String DATE_FORMAT = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			return sdf.format(fechaEmision);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en obtenerFechaEmision: "
					+ e.getMessage());
			return "Error Fecha Emision";
		}
	}

	/**
	 * Incrementa el Numero de Factura como Secuencial de la Dosificacion
	 * incrementarSecuencialDosificacion(). <br>
	 * 
	 * @param none
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void incrementarSecuencialDosificacion() {
		try {
			Sucursal sucursal = em.find(Sucursal.class, usuarioSession
					.getSucursal().getId());
			Dosificacion dosificacionActiva=sucursalRepository.dosificacionActiva(usuarioSession
					.getSucursal());
			int secuencial = dosificacionActiva.getNumeroSecuencia() + 1;
			dosificacionActiva.setNumeroSecuencia(secuencial);
			List<Dosificacion> dosificaciones=new ArrayList<>();
			dosificaciones.add(dosificacionActiva);
			sucursalRegistration.updated(sucursal,dosificaciones);
			System.out.println("Registro Secuencia Factura Correcto: Next: "
					+ secuencial);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en incrementarSecuencialDosificacion: "
					+ e.getMessage());
		}
	}

	/**
	 * Carga los datos de la factura cargarDatosFactura(). <br>
	 * 
	 * @param Stirng
	 *            CC, Date fechaFactura
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void cargarDatosFactura(String CC, Date fechaFactura) {
		try {
			int numeroFactura = traerSecuencialNumeroFactura();
			System.out.println("Factura Numero #: " + numeroFactura);
			Dosificacion dosificacionActiva=sucursalRepository.dosificacionActiva(usuarioSession
					.getSucursal());
			newFactura.setNumeroFactura(String.valueOf(numeroFactura)); // Numero
																		// Factura
			newFactura.setConcepto("Venta: " + numeroFactura); // Concepto o
																// Fecha Emision
																// de la Factura
			newFactura.setFechaFactura(fechaFactura); // Fecha Factura
			newFactura.setCodigoControl(CC); // Codigo de Contro
			newFactura.setNumeroAutorizacion(dosificacionActiva.getNumeroAutorizacion()); // Numero Autorizacion

			newFactura.setTipoPago("EFECTIVO");
			newFactura.setTotalEfectivo(totalEntregado); // Total Entregado BS
			newFactura.setTotalEfectivoDolares(totalEntregadoDolares); // Total
																		// Entregado
																		// Dolares
			newFactura.setTipoCambio(tipoCambio); // Tipo Cambio
			newFactura.setTotalPagar(totalBolivianos); // Total Cobrar
			newFactura.setTotalPagoDebito(totalEntregadoDebito); // Total Pago
																	// Debito
			newFactura.setCambio(totalCambio); // Total Cambio
			newFactura.setTotalFacturado(totalBolivianos); // Total Facturado
			newFactura.setTotalLiteral(obtenerMontoLiteral(totalBolivianos)); // Total
																				// Literal
			newFactura.setFechaLimiteEmision(dosificacionActiva.getFechaLimiteEmision()); // Fecha Limite Emision
			newFactura.setIdSucursal(usuarioSession.getSucursal().getId()); // ID
																			// Sucursal
			newFactura.setIdUsuario(usuarioSession.getId()); // ID Usuario
			newFactura.setEstado("V"); // Estado
			newFactura.setFechaRegistro(new Date()); // Fecha Registro
			newFactura.setUsuarioRegistro(usuarioSession.getLogin()); // Usuario
																		// Registro

			// PARA LIBRO DE VENTAS IVA

			// subtotal E = A-B-C-D
			double subtotal = newFactura.getTotalFacturado()
					- newFactura.getImporteICE()
					- newFactura.getImporteExportaciones()
					- newFactura.getImporteVentasGrabadasTasaCero();
			newFactura.setImporteSubTotal(subtotal);

			// Importe Base G = E-F
			double importeBase = subtotal
					- newFactura.getImporteDescuentosBonificaciones();
			newFactura.setImporteBaseDebitoFiscal(importeBase);

			// Debito Fiscal H = G*13%
			double debitoFiscal = importeBase * 0.13;
			newFactura.setDebitoFiscal(debitoFiscal);

			// Mes y Gestion
			String mes = obtenerMes(fechaFactura);
			String gestion = obtenerGestion(fechaFactura);
			System.out.println("Fecha Factura: " + fechaFactura);
			System.out.println("Mes :" + mes + ", Gestion: " + gestion
					+ ", Factura: " + newFactura.getCodigoControl());
			newFactura.setMes(mes);
			newFactura.setGestion(gestion);

			resulConcatenarCodigoRespuestaRapida = armarCadenaQR();
			if (resulConcatenarCodigoRespuestaRapida) {
				newFactura.setCodigoRespuestaRapida(this.getCadenaQR());
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out
					.println("Error en cargarDatosFactura: " + e.getMessage());
		}
	}

	public String obtenerMes(Date fecha) {
		try {
			String DATE_FORMAT = "MM";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			return sdf.format(fecha);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en obtenerMes: " + e.getMessage());
			return null;
		}
	}

	public String obtenerGestion(Date fecha) {
		try {
			String DATE_FORMAT = "yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			return sdf.format(fecha);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en obtenerGestion: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Retorna el Secuencial del Siguiente Numero de Factura
	 * traerSecuencialNumeroFactura(). <br>
	 * 
	 * @param none
	 * @returns Integer numeroSecuencial
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public int traerSecuencialNumeroFactura() {
		try {
			Dosificacion dosificacionActiva=sucursalRepository.dosificacionActiva(usuarioSession
					.getSucursal());
			return dosificacionActiva.getNumeroSecuencia();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerSecuencialNumeroFactura: "
					+ e.getMessage());
			return 0;
		}
	}

	/**
	 * Retorna el monto literal de la factura obtenerMontoLiteral(). <br>
	 * 
	 * @param double totalFactura
	 * @returns String totalLiteral.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public String obtenerMontoLiteral(double totalFactura) {
		System.out.println("Total Entero Factura >>>>> " + totalFactura);
		String totalLiteral;
		try {
			totalLiteral = NumerosToLetras.convertNumberToLetter(totalFactura);
			return totalLiteral;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en obtenerMontoLiteral: "
					+ e.getMessage());
			return "Error Literal";
		}
	}

	/**
	 * Metodo que genera el Codigo de Control obtenerCodigoControl(). <br>
	 * 
	 * @param Date
	 *            fechaFactura
	 * @returns String codigoControl.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public String obtenerCodigoControl(Date fechaFactura) {
		try {
			System.out.println("Certificar Codigo Control... ");
			Dosificacion dosificacionActiva=sucursalRepository.dosificacionActiva(usuarioSession
					.getSucursal());
			
			CodigoControl7 cc = new CodigoControl7();
			int montoFactura = (int) totalBolivianos;

			cc.setNumeroAutorizacion(dosificacionActiva.getNumeroAutorizacion());
			cc.setNumeroFactura(dosificacionActiva.getNumeroSecuencia());
			cc.setNitci(newFactura.getNitCi());
			cc.setFechaTransaccion(fechaFactura);
			cc.setMonto(montoFactura);
			cc.setLlaveDosificacion(dosificacionActiva.getLlaveControl());

			// Obtener Codigo Control V7
			String codigoControlV7 = cc.obtener();
			System.out.println("Codigo Control V7: " + codigoControlV7);
			return codigoControlV7;

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error al generarCodigoControlV7: "
					+ e.getMessage());
			return "Error CC";
		}

		// CodigoControlServicio cc = new CodigoControlServicio();
		// String codigoControl;
		// try {
		// int montoFactura = (int) totalBolivianos;
		// System.out.println("Monto Factura: "+montoFactura);
		// codigoControl =
		// cc.generaCC(usuarioSession.getSucursal().getNumeroAutorizacion(),
		// String.valueOf(usuarioSession.getSucursal().getNumeroSecuencial()),
		// newFactura.getNitCi(),
		// obtenerFechaFactura(),
		// String.valueOf(montoFactura),
		// String.valueOf(usuarioSession.getSucursal().getLlaveControl()));
		//
		// return codigoControl;
		//
		// } catch (Exception e) {
		// // TODO: handle exception
		// e.printStackTrace();
		// System.out.println("Error en obtenerCodigoControl: "+e.getMessage());
		// return "Error CC";
		// }
	}

	/**
	 * Retorna la fecha en formato YYYYMMDD obtenerFechaFactura(). <br>
	 * 
	 * @param None
	 * @returns fechaFactura.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public String obtenerFechaFactura() {
		try {
			String DATE_FORMAT = "yyyy/MM/dd";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			Calendar c1 = Calendar.getInstance(); // today
			return sdf.format(c1.getTime());
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en obtenerFechaFactura: "
					+ e.getMessage());
			return "Error Fecha";
		}
	}

	/**
	 * Define la busqueda del Pedido buscarPedido(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void buscarPedido() {
		try {
			System.out.println("Ingreso a Buscar Pedido: "
					+ this.getNotaVenta());
			MSAccessPedido access = new MSAccessPedido();
			listaUltimosPedidoModal.clear();
			listaPedidoModal.clear();

			if (!this.getNotaVenta().isEmpty()) {
				// listaPedidoModal =
				// access.buscarDetalleNotaVenta(this.getNotaVenta());
				System.out.println("Filtro: " + this.getNotaVenta());
				listaUltimosPedidoModal = access.buscarNotasVentaConcat(this
						.getNotaVenta());
			} else {
				System.out.println("Filtro Vacio: " + this.getNotaVenta());
				listaUltimosPedidoModal = access.traerUltimasNotasVenta();
			}
		} catch (Exception e) {
			// TODO: handle exception
			listaPedidoModal.clear();
			listaUltimosPedidoModal.clear();
			System.err.println("Error en buscarPedido: " + e.getMessage());
		}
	}


	// criterioBusquedaPV
	// listaModalProformasVenta
	// selectedProformaVenta

	public void cargarProformasVenta() {
		try {
			System.out.println("Ingreso a cargarProformasVenta de Venta...");
			selectedProformaVenta = null;
			listaConcepto.clear();
			listaModalProformasVenta.clear();

			listaModalProformasVenta = proformaVentaRepository
					.traerProformaVentaPendientes();

			if (!listaModalProformasVenta.isEmpty()) {
				selectedProformaVenta = listaModalProformasVenta.get(0);

				this.setMontoPagar(selectedProformaVenta.getTotal() - selectedProformaVenta.getTotalPagado());

			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en cargarProformasVenta: "
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			criterioBusquedaPV = "";
		}
	}

	/**
	 * Define la busqueda del Pedido buscarPedido(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void buscarProforma() {
		try {
			System.out.println("Ingreso a Buscar Proforma: "
					+ this.getProformaVentaCliente());
			listaModalProformas.clear();

			if (!this.getProformaVentaCliente().isEmpty()) {
				System.out.println("Filtro: " + this.getProformaVentaCliente());
				listaModalProformas = proformaRepository
						.buscarProformaCliente(this.getProformaVentaCliente()
								.toUpperCase());
			} else {
				System.out.println("Filtro Vacio: "
						+ this.getProformaVentaCliente());
				listaModalProformas = proformaRepository
						.traerProformasProcesadas();
			}
		} catch (Exception e) {
			// TODO: handle exception
			listaModalProformas.clear();
			System.err.println("Error en buscarProforma: " + e.getMessage());
		}
	}

	/**
	 * Define la busqueda del Pedido buscarProformaVenta(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void buscarProformaVenta() {
		try {
			System.out.println("Ingreso a buscarProformaVenta: "
					+ this.getCriterioBusquedaPV());
			listaModalProformasVenta.clear();

			// limpiar detalles
			selectedProformaVenta = null;

			if (!this.getCriterioBusquedaPV().isEmpty()) {
				System.out.println("Filtro: " + this.getCriterioBusquedaPV());
				listaModalProformasVenta = proformaVentaRepository
						.findAllProformaVentaClienteNombre(this
								.getCriterioBusquedaPV().toUpperCase());
			} else {
				System.out.println("Filtro Vacio: "
						+ this.getCriterioBusquedaPV());
				listaModalProformasVenta = proformaVentaRepository
						.traerProformaVentaPendientes();
			}
		} catch (Exception e) {
			// TODO: handle exception
			listaModalProformasVenta.clear();
			System.err.println("Error en buscarProformaVenta: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Define la busqueda del Pedido buscarPedido(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void buscarFactura() {
		try {
			listaUltimasFacturas.clear();
			listaDetalleFactura.clear();
			System.out.println("Ingreso a buscarFactura... "
					+ this.getCriterioBusquedaFactura());
			if (!this.getCriterioBusquedaFactura().isEmpty()) {
				listaUltimasFacturas = facturaRepository.buscarFacturas(this
						.getCriterioBusquedaFactura().toUpperCase());
			}

		} catch (Exception e) {
			// TODO: handle exception
			listaUltimasFacturas.clear();
			listaDetalleFactura.clear();
			System.err.println("Error en buscarFactura: " + e.getMessage());
		}
	}

	/**
	 * Obtiene el listado de las ultimas 100 Facturas,
	 * buscarUltimas100Facturas(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void buscarUltimas100Facturas() {
		try {
			System.out.println("Ingreso a buscarUltimas100Facturas...");
			listaUltimasFacturas.clear();
			listaDetalleFactura.clear();
			listaUltimasFacturas = facturaRepository.findUltimas100Facturas();

		} catch (Exception e) {
			// TODO: handle exception
			listaUltimasFacturas.clear();
			listaDetalleFactura.clear();
			System.out.println("Error en buscarUltimasFacturas: "
					+ e.getMessage());
		} finally {
			criterioBusquedaFactura = null;
		}
	}

	/**
	 * Seleccionar Nota Venta y Cargar el Detalle del Pedido
	 * seleccionarNotaVenta(). <br>
	 * 
	 * @param String
	 *            notaVenta
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void seleccionarNotaVenta(String notaVenta) {
		try {
			System.out.println("Ingreso seleccionarNotaVenta: " + notaVenta);
			MSAccessPedido access = new MSAccessPedido();
			listaPedidoModal.clear();
			listaPedidoModal = access.selecccionarNotaVenta(notaVenta);
		} catch (Exception e) {
			// TODO: handle exception
			listaPedidoModal.clear();
			System.err.println("Error en seleccionarNotaVenta: "
					+ e.getMessage());
		}
	}

	/**
	 * Buscar detalle de la Factura, seleccionarFactura(). <br>
	 * 
	 * @param Factura
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void seleccionarFactura(Factura factura) {
		try {
			System.out
					.println("Ingreso seleccionarFactura: " + factura.getId());
			listaDetalleFactura.clear();
			listaDetalleFactura = detalleFacturaRepository
					.findDetalleFactura(factura.getId());

		} catch (Exception e) {
			// TODO: handle exception
			listaDetalleFactura.clear();
			System.err
					.println("Error en seleccionarFactura: " + e.getMessage());
		}
	}

	/**
	 * Define el metodo para cargar la lista del pedido a la lista de conceptos.
	 * cargarPedido(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void cargarPedido() {
		try {
			System.out.println("Ingreso a Cargar Pedido..."
					+ listaPedidoModal.size());
			for (EstructuraDetallePedido pedido : listaPedidoModal) {
				if (pedido.isSelected()) {
					System.out.println("Valor Preparado: "
							+ pedido.getValorPreparado());
					System.out.println("Valor Descuento: "
							+ pedido.getValorDescuento());
					listaConcepto.add(
							0,
							new EstructuraConcepto(obtenerRandom(), pedido
									.getNotaVenta(), pedido.getNomPreparado(),
									pedido.getCantidadEnvase(), pedido
											.getPrecioUnitPreElab(), pedido
											.getValorPreparado(), pedido
											.getValorDescuento(), "PEDIDO"));
					System.out.println("Producto Pedido Agregado: "
							+ pedido.getNomPreparado());
				}
			}
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Pedido Agregado Correctamente!", "Ok!");
			facesContext.addMessage(null, m);

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error al cargarPedido: " + e.getMessage());
		}
	}

	

	public void showModalAgregarConcepto() {
		try {
			System.out.println("Ingreso a agregarConcepto: ");
			newConcepto = new EstructuraConcepto();
			newConcepto.setId(obtenerRandom());
			newConcepto.setCantidad(1);
			newConcepto.setDescuento(0);
			newConcepto.setCodigo(String.valueOf(newConcepto.getId()));
			newConcepto.setTipo("SERVICIO");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void agregarConcepto() {
		try {
			System.out.println("Ingreso a AgregarConcepto: ");
			listaConcepto.add(0, newConcepto);

		} catch (Exception e) {
			// TODO: handle exception
			newConcepto = new EstructuraConcepto();
			e.printStackTrace();
		}
	}

	public void actualizarPrecio() {
		newConcepto.setPrecioTotal(newConcepto.getPrecioUnitario()
				* newConcepto.getCantidad());
	}

	/**
	 * Define el metodo para cargar la proforma de venta para facturar. <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void cargarProformaVenta() {
		try {
			System.out.println("Ingreso a cargarProformaVenta...");
			

			if (this.getSelectedProformaVenta().getTipoProforma()
					.equals("SERVICIO") && this.getSelectedProformaVenta().getTotal() == this.getMontoPagar() && this.getSelectedProformaVenta().getTotalPagado()==0
							&& this.getSelectedProformaVenta().getTotal() == (this.getSelectedProformaVenta().getTotal() - this.getSelectedProformaVenta().getTotalPagado())) {

				listaDetalleServicios = detalleServicioRepository
						.traerDetalleServicio(this.getSelectedProformaVenta());

				for (DetalleServicio detalle : listaDetalleServicios) {
					listaConcepto.add(
							0,
							new EstructuraConcepto(obtenerRandom(), "SER00"
									+ detalle.getServicio().getId(), detalle
									.getServicio().getNombreServicio(), detalle
									.getCantidad(), detalle.getServicio()
									.getPrecioUnitarioVenta(), detalle
									.getTotal(), cancularPorcentaje(detalle.getServicio().getPrecioUnitarioVenta(), detalle.getCantidad(), detalle.getDescuento()),
									"SERVICIO"));
				}
				
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Proforma Agregada Correctamente!", "ID: "
								+ this.getSelectProforma().getId());
				facesContext.addMessage(null, m);
				
				return;
			}
			
			if(this.getSelectedProformaVenta().getTipoProforma().equals("REPUESTO")){
				listaConcepto.add(
						0,
						new EstructuraConcepto(obtenerRandom(), "REP001"
								, "ANTICIPO POR VENTA DE REPUESTOS Y ACCESORIOS EN GENERAL", 1, this.getMontoPagar(), this.getMontoPagar()
								, 0, "REPUESTO"));
				
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Proforma Agregada Correctamente!", "ID: "
								+ this.getSelectProforma().getId());
				facesContext.addMessage(null, m);
				
				return;
			}
			
			if(this.getSelectedProformaVenta().getTipoProforma().equals("SERVICIO")){
				listaConcepto.add(
						0,
						new EstructuraConcepto(obtenerRandom(), "SER001"
								, "ANTICIPO POR PRESTACION DE SERVICIOS EN GENERAL", 1, this.getMontoPagar(), this.getMontoPagar()
								, 0, "SERVICIO"));
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Proforma Agregada Correctamente!", "ID: "
								+ this.getSelectProforma().getId());
				facesContext.addMessage(null, m);
				
				return;
			}

			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("Error en cargarProformaVenta: "
					+ e.getMessage());
		}
	}
	
	public static double cancularPorcentaje(double precioUnit, double cantidad, double descuento){
		try {
			if(precioUnit>=0){
				return (descuento*precioUnit*cantidad)/100;
			}else
				return 0;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en cancularPorcentaje: "+e.getMessage());
			return 0;
		}
	}

	/**
	 * Carga la Factura Seleccionada en la Busqueda de Factura. cargarFactura(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void cargarFactura() {
		try {
			System.out.println("Ingreso a cargarFactura..."
					+ selectedFactura.getId());

			newFactura = em.find(Factura.class, selectedFactura.getId());
			showButtonImpresion = true;
			cadenaQR = newFactura.getCodigoRespuestaRapida();

			List<DetalleFactura> listaDetalleFactura = detalleFacturaRepository
					.findDetalleFactura(selectedFactura.getId());
			listaConcepto.clear();

			for (DetalleFactura detalle : listaDetalleFactura) {
				EstructuraConcepto concepto = new EstructuraConcepto();
				concepto.setId(obtenerRandom());
				concepto.setCodigo(detalle.getCodigoProducto());
				concepto.setConcepto(detalle.getConcepto());
				concepto.setCantidad(detalle.getCantidad());
				concepto.setPrecioUnitario(detalle.getPrecioUnitario());
				concepto.setPrecioTotal(detalle.getPrecioTotal());
				concepto.setDescuento(detalle.getDescuentos());
				concepto.setTipo(detalle.getOrigen());
				listaConcepto.add(concepto);
			}

			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Factura Cargada Correctamente!", "Ok!");
			facesContext.addMessage(null, m);

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error al cargarFactura: " + e.getMessage());
		}
	}

	/**
	 * Define la busqueda del Producto buscarProducto(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void buscarProducto() {
		try {
			System.out.println("Ingreso a Buscar Producto: "
					+ this.getDescripcionProducto());
			MSAccessPedido access = new MSAccessPedido();
			listaProductoModal.clear();

			if (!this.getDescripcionProducto().isEmpty()) {
				listaProductoModal = access.buscarProducto(this
						.getDescripcionProducto());
			} else {
				listaProductoModal = access.buscarUltimos100Producto();
			}
		} catch (Exception e) {
			// TODO: handle exception
			listaProductoModal.clear();
			System.err.println("Error en buscarProducto: " + e.getMessage());
		}
	}

	/**
	 * Encontrar Productos por el criterio de busqueda <br>
	 * 
	 * @param none
	 * @returns none
	 * @exception None.
	 * @author Isai Galarza
	 * @version 2.0
	 */
	public void buscarProductosV2() {
		try {
			System.out.println("Ingreso a Buscar buscarProductosV2: "
					+ this.getDescripcionProducto());
			listaProductoModal.clear();

			List<Producto> resultadoConsulta = productoRepository
					.findAllProductosForDescription(this
							.getDescripcionProducto());

			for (Producto producto : resultadoConsulta) {
				listaProductoModal.add(new EstructuraProducto(producto
						.getCodigoProducto(), 0, null, producto
						.getCodigoLinea(), String.valueOf(producto.getId()),
						producto.getNombreProducto(), producto
								.getNombreProducto(), producto
								.getNombreProducto(), null, null, null, String
								.valueOf(0), 0, 0, false, 0, 0, 0, 0, 0, false,
						producto.getFechaVencimiento().toString(), true, null,
						producto.getUsuarioRegistro(), producto
								.getFechaRegistro().toString(), producto
								.getCodigoBarra(), null, null, 0, 0, 0, 0,
						null, 0, null, null, null, 1, false));

			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en buscarProductosV2: " + e.getMessage());

		}
	}

	/**
	 * Define el metodo para cargar la lista de productos a la lista de
	 * conceptos. agregarProducto(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void agregarProducto() {
		try {
			System.out.println("Ingreso a Agregar Producto...");
			for (EstructuraProducto producto : listaProductoModal) {
				if (producto.isSelected()) {
					listaConcepto.add(
							0,
							new EstructuraConcepto(obtenerRandom(), producto
									.getCodigoProducto(),
									producto.getNombre2(), producto
											.getCantidad(), producto
											.getPrecioUnitarioVenta(), producto
											.getCostoTotal(), "PRODUCTO"));
					System.out.println("Producto Agregado: "
							+ producto.getNombre2());
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error al cargarProducto: " + e.getMessage());
		}
	}

	/**
	 * Define la busqueda del Servicio buscarServicio(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void buscarServicio() {
		try {
			System.out.println("Ingreso a Buscar Servicio: "
					+ this.getDescripcionServicio());
			listaServicioModal.clear();
			if (!this.getDescripcionServicio().isEmpty()) {
				List<Servicio> listaServicios = servicioRepository
						.findAllServicesForDescription(this
								.getDescripcionServicio());
				for (Servicio servicio : listaServicios) {
					listaServicioModal.add(new EstructuraServicio(servicio, 1,
							false));
				}
			} else {
				List<Servicio> listaServicios = servicioRepository
						.traerServicios();
				for (Servicio servicio : listaServicios) {
					listaServicioModal.add(new EstructuraServicio(servicio, 1,
							false));
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			listaServicioModal.clear();
			System.err.println("Error en buscarServicio: " + e.getMessage());
		}
	}

	/**
	 * Define el metodo para cargar la lista de servicios a la lista de
	 * conceptos. agregarServicio(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void agregarServicio() {
		try {
			System.out.println("Ingreso a Agregar Servicio...");
			for (EstructuraServicio servicio : listaServicioModal) {
				if (servicio.isSelected()) {
					listaConcepto.add(
							0,
							new EstructuraConcepto(obtenerRandom(), "SER-"
									+ servicio.getServicio().getId(), servicio
									.getServicio().getNombreServicio(),
									servicio.getCantidad(), servicio
											.getServicio()
											.getPrecioUnitarioVenta(), servicio
											.getSubTotal(), "SERVICIO"));
					System.out.println("Servicio Agregado: "
							+ servicio.getServicio().getNombreServicio());
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error al agregarServicio: " + e.getMessage());
		}
	}

	/**
	 * Define la Modificacion del Concepto modificarConcepto(). <br>
	 * 
	 * @param None
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void modificarConcepto() {
		try {
			System.out.println("Ingreso a Modificar Concepto..."
					+ selectedConcepto.getId());
			int row = 0;
			for (EstructuraConcepto concepto : listaConcepto) {
				if (concepto.getId() == selectedConcepto.getId()) {
					listaConcepto.remove(row);
					selectedConcepto.setPrecioTotal((selectedConcepto
							.getPrecioUnitario() - selectedConcepto
							.getDescuento())
							* selectedConcepto.getCantidad());
					listaConcepto.add(row, selectedConcepto);
				}
				row++;
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error al modificarConcepto: " + e.getMessage());
		}
	}


	public void imprimirFactura() {
		System.out.println("Ingreso a Imprimir Factura...");
		try {

			FileInputStream inputStream = null;
			try {
				// inputStream = new FileInputStream("c:/archivo.pdf");
				inputStream = new FileInputStream("c:/factura.pdf");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (inputStream == null) {
				return;
			}

			DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
			Doc document = new SimpleDoc(inputStream, docFormat, null);

			PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();

			PrintService defaultPrintService = PrintServiceLookup
					.lookupDefaultPrintService();

			if (defaultPrintService != null) {
				DocPrintJob printJob = defaultPrintService.createPrintJob();
				try {
					printJob.print(document, attributeSet);

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.err.println("No existen impresoras instaladas");
			}

			inputStream.close();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en imprimirFactura: " + e.getMessage());
			e.printStackTrace();
		}

	}

	public void imprimirFactura2() throws IOException, URISyntaxException {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream("c:/factura.pdf");
			// String urlAsString =
			// "http://localhost:8080/buffalo/factura2?idFactura=30";
			// URL url = new URL(urlAsString);
			// File file = new File(url.toURI());
			// inputStream = new FileInputStream(file);

		} catch (FileNotFoundException e) {
			System.out
					.println("Error al Imprirmir Factura..." + e.getMessage());
			e.printStackTrace();
		}
		if (inputStream == null) {
			return;
		}

		DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc document = new SimpleDoc(inputStream, docFormat, null);

		PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();

		PrintService defaultPrintService = PrintServiceLookup
				.lookupDefaultPrintService();

		if (defaultPrintService != null) {
			DocPrintJob printJob = defaultPrintService.createPrintJob();
			try {
				printJob.print(document, attributeSet);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("No existen impresoras instaladas");
		}

		inputStream.close();
	}

	public static void main2(String[] args) {
		try {
	

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws PrintException, IOException {
		FacturacionController obj = new FacturacionController();
		try {
			obj.imprimirFactura3();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("obtenerFechaFactura: "+
		// obj.obtenerFechaFactura());

		// DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
		// PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
		// patts.add(Sides.DUPLEX);
		// PrintService[] ps = PrintServiceLookup.lookupPrintServices(flavor,
		// patts);
		// if (ps.length == 0) {
		// throw new IllegalStateException("No Printer found");
		// }
		// System.out.println("Available printers: " + Arrays.asList(ps));
		//
		// //PrintService defaultPrintService =
		// PrintServiceLookup.lookupDefaultPrintService();
		//
		// PrintService myService = null;
		// for (PrintService printService : ps) {
		// if (printService.getName().equals("Canon MG2400 series")) {
		// myService = printService;
		// break;
		// }
		// }
		//
		// if (myService == null) {
		// throw new IllegalStateException("Printer not found");
		// }
		//
		// FileInputStream fis = new FileInputStream("C:/factura.pdf");
		// Doc pdfDoc = new SimpleDoc(fis, DocFlavor.SERVICE_FORMATTED.PAGEABLE,
		// null);
		// DocPrintJob printJob = myService.createPrintJob();
		// printJob.print(pdfDoc, new HashPrintRequestAttributeSet());
		// fis.close();
	}

	public void imprimirFactura3() {
		try {
			System.out.println("Ingreso a imprimirFactura3....");

			// PrinterJob job = PrinterJob.getPrinterJob();
			// job.printDialog();
			// String impresora=job.getPrintService().getName();
			// System.out.println("Impresora Name: "+impresora);

			// Archivo que se desea imprimir
			// FileInputStream inputStream = new
			// FileInputStream("c://archivo.pdf");
			FileInputStream inputStream = new FileInputStream(new File(
					"C:/factura.pdf"));

			// Formato de Documento
			DocFlavor docFormat = DocFlavor.INPUT_STREAM.GIF;
			// Lectura de Documento
			Doc document = new SimpleDoc(inputStream, docFormat, null);

			// Nombre de la impresora
			String printerName = "Canon MG2400 series";

			// Inclusion del nombre de impresora y sus atributos
			AttributeSet attributeSet = new HashAttributeSet();
			attributeSet.add(new PrinterName(printerName, null));
			attributeSet = new HashAttributeSet();
			// Soporte de color o no
			attributeSet.add(ColorSupported.NOT_SUPPORTED);

			// Busqueda de la impresora por el nombre asignado en attributeSet
			PrintService[] services = PrintServiceLookup.lookupPrintServices(
					docFormat, attributeSet);

			// System.out.println("Imprimiendo en 0: " + services[0].getName());
			// System.out.println("Imprimiendo en 1: " + services[1].getName());
			// System.out.println("Imprimiendo en 2: " + services[2].getName());
			// System.out.println("Imprimiendo en 3: " + services[3].getName());
			System.out.println("Imprimiendo en 4: " + services[4].getName());

			DocPrintJob printJob = services[4].createPrintJob();
			// Envio a la impresora
			printJob.print(document, new HashPrintRequestAttributeSet());

			inputStream.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * Recolecta una lista de impresoras disponibles, cargarImpresoras(). <br>
	 * 
	 * @param none
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void cargarImpresoras() {
		try {
			System.out.println("Ingreso a cargarImpresoras...");
			listaImpresora.clear();
			DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
			PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
			patts.add(Sides.DUPLEX);

			PrintService[] ps = PrintServiceLookup.lookupPrintServices(flavor,
					patts);
			int pos = 1;
			for (PrintService printService : ps) {
				listaImpresora.add(new EstructuraImpresora(pos, printService
						.getName()));
				pos++;
			}

		} catch (Exception e) {
			// TODO: handle exception
			listaImpresora.clear();
			System.out.println("Error en cargarImpresoras: " + e.getMessage());
		}
	}

	/**
	 * Enviar una orden de impresion a la Impresora Seleccionada,
	 * impresionAtendida(). <br>
	 * 
	 * @param none
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void impresionAtendida() {
		try {
			System.out.println("Ingreso a impresionAtendida... "
					+ selectedImpresora);
			DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
			PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
			patts.add(Sides.DUPLEX);

			PrintService[] ps = PrintServiceLookup.lookupPrintServices(flavor,
					patts);
			PrintService myService = null;
			for (PrintService printService : ps) {
				if (printService.getName().equals(selectedImpresora)) {
					System.out.println("Impresora Encontrada: "
							+ printService.getName());
					myService = printService;
					break;
				}
			}

			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length()
					- request.getRequestURI().length())
					+ request.getContextPath() + "/";
			System.out.println("urlPath >> " + urlPath);

			String urlPDFreporte = urlPath + "factura2?idFactura="
					+ newFactura.getId();
			System.out.println("URL Reporte PDF: " + urlPDFreporte);
			// http://localhost:8080/buffalo/codeQR?qrtext=162264025|32|7901001300997|06/10/2014|276.0|276.0|01-0A-64-6D-89|6296719|0|0|0|0

			// IMPRIME EN IMPRESORA POR DEFAULT
			URL url = new URL(urlPDFreporte);
			printPDF(url, myService);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en impresionAtendida: " + e.getMessage());
		}
	}

	public void enviarImpresion() {
		try {
			System.out.println("Ingreso a Imprimir Factura...");
			System.out.println("Impresion Silencionsa >>> "
					+ usuarioSession.isImpresionSilencionsa());
			if (usuarioSession.isImpresionSilencionsa()) {
				impresionSilencionsa();
			} else {
				// impresionAtendida();
				// printDialog();

				DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
				PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
				patts.add(Sides.DUPLEX);

				PrintService[] ps = PrintServiceLookup.lookupPrintServices(
						flavor, patts);
				if (ps.length == 0) {
					throw new IllegalStateException("No Printer found");
				}
				System.out.println("Available printers: " + Arrays.asList(ps));

				// PrintService defaultPrintService =
				// PrintServiceLookup.lookupDefaultPrintService();

				PrintService myService = null;
				for (PrintService printService : ps) {
					if (printService.getName().equals("Canon MG2400 series")) {
						System.out.println("Impresora Encontrada: "
								+ printService.getName());
						myService = printService;
						break;
					}
				}

				if (myService == null) {
					throw new IllegalStateException("Printer not found");
				}

				// //Step 1. Create a PdfDecoder object
				// PdfDecoder decodePdf = new PdfDecoder(true); //Set to true as
				// I don’t want to render it to screen in this tutorial
				// try {
				// decodePdf.openPdfFile("C:/factura.pdf");
				// // FontMappings.setFontReplacements();
				// }
				// catch (Exception e) {
				// System.out.println("Error en step1... "+e.getMessage());
				// }
				//
				// //Step 2. Set your attributes
				// PrintRequestAttributeSet attributeSet = new
				// HashPrintRequestAttributeSet();
				// JobName jobName = new JobName("Example Print", null);
				// attributeSet.add(jobName);
				//
				// decodePdf.setPrintAutoRotateAndCenter(true);
				// decodePdf.setPrintPageScalingMode(PrinterOptions.PAGE_SCALING_FIT_TO_PRINTER_MARGINS);
				// decodePdf.setPrintPageScalingMode(PrinterOptions.PAGE_SCALING_NONE);
				// decodePdf.setPrintPageScalingMode(PrinterOptions.PAGE_SCALING_REDUCE_TO_PRINTER_MARGINS);
				// decodePdf.setPagePrintRange(1, decodePdf.getPageCount());
				//
				// //Step 3. Find a printer
				// PrintService[] services =
				// PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE,
				// attributeSet);
				// for(PrintService s : services) {
				// System.out.println(s.getName());
				// }
				//
				// PrintService printingDevice;
				// for(PrintService s : services) {
				// if(s.getName().equals("Canon MG2400 series")) {
				// printingDevice = s;
				// }
				// }
				//
				// //Step 4. Create a Pageable PDF
				// PdfBook pdfBook = new PdfBook(decodePdf, printingDevice,
				// attributeSet);
				// SimpleDoc doc = new SimpleDoc(pdfBook,
				// DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
				//
				// //Step 5. Print it!
				// DocPrintJob printJob = printingDevice.createPrintJob();
				// try {
				// printJob.print(doc, attributeSet);
				// } catch (PrintException e) {
				// System.out.println("Error Step5: "+e.getMessage());
				// }
			}

			System.out.println("Impresion Realizada Corrrectamente!!!");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error al Imprimir Factura: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Enviar una orden de impresion directamente (Impresora por Default),
	 * impresionSilencionsa(). <br>
	 * 
	 * @param none
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void impresionSilencionsa() {
		try {
			System.out.println("Ingreso a impresionSilencionsa...");
			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length()
					- request.getRequestURI().length())
					+ request.getContextPath() + "/";
			System.out.println("urlPath >> " + urlPath);

			String urlPDFreporte = urlPath + "factura2?idFactura="
					+ newFactura.getId();
			System.out.println("URL Reporte PDF: " + urlPDFreporte);
			// http://localhost:8080/buffalo/codeQR?qrtext=162264025|32|7901001300997|06/10/2014|276.0|276.0|01-0A-64-6D-89|6296719|0|0|0|0

			// IMPRIME EN IMPRESORA POR DEFAULT
			URL url = new URL(urlPDFreporte);
			PrintService defaultService = PrintServiceLookup
					.lookupDefaultPrintService();
			printPDF(url, defaultService);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en impresionSilencionsa: "
					+ e.getMessage());
		}
	}

	public static void printDialog() {
		try {

			List<PDDocument> docs = new ArrayList<PDDocument>();
			try {
				docs.add(PDDocument.load("c:/factura.pdf"));
				docs.add(PDDocument.load("c:/factura.pdf"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
				DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
				PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
				aset.add(MediaSizeName.ISO_A4);
				aset.add(new Copies(1));
				aset.add(Sides.ONE_SIDED);
				aset.add(Finishings.STAPLE);

				PrintService printService[] = PrintServiceLookup
						.lookupPrintServices(flavor, pras);
				PrintService defaultService = PrintServiceLookup
						.lookupDefaultPrintService();
				PrintService service = ServiceUI.printDialog(null, 200, 200,
						printService, defaultService, flavor, pras);

				if (service != null && !docs.isEmpty()) {
					for (PDDocument doc : docs) {
						PrinterJob printJob = PrinterJob.getPrinterJob();
						printJob.setPrintService(service);
						doc.silentPrint(printJob);
					}
				}
			} catch (PrinterException e) {
				e.printStackTrace();
			} finally {
				for (PDDocument doc : docs) {
					if (doc != null) {
						try {
							doc.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * Enviar una orden de impresion a la Impresora Seleccionada,
	 * impresionAtendida(). <br>
	 * 
	 * @param none
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void impresionAtendida2() {
		try {
			System.out.println("Ingreso a impresionAtendida...");

			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length()
					- request.getRequestURI().length())
					+ request.getContextPath() + "/";
			System.out.println("urlPath >> " + urlPath);

			String urlPDFreporte = urlPath + "factura2?idFactura="
					+ newFactura.getId();
			System.out.println("URL Reporte PDF: " + urlPDFreporte);
			URL url = new URL(urlPDFreporte);
			// http://localhost:8080/buffalo/codeQR?qrtext=162264025|32|7901001300997|06/10/2014|276.0|276.0|01-0A-64-6D-89|6296719|0|0|0|0

			PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
			DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
			PrintService printService[] = PrintServiceLookup
					.lookupPrintServices(flavor, pras);
			PrintService defaultService = PrintServiceLookup
					.lookupDefaultPrintService();
			PrintService service = ServiceUI.printDialog(null, 200, 200,
					printService, defaultService, flavor, pras);

			// IMPRIME EN IMPRESORA SELECCIONADA POR EL USUARIO
			printPDF(url, service);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en impresionAtendida: " + e.getMessage());
		}
	}

	public static void printPDF(URL url, PrintService printer)
			throws IOException, PrinterException {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintService(printer);
		PDDocument doc = PDDocument.load(url);
		doc.silentPrint(job);
	}

	public static PrintService choosePrinter() {
		PrinterJob printJob = PrinterJob.getPrinterJob();
		if (printJob.printDialog()) {
			return printJob.getPrintService();
		} else {
			return null;
		}
	}

	public static void printPDF(String fileName, PrintService printer)
			throws IOException, PrinterException {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintService(printer);
		PDDocument doc = PDDocument.load(fileName);
		doc.silentPrint(job);
	}

	// SELECT CONCEPTO
	public void onRowSelectConcepto(SelectEvent event) {
		try {
			EstructuraConcepto concepto = (EstructuraConcepto) event
					.getObject();
			System.out
					.println("onRowSelectConcepto  " + concepto.getConcepto());

			this.setSelectedConcepto(concepto);

			FacesMessage msg = new FacesMessage("Concepto Selected:",
					concepto.getConcepto());
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error in onRowSelectConcepto: "
					+ e.getMessage());
		}
	}

	// UNSELECT CONCEPTO
	public void onRowUnselectConcepto(UnselectEvent event) {
		try {
			EstructuraConcepto concepto = (EstructuraConcepto) event
					.getObject();
			System.out.println("onRowUnselectConcepto  "
					+ concepto.getConcepto());
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error in onRowUnselectConcepto: "
					+ e.getMessage());
		}
	}

	// SELECT NOTA DE VENTA
	public void onRowSelectNotaVenta(SelectEvent event) {
		try {
			EstructuraDetallePedido nota = (EstructuraDetallePedido) event
					.getObject();
			System.out.println("onRowSelectNotaVenta  " + nota.getNotaVenta());

			seleccionarNotaVenta(nota.getNotaVenta());

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error in onRowSelectNotaVenta: "
					+ e.getMessage());
		}
	}



	// SELECT BUSCAR PROFORMA VENTA
	public void onRowSelectProformaVenta(SelectEvent event) {
		try {
			Proforma proformaVenta = (Proforma) event.getObject();
			System.out.println("onRowSelectProformaVenta  "	+ proformaVenta.getId());
			
			this.setMontoPagar(proformaVenta.getTotal() - proformaVenta.getTotalPagado());
			System.out.println("Monto Pagar: " + this.getMontoPagar());
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error in onRowSelectProformaVenta: "
					+ e.getMessage());
		}
	}

	// SELECT FACTURA
	public void onRowSelectFactura(SelectEvent event) {
		try {
			Factura factura = (Factura) event.getObject();
			System.out.println("onRowSelectFactura  " + factura.getId() + "-"
					+ factura.getCodigoControl() + "-"
					+ factura.getNombreFactura());
			seleccionarFactura(factura);
		} catch (Exception e) {
			// TODO: handle exception
			System.out
					.println("Error in onRowSelectFactura: " + e.getMessage());
		}
	}

	/**
	 * Metodo para Recordar Datos de Facturacion del Cliente
	 * recordarDatosCliente(). <br>
	 * 
	 * @param EstructuraDetallePedido
	 *            nota
	 * @returns none.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void recordarDatosCliente() {
		try {
			System.out.println("Ingreso a recordarDatosCliente...");

			Cliente cliente = clienteRepository.buscarClienteNIT(newFactura
					.getNitCi());

			if (cliente != null) {
				// Existe --> Recordar Ultima Transaccion (Nombre, NIT)
				System.out.println("Cliente Existe: "
						+ cliente.getNombreCompleto());
				List<NitCliente> listaNitCliente = nitClienteRepository
						.findNitCliente(cliente.getId());

				if (!listaNitCliente.isEmpty()) {
					System.out.println("Cliente Encontrado Historial...");
					if (newFactura.getNitCi().trim().equals("0")) {
						newFactura.setNitCi("0");
						newFactura.setNombreFactura("S/N");
					} else {
						newFactura.setNitCi(listaNitCliente.get(0).getNitCi());
						newFactura.setNombreFactura(listaNitCliente.get(0)
								.getNombreFactura());
					}
				} else {
					// No Existe
					System.out
							.println("Cliente existe pero no tiene historial de NIT...");
				}

			} else {
				// No Existe Cliente
				System.out.println("No existe cliente...");

				// System.out.println("nit: "+newFactura.getNitCi());
				// System.out.println("nombre: "+newFactura.getNombreFactura());
				//
				// //Registrar Nuevo Cliente
				// Cliente newCliente = new Cliente();
				// newCliente.setId(newFactura.getNitCi());
				// newCliente.setNombreCompleto(newFactura.getNombreFactura());
				// newCliente.setEstado("AC");
				// newCliente.setUsuarioRegistro(usuarioSession.getLogin());
				// newCliente.setFechaRegistro(new Date());
				// clienteRegistration.register(newCliente);
				// System.out.println("Cliente Registrado.");

			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en recordarDatosCliente: "
					+ e.getMessage());
		}
	}

	// SELECT CONCEPTO CLICK
	public void onRowSelectConceptoClick(SelectEvent event) {
		try {
			EstructuraConcepto concepto = (EstructuraConcepto) event
					.getObject();
			System.out
					.println("onRowSelectConcepto  " + concepto.getConcepto());
			this.setConceptoID(concepto.getId());

			// selectedConcepto = new EstructuraConcepto(concepto);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error in onRowSelectConceptoClick: "
					+ e.getMessage());
		}
	}

	// SELECT FACTURA CLICK
	public void onRowSelectFacturaClick(SelectEvent event) {
		try {
			Factura factura = (Factura) event.getObject();
			System.out.println("onRowSelectFacturaClick  " + factura.getId());
			this.setFacturaID(factura.getId());

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error in onRowSelectFacturaClick: "
					+ e.getMessage());
		}
	}

	/**
	 * Metodo que recupera las ultimas notas de venta traerUltimosPedidos(). <br>
	 * 
	 * @param None
	 * @returns List<EstructuraDetallePedido>.
	 * @exception None.
	 * @author Isai Galarza
	 * @version 1.0
	 */
	public void traerUltimosPedidos() {

		try {
			System.out.println("Ingreso a traerUltimosPedidos...");
			listaUltimosPedidoModal = ultimosPedidos.getListaUltimosPedidos();
			// MSAccessPedido access = new MSAccessPedido();
			// listaUltimosPedidoModal = access.traerUltimasNotasVenta();
			// System.out.println("Size Lista Pedidos Modal: "
			// +listaUltimosPedidoModal.size());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en traerUltimosPedidos: "
					+ e.getMessage());
		}
	}

	public void showModalBuscarPedido() {
		System.out.println("showModalBuscarPedido...");
		notaVenta = null;
		listaPedidoModal.clear();

	}

	public void showModalAgregarProducto() {
		System.out.println("showModalAgregarProducto...");
		descripcionProducto = null;
		listaProductoModal.clear();

		listaProductoModal = traerProductos();

		// try {
		// System.out.println("Ingreso a cargar ultimos 100 Productos...");
		// MSAccessPedido access = new MSAccessPedido();
		// listaProductoModal.clear();
		// listaProductoModal = access.buscarUltimos100Producto();
		// } catch (Exception e) {
		// // TODO: handle exception
		// System.out.println("Error al cargar ultimos 100 productos: "+e.getMessage());
		// }
	}

	public void showModalAgregarServicio() {
		System.out.println("showModalAgregarServicio...");
		descripcionServicio = null;
		listaServicioModal.clear();

		try {
			System.out.println("Cargando Ultimos Servicios...");
			listaServicioModal.clear();
			List<Servicio> listaServicios = servicioRepository.traerServicios();
			for (Servicio servicio : listaServicios) {
				listaServicioModal.add(new EstructuraServicio(servicio, 1,
						false));
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error al cargarUltimos Servicios: "
					+ e.getMessage());
		}
	}

	public void showModalProcesarFactura() {
		System.out.println("showModalProcesarFactura...");
		// recordarDatosCliente(selectNotaVenta);
	}

	public String getUrlCodeQR() {
		try {
			System.out.println("Ingreso a getUrlCodeQR...");
			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length()
					- request.getRequestURI().length())
					+ request.getContextPath() + "/";
			System.out.println("urlPath >> " + urlPath);
			urlCodeQR = urlPath + "codeQR?qrtext=" + this.getCadenaQR();
			return urlCodeQR;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en getUrlCodeQR: " + e.getMessage());
			return null;
		}

	}

	public void setUrlCodeQR(String urlCodeQR) {
		this.urlCodeQR = urlCodeQR;
	}

	public void cambiarCheckEfectivo() {
		if (this.isPagoEfectivo()) {
			this.setPagoTarjeta(false);
		} else {
			this.setPagoTarjeta(true);
		}
	}

	public void cambiarCheckTarjeta() {
		if (this.isPagoTarjeta()) {
			this.setPagoEfectivo(false);
		} else {
			this.setPagoEfectivo(true);
		}
	}

	public void vistaPreviaFactura() {
		try {
			System.out.println("Ingreso a Vista Previa  de Factura....");
			armarURLFactura();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void armarURLFactura() {
		try {
			System.out.println("Ingreso a armarURLVentasSFV...");
			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length()
					- request.getRequestURI().length())
					+ request.getContextPath() + "/";
			System.out.println("urlPath >> " + urlPath);

			urlFactura = urlPath + "factura2?idFactura=" + newFactura.getId();
			System.out.println("URL Reporte Factura: " + urlFactura);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en armarURLFactura: " + e.getMessage());
		}
	}

	@Inject
	private NotaVentaRepository notaVentaRepository;

	public void armarURLNotaVenta() {
		try {
			System.out.println("Ingreso a armarURLNotaVenta...");
			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length()
					- request.getRequestURI().length())
					+ request.getContextPath() + "/";
			System.out.println("urlPath >> " + urlPath);
			NotaVenta nv = notaVentaRepository
					.findAllOrderedByIDForFactura(newFactura);

//			urlNotaVenta = urlPath + "ReporteNotaVentaPrevia?idFactura="
//					+ newFactura.getId() + "&pUsuario="
//					+ usuarioSession.getName() + "&pEstado="
//					+ nv.getProformaVenta().getProforma().getTipoProforma();
			
			System.out.println("URL Reporte Nota Venta: " + urlNotaVenta);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en armarURLNotaVenta: " + e.getMessage());
		}
	}

	// total efectivo (efectivo bs + efectivo $)

	private double efectivoFactura = 0;

	public void calcularCambioDevolucion() {
		totalCambio = (totalEntregadoDebito
				+ (totalEntregadoDolares * tipoCambio) + totalEntregado)
				- totalBolivianos;
		totalCambio = round(totalCambio, 0);
		totalCambioString = String.valueOf(totalCambio);
	}

	public void calcularCambioDevolucionString() {
		try {
			System.out.println("Ingreso a calcularCambioDevolucionString...");
			System.out.println("totalEntregadoString: " + totalEntregadoString);
			System.out.println("totalEntregadoDolaresString :"
					+ totalEntregadoDolaresString);

			double totalBolivianosDouble = 0;
			double totalDolaresDouble = 0;

			// bolivianos
			if (!totalEntregadoString.isEmpty()) {
				totalBolivianosDouble = Double
						.parseDouble(totalEntregadoString);
			}

			// dolares
			if (!totalEntregadoDolaresString.isEmpty()) {
				totalDolaresDouble = Double
						.parseDouble(totalEntregadoDolaresString);
			}

			System.out.println("totalBolivianos: " + totalBolivianos);
			System.out.println("totalDolares: " + totalDolares);

			totalCambio = (totalEntregadoDebito
					+ (totalDolaresDouble * tipoCambio) + totalBolivianosDouble)
					- totalBolivianos;
			totalCambio = round(totalCambio, 0);
			totalCambioString = String.valueOf(totalCambio);
			System.out.println("totalCambioString: " + totalCambioString);
			totalEntregado = totalBolivianosDouble;
			totalEntregadoDolares = totalDolaresDouble;
		} catch (Exception e) {
			// TODO: handle exception
			totalCambioString = "0.00";
			e.printStackTrace();
			System.out.println("Error en calcularCambioDevolucionString: "
					+ e.getMessage());
		}
	}

	public void calcularCambio() {
		totalCambio = totalEntregado - totalBolivianos;
		totalCambio = round(totalCambio, 0);
	}

	public void calcularCambioDolares() {
		totalCambio = (totalEntregadoDolares * tipoCambio) - totalBolivianos;
		totalCambio = round(totalCambio, 0);
	}

	public void calcularCambioDebitoTarjeta() {
		totalCambio = totalEntregadoDebito - totalBolivianos;
		totalCambio = round(totalCambio, 0);
	}

	public String getNotaVenta() {
		return notaVenta;
	}

	public List<EstructuraDetallePedido> getListaPedido() {
		return listaPedido;
	}

	public void setListaPedido(List<EstructuraDetallePedido> listaPedido) {
		this.listaPedido = listaPedido;
	}

	@Produces
	@Named
	public Factura getNewFactura() {
		return newFactura;
	}

	@Produces
	@Named
	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	public void setNotaVenta(String notaVenta) {
		this.notaVenta = notaVenta;
	}

	public List<EstructuraDetallePedido> getListaPedidoModal() {
		return listaPedidoModal;
	}

	public void setListaPedidoModal(
			List<EstructuraDetallePedido> listaPedidoModal) {
		this.listaPedidoModal = listaPedidoModal;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	private String getRootErrorMessage(Exception e) {
		// Default to general error message that registration failed.
		String errorMessage = "Registration failed. See server log for more information";
		if (e == null) {
			// This shouldn't happen, but return the default messages
			return errorMessage;
		}

		// Start with the exception and recurse to find the root cause
		Throwable t = e;
		while (t != null) {
			// Get the message from the Throwable class instance
			errorMessage = t.getLocalizedMessage();
			t = t.getCause();
		}
		// This is the root cause message
		return errorMessage;
	}

	public double getTotalDolares() {
		try {
			totalDolares = 0;
			totalBolivianos = 0;
			for (EstructuraConcepto concepto : listaConcepto) {
				totalBolivianos += concepto.getPrecioTotal();
			}
			totalDolares = totalBolivianos / tipoCambio;
			totalDolares = round(totalDolares, 0);
			return totalDolares;
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error en getTotalDolares: " + e.getMessage());
			return 0;
		}
	}

	public void setTotalDolares(double totalDolares) {
		this.totalDolares = totalDolares;
	}

	public double getTotalBolivianos() {
		try {
			totalBolivianos = 0;
			for (EstructuraConcepto concepto : listaConcepto) {
				totalBolivianos += concepto.getPrecioTotal();
			}
			totalBolivianos = round(totalBolivianos, 0);
			return totalBolivianos;
		} catch (Exception e) {
			// TODO: handle exception
			System.err
					.println("Error en getTotalBolivianos: " + e.getMessage());
			return 0;
		}
	}

	public void setTotalBolivianos(double totalBolivianos) {
		this.totalBolivianos = totalBolivianos;
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public List<EstructuraConcepto> getListaConcepto() {
		return listaConcepto;
	}

	public void setListaConcepto(List<EstructuraConcepto> listaConcepto) {
		this.listaConcepto = listaConcepto;
	}

	public String getDescripcionProducto() {
		return descripcionProducto;
	}

	public void setDescripcionProducto(String descripcionProducto) {
		this.descripcionProducto = descripcionProducto;
	}

	public List<EstructuraProducto> getListaProductoModal() {
		return listaProductoModal;
	}

	public void setListaProductoModal(
			List<EstructuraProducto> listaProductoModal) {
		this.listaProductoModal = listaProductoModal;
	}

	public String getDescripcionServicio() {
		return descripcionServicio;
	}

	public void setDescripcionServicio(String descripcionServicio) {
		this.descripcionServicio = descripcionServicio;
	}

	public List<EstructuraServicio> getListaServicioModal() {
		return listaServicioModal;
	}

	public void setListaServicioModal(
			List<EstructuraServicio> listaServicioModal) {
		this.listaServicioModal = listaServicioModal;
	}

	public EstructuraConcepto getSelectedConcepto() {
		return selectedConcepto;
	}

	public void setSelectedConcepto(EstructuraConcepto selectedConcepto) {
		this.selectedConcepto = selectedConcepto;
	}

	public int obtenerRandom() {
		return randInt(100000, 999999);
	}

	public int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	// public static void main(String[] args)
	// {
	// double valor = 1254.625;
	// String val = valor+"";
	// BigDecimal big = new BigDecimal(val);
	// big = big.setScale(0, RoundingMode.HALF_UP);
	// System.out.println("Número : "+big);
	// }

	public Usuario getUsuarioSession() {
		return usuarioSession;
	}

	public void setUsuarioSession(Usuario usuarioSession) {
		this.usuarioSession = usuarioSession;
	}

	public double getTotalEntregado() {
		try {
			totalEntregado = round(totalEntregado, 0);
			return totalEntregado;
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error en getTotalEntregado: " + e.getMessage());
			return 0;
		}
	}

	public void setTotalEntregado(double totalEntregado) {
		this.totalEntregado = totalEntregado;
	}

	public double getTotalCambio() {
		return totalCambio;
	}

	public void setTotalCambio(double totalCambio) {
		this.totalCambio = totalCambio;
	}

	public double getTotalEntregadoDolares() {
		try {
			totalEntregadoDolares = round(totalEntregadoDolares, 0);
			return totalEntregadoDolares;
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error en getTotalEntregadoDolares: "
					+ e.getMessage());
			return 0;
		}
	}

	public void setTotalEntregadoDolares(double totalEntregadoDolares) {
		this.totalEntregadoDolares = totalEntregadoDolares;
	}

	public boolean isPagoEfectivo() {
		return pagoEfectivo;
	}

	public void setPagoEfectivo(boolean pagoEfectivo) {
		this.pagoEfectivo = pagoEfectivo;
	}

	public boolean isPagoTarjeta() {
		return pagoTarjeta;
	}

	public void setPagoTarjeta(boolean pagoTarjeta) {
		this.pagoTarjeta = pagoTarjeta;
	}

	public double getTotalEntregadoDebito() {
		return totalEntregadoDebito;
	}

	public void setTotalEntregadoDebito(double totalEntregadoDebito) {
		this.totalEntregadoDebito = totalEntregadoDebito;
	}

	public boolean isShowButtonImpresion() {
		return showButtonImpresion;
	}

	public void setShowButtonImpresion(boolean showButtonImpresion) {
		this.showButtonImpresion = showButtonImpresion;
	}

	public String getCadenaQR() {
		return cadenaQR;
	}

	public void setCadenaQR(String cadenaQR) {
		this.cadenaQR = cadenaQR;
	}

	public boolean isResulConcatenarCodigoRespuestaRapida() {
		return resulConcatenarCodigoRespuestaRapida;
	}

	public void setResulConcatenarCodigoRespuestaRapida(
			boolean resulConcatenarCodigoRespuestaRapida) {
		this.resulConcatenarCodigoRespuestaRapida = resulConcatenarCodigoRespuestaRapida;
	}

	public List<EstructuraDetallePedido> getListaUltimosPedidoModal() {
		return listaUltimosPedidoModal;
	}

	public void setListaUltimosPedidoModal(
			List<EstructuraDetallePedido> listaUltimosPedidoModal) {
		this.listaUltimosPedidoModal = listaUltimosPedidoModal;
	}

	public EstructuraDetallePedido getSelectNotaVenta() {
		return selectNotaVenta;
	}

	public void setSelectNotaVenta(EstructuraDetallePedido selectNotaVenta) {
		this.selectNotaVenta = selectNotaVenta;
	}

	public int getConceptoID() {
		return conceptoID;
	}

	public void setConceptoID(int conceptoID) {
		this.conceptoID = conceptoID;
	}

	public Factura getSelectedFactura() {
		return selectedFactura;
	}

	public void setSelectedFactura(Factura selectedFactura) {
		this.selectedFactura = selectedFactura;
	}

	public List<Factura> getListaUltimasFacturas() {
		return listaUltimasFacturas;
	}

	public void setListaUltimasFacturas(List<Factura> listaUltimasFacturas) {
		this.listaUltimasFacturas = listaUltimasFacturas;
	}

	public List<DetalleFactura> getListaDetalleFactura() {
		return listaDetalleFactura;
	}

	public void setListaDetalleFactura(List<DetalleFactura> listaDetalleFactura) {
		this.listaDetalleFactura = listaDetalleFactura;
	}

	public List<EstructuraImpresora> getListaImpresora() {
		return listaImpresora;
	}

	public void setListaImpresora(List<EstructuraImpresora> listaImpresora) {
		this.listaImpresora = listaImpresora;
	}

	public String getSelectedImpresora() {
		return selectedImpresora;
	}

	public void setSelectedImpresora(String selectedImpresora) {
		this.selectedImpresora = selectedImpresora;
	}

	public int getFacturaID() {
		return facturaID;
	}

	public void setFacturaID(int facturaID) {
		this.facturaID = facturaID;
	}

	public String getCriterioBusquedaFactura() {
		return criterioBusquedaFactura;
	}

	public void setCriterioBusquedaFactura(String criterioBusquedaFactura) {
		this.criterioBusquedaFactura = criterioBusquedaFactura;
	}

	public double getEfectivoFactura() {
		try {
			efectivoFactura = (newFactura.getTotalEfectivoDolares() * newFactura
					.getTipoCambio()) + newFactura.getTotalEfectivo();
			return efectivoFactura;
		} catch (Exception e) {
			// TODO: handle exception
			System.out
					.println("Error en getEfectivoFactura: " + e.getMessage());
			return 0;
		}
	}

	public void setEfectivoFactura(double efectivoFactura) {
		this.efectivoFactura = efectivoFactura;
	}

	public String getTotalCambioString() {
		return totalCambioString;
	}

	public void setTotalCambioString(String totalCambioString) {
		this.totalCambioString = totalCambioString;
	}

	public String getTotalEntregadoString() {
		return totalEntregadoString;
	}

	public void setTotalEntregadoString(String totalEntregadoString) {
		this.totalEntregadoString = totalEntregadoString;
	}

	public String getTotalEntregadoDolaresString() {
		return totalEntregadoDolaresString;
	}

	public void setTotalEntregadoDolaresString(
			String totalEntregadoDolaresString) {
		this.totalEntregadoDolaresString = totalEntregadoDolaresString;
	}

	public String getProformaVentaCliente() {
		return proformaVentaCliente;
	}

	public void setProformaVentaCliente(String proformaVentaCliente) {
		this.proformaVentaCliente = proformaVentaCliente;
	}

	public List<Proforma> getListaModalProformas() {
		return listaModalProformas;
	}

	public void setListaModalProformas(List<Proforma> listaModalProformas) {
		this.listaModalProformas = listaModalProformas;
	}

	public Proforma getSelectProforma() {
		return selectProforma;
	}

	public void setSelectProforma(Proforma selectProforma) {
		this.selectProforma = selectProforma;
	}


	public List<Proforma> getListaModalProformasVenta() {
		return listaModalProformasVenta;
	}

	public void setListaModalProformasVenta(
			List<Proforma> listaModalProformasVenta) {
		this.listaModalProformasVenta = listaModalProformasVenta;
	}

	public Proforma getSelectedProformaVenta() {
		return selectedProformaVenta;
	}

	public void setSelectedProformaVenta(Proforma selectedProformaVenta) {
		this.selectedProformaVenta = selectedProformaVenta;
	}

	public String getCriterioBusquedaPV() {
		return criterioBusquedaPV;
	}

	public void setCriterioBusquedaPV(String criterioBusquedaPV) {
		this.criterioBusquedaPV = criterioBusquedaPV;
	}

	public double getMontoPagar() {
		return montoPagar;
	}

	public void setMontoPagar(double montoPagar) {
		this.montoPagar = montoPagar;
	}

	public String getUrlNotaVenta() {
		return urlNotaVenta;
	}

	public void setUrlNotaVenta(String urlNotaVenta) {
		this.urlNotaVenta = urlNotaVenta;
	}

	public EstructuraConcepto getNewConcepto() {
		return newConcepto;
	}

	public void setNewConcepto(EstructuraConcepto newConcepto) {
		this.newConcepto = newConcepto;
	}

	public List<DetalleServicio> getListaDetalleServicios() {
		return listaDetalleServicios;
	}

	public void setListaDetalleServicios(
			List<DetalleServicio> listaDetalleServicios) {
		this.listaDetalleServicios = listaDetalleServicios;
	}


}