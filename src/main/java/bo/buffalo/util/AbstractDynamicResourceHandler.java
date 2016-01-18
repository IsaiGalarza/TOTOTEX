package bo.buffalo.util;

import java.util.Map;

import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * 
 * @author mauriciobejaranorivera
 *
 */
public abstract class AbstractDynamicResourceHandler {

	private static final StreamedContent EMPTY_STREAMED_CONTENT = new DefaultStreamedContent();

	/**
	 * MOLDE
	 * @return StreamedContent
	 * @throws Exception
	 */
	public StreamedContent getStreamedContentMolde() throws Exception {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceHandler handler = context.getApplication().getResourceHandler();
		
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		String param = params.get("param");
		System.out.println("param: "+param);

		if (handler.isResourceRequest(context)){
			return buildStreamedContentMolde(context,Integer.parseInt(param));
		}else{
			return EMPTY_STREAMED_CONTENT;
		}
	}

	protected abstract StreamedContent buildStreamedContentMolde(FacesContext context,Integer idFichaTecnica) throws Exception;

}