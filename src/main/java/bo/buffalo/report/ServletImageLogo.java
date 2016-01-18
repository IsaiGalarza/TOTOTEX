package bo.buffalo.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Servlet implementation class ServletImageTaxistaCar
 */
@WebServlet("/ServletImageLogo")
public class ServletImageLogo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServletImageLogo() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String pId = "";
		pId = request.getParameter("pId");
		System.out.println("ServletImageUserParam --->  doGet  : "+request.getContextPath()+"  | pId:"+pId+"-");
		byte[] imagenData = null;
		try{
			imagenData =  toByteArrayUsingJava(getImageDefault().getStream());

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
			imagenData =  toByteArrayUsingJava(getImageDefault().getStream());
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

	private StreamedContent getImageDefault() {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		InputStream stream = classLoader
				.getResourceAsStream("logo.jpg");
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