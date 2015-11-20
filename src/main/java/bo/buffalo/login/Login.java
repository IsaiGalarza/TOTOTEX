package bo.buffalo.login;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Named(value = "login")
public class Login{
	
	@Inject
	private Credentials credential;
	
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Login.class);
	
	public boolean authenticate(){
		try {
			logger.info("Entry authenticate...");
			logger.info("Username: "+credential.getUsername());
			logger.info("Password: "+credential.getPassword());
			return true;
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Error in authenticate: "+e.getMessage(), e);
			return false;
		}
	}
	
	
    public void logout() {
//        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    	
    	FacesContext context = FacesContext.getCurrentInstance();  
        try {  
        	
             HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
             HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
             RequestDispatcher dispatcher = request.getRequestDispatcher("/logoutServlet");
             dispatcher.forward(request, response);
        	
        	
        }catch (Exception e) {  
           e.printStackTrace();  
        }  
        finally{  
           context.responseComplete();  
        }  
    	
    	
        //return "/login";
    }
       
    
    public Login() {
		
	}
    
}
