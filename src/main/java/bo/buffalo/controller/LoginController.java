package bo.buffalo.controller;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.Principal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Usuario;
import bo.buffalo.util.DateUtility;



@Named
@SessionScoped
public class LoginController implements Serializable {

	
	private static final long serialVersionUID = -17662900381487364L;
	@Inject
    private transient Logger logger;
	private @Inject FacesContext facesContext;
	private @Inject UsuarioRepository usuarioRepository;
    private String username;
    private String password;
    private Usuario usuarioSession;
  

    /**
     * Creates a new instance of LoginController //update
     */
    public LoginController() {
    }

    //  Getters and Setters
    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Listen for button clicks on the #{loginController.login} action,
     * validates the username and password entered by the user and navigates to
     * the appropriate page.
     *
     * @param actionEvent
     */
    public void login(ActionEvent actionEvent) {
    	FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
    	
        try {
        	
        	Usuario u = usuarioRepository.findByLogin(username, password);
        	if(u!=null){
        		logger.log(Level.SEVERE, "Usuario Empresa: " +u.getName());
        		
        		logger.log(Level.SEVERE, "Otro Usuario ");
                try {
                    if (request.getUserPrincipal() != null) {
                        request.logout();
                    }
                    String navigateString = "";
                    // Comprueba si nombre de usuario y la contraseña son válidos si no se lanza un ServletException
                    request.login(username, password);
                    logger.log(Level.SEVERE, username +" : " +password);
                    // gets the user principle and navigates to the appropriate page
                    Principal principal = request.getUserPrincipal();
                    if (request.isUserInRole("superadmin")) {
                    	navigateString = "/super/gestion_empresa.xhtml"; 
                    }else{
                    	navigateString = "/pages/dashboard.xhtml";
                    }
//                    } else if (request.isUserInRole("Empresa")) {
//                        navigateString = "/empresa/dashboard.xhtml";
//                    } else if (request.isUserInRole("Usuario")) {
//                        navigateString = "/cliente/company.xhtml";
//                    }
                        setImageUserSession();
                        
                        FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido!", u.getName());
                        facesContext.addMessage(null, m);
                        
                        
                    try {
                        logger.log(Level.INFO, "User ({0}) loging in #" + DateUtility.getCurrentDateTime(), request.getUserPrincipal().getName());
                        context.getExternalContext().redirect(request.getContextPath() + navigateString);
                        
                        FacesMessage mok = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido!", u.getName());
                        facesContext.addMessage(null, mok);
                        
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "IOException, Login Controller" + "Username : " + principal.getName(), ex);
                        
                        FacesMessage me = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Ocurrio una Excepción!");
                        facesContext.addMessage(null, me);
                        
                        //context.addMessage(null, new FacesMessage("Error!", "Ocurrio una Excepción!"));
                    }
                } catch (ServletException e) {
                	logger.log(Level.SEVERE, username +" : " +password);
                    logger.log(Level.SEVERE, e.toString());
                    
                    FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Usuario o contraseña incorrectos.");
                    facesContext.addMessage(null, m);
                    
                    //context.addMessage(null, new FacesMessage("Error!", "El nombre de usuario o la contraseña que ha proporcionado no coincide con nuestros registros."));
                }
        	}  
     
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.log(Level.SEVERE, username +" : " +password);
            logger.log(Level.SEVERE, e.toString());
            
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Usuario o contraseña incorrectos.");
            facesContext.addMessage(null, m);
		}
        
    }

    /**
     * Listen for logout button clicks on the #{loginController.logout} action
     * and navigates to login screen.
     */
    public void logout() {
    	logger.log(Level.INFO,"Cerrar sesion");
    	FacesContext context = FacesContext.getCurrentInstance();
    	HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        logger.log(Level.INFO, "User ({0}) Cerrando sesion #" + DateUtility.getCurrentDateTime(), request.getUserPrincipal().getName());
        if (session != null) {
            session.invalidate();
            try {
				context.getExternalContext().redirect(request.getContextPath() + "/index.xhtml");
			} catch (IOException e) {
				logger.log(Level.INFO,e.toString());
			}
        }
    }
    

	public Usuario getUsuarioSession() {
		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
    	request.getUserPrincipal().getName();
    	usuarioSession = usuarioRepository.findByLogin(request.getUserPrincipal().getName());
    	logger.log(Level.INFO,"Usuario activo: "+usuarioSession.getName());
		return usuarioSession;
	}

	public void setUsuarioSession(Usuario usuarioSession) {
		this.usuarioSession = usuarioSession;
	}
	
	public int diferenciaEnDias(Date fechaMayor, Date fechaMenor) {
		long diferenciaEn_ms = fechaMayor.getTime() - fechaMenor.getTime();
		long dias = diferenciaEn_ms / (1000 * 60 * 60 * 24);
		return (int) dias;
	}
	
	public StreamedContent getImageUserSession(){
		String mimeType = "image/jpg";
		InputStream is = null;
		try{
			HttpSession request = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			byte[] image = (byte[]) request.getAttribute("imageUser");
			is= new ByteArrayInputStream(image);
			return new DefaultStreamedContent(new ByteArrayInputStream(toByteArrayUsingJava(is)), mimeType);
		}catch(Exception e){
			System.out.println("getEmpresaSession() -> error : "+e.getMessage());
			return null;
		}
	}
	public void setImageUserSession() {
		//cargar foto del usuario
		try{
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			byte[] image = getUsuarioSession().getFotoPerfil();
			if(image == null){
				image =  toByteArrayUsingJava(getImageDefaul().getStream());
			}
			session.setAttribute("imageUser", image);

		}catch(Exception e){
			System.out.println("setImageUserSession() - Error: "+e.getMessage());
		}
	}

	private StreamedContent getImageDefaul() {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		InputStream stream = classLoader
				.getResourceAsStream("logo_cajero.jpg");
		return new DefaultStreamedContent(stream, "image/jpeg");
	}

	public static byte[] toByteArrayUsingJava(InputStream is) throws IOException{ 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int reads = is.read();
		while(reads != -1){
			baos.write(reads); reads = is.read(); 
		}
		return baos.toByteArray();
	}
}
