package bo.buffalo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.inject.Inject;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Usuario;

/**
 * Servlet implementation class ServletImageTaxistaCar
 */
@WebServlet("/ServletImageUserParam")
public class ServletImageUserParam extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private @Inject UsuarioRepository usuarioRepository;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServletImageUserParam() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String email = "";
		email = request.getParameter("email");
		System.out.println("ServletImageUserParam --->  doGet  : "+request.getContextPath()+"  | email:"+email+"-");
		byte[] imagenData = null;
		try{
			if(email != null){
				//obtener usuario logeado
				Usuario usuarioSession = usuarioRepository.findByEmail(email);
				System.out.println("usuarioSession: "+usuarioSession.getName());
				imagenData = usuarioSession.getFotoPerfil();
				if (imagenData == null) {
					imagenData =  toByteArrayUsingJava(getImageDefaul().getStream());
				}
			}else{
				System.out.println("else -> email=null");
				imagenData =  toByteArrayUsingJava(getImageDefaul().getStream());
			}
			try{

				response.setContentType("image/jpeg");
				response.setHeader("Content-Disposition", "inline; filename=imagen.jpg");
				response.setHeader("Cache-control", "public");
				ServletOutputStream sout = response.getOutputStream();
				sout.write(imagenData);
				sout.flush();
				sout.close();
			} catch (Exception e) {
				System.out.println("Error imagen: "+e.getMessage());
			}
		}catch(Exception e){
			imagenData =  toByteArrayUsingJava(getImageDefaul().getStream());
			response.setContentType("image/jpeg");
			response.setHeader("Content-Disposition", "inline; filename=imagen.jpg");
			response.setHeader("Cache-control", "public");
			ServletOutputStream sout = response.getOutputStream();
			sout.write(imagenData);
			sout.flush();
			sout.close();
			System.out.println("Error doGet: "+e.getMessage());
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}