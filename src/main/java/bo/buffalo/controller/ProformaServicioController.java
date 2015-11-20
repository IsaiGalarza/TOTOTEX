package bo.buffalo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.CiudadRepository;
import bo.buffalo.data.ClienteRepository;
import bo.buffalo.data.DetalleServicioRepository;
import bo.buffalo.data.ProformaRepository;
import bo.buffalo.data.ServicioRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Ciudad;
import bo.buffalo.model.Cliente;
import bo.buffalo.model.DetalleServicio;
import bo.buffalo.model.DetalleServicioVenta;
import bo.buffalo.model.Producto;
import bo.buffalo.model.Proforma;
import bo.buffalo.model.ProformaVenta;
import bo.buffalo.model.Servicio;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.ClienteRegistration;
import bo.buffalo.service.DetalleServicioRegistration;
import bo.buffalo.service.DetalleServicioVentaRegistration;
import bo.buffalo.service.ProformaRegistration;
import bo.buffalo.service.ProformaVentaRegistration;

@Named(value = "proformaServicioController")
@ConversationScoped
public class ProformaServicioController implements Serializable  {

	private static final long serialVersionUID = 5994212188548200203L;
	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	private EntityManager em;

	@Inject
	Conversation conversation;

	@Inject
	ProformaRegistration proformaRegistration;

	@Inject
	ProformaRepository proformaRepository;

	@Inject
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	private StreamedContent file;
	private StreamedContent streamedContent;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private boolean modificar = false;
	private String tituloPanel = "Registrar Proforma";
	private Proforma selectedProforma;
	
	private Producto[] selectListaProducto;
	private Servicio[] selectListaServicio;
	private Proforma newProforma;
	private boolean nuevo=false;
	private Servicio newServicio;

	public Servicio getNewServicio() {
		return newServicio;
	}

	public void setNewServicio(Servicio newServicio) {
		this.newServicio = newServicio;
	}

	@Inject
	private ProformaVentaRegistration proformaVentaRegistration;

	private List<Proforma> listaProformaServicio;

	@Inject
	private ClienteRepository clienteRepository;
	@Inject
	private ClienteRegistration clienteRegistration;
	private List<Cliente> listaCliente;

	private List<DetalleServicio> listaDetalleServicio = new ArrayList<DetalleServicio>();
	
	private DetalleServicio detalleServicio;
	private DetalleServicio selectedDetalleServicio;
	private Integer cantidad;
	private double precio;
	private double descuento;
	private double total;
	@Inject
	private DetalleServicioRegistration detalleServicioRegistration;
	@Inject
	private DetalleServicioRepository detalleServicioRepository;
	@Inject
	private ServicioRepository servicioRepository;
	private List<Servicio> listaServicio;

	private boolean quitar;
	private boolean agregar;

	@Inject
	private DetalleServicioVentaRegistration detalleServicioVentaRegistration;
	
	//MODAL AGREGAR SERVICIO
	private Servicio selectedServicio;
	private double cantidadServicio;
	private double totalDescuento;
	private double totalServicio;
	
	@PostConstruct
	public void initNewProforma() {

		// initConversation();
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init NewProformaServicio*********************************");
		System.out.println("request.getClass().getName():"
				+ request.getClass().getName());
		System.out.println("isVentas:" + request.isUserInRole("ventas"));
		System.out.println("remoteUser:" + request.getRemoteUser());
		System.out.println("userPrincipalName:"
				+ (request.getUserPrincipal() == null ? "null" : request
						.getUserPrincipal().getName()));
		
		usuarioSession = usuarioRepository.findByLogin(request.getUserPrincipal().getName());
		
		newProforma = new Proforma();
		newProforma.setEstado("AC");
		newProforma.setTipoProforma("SERVICIO");
		newProforma.setTotalDescuento(0);
		newProforma.setTotal(0);
		newProforma.setObservacion("NINGUNO");
		newProforma.setFechaRegistro(new Date());
		newProforma.setUsuarioRegistro(usuarioSession.getLogin());
		newProforma.setFechaEntrega(new Date());
		
		selectedProforma = null;
		selectedDetalleServicio = null;

		

		listaProformaServicio = proformaRepository.findAllOrderedFechaRegistro();

		listaCliente = clienteRepository.traerClientesActivos();

		modificar = false;
		tituloPanel = "Registrar Proforma";
		nuevo = false;
		quitar = false;
		agregar = false;
		selectedProforma = new Proforma();

	}

	// GENERACION DE PROFORMA DE VENTA SERVICIO

	private ProformaVenta newProformaVenta;

