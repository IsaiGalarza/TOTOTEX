package bo.buffalo.controller;

import java.io.Serializable;
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

import org.primefaces.event.SelectEvent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.CompraRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Compra;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.CompraRegistration;

@Named(value="compraController")
@ConversationScoped
public class CompraController implements Serializable{

	private static final long serialVersionUID = -5224755258234711054L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

    @Inject
    private FacesContext facesContext;
    
    @Inject
    private EntityManager em;
    
    @Inject 
	Conversation conversation;
    
    @Inject
    private CompraRegistration compraRegistration;
    
    @Inject
    private CompraRepository compraRepository;
    
    @Inject 
    UsuarioRepository usuarioRepository;
    
    private Usuario usuarioSession;

	@Inject
    @Push(topic = PUSH_CDI_TOPIC)
    Event<String> pushEventCompra;
	
    private boolean modificar = false;
    private String tituloPanel = "Registrar Compra";
    private Compra selectedCompra;
	private Compra newCompra;
	
	private List<Compra> listaCompras = new ArrayList<Compra>();

    // @Named provides access the return value via the EL variable name "servicios" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Compra> getListaCompras() {
        return listaCompras;
    }
	
	@PostConstruct
    public void initNewCompra() {
		
		try {
			
			// initConversation();
	    	beginConversation();
	    	
	    	HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			System.out.println("init NewCompra*********************************");
			System.out.println("request.getClass().getName():"
					+ request.getClass().getName());
			System.out.println("isVentas:" + request.isUserInRole("ventas"));
			System.out.println("remoteUser:" + request.getRemoteUser());
			System.out.println("userPrincipalName:"
					+ (request.getUserPrincipal() == null ? "null" : request
							.getUserPrincipal().getName()));

			usuarioSession = usuarioRepository.findByLogin(request
					.getUserPrincipal().getName());
			System.out.println("Sucursal Usuario: "
					+ usuarioSession.getSucursal().getNombre());
			
			newCompra = new Compra();
			newCompra.setEstado("AC");
			newCompra.setFechaFactura(new Date());
			newCompra.setFechaRegistro(new Date());
			newCompra.setCodigoControl("0");
			newCompra.setNumeroDUI("0");
			newCompra.setUsuarioRegistro(usuarioSession.getLogin());
			
			//actualizar libro de compras
			listaCompras.clear();
			listaCompras = compraRepository.findAllOrderedByID();
			modificar = false;
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en initNewCompra: "+e.getMessage());
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

	// SELECT COMPRA CLICK
	public void onRowSelectCompraClick(SelectEvent event) {
		try {
			Compra compra = (Compra) event.getObject();
			System.out.println("onRowSelectCompraClick  " + compra.getId());
			selectedCompra = compra;
			
			//cargar para modificar
			newCompra = em.find(Compra.class, compra.getId());
			newCompra.setFechaRegistro(new Date());
			newCompra.setUsuarioRegistro(usuarioSession.getLogin());
			
			tituloPanel = "Modificar Compra";
			modificar = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error in onRowSelectCompraClick: "
					+ e.getMessage());
		}
	}

	
	public String obtenerMes(Date fecha){
    	try {
    		String DATE_FORMAT = "MM";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    		return sdf.format(fecha);
    		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en obtenerMes: "+e.getMessage());
			return null;
		}
	}
	
	public String obtenerGestion(Date fecha){
    	try {
    		String DATE_FORMAT = "yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    		return sdf.format(fecha);
    		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en obtenerGestion: "+e.getMessage());
			return null;
		}
	}
	
	public int obtenerCorrelativo(String mes, String gestion){
		try {
			return compraRepository.traerComprasGestionFiscal(mes, gestion).size();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}
	}
	
	//CORREGIR ESTE CAMPO/
	
	public void actualizarImportes(){
//		try {
//			System.out.println("Ingreso a actualizarImportes..."+usuarioSession.getSucursal().getNormaAplicada());
//			if(usuarioSession.getSucursal().getNormaAplicada().equals("NSF-07")){
//				//importeBaseCreditoFiscal = ImporteICE - ImporteExcentos
//				double importeBaseCreditoFiscal = this.getNewCompra().getImporteTotal()-this.getNewCompra().getImporteICE()-this.getNewCompra().getImporteExcentos();
//				this.getNewCompra().setImporteBaseCreditoFiscal(importeBaseCreditoFiscal);
//			}
//			if(usuarioSession.getSucursal().getNormaAplicada().equals("SFV-14")){
//				//importeSubTotal = ImporteTotal = Importe No Sujeto a Credito Fiscal
//				double importeSubTotal =  this.getNewCompra().getImporteTotal() - this.getNewCompra().getImporteNoSujetoCreditoFiscal();
//				this.getNewCompra().setImporteSubTotal(importeSubTotal);
//				
//				//importeBaseCreditoFiscal = importeSubTotal - descuentosBonosRebajas;
//				double importeBaseCreditoFiscal = this.getNewCompra().getImporteSubTotal()-this.getNewCompra().getDescuentosBonosRebajas();
//				this.getNewCompra().setImporteBaseCreditoFiscal(importeBaseCreditoFiscal);
//			}
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.out.println("Error en actualizarImportes: "+e.getMessage());
//		}
	}
	
	public void registrarCompra(){
        try {
            
            System.out.println("Ingreso a registrarCompra: "+newCompra.getId());
            String mes = obtenerMes(newCompra.getFechaFactura());
        	String gestion = obtenerGestion(newCompra.getFechaFactura());
            int correlativo = obtenerCorrelativo(mes, gestion);
            System.out.println("Correlativo:  Mes:"+mes+" - Gestion: "+gestion+":  "+correlativo);
            newCompra.setCorrelativo(correlativo+1);
        	newCompra.setMes(mes);
        	newCompra.setGestion(gestion);
            compraRegistration.register(newCompra);
            
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Compra Registrado!", "Factura: "+newCompra.getNumeroFactura());
            facesContext.addMessage(null, m);
//            pushEventCompra.fire(String.format("Compra Registrada: %s (id: %d)", newCompra.getId(), "Factura: "+newCompra.getNumeroFactura()));
            
            System.out.println("Paso..................");
            //actualizar libro de compras
    		listaCompras.clear();
    		listaCompras = compraRepository.findAllOrderedByID();
            
    		initNewCompra();
            
        } catch (Exception e) {
        	e.printStackTrace();
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void modificarCompra(){
        try {
        	System.out.println("Ingreso a modificarCompra: "+newCompra.getId());
        	String mes = obtenerMes(newCompra.getFechaFactura());
        	String gestion = obtenerGestion(newCompra.getFechaFactura());
        	newCompra.setMes(mes);
        	newCompra.setGestion(gestion);
        	newCompra.setFechaRegistro(new Date());
        	newCompra.setUsuarioRegistro(usuarioSession.getLogin());
        	compraRegistration.updated(newCompra);
            
        	FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Compra Modificada!", "Factura: "+newCompra.getNumeroFactura());
            facesContext.addMessage(null, m);
//            pushEventCompra.fire(String.format("Compra Modificada: %s (id: %d)", newCompra.getId(), "Factura: "+newCompra.getNumeroFactura()));
        	
            initNewCompra();
        } catch (Exception e) {
        	e.printStackTrace();
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Modificado Incorrecto.");
            facesContext.addMessage(null, m);
        }
    }
	
	public void eliminarCompra(){
        try {
        	System.out.println("Ingreso a eliminarCompra: "+newCompra.getId());
        	newCompra.setFechaRegistro(new Date());
        	newCompra.setUsuarioRegistro(usuarioSession.getLogin());
        	compraRegistration.remover(newCompra);
            
        	FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Compra Borrada!", "Factura: "+newCompra.getNumeroFactura());
            facesContext.addMessage(null, m);
//            pushEventCompra.fire(String.format("Compra Borrada: %s (id: %d)", newCompra.getId(), "Factura: "+newCompra.getNumeroFactura()));
        	
            initNewCompra();
        } catch (Exception e) {
        	e.printStackTrace();
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Borrado Incorrecto.");
            facesContext.addMessage(null, m);
        }
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
	
	
	//get and set
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

	public Compra getSelectedCompra() {
		return selectedCompra;
	}

	public void setSelectedCompra(Compra selectedCompra) {
		this.selectedCompra = selectedCompra;
	}

	public Compra getNewCompra() {
		return newCompra;
	}

	public void setNewCompra(Compra newCompra) {
		this.newCompra = newCompra;
	}
	
	public Usuario getUsuarioSession() {
		return usuarioSession;
	}

	public void setUsuarioSession(Usuario usuarioSession) {
		this.usuarioSession = usuarioSession;
	}
	
}
