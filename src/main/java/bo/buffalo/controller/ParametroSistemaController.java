package bo.buffalo.controller;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.SelectEvent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.ParametroSistemaRepository;
import bo.buffalo.model.ParametroSistema;
import bo.buffalo.service.ParametroSistemaRegistration;


@Named(value = "parametroSistemaController")
@ConversationScoped
public class ParametroSistemaController implements Serializable{

	private static final long serialVersionUID = 4329595813693192177L;
	public static final String PUSH_CDI_TOPIC = "pushCdi";
	private @Inject ParametroSistemaRepository parametroSistemaRepository;
	private @Inject ParametroSistemaRegistration parametroSistemaRegistration;
	@Inject @Push(topic = PUSH_CDI_TOPIC) Event<String> pushEventSucursal;
	@Inject Conversation conversation;
	private ParametroSistema newParametroSistema;
	private TabView tabView;
	private List<ParametroSistema> listParametros=new ArrayList<>();
	private String tituloPanel;
	private double valorN;
	private String valorT;
	private boolean tipoValor;
	private boolean modificar;
	
	@PostConstruct
	public void initParametros(){
		System.out.println("Iniciando Elementos PS");
		this.tipoValor=true;
		this.modificar=false;
		this.setValorN(0);
		this.setValorT("");
		this.tituloPanel="Registrar Parametro";
		this.newParametroSistema=new ParametroSistema();
		this.listParametros= parametroSistemaRepository.findAllOrderedById();
	}
	
	public void guardarParametro(){
		try {
			if(tipoValor){
				newParametroSistema.setValor(String.valueOf(valorN));
			}else{
				newParametroSistema.setValor(valorT);
			}
			parametroSistemaRegistration.register(newParametroSistema);
			System.out.println("Guardado PS");
			initParametros();
			saveMessage(FacesMessage.SEVERITY_INFO, "Guardado!", "Dato Guardado Correctamente");
		} catch (Exception e) {
			System.out.println("Error en Guardar PS");
			saveMessage(FacesMessage.SEVERITY_ERROR, "Error!", "No se pudo Guardar el Parametro");
		}
	}
	
	public void modificarParametro(){
		try {
			if(tipoValor){
				newParametroSistema.setValor(String.valueOf(valorN));
			}else{
				newParametroSistema.setValor(valorT);
			}
			parametroSistemaRegistration.updated(newParametroSistema);
			initParametros();
			System.out.println("Modificando PS");
			saveMessage(FacesMessage.SEVERITY_INFO, "Modificado!", "Dato Modificado Correctamente");
		} catch (Exception e) {
			System.out.println("Error en Guardar PS");
			saveMessage(FacesMessage.SEVERITY_ERROR, "Error!", "No se pudo Modificar el Parametro");
		}
	}
	
	public void onRowSelectParametroClick(SelectEvent event) {
		try {
			ParametroSistema parametroSistema = (ParametroSistema) event.getObject();
			newParametroSistema=parametroSistema;
			modificar=true;
			try {
				valorN=Double.valueOf(newParametroSistema.getValor());
				tipoValor=true;
			} catch (Exception e) {
				tipoValor=false;
				valorT=newParametroSistema.getValor();
			}
			System.out.println("onRowSelectDetalleClick  " + parametroSistema.getId());

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in onRowSelectDetalleClick: "
					+ e.getMessage());
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
		
	
	public void saveMessage(FacesMessage.Severity severity, String title, String msj) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(severity, title,  msj) );
    }

	public ParametroSistema getNewParametroSistema() {
		return newParametroSistema;
	}

	public void setNewParametroSistema(ParametroSistema newParametroSistema) {
		this.newParametroSistema = newParametroSistema;
	}

	public void setTabView(TabView tabView) {
        this.tabView = tabView;
    }

    public TabView getTabView() {
        FacesContext fc = FacesContext.getCurrentInstance();
        tabView = (TabView) fc.getApplication().createComponent("org.primefaces.component.TabView");
        this.listParametros=parametroSistemaRepository.findAllOrderedById();

        //Se crean dinamicamente las tabs y en su contenido, unas cajas de texto
        for (ParametroSistema sub : listParametros) {
            Tab tab = new Tab();
            tab.setTitle(sub.getDescripcion());
            OutputLabel label= new OutputLabel();
            InputText inputtext = new InputText();
            inputtext.setLabel("Label");
            try {
				Double.valueOf(sub.getValor());
				label.setValue("Valor Numerico: ");
				NumberFormat formatter = new DecimalFormat("#,##0.000");   
				inputtext.setValue(formatter.format(Double.valueOf(sub.getValor())));
			} catch (Exception e) {
				label.setValue("Valor Texto: ");
				inputtext.setValue(sub.getValor());
			}
            tab.getChildren().add(label);
            inputtext.setOnfocus("");
            tab.getChildren().add(inputtext);
      
            tabView.getChildren().add(tab);
        }
        return tabView;
    }

	public List<ParametroSistema> getListParametros() {
		return listParametros;
	}

	public void setListParametros(List<ParametroSistema> listParametros) {
		this.listParametros = listParametros;
	}

	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public boolean isTipoValor() {
		return tipoValor;
	}

	public void setTipoValor(boolean tipoValor) {
		this.tipoValor = tipoValor;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public double getValorN() {
		return valorN;
	}

	public void setValorN(double valorN) {
		this.valorN = valorN;
	}

	public String getValorT() {
		return valorT;
	}

	public void setValorT(String valorT) {
		this.valorT = valorT;
	}

}
