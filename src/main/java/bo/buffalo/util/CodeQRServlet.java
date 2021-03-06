package bo.buffalo.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
  
import java.util.Map;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

@WebServlet("/codeQR")
public class CodeQRServlet extends HttpServlet {

	private static final long serialVersionUID = -8293842468288122208L;

	@Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
		
		try {
			 String qrtext = request.getParameter("qrtext");
		        System.out.println("Texto Parametro: " + qrtext);
		        
		        if(!qrtext.isEmpty()){
//			        ByteArrayOutputStream out2 = QRCode.from(qrtext).to(
//	                ImageType.PNG).stream();
	        
				        ByteArrayOutputStream out = QRCode.from(qrtext).to(ImageType.PNG).withSize(200, 200).stream();
				        
				        
				        response.setContentType("image/png");
				        response.setContentLength(out.size());
				         
				        OutputStream outStream = response.getOutputStream();
				        
				        outStream.write(out.toByteArray());
				 
				        outStream.flush();
				        outStream.close();
		        }else{
		        	System.out.println("Esperando QR...");
		        }

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Error en QRCode: "+e.getMessage());
		}
		
       
    }
	
//	@Override
//    protected void doGet(HttpServletRequest request,
//            HttpServletResponse response) throws ServletException, IOException {
//		
//		try {
//			
//			String qrCodeData = "Codigo!";
//			String filePath = "QRCode2.png";
//			String charset = "UTF-8"; // or "ISO-8859-1"
//			Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
//			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
//
//			BufferedImage bufferedImage = createQRCodeToBufferedImage(qrCodeData, filePath, charset, hintMap, 200, 200);
//			
//			Image image = bufferedImage;
//			
//	        String qrtext = request.getParameter("qrtext");
//	        System.out.println("Texto Parametro: " + qrtext);
//	        
//	        ByteArrayOutputStream out = QRCode.from(qrtext).to(
//	                ImageType.PNG).stream();
//	         
//	        response.setContentType("image/png");
//	        response.setContentLength(out.size());
//	         
//	        OutputStream outStream = response.getOutputStream();
//	 
//	        outStream.write(out.toByteArray());
//	 
//	        outStream.flush();
//	        outStream.close();
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			System.out.println("Error en QRCode: "+e.getMessage());
//		}
//		
//    }

	public static void createQRCode(String qrCodeData, String filePath,
			String charset, Map hintMap, int qrCodeheight, int qrCodewidth)
			throws WriterException, IOException {
 		
		BitMatrix matrix = new MultiFormatWriter().encode(
				new String(qrCodeData.getBytes(charset), charset),
				BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
		
		MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath
				.lastIndexOf('.') + 1), new File(filePath));
	}
	
	public BufferedImage createQRCodeToBufferedImage(String qrCodeData, String filePath,
			String charset, Map hintMap, int qrCodeheight, int qrCodewidth)
			throws WriterException, IOException {
 		
		BitMatrix matrix = new MultiFormatWriter().encode(
				new String(qrCodeData.getBytes(charset), charset),
				BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
		MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath
				.lastIndexOf('.') + 1), new File(filePath));
		
		return MatrixToImageWriter.toBufferedImage(matrix);

	}
	 
}
