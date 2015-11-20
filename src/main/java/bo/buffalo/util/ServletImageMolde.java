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

import bo.buffalo.data.FichaTecnicaRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.FichaTecnica;
import bo.buffalo.model.Usuario;

/**
 * Servlet implementation class ServletImageTaxistaCar
 */
@WebServlet("/ServletImageMolde")
public class ServletImageMolde extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private @Inject FichaTecnicaRepository fichaTecnicaRepository;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServletImageMolde() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idficha = "";
		idficha = request.getParameter("idficha");
		System.out.println("ServletImageUserParam --->  doGet  : "+request.getContextPath()+"  | ficha id:"+idficha+"-");
		System.out.println("ServletImageUser --->  doGet  : "+request.getContextPath());
		byte[] imagenData = null;
		try{

			FichaTecnica fichaTecnica=fichaTecnicaRepository.findById(Integer.valueOf(idficha));
			System.out.println("Ficha Tecnica: "+fichaTecnica);
			imagenData = fichaTecnica.getMolde();
			if (imagenData == null) {
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
			System.out.println("Error doGet: "+e.getMessage());
		}
	}

	private StreamedContent getImageDefaul() {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			InputStream stream = classLoader
					.getResourceAsStream("molde.jpg");
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