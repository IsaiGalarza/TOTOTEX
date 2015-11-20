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
import bo.buffalo.data.DetallePedidoRepository;
import bo.buffalo.data.DetalleServicioRepository;
import bo.buffalo.data.PedidoRepository;
import bo.buffalo.data.ProductoRepository;
import bo.buffalo.data.ProformaRepository;
import bo.buffalo.data.ServicioRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Ciudad;
import bo.buffalo.model.Cliente;
import bo.buffalo.model.DetallePedido;
import bo.buffalo.model.DetalleServicio;
import bo.buffalo.model.DetalleServicioVenta;
import bo.buffalo.model.Pedido;
import bo.buffalo.model.Producto;
import bo.buffalo.model.Proforma;
import bo.buffalo.model.ProformaVenta;
import bo.buffalo.model.Servicio;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.ClienteRegistration;
import bo.buffalo.service.DetallePedidoRegistration;
import bo.buffalo.service.DetalleServicioRegistration;
import bo.buffalo.service.DetalleServicioVentaRegistration;
import bo.buffalo.service.PedidoRegistration;
import bo.buffalo.service.ProformaRegistration;
import bo.buffalo.service.ProformaVentaRegistration;

@Named(value = "pedidoController")
@ConversationScoped
public class PedidoController implements Serializable  {
	
	private static final long serialVersionUID = 1L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	private EntityManager em;

	@Inject
	Conversation conversation;

	@Inject
	PedidoRegistration pedidoRegistration;

	@Inject
	PedidoRepository pedidoRepository;
	
	@Inject 
	ProductoRepository productoRepository;
	
	@Inject
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	private StreamedContent file;
	private StreamedContent streamedContent;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	private boolean modificar = false;
	private String tituloPanel = "Registrar Pedido";
	private Pedido selectedPedido;
	
	private Producto[] selectListaProducto;
	private Pedido newPedido;
	private boolean nuevo=false;


	private List<Pedido> listaPedido;
	private List<DetallePedido> listaDetallePedido;
	private DetallePedido selectedDetallePedido;
	

	@Inject
	private ClienteRepository clienteRepository;
	@Inject
	private ClienteRegistration clienteRegistration;
	private List<Cliente> listaCliente;
	private Integer cantidad;
	private double precio;
	private double descuento;
	private double total;
	
	@Inject
	private DetallePedidoRegistration detallePedidoRegistration;
	@Inject
	private DetallePedidoRepository detallePedidoRepository;

	private boolean quitar;
	private boolean agregar;
	
	//MODAL AGREGAR PRODUCTO
	private Producto selectedProducto;
	private double cantidadProducto;
	private double totalDescuento;
	private double totalProducto;
	
	@PostConstruct
	public void initNewPedido() {

		// initConversation();
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init initNewPedido*********************************");
		System.out.println("request.getClass().getName():"
				+ request.getClass().getName());
		System.out.println("isVentas:" + request.isUserInRole("ventas"));
		System.out.println("remoteUser:" + request.getRemoteUser());
		System.out.println("userPrincipalName:"
				+ (request.getUserPrincipal() == null ? "null" : request
						.getUserPrincipal().getName()));
		
		usuarioSession = usuarioRepository.findByLogin(request.getUserPrincipal().getName());
		
		newPedido = new Pedido();
		newPedido.setNumeroCuotas(0);
		newPedido.setCredito(false);
		newPedido.setEstado("AC");
		newPedido.setTotalDescuento(0);
		newPedido.setImporteTotal(0);
		newPedido.setImporteTotalDescuento(0);
		newPedido.setTotalPagado(0);
		newPedido.setTotalSaldo(0);
		newPedido.setObservacion("NINGUNO");
		newPedido.setFechaRegistro(new Date());
		newPedido.setUsuarioRegistro(usuarioSession.getLogin());
		newPedido.setFechaEntrega(new Date());
		
		selectedPedido = null;
		selectedDetallePedido = null;
		
		listaPedido = pedidoRepository.findAllOrderedByRegistro();
		listaCliente = clienteRepository.traerClientesActivos();

		modificar = false;
		tituloPanel = "Registrar Pedido";
		nuevo = false;
		quitar = false;
		agregar = false;
		
		selectedPedido = new Pedido();
		selectedDetallePedido = new DetallePedido();
		
	}
	
	// seleccionar pedido
	public void onRowSelectPedidoClick(SelectEvent event) {
		try {
			selectedPedido = (Pedido) event.getObject();
			System.out.println("onRowSelectPedidoClick  "
					+ selectedPedido.getId());

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoClick: "
					+ e.getMessage());
		}
	}
	
	// SELECT PEDIDO DBL CLICK
	public void onRowSelectPedidoDBLClick(SelectEvent event) {
		try {
			Pedido pedido = (Pedido) event.getObject();
			System.out.println("onRowSelectPedidoDBLClick  "
					+ pedido.getId());
			selectedPedido = pedido;
			newPedido = em.find(Pedido.class, pedido.getId());

			if (newPedido.getCliente() != null) {
				nombreCliente = newPedido.getCliente().getNombreCompleto();
			}
			newPedido.setFechaRegistro(new Date());
			newPedido.setUsuarioRegistro(usuarioSession.getLogin());
			
			
			listaDetallePedido = detallePedidoRepository.traerDetalleProductos(newPedido);
			tituloPanel = "Modificar Pedido";
			
			modificar = true;
			nuevo = true;
			System.out.println(modificar + " , " + nuevo);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in onRowSelectPedidoDBLClick: "
					+ e.getMessage());
		}
	}
	
	// detalle pedido
	public void onRowSelectDetallePedidoClick(SelectEvent event) {
		try {
			selectedDetallePedido = (DetallePedido) event.getObject();
			System.out.println("onRowSelectDetallePedidoClick  "
					+ selectedDetallePedido.getId());

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in onRowSelectDetallePedidoClick: "
					+ e.getMessage());
		}
	}
	
	public void actualizarMonto(){
		System.out.println("Ingreso a actualizarMonto...");
		System.out.println("Cantidad Servicio: " + this.getCantidadProducto()); //cantidadServicio
		System.out.println("Descuento: " + this.getTotalDescuento());
		totalProducto = calcularTotalProducto(selectedProducto.getPrecio(), this.getCantidadProducto(), this.getTotalDescuento());
	}
	
	public void removeDetalle(DetallePedido det) {

		System.out.println("Ingreso a removeDetalle");
		listaDetallePedido.remove(det);
		calcualteTotals();
	}
	
	public void removeDetalleAll() {
		System.out.println("Entry in removeDetalleAll");
		listaDetallePedido.clear();
		calcualteTotals();
		newPedido.setTotalDescuento(0);
		newPedido.setImporteTotal(0);
	}
	
	public void calcualteTotals() {
		System.out.println("ENtry in calcualteTotals");
		double importeTotal = 0;
		double importeDescuento = 0;
		newPedido.setTotalDescuento(importeDescuento);
		newPedido.setImporteTotal(importeTotal);
		Integer index = 1;
		for (DetallePedido detalle : listaDetallePedido) {
			detalle.setTotal(calcularTotalProducto(detalle.getPrecio(), detalle.getCantidad(), detalle.getDescuento()));
//			detalle.setTotal((detalle.getCantidad() * detalle.getPrecio()) - detalle.getDescuento());
			importeTotal += detalle.getTotal();
			//importeDescuento += detalle.getDescuento();
			importeDescuento += detalle.getCantidad() * cancularPorcentaje(detalle.getPrecio(), detalle.getDescuento());
			index++;
		}

		// actualizando pedido
		newPedido.setTotalDescuento(importeDescuento);
		newPedido.setImporteTotal(importeTotal);
	}
	
	public static double calcularTotalProducto(double precioUnit, double cantidad, double descuento){
		try {
			if(cantidad>=1 && precioUnit>=0){
				return (precioUnit*cantidad) - (descuento*precioUnit*cantidad)/100;
			}else
				return 0;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en calcularTotalProducto: "+e.getMessage());
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
	
	// SELECTED PRODUCTO
	public void onItemSelectProducto(SelectEvent event) {
		// Servicio servicio = (Servicio) event.getObject();
		System.out.println("onItemSelectProducto: "
				+ event.getObject().toString());
		// selectedServicio = servicio;
		selectedProducto = em.find(Producto.class,
				Integer.valueOf(event.getObject().toString()));
		totalDescuento = 0;
		cantidadProducto = 1;
		totalProducto = calcularTotalProducto(
				selectedProducto.getPrecio(), cantidadProducto,
				totalDescuento);

	}
	
	// BUSCAR PRODUCTO
	public List<Producto> completeSearhProducto(String query) {
		return productoRepository.buscarProductoNombre(query);
	}
	
	public void crearPedido() {
		System.out.println("entro crearProforma...");
		
		nuevo = true;
		modificar = true;

		System.out.println("estado : " + modificar + "  , " + nuevo);

		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Creando Nuevo Pedido!","");
		facesContext.addMessage(null, m);
	}
	
	public void modificarPedido(){
		System.out.println("Modificar Pedido...");
		
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

	public void verModificarPedido() {
		try {
			System.out.println("Ingreso a verModificarPedido...");

			modificar = true;
			nuevo = true;

			if (newPedido.getCliente() != null) {
				nombreCliente = newPedido.getCliente().getNombreCompleto();
			}else{
				nombreCliente = null;
			}
		
			
		} catch (Exception e) {
			System.out.println("Error en verModificarPedido: "
					+ e.getMessage());
		}
	}

	
	public void agregarProducto(){
		try {
			
			System.out.println("Ingreso a agregar servicio...");
			DetallePedido detall = new DetallePedido();
			detall.setFechaRegistro(new Date());
			detall.setUsuarioRegistro(usuarioSession.getLogin());
			detall.setEstado("AC");
			detall.setPedido(newPedido);
			detall.setProducto(selectedProducto);
			detall.setCantidad(this.getCantidadProducto());
			detall.setDescuento(this.getTotalDescuento());
			detall.setTotal(this.getTotalProducto());
			listaDetallePedido.add(0, detall);
			
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Producto Agregado!","");
			facesContext.addMessage(null, m);
//			
			calcualteTotals();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en agregarProducto: "+e.getMessage());
		}
	}
	
	
	public void procesarPedido() {
		try {
			System.out.println("Ingreso a procesarPedido: "
					+ newPedido.getId());
			newPedido.setEstado("PR");
			pedidoRegistration.updated(newPedido);
//			for (DetalleServicio value : listaDetalleServicio) {
//				value.setProforma(newProforma);
//				System.err.println("registracion: "
//						+ value.getServicio().getNombreServicio());
//				if (value.getId() != null) {
//					detalleServicioRegistration.updated(value);
//				} else {
//					detalleServicioRegistration.register(value);
//				}
//
//			}
			initNewPedido();
			
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Pedido Procesado:", "ID: " + newPedido.getId());
			facesContext.addMessage(null, m);
			pushEventSucursal.fire(String.format(
					"Pedido Registrada: %s (id: %d)",
					newPedido.getObservacion(), newPedido.getId()));

		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registro Incorrecto.");
			facesContext.addMessage(null, m);
		}
	}

	

	public void eliminarPedido() {
		try {
			System.out.println("Ingreso a eliminarPedido: " + newPedido.getId());
			int code = newPedido.getId();
			pedidoRegistration.remover(newPedido);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Pedido Borrado!", "Pedido: "+ code);
			facesContext.addMessage(null, m);
			
			initNewPedido();
			
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
			String urlPDFreporte = urlPath + "ReporteProformaServicio?idProforma="+ newPedido.getId(); 
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
					+ newPedido.getId() + "&pUsuario="
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
				newPedido.setCliente(cliente);
				
				System.out.println("CLIENTE: "+cliente.getNombreCompleto());
				System.out.println("Credito: "+cliente.getCredito());
				
				newPedido.setCredito(cliente.getCredito());
			}
		}

		System.out.println("Cliente : "
				+ newPedido.getCliente().getNombreCompleto());
		facesContext.addMessage(null, m);
	}

	public void onItemSelectClienteVenta(SelectEvent event) {
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Cliente: ", event.getObject().toString());
		for (Cliente cliente : listaCliente) {
			if (cliente.getNombreCompleto()
					.equals(event.getObject().toString())) {
				newPedido.setCliente(cliente);
			}
		}

		System.out.println("Cliente : "
				+ newPedido.getCliente().getNombreCompleto());
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

	public List<Cliente> getListaCliente() {
		return listaCliente;
	}

	public void setListaCliente(List<Cliente> listaCliente) {
		this.listaCliente = listaCliente;
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

	public double getTotalDescuento() {
		return totalDescuento;
	}

	public void setTotalDescuento(double totalDescuento) {
		this.totalDescuento = totalDescuento;
	}


	public List<Pedido> getListaPedido() {
		return listaPedido;
	}


	public void setListaPedido(List<Pedido> listaPedido) {
		this.listaPedido = listaPedido;
	}
	
	public Pedido getNewPedido() {
		return newPedido;
	}


	public void setNewPedido(Pedido newPedido) {
		this.newPedido = newPedido;
	}


	public Pedido getSelectedPedido() {
		return selectedPedido;
	}


	public void setSelectedPedido(Pedido selectedPedido) {
		this.selectedPedido = selectedPedido;
	}

	public List<DetallePedido> getListaDetallePedido() {
		return listaDetallePedido;
	}

	public void setListaDetallePedido(List<DetallePedido> listaDetallePedido) {
		this.listaDetallePedido = listaDetallePedido;
	}

	public double getTotalProducto() {
		return totalProducto;
	}

	public void setTotalProducto(double totalProducto) {
		this.totalProducto = totalProducto;
	}
	
	public double getCantidadProducto() {
		return cantidadProducto;
	}

	public void setCantidadProducto(double cantidadProducto) {
		this.cantidadProducto = cantidadProducto;
	}
	
	public Producto getSelectedProducto() {
		return selectedProducto;
	}

	public void setSelectedProducto(Producto selectedProducto) {
		this.selectedProducto = selectedProducto;
	}

	public DetallePedido getSelectedDetallePedido() {
		return selectedDetallePedido;
	}

	public void setSelectedDetallePedido(DetallePedido selectedDetallePedido) {
		this.selectedDetallePedido = selectedDetallePedido;
	}


}
