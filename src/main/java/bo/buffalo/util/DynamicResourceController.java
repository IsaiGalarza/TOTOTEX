package bo.buffalo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import bo.buffalo.data.FichaTecnicaRepository;
import bo.buffalo.model.FichaTecnica;
import bo.buffalo.util.AbstractDynamicResourceHandler;

import javax.enterprise.context.RequestScoped;


/**
 * 
 * @author mauriciobejaranorivera
 *
 */

@Named(value = "dynamicResourceController")
@RequestScoped
public class DynamicResourceController  extends AbstractDynamicResourceHandler implements Serializable{

	private static final long serialVersionUID = -3756873687377670050L;

	//REPOSITORY
	private @Inject FichaTecnicaRepository fichaTecnicaRepository;

	//OBJECT
	private StreamedContent streamedContentMolde;

	@PostConstruct
	public void init(){
		System.out.println("init() - dynamicResourceController");

	}

	private byte[] toByteArrayUsingJava(InputStream is)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int reads = is.read();
		while (reads != -1) {
			baos.write(reads);
			reads = is.read();
		}
		return baos.toByteArray();
	}

	@Override
	protected StreamedContent buildStreamedContentMolde(FacesContext context,Integer idFichaTecnica) throws Exception {
		streamedContentMolde=null;
		FichaTecnica entity = fichaTecnicaRepository.findById(idFichaTecnica);
		try {
			if(idFichaTecnica!=0){
				if(entity.getMolde()!=null){
					InputStream is = null;
					is = new ByteArrayInputStream(entity.getMolde());
					System.out.println("Hay imagen: "+is);
					streamedContentMolde= new DefaultStreamedContent(new ByteArrayInputStream(
							toByteArrayUsingJava(is)));
					return streamedContentMolde;
				}
			}
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/gfx");
			InputStream inputStream = new FileInputStream(path+ File.separator +  "molde.jpg");
			System.out.println("No hay imagen: "+inputStream);
			streamedContentMolde= new DefaultStreamedContent(inputStream, "image/jpeg");

		} catch (Exception e) {
			System.out.println("Error obenter Imagen: "+e.getMessage());
		}
		return streamedContentMolde;
	}

	public void setStreamedContentMolde(StreamedContent streamedContentMolde) {
		this.streamedContentMolde = streamedContentMolde;
	}

}