	public void crearProformaVenta() {
		try {
			System.out.println("Ingreso a crearProformaVenta...");
			newProformaVenta.setCliente(newProforma.getCliente());
			newProformaVenta.setFechaRegistro(new Date());
			newProformaVenta.setUsuarioRegistro(usuarioSession.getLogin());
			proformaVentaRegistration.register(newProformaVenta);
			copiarRegistrosAVenta();
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Proforma Venta Creada", "Codigo: PV00"
							+ newProformaVenta.getId().toString());
			facesContext.addMessage(null, m);

			vistaPreviaProformaVenta();
			initNewProforma();
		} catch (Exception e) {
			System.out
					.println("Error en crearProformaVenta: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void copiarRegistrosAVenta() {
		try {
			System.out.println("Ingreso a copiarRegistrosAVenta"
					+ newProformaVenta.getId());
			for (DetalleServicio value : listaDetalleServicio) {
				DetalleServicioVenta detalleVenta = new DetalleServicioVenta();
				detalleVenta.setTotal(value.getTotal());
				detalleVenta.setCantidad(value.getCantidad());
				detalleVenta.setProforma(newProformaVenta);
				detalleVenta.setPrecio(value.getPrecio());
				detalleVenta.setEstado(value.getEstado());
				detalleVenta.setCorrelativo(value.getCorrelativo());
				detalleVenta.setFechaRegistro(value.getFechaRegistro());
				detalleVenta.setUsuarioRegistro(value.getUsuarioRegistro());
				detalleVenta.setServicio(value.getServicio());
				// REGISTRAR DETALLE
				detalleServicioVentaRegistration.register(detalleVenta);
				System.out.println("REGISTRADO DETALLE : "
						+ detalleVenta.getId());

			}
		} catch (Exception e) {
			System.err.println("Error en copiarRegistrosAVenta : "
					+ e.getMessage());
			e.getStackTrace();
		}
	}
	
	//BUSCAR SERVICIO 
	public List<Servicio> completeSearhServicio(String query) {
		return servicioRepository.findAllServicesForDescription(query);
    }
	
	//SELECTED SERVICIO
	public void onItemSelectServicio(SelectEvent event) {
//		Servicio servicio = (Servicio) event.getObject();
		System.out.println("onItemSelectServicio: "+event.getObject().toString());
		//selectedServicio = servicio;
		selectedServicio = em.find(Servicio.class, Integer.valueOf(event.getObject().toString()));
		totalDescuento = 0;
		cantidadServicio = 1;
		totalServicio = calcularTotalServicio(selectedServicio.getPrecioUnitarioVenta(), cantidadServicio, totalDescuento);
		
	}
//	
//	public static void main(String[] args){
//		System.out.println("Precio: "+calcularTotalServicio(1000, 1, 0));
//	}
//	
	
	
	
	
	public void actualizarMonto(){
		System.out.println("Ingreso a actualizarMonto...");
		System.out.println("Cantidad Servicio: " + this.getCantidadServicio()); //cantidadServicio
		System.out.println("Descuento: " + this.getTotalDescuento());
		totalServicio = calcularTotalServicio(selectedServicio.getPrecioUnitarioVenta(), this.getCantidadServicio(), this.getTotalDescuento());
	}
	
	public static double calcularTotalServicio(double precioUnit, double cantidad, double descuento){
		try {
			if(cantidad>=1 && precioUnit>=0){
				return (precioUnit*cantidad) - (descuento*precioUnit*cantidad)/100;
			}else
				return 0;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en calcularTotalServicio: "+e.getMessage());
			return 0;
		}
	}
	
	public static double cancularPorcentaje(double precioUnit, double descuento){
		try {
			if(precioUnit>=0){
				return (descuento*precioUnit)/100;
			}else
				return 0;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en cancularPorcentaje: "+e.getMessage());
			return 0;
		}
	}

	public void showModalCrearProformaVenta() {

		try {
			System.out.println("Ingreso a showModalCrearProformaVenta...");
			newProformaVenta = new ProformaVenta();
			newProformaVenta.setCliente(newProforma.getCliente());
			newProformaVenta.setComisionMedico(true);
			newProformaVenta.setCredito(false);
			newProformaVenta.setEstado("AC");
			newProformaVenta.setFechaRegistro(new Date());
			newProformaVenta.setUsuarioRegistro(usuarioSession.getLogin());
			newProformaVenta.setFechaEntrega(newProforma.getFechaEntrega());
			newProformaVenta.setProforma(newProforma);
			newProformaVenta.setTotalPagar(newProforma.getTotal());

		} catch (Exception e) { // 
			System.out.println("Error en showModalCrearProformaVenta: "
					+ e.getMessage());
			e.printStackTrace();
		}

	}

	public String obtenerFecha(Date fecha) {
		try {
			String DATE_FORMAT = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			return sdf.format(fecha);

		} catch (Exception e) {
			System.out.println("Error en obtenerFecha: " + e.getMessage());
			return "NULL";
		}
	}



	public void crearProforma() {
		System.out.println("entro crearProforma...");
		try {
			initNewProforma();
			newProforma = new Proforma();
			newProforma.setEstado("AC");
			newProforma.setTipoProforma("SERVICIO");
			newProforma.setTotalDescuento(0);
			newProforma.setTotal(0);
			newProforma.setObservacion("NINGUNO");
			newProforma.setFechaRegistro(new Date());
			newProforma.setUsuarioRegistro(usuarioSession.getLogin());
			newProforma.setFechaEntrega(new Date());
			proformaRegistration.register(newProforma);
			System.out.println("Proforma Registrada Correctamente");

			listaDetalleServicio.clear();
			nuevo = true;
			modificar = false;

		} catch (Exception e) {
			e.printStackTrace();
		}

		// traer todas las empresas ordenadas por nombre

		detalleServicio = new DetalleServicio();
		nuevo = true;
		modificar = true;

		System.out.println("estado : " + modificar + "  , " + nuevo);

		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Nueva Proforma Creada ","");
		facesContext.addMessage(null, m);
	}

	public void eliminarDetalleItem() {
		try {
			System.out.println("Ingreso a Remover SERVICIO...");
			detalleServicioRegistration.remover(selectedDetalleServicio);
			quitar = false;

			// actualizar cabecera
			listaDetalleServicio = detalleServicioRepository
					.findAllOrderedByIDForProforma(newProforma);
			double importeTotal = 0;
			double importeDescuento = 0;
			newProforma.setTotalDescuento(importeDescuento);
			newProforma.setTotal(importeTotal);
			for (DetalleServicio detalle : listaDetalleServicio) {
				importeTotal += detalle.getTotal();
				importeDescuento += detalle.getDescuento();
			}

			// actualizando cabecera SERVICIO
			newProforma.setTotalDescuento(importeDescuento);
			newProforma.setTotal(importeTotal);
			proformaRegistration.updated(newProforma);

			System.out.println(" ACTUALIZADO");

			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Servicio Removido: ", selectedDetalleServicio.getServicio()
							.getNombreServicio());
			facesContext.addMessage(null, m);
		} catch (Exception e) {
			System.out.println("Error en eliminarDetalleItem: "
					+ e.getMessage());
			e.printStackTrace();
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

	public void buscarProforma() {
		modificar = true;
		nuevo = false;
		System.out.println(modificar + " , " + nuevo);
	}

	public void verModificarProforma() {
		try {
			System.out.println("Ingreso a verModificarProforma...");

			modificar = true;
			nuevo = true;

			if (newProforma.getCliente() != null) {
				nombreCliente = newProforma.getCliente().getNombreCompleto();
			}else{
				nombreCliente = null;
			}
		
			
		} catch (Exception e) {
			System.out.println("Error en verModificarProforma: "
					+ e.getMessage());
		}
	}

	// SELECT EMPRESA CLICK
	public void onRowSelectProformaDBLClick(SelectEvent event) {
		try {
			Proforma empresa = (Proforma) event.getObject();
			System.out.println("onRowSelectProformaDBLClick  "
					+ empresa.getId());
			setSelectedProforma(empresa);
			newProforma = em.find(Proforma.class, empresa.getId());


			if (newProforma.getCliente() != null) {
				nombreCliente = newProforma.getCliente().getNombreCompleto();
			}
			newProforma.setFechaRegistro(new Date());
			newProforma.setUsuarioRegistro(usuarioSession.getLogin());
			listaDetalleServicio = detalleServicioRepository
					.findAllOrderedByIDForProforma(newProforma);
			tituloPanel = "Modificar Proforma";
			modificar = true;
			nuevo = true;
			System.out.println(modificar + " , " + nuevo);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in onRowSelectProformaDBLClick: "
					+ e.getMessage());
		}
	}

	// SELECT EMPRESA CLICK
	public void onRowSelectProformaClick(SelectEvent event) {
		try {
			Proforma empresa = (Proforma) event.getObject();
			System.out.println("onRowSelectProformaClick  " + empresa.getId());
			setSelectedProforma(empresa);
			newProforma = em.find(Proforma.class, empresa.getId());


			if (newProforma.getCliente() != null) {
				nombreCliente = newProforma.getCliente().getNombreCompleto();
			}

			newProforma.setFechaRegistro(new Date());
			newProforma.setUsuarioRegistro(usuarioSession.getLogin());
			listaDetalleServicio = detalleServicioRepository
					.findAllOrderedByIDForProforma(newProforma);

			tituloPanel = "Modificar Proforma";
			modificar = true;
			nuevo = false;
			System.out.println(modificar + " , " + nuevo);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in onRowSelectProformaClick: "
					+ e.getMessage());
		}
	}

	public void calcualteTotals() {
		System.out.println("ENtry in calcualteTotals");
		double importeTotal = 0;
		double importeDescuento = 0;
		newProforma.setTotalDescuento(importeDescuento);
		newProforma.setTotal(importeTotal);
		Integer index = 1;
		for (DetalleServicio detalle : listaDetalleServicio) {
			/*
			 * detalle.setTotal((detalle.getCantidad()*detalle.getProductop().
			 * getPrecioUnitarioVenta())-detalle.getDescuento());
			 */
			detalle.setTotal(calcularTotalServicio(detalle.getPrecio(), detalle.getCantidad(), detalle.getDescuento()));
//			detalle.setTotal((detalle.getCantidad() * detalle.getPrecio()) - detalle.getDescuento());
			importeTotal += detalle.getTotal();
			//importeDescuento += detalle.getDescuento();
			importeDescuento += detalle.getCantidad() * cancularPorcentaje(detalle.getPrecio(), detalle.getDescuento());
			detalle.setCorrelativo(index);
			index++;
		}

		// actualizando cabecera SERVICIO
		newProforma.setTotalDescuento(importeDescuento);
		newProforma.setTotal(importeTotal);
	}
	
	public boolean existServicio(Servicio s) {
		for (DetalleServicio value : listaDetalleServicio) {
			System.out.println("Producto : " + value.getServicio().getId());
			if (value.getServicio().getId() == s.getId()) {
				return true;
			}
		}
		return false;
	}

	// SELECT PRODUCTO CLICK
	public void onRowSelectProductoClick(SelectEvent event) {
		try {
			Servicio servicio = (Servicio) event.getObject();
			System.out.println("onRowSelectProductoClick  " + servicio.getId());
			System.out.println("INGRESO A CREAR PROFORMA....proforma = "
					+ newProforma.getId());
			setSelectedServicio(servicio);
			newServicio = new Servicio();
			newServicio = em.find(Servicio.class, servicio.getId());

			detalleServicio = new DetalleServicio();
			newServicio.setFechaRegistro(new Date());
			newServicio.setUsuarioRegistro(usuarioSession.getLogin());
			detalleServicio.setFechaRegistro(new Date());
			detalleServicio.setUsuarioRegistro(usuarioSession.getLogin());
			/* detalleFarmaco.setPrecio(newProducto.getPrecioUnitarioVenta()); */
			detalleServicio.setPrecio(0);
			detalleServicio.setServicio(newServicio);
			detalleServicio.setProforma(newProforma);
			detalleServicio.setCantidad(0);
			/*
			 * detalleFarmaco.setTotal(detalleFarmaco.getProductop().
			 * getPrecioUnitarioVenta());
			 */
			detalleServicio.setTotal(0);
			calcualteTotals();

			tituloPanel = "Modificar Proforma";
			modificar = true;

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in onRowSelectProductoClick: "
					+ e.getMessage());
		}
	}

	// modificar detalle farmaco
	public void onRowSelectDetalleFarmacoClick(SelectEvent event) {
		try {
			selectedDetalleServicio = (DetalleServicio) event.getObject();
			System.out.println("onRowSelectDetalleFarmacoClick  "
					+ selectedDetalleServicio.getId());

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in onRowSelectDetalleFarmacoClick: "
					+ e.getMessage());
		}
	}

	public void actualizarDetalleFarmaco() {
		try {
			System.out.println("Ingreso a actualizarDetalleFarmaco...");
			detalleServicio.setUsuarioRegistro(usuarioSession.getLogin());
			detalleServicio.setFechaRegistro(new Date());
			detalleServicio.setEstado("AC");
			/*
			 * detalleFarmaco.setTotal((detalleFarmaco.getCantidad()*detalleFarmaco
			 * .
			 * getProductop().getPrecioUnitarioVenta())-detalleFarmaco.getDescuento
			 * ());
			 */
			detalleServicio.setTotal((detalleServicio.getCantidad() * 0)
					- detalleServicio.getDescuento());
			detalleServicioRegistration.updated(detalleServicio);
			System.out.println("DETALLE FARMACO MODIFICADO");

			// actualizar cabecera farmaco
			listaDetalleServicio = detalleServicioRepository
					.findAllOrderedByIDForProforma(newProforma);
			double importeTotal = 0;
			double importeDescuento = 0;
			newProforma.setTotalDescuento(importeDescuento);
			newProforma.setTotal(importeTotal);
			for (DetalleServicio detalle : listaDetalleServicio) {
				importeTotal += detalle.getTotal();
				importeDescuento += detalle.getDescuento();
			}

			// actualizando cabecera farmaco
			newProforma.setTotalDescuento(importeDescuento);
			newProforma.setTotal(importeTotal);
			proformaRegistration.updated(newProforma);

			System.out.println(" REGISTRADO");

			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Farmaco Actualizado: ", detalleServicio.getServicio()
							.getNombreServicio());
			facesContext.addMessage(null, m);

		} catch (Exception e) {
			System.out.println("Error en actualizarDetalleFarmaco: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public void actualizar() {
		detalleServicio.setUsuarioRegistro(usuarioSession.getLogin());
		detalleServicio.setFechaRegistro(new Date());
		detalleServicio.setEstado("AC");
		/*
		 * detalleServicio.setTotal((detalleFarmaco.getCantidad()*detalleFarmaco.
		 * getProductop
		 * ().getPrecioUnitarioVenta())-detalleFarmaco.getDescuento());
		 */
		detalleServicio.setTotal((detalleServicio.getCantidad() * 0)
				- detalleServicio.getDescuento());

		// actualizar cabecera farmaco
		listaDetalleServicio.add(detalleServicio);
		double importeTotal = 0;
		double importeDescuento = 0;
		newProforma.setTotalDescuento(importeDescuento);
		newProforma.setTotal(importeTotal);
		for (DetalleServicio detalle : listaDetalleServicio) {
			importeTotal += detalle.getTotal();
			importeDescuento += detalle.getDescuento();
		}

		// actualizando cabecera farmaco
		newProforma.setTotalDescuento(importeDescuento);
		newProforma.setTotal(importeTotal);

		System.out.println(" adicionado ");
	}
	
	public void agregarServicio(){
		try {
			
			System.out.println("Ingreso a agregar servicio...");
			DetalleServicio detall = new DetalleServicio();
			detall.setFechaRegistro(new Date());
			detall.setUsuarioRegistro(usuarioSession.getLogin());
			detall.setEstado("AC");
			detall.setPrecio(selectedServicio.getPrecioUnitarioVenta());
			detall.setServicio(selectedServicio);
			detall.setProforma(newProforma);
			detall.setCantidad(this.getCantidadServicio());
			detall.setDescuento(this.getTotalDescuento());
			detall.setTotal(this.getTotalServicio());
			listaDetalleServicio.add(0, detall);
			
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Servicio Agregado!", selectedServicio.getNombreServicio());
			facesContext.addMessage(null, m);
			
			calcualteTotals();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en agregarServicio: "+e.getMessage());
		}
	}
	
	public void agregarDetalle() {
		System.out
				.println("agregarDetalle size: " + selectListaServicio.length);
		for (int i = 0; i < selectListaServicio.length; i++) {
			newServicio = selectListaServicio[i];
			System.out.println("newServicio : "
					+ newServicio.getId());
			newServicio = em.find(Servicio.class, newServicio.getId());
			System.out.println("newServicio : "
					+ newServicio.getId());
			if (!existServicio(newServicio)) {
				System.out.println("no existe el producto");
				DetalleServicio detall = new DetalleServicio();

				detall.setFechaRegistro(new Date());
				detall.setUsuarioRegistro(usuarioSession.getLogin());
				/* detall.setPrecio(newProducto.getPrecioUnitarioVenta()); */
				detall.setPrecio(newServicio.getPrecioUnitarioVenta());
				detall.setServicio(newServicio);
				detall.setProforma(newProforma);
				detall.setCantidad(1);
				/*
				 * detall.setTotal(detall.getProductop().getPrecioUnitarioVenta()
				 * );
				 */
				detall.setTotal(0);
				detall.setEstado("AC");
				/*
				 * detall.setTotal((detall.getCantidad()*detall.getProductop().
				 * getPrecioUnitarioVenta())-detall.getDescuento());
				 */
				detall.setTotal((detall.getCantidad() * detall.getPrecio())
						- detall.getDescuento());
				listaDetalleServicio.add(detall);
				System.out.println("producto adicionado...");

			} else {
				System.out.println("existe el producto");
			}
		}
		calcualteTotals();
		tituloPanel = "Modificar Proforma";
		modificar = true;
	}

	public void agregarDetalleAll() {
		System.out.println("agregarDetalle size: " + listaServicio.size());
		for (int i = 0; i < listaServicio.size(); i++) {
			newServicio = listaServicio.get(i);
			System.out.println("newServicio : "
					+ newServicio.getId());
			newServicio = em.find(Servicio.class, newServicio.getId());
			System.out.println("newServicio : "
					+ newServicio.getId());
			if (!existServicio(newServicio)) {
				System.out.println("no existe el producto");
				DetalleServicio detall = new DetalleServicio();
				newServicio.setFechaRegistro(new Date());
				newServicio.setUsuarioRegistro(usuarioSession.getLogin());
				detall.setFechaRegistro(new Date());
				detall.setUsuarioRegistro(usuarioSession.getLogin());
				/* detall.setPrecio(newServicio.getPrecioUnitarioVenta()); */
				detall.setPrecio(newServicio.getPrecioUnitarioVenta());
				detall.setServicio(newServicio);
				detall.setProforma(newProforma);
				detall.setCantidad(1);
				/*
				 * detall.setTotal(detall.getProductop().getPrecioUnitarioVenta()
				 * );
				 */
				detall.setTotal(0);
				detall.setEstado("AC");
				/*
				 * detall.setTotal((detall.getCantidad()*detall.getProductop().
				 * getPrecioUnitarioVenta())-detall.getDescuento());
				 */
				detall.setTotal((detall.getCantidad() * 0)
						- detall.getDescuento());
				listaDetalleServicio.add(detall);
				System.out.println("Servicio adicionado...");
			} else {
				System.out.println("existe el Servicio");
			}
		}
		calcualteTotals();
		tituloPanel = "Modificar Proforma";
		modificar = true;
	}
	
	
	public void showModalServicios(){
		try {
			System.out.println("Ingreso a showModalServicios...");
			selectedServicio = new Servicio();
			cantidadServicio = 1;
			totalDescuento = 0;
			totalServicio = 0;
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en showModalServicios: " + e.getMessage());
		}
	}
	
	public void obtenerServicios() {
		try {
			System.out.println("Ingreso a servicios");
			listaServicio = servicioRepository.traerServicios();
		} catch (Exception e) {
			System.err.println("Error en obtenerServicios : " + e.getMessage());
			e.getStackTrace();
		}
	}

	public void removeDetalle(DetalleServicio det) {

		System.out.println("Ingreso a removeDetalle");
		listaDetalleServicio.remove(det);
//		for (int i = 0; i < listaServicio.size(); i++) {
//			if (det.getCorrelativo().equals(
//					listaDetalleServicio.get(i).getCorrelativo())) {
//				listaDetalleServicio.remove(i);
//				return;
//			}
//		}
		calcualteTotals();
	}

	public void removeDetalleAll() {
		System.out.println("Entry in removeDetalleAll");
		listaDetalleServicio.clear();
		calcualteTotals();
		newProforma.setTotal(0);
	}

	public void registrarDetalleFarmaco() {
		try {
			System.out.println("INGRESO A REGISTRAR DETALLE_FARMACO");
			// registrando detalle farmaco
			detalleServicio.setUsuarioRegistro(usuarioSession.getLogin());
			detalleServicio.setFechaRegistro(new Date());
			detalleServicio.setEstado("AC");
			/*
			 * detalleFarmaco.setTotal((detalleFarmaco.getCantidad()*detalleFarmaco
			 * .
			 * getServiciop().getPrecioUnitarioVenta())-detalleFarmaco.getDescuento
			 * ());
			 */
			detalleServicio.setTotal((detalleServicio.getCantidad() * 0)
					- detalleServicio.getDescuento());

			// actualizar cabecera farmaco
			listaDetalleServicio.add(detalleServicio);
			double importeTotal = 0;
			double importeDescuento = 0;
			newProforma.setTotalDescuento(importeDescuento);
			newProforma.setTotal(importeTotal);
			for (DetalleServicio detalle : listaDetalleServicio) {
				importeTotal += detalle.getTotal();
				importeDescuento += detalle.getDescuento();
			}

			// actualizando cabecera farmaco
			newProforma.setTotalDescuento(importeDescuento);
			newProforma.setTotal(importeTotal);

			System.out.println(" adicionado ");
		} catch (Exception e) {
			System.out.println("Error en registrarDetalleFarmaco: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public void registrarProforma() {
		try {
			System.out.println("Ingreso a registrarProforma ");
			proformaRegistration.register(newProforma);
			detalleServicioRegistration.removerForProforma(newProforma);
			Integer correlaltivo = 1;
			for (DetalleServicio value : listaDetalleServicio) {
				value.setCorrelativo(correlaltivo);
				correlaltivo++;
				value.setId(null);
				value.setProforma(newProforma);
				value.setServicio(value.getServicio());
				detalleServicioRegistration.register(value);

			}
			initNewProforma();
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Proforma Registrada!", newProforma.getObservacion());
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"Proforma Registrada: %s (id: %d)",
					newProforma.getObservacion(), newProforma.getId()));

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void procesarProforma() {
		try {
			System.out.println("Ingreso a procesarProforma: "
					+ newProforma.getId());
			newProforma.setEstado("PR");
			proformaRegistration.updated(newProforma);
			for (DetalleServicio value : listaDetalleServicio) {
				value.setProforma(newProforma);
				System.err.println("registracion: "
						+ value.getServicio().getNombreServicio());
				if (value.getId() != null) {
					detalleServicioRegistration.updated(value);
				} else {
					detalleServicioRegistration.register(value);
				}

			}
			initNewProforma();
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Proforma Procesada:", "ID: " + newProforma.getId());
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"Proforma Registrada: %s (id: %d)",
					newProforma.getObservacion(), newProforma.getId()));

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void modificarProforma() {
		try {
			System.out.println("Ingreso a modificarProforma: "
					+ newProforma.getId());

			calcualteTotals();
			
			//UPDATE PROFORMA
			newProforma.setUsuarioRegistro(usuarioSession.getLogin());
			newProforma.setFechaRegistro(new Date());
			proformaRegistration.updated(newProforma);

			detalleServicioRegistration.removerForProforma(newProforma);
			Integer correlaltivo = 1;
			for (DetalleServicio value : listaDetalleServicio) {
				value.setCorrelativo(correlaltivo);
				correlaltivo++;
				value.setId(null);
				value.setProforma(newProforma);
				value.setProforma(newProforma);
				value.setServicio(value.getServicio());
				detalleServicioRegistration.register(value);

			}
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Proforma Modificada", "ID: " + newProforma.getId());
			facesContext.addMessage(null, m);

			initNewProforma();

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Modificado Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	public void eliminarProforma() {
		try {
			System.out.println("Ingreso a eliminarProforma: " + newProforma.getId());
			int code = newProforma.getId();
			proformaRegistration.remover(newProforma);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Eliminado Correcto!", "Proforma: "+ code);
			facesContext.addMessage(null, m);
			
			initNewProforma();
			
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Ocurrio un error.");
			facesContext.addMessage(null, m);
		}
	}

	/**
	 * Imprimir Proforma Preprado
	 */
	public StreamedContent getStreamedContent() {
		try {
			System.out.println("Ingreso a descargarPDF...");
			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length()
					- request.getRequestURI().length())
					+ request.getContextPath() + "/";
			System.out.println("urlPath >> " + urlPath);
			String urlPDFreporte = urlPath + "ReporteProformaServicio?idProforma="+ newProforma.getId(); 
			System.out.println("URL Reporte PDF: " + urlPDFreporte);

			URL url = new URL(urlPDFreporte);

			// Read the PDF from the URL and save to a local file
			InputStream is1 = url.openStream();
			File f = stream2file(is1);
			System.out.println("Size Bytes: " + f.length());
			InputStream stream = new FileInputStream(f);
			streamedContent = new DefaultStreamedContent(stream,
					"application/pdf", "Proforma Servicio.pdf");
			setFile(new DefaultStreamedContent(stream, "application/pdf",
					"ReporteProformaFarmaco.pdf"));
			return streamedContent;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error en descargarPDF: " + e.getMessage());
			return null;
		}
	}

	private static File stream2file(InputStream in) throws IOException {

		final File tempFile = File.createTempFile("ReporteVentas", ".pdf");
		tempFile.deleteOnExit();

		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
		}

		return tempFile;
	}

	/**
	 * 
	 * Vista PREVIA DE PROFORMA VENTA
	 */

	public void vistaPreviaProformaVenta() {
		try {
			System.out.println("Ingreso a vistaPreviaProformaVenta....");
			armarURLProformaVenta();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String urlFactura;

	public void armarURLProformaVenta() {
		try {
			System.out.println("Ingreso a armarURLVentasSFV...");
			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length()
					- request.getRequestURI().length())
					+ request.getContextPath() + "/";
			System.out.println("urlPath >> " + urlPath);

			urlFactura = urlPath
					+ "ReporteProformaVentaPrevia?idProformaVenta="
					+ newProformaVenta.getId() + "&pUsuario="
					+ usuarioSession.getName() + "&pEstado=FARMACO";
			System.out.println("URL Reporte Proforma Venta: " + urlFactura);

		} catch (Exception e) {
			System.out.println("Error en armarURLFactura: " + e.getMessage());
		}
	}




	public void onItemSelectCliente(SelectEvent event) {
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Cliente: ", event.getObject().toString());
		for (Cliente cliente : listaCliente) {
			if (cliente.getNombreCompleto()
					.equals(event.getObject().toString())) {
				newProforma.setCliente(cliente);
				
				System.out.println("CLIENTE: "+cliente.getNombreCompleto());
				System.out.println("Credito: "+cliente.getCredito());
				
				newProforma.setCredito(cliente.getCredito());
			}
		}

		System.out.println("Cliente : "
				+ newProforma.getCliente().getNombreCompleto());
		facesContext.addMessage(null, m);
	}

	public void onItemSelectClienteVenta(SelectEvent event) {
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Cliente: ", event.getObject().toString());
		for (Cliente cliente : listaCliente) {
			if (cliente.getNombreCompleto()
					.equals(event.getObject().toString())) {
				newProformaVenta.setCliente(cliente);
			}
		}

		System.out.println("Cliente : "
				+ newProforma.getCliente().getNombreCompleto());
		facesContext.addMessage(null, m);
	}

	private String nombreMedico;
	private String nombreCliente;



	// AUTOCOMPLETADO DE MEDICOS Y CLIENTES
	public List<String> completeDescripcionClientes(String query) {
		try {
			System.out.println("Ingreso a completeDescripcionClientes..."
					+ query);
			listaCliente = clienteRepository.findAllClientsForName(query.toUpperCase());
			List<String> results = new ArrayList<String>();
			for (Cliente cliente : listaCliente) {
				results.add(cliente.getNombreCompleto());
			}
			return results;
		} catch (Exception e) {
			System.out.println("Error en completeDescripcionClientes: "
					+ e.getMessage());
			return null;
		}
	}
	
	
	
	
	//Registro CLiente
		private Cliente newCliente;
		

		private List<Ciudad> listaCiudad= new ArrayList<Ciudad>();
		@Inject CiudadRepository ciudadRepository;
		
		public void crearCliente(){
			try {
				System.out.println("Ingreso a crearCliente");
				newCliente = new Cliente();
				newCliente.setEstado("AC");
				newCliente.setFechaRegistro(new Date());
				newCliente.setUsuarioRegistro(usuarioSession.getLogin());
				setListaCiudad(ciudadRepository.traerCiudad());
			} catch (Exception e) {
				System.err.println("Error en crearCliente : "+e.getMessage());
			}
		}
		
		public void registrarCliente(){
	        try {
	        	System.out.println("Ingreso a registrarCliente: "+newCliente.getId());
	        	clienteRegistration.register(newCliente);
	           
	        	FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente Registrado!", newCliente.getNombreCompleto());
	            facesContext.addMessage(null, m);
	            //pushEventSucursal.fire(String.format("Nuevo Cliente Registrado: %s (id: %d)", newCliente.getNombreCompleto(), newCliente.getId()));
	           
	            
	        } catch (Exception e) {
	            String errorMessage = getRootErrorMessage(e);
	            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
	            facesContext.addMessage(null, m);
	        }
	    }
		
		
			



	public void destroyWorld() {
		addMessage("System Error", "Please try again later.");
	}

	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
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

	// get and set

	@Produces
	@Named
	public List<DetalleServicio> getListaDetalleServicio() {
		return listaDetalleServicio;
	}

	public void setListaDetalleServicio(List<DetalleServicio> listaDetalleServicio) {
		this.listaDetalleServicio = listaDetalleServicio;
	}
	
	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public Proforma getSelectedProforma() {
		return selectedProforma;
	}

	public void setSelectedProforma(Proforma selectedProforma) {
		this.selectedProforma = selectedProforma;
	}

	public Proforma getNewProforma() {
		return newProforma;
	}

	public void setNewProforma(Proforma newProforma) {
		this.newProforma = newProforma;
	}


	public List<Cliente> getListaCliente() {
		return listaCliente;
	}

	public void setListaCliente(List<Cliente> listaCliente) {
		this.listaCliente = listaCliente;
	}

	@Produces
	@Named
	public List<Servicio> getListaServicio() {
		return listaServicio;
	}

	public void setListaServicio(List<Servicio> listaServicio) {
		this.listaServicio = listaServicio;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean activo) {
		this.nuevo = activo;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public boolean isQuitar() {
		return quitar;
	}

	public void setQuitar(boolean quitar) {
		this.quitar = quitar;
	}

	public boolean isAgregar() {
		return agregar;
	}

	public void setAgregar(boolean agregar) {
		this.agregar = agregar;
	}

	public Producto[] getSelectListaProducto() {
		return selectListaProducto;
	}

	public void setSelectListaProducto(Producto[] selectListaProducto) {
		this.selectListaProducto = selectListaProducto;
	}

	public ProformaVenta getNewProformaVenta() {
		return newProformaVenta;
	}

	public void setNewProformaVenta(ProformaVenta newProformaVenta) {
		this.newProformaVenta = newProformaVenta;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getUrlFactura() {
		return urlFactura;
	}

	public void setUrlFactura(String urlFactura) {
		this.urlFactura = urlFactura;
	}

	public String getNombreMedico() {
		return nombreMedico;
	}

	public void setNombreMedico(String nombreMedico) {
		this.nombreMedico = nombreMedico;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}
	
	public Cliente getNewCliente() {
		return newCliente;
	}

	public void setNewCliente(Cliente newCliente) {
		this.newCliente = newCliente;
	}

	public List<Ciudad> getListaCiudad() {
		return listaCiudad;
	}

	public void setListaCiudad(List<Ciudad> listaCiudad) {
		this.listaCiudad = listaCiudad;
	}

	public Servicio getSelectedServicio() {
		return selectedServicio;
	}

	public void setSelectedServicio(Servicio selectedServicio) {
		this.selectedServicio = selectedServicio;
	}

	public Servicio[] getSelectListaServicio() {
		return selectListaServicio;
	}

	public void setSelectListaServicio(Servicio[] selectListaServicio) {
		this.selectListaServicio = selectListaServicio;
	}

	public DetalleServicio getDetalleServicio() {
		return detalleServicio;
	}

	public void setDetalleServicio(DetalleServicio detalleServicio) {
		this.detalleServicio = detalleServicio;
	}

	public DetalleServicio getSelectedDetalleServicio() {
		return selectedDetalleServicio;
	}

	public void setSelectedDetalleServicio(DetalleServicio selectedDetalleServicio) {
		this.selectedDetalleServicio = selectedDetalleServicio;
	}
	
	@Produces
	@Named
	public List<Proforma> getListaProformaServicio() {
		return listaProformaServicio;
	}

	public void setListaProformaServicio(List<Proforma> listaProformaServicio) {
		this.listaProformaServicio = listaProformaServicio;
	}

	public double getCantidadServicio() {
		return cantidadServicio;
	}

	public void setCantidadServicio(double cantidadServicio) {
		this.cantidadServicio = cantidadServicio;
	}

	public double getTotalServicio() {
		return totalServicio;
	}

	public void setTotalServicio(double totalServicio) {
		this.totalServicio = totalServicio;
	}

	public double getTotalDescuento() {
		return totalDescuento;
	}

	public void setTotalDescuento(double totalDescuento) {
		this.totalDescuento = totalDescuento;
	}


}
