package bo.buffalo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.AlmacenProductoRepository;
import bo.buffalo.data.AtributoRepository;
import bo.buffalo.data.ConfeccionistaRepository;
import bo.buffalo.data.DetalleOrdenProduccionRepository;
import bo.buffalo.data.FichaDetalleInsumoAcabadoRepository;
import bo.buffalo.data.FichaInsumoCorteRepository;
import bo.buffalo.data.FichaDetalleProductoRepository;
import bo.buffalo.data.FichaTecnicaRepository;
import bo.buffalo.data.OperarioRepository;
import bo.buffalo.data.OrdenProduccionRepository;
import bo.buffalo.data.ProductoRepository;
import bo.buffalo.data.TipoProductoRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Atributo;
import bo.buffalo.model.AtributoTipoProducto;
import bo.buffalo.model.Confeccionista;
import bo.buffalo.model.DetalleOrdenProduccion;
import bo.buffalo.model.FichaDetalleInsumoAcabado;
import bo.buffalo.model.FichaInsumoCorte;
import bo.buffalo.model.FichaDetalleProducto;
import bo.buffalo.model.FichaTecnica;
import bo.buffalo.model.Operario;
import bo.buffalo.model.OrdenProduccion;
import bo.buffalo.model.ProcesoCorte;
import bo.buffalo.model.Producto;
import bo.buffalo.model.TipoProducto;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.ConfeccionistaRegistration;
import bo.buffalo.service.FichaDetalleProductoRegistration;
import bo.buffalo.service.FichaTecnicaRegistration;
import bo.buffalo.service.OperarioRegistration;
import bo.buffalo.service.OrdenProduccionRegistration;
import bo.buffalo.service.ProcesoCorteRegistration;
import bo.buffalo.service.TipoProductoRegistration;
import bo.buffalo.util.SessionMain;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;

import com.ahosoft.utils.FacesUtil;

/**
 * 
 * @author mauriciobejaranorivera
 *
 */

@Named(value = "fichaTecnicaV2Controller")
@ConversationScoped
public class FichaTecnicaControllerV2 implements Serializable{

	private static final long serialVersionUID = -3756873687377670050L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventSucursal;

	@Inject
	Conversation conversation;

	//REPOSITORY
	private @Inject UsuarioRepository usuarioRepository;
	private @Inject FichaTecnicaRepository fichaTecnicaRepository;
	private @Inject AtributoRepository atributoRepository;
	private @Inject AlmacenProductoRepository almacenProductoRepository;
	private @Inject ProductoRepository productoRepository;
	private @Inject FichaDetalleProductoRepository fichaDetalleProductoRepository;
	private @Inject FichaInsumoCorteRepository fichaDetalleInsumoCorteRepository;
	private @Inject FichaDetalleInsumoAcabadoRepository fichaDetalleInsumoAcabadoRepository;
	private @Inject TipoProductoRepository tipoProductoRepository;
	private @Inject OrdenProduccionRepository ordenProduccionRepository;
	private @Inject DetalleOrdenProduccionRepository detalleOrdenProduccionRepository;
	private @Inject ConfeccionistaRepository confeccionistaRepository;
	private @Inject OperarioRepository operarioRepository;

	//REGISTRATION
	private @Inject FichaTecnicaRegistration fichaTecnicaRegistration;
	private @Inject TipoProductoRegistration tipoProductoRegistration;
	private @Inject ProcesoCorteRegistration procesoCorteRegistration;
	private @Inject ConfeccionistaRegistration confeccionistaRegistration;
	private @Inject OperarioRegistration operarioRegistration;
	private @Inject OrdenProduccionRegistration ordenProduccionRegistration;

	//ESTADO
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean verLista;
	private boolean aprobarFichaTecnica;
	private boolean buttonCargarFichaTecnica;
	private boolean buttonVerLista;
	private boolean modificar; 
	private boolean administrador;
	private boolean detalle;
	private boolean buttonRegistrar;

	//OBJECT
	private FichaTecnica selectedFichaTecnica;
	private Confeccionista selectedConfeccionista;
	private FichaDetalleProducto fichaProducto;
	private FichaInsumoCorte fichaInsumoCorte;
	private FichaDetalleInsumoAcabado fichaInsumoAcabado;
	private FichaTecnica entity;
	private Operario selectedOperario;

	//LIST
	private ArrayList<String> frms;
	private List<FichaDetalleProducto> listaFichaProducto;
	private List<FichaInsumoCorte> listaFichaInsumoCorte;
	private List<FichaDetalleInsumoAcabado> listaFichaInsumoAcabado;
	private List<FichaDetalleProducto> deleteListaFichaProducto;
	private List<FichaInsumoCorte> deleteListaFichaInsumoCorte;
	private List<FichaDetalleInsumoAcabado> deleteListaFichaInsumoAcabado;
	private List<Atributo> listaTalla;
	private List<Producto> listaProductoInsumo;
	private List<Producto> listaProductoTela;
	private List<Atributo> listaColores;
	private List<Atributo> listaMarca;
	private List<FichaTecnica> entitys;
	private List<Confeccionista> listConfeccionista;
	private List<Operario> listOperario;

	//VAR
	private String codigoPrenda;
	private String textConfeccionista;
	private String textoAutoCompleteConfeccionista;
	private String textoAutoCompleteOperario;
	private String titulo;
	private String urlVista;

	//private StreamedContent streamedContent;

	//SESION
	private @Inject SessionMain sessionMain; //variable del login
	private String usuario;
	private String password;
	private Usuario usuarioSistema;

	//ORDEN PRODUCCION
	private OrdenProduccion selectedOrdenProduccion;
	private DetalleOrdenProduccion selectedDetalleOrdenProduccion;

	HttpServletRequest request ;

	@PostConstruct
	public void init(){
		System.out.println("init() - FichaTecnica");
		request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		usuarioSistema = sessionMain.getUsuarioSistema();
		administrador = sessionMain.isAdministrador();

		listConfeccionista = confeccionistaRepository.findAllOrderedByNombre();
		listOperario = operarioRepository.findAllOrderedByNombre();

		newEntity();
		cargarListaTalla();
		cargarListaColor();
		cargarListaMarca();
		cargarListaProductoInsumo();
		cargarListaProductoTela();
		frms=new ArrayList<>();
		frms.add("frm_accion");
		frms.add("frm_list");
		frms.add("frm");
		nuevoEntity();//INICIAR DIRECTO A CREAR FICHA

		String pIdOrdenProduccion = sessionMain.getAttributeSession("pIdOrdenProduccion");
		System.out.println("pIdOrdenProduccion:"+pIdOrdenProduccion);
		if(pIdOrdenProduccion != null){
			//cargar la orden de produccion
			selectedOrdenProduccion = ordenProduccionRepository.findById(Integer.parseInt(pIdOrdenProduccion));
			System.out.println("selectedOrdenProduccion :"+selectedOrdenProduccion.getId());
			//cargar el detalle orden de produccion
			selectedDetalleOrdenProduccion = detalleOrdenProduccionRepository.findbyOrdenProduccion(selectedOrdenProduccion);
			//VERIFICAR:  SI YA SE CREO UNA FICHA TECNICA
			if(selectedDetalleOrdenProduccion.getProcesoCorte().getFichaTecnica() == null){
				System.out.println("FichaTecnica: ");
				//si no hay ficha tecnica:
				this.buttonRegistrar = true;
				this.nuevo = true;
				this.verLista = false;
				this.buttonVerLista = true;
				this.aprobarFichaTecnica = false;
				this.buttonCargarFichaTecnica = false;		
			}else {
				System.out.println("FichaTecnica: "+selectedDetalleOrdenProduccion.getProcesoCorte().getFichaTecnica().getId());
				entity = selectedDetalleOrdenProduccion.getProcesoCorte().getFichaTecnica();
				selectedConfeccionista = entity.getConfeccionista()==null?selectedConfeccionista:entity.getConfeccionista();
				textoAutoCompleteConfeccionista = selectedConfeccionista.getId()==0?"":selectedConfeccionista.getNombre();
				selectedOperario = entity.getOperario()==null?selectedOperario:entity.getOperario();
				textoAutoCompleteOperario = selectedOperario.getId()==0?"":selectedOperario.getNombre();
				// - VERIFICAR SI ESTA PROCESADA
				System.out.println("estado: "+entity.getEstado());
				if(entity.getEstado().equals("AP")){
					this.buttonRegistrar = false;
					this.nuevo = true;
					this.verLista = false;
					this.buttonVerLista = false;
					this.aprobarFichaTecnica = false;
					this.buttonCargarFichaTecnica = false;
					//listaFichaProducto = fichaDetalleProductoRepository.findByFichaTecnica(entity);

				}else if(entity.getEstado().equals("AC")){//  - SI NO ESTA PROCESADA
					//  cargar los parametros por defecto de una ficha tecnica
					this.buttonRegistrar = false;
					this.nuevo = false;
					this.verLista = false;
					this.buttonVerLista = false;
					this.aprobarFichaTecnica = true;
					this.buttonCargarFichaTecnica = false;
					//listaFichaProducto = fichaDetalleProductoRepository.findByFichaTecnica(entity);
				}
			}
			cargarClasePrenda();	
		}
	}

	/**
	 * cargar segun sea la clase:
	 *  - TELA (Tipo - Marca)
	 *  - TALLA
	 */
	private void cargarClasePrenda(){
		String pClasePrenda = selectedOrdenProduccion.getClasePrenda();
		if(pClasePrenda != null){
			switch (pClasePrenda) {
			case "HOMBRE-PANTALON-MAYOR":
				System.out.println("OK - pClasePrenda:"+pClasePrenda);
				codigoPrenda="HPM";
				break;
			case "HOMBRE-PANTALON-JUVENIL":
				System.out.println("OK - pClasePrenda:"+pClasePrenda);
				codigoPrenda="HPJ";
				break;
			case "HOMBRE-PANTALON-KIDS":
				System.out.println("OK - pClasePrenda:"+pClasePrenda);
				codigoPrenda="HPK";
				break;
			case "MUJER-PANTALON-MAYOR":
				System.out.println("OK - pClasePrenda:"+pClasePrenda);
				codigoPrenda="MPM";
				break;
			case "MUJER-PANTALON-JUVENIL":
				System.out.println("OK - pClasePrenda:"+pClasePrenda);
				codigoPrenda="MPJ";
				break;
			case "MUJER-PANTALON-KIDS":
				System.out.println("OK - pClasePrenda:"+pClasePrenda);
				codigoPrenda="MPK";
				break;
				//CAMISA
				// ...
				//OTROS
			default:
				break;
			}
		}
		entity.setCodigo(codigoPrenda+"-"+String.format("%06d", entity.getCorrelativo()));//Ej: HPJ-000001
		entity.setPartida(pClasePrenda);
	}

	private void newEntity(){
		this.selectedOperario = new Operario();
		this.textoAutoCompleteOperario = "";

		this.selectedConfeccionista = new Confeccionista();
		this.textoAutoCompleteConfeccionista = "";

		this.buttonRegistrar = false;
		this.nuevo = true;
		this.verLista = false;
		this.buttonVerLista = false;
		this.aprobarFichaTecnica = false;
		this.buttonCargarFichaTecnica = false;
		selectedFichaTecnica = new FichaTecnica();
		selectedOrdenProduccion = new OrdenProduccion();
		selectedDetalleOrdenProduccion = new DetalleOrdenProduccion();

		selectedConfeccionista = new Confeccionista();
		entity=new FichaTecnica();
		entity.setCorrelativo(obtenerCorrelativo());
		entity.setCodigo(codigoPrenda+"-"+String.format("%06d", entity.getCorrelativo()));//Ej: HPJ-000001
		entity.setEstado("AC");
		entity.setFechaRegistro(new Date());
		entity.setUsuarioRegistro(usuarioSistema.getLogin());
		entitys=fichaTecnicaRepository.findAllByParameterObject("baja", false);
		nuevo=false;
		modificar=false;
		detalle=false;
		titulo="Registrar Ficha Tecnica";
		fichaProducto=new FichaDetalleProducto();
		fichaInsumoCorte=new FichaInsumoCorte();
		fichaInsumoAcabado=new FichaDetalleInsumoAcabado();
		listaFichaProducto=new ArrayList<>();
		listaFichaInsumoCorte=new ArrayList<>();
		listaFichaInsumoAcabado=new ArrayList<>();
		deleteListaFichaProducto=new ArrayList<>();
		deleteListaFichaInsumoCorte=new ArrayList<>();
		deleteListaFichaInsumoAcabado=new ArrayList<>();
	}

	private int obtenerCorrelativo(){
		return fichaTecnicaRepository.findCorrelativo();
	}

	public void initConversation() {
		if (!FacesContext.getCurrentInstance().isPostback() && conversation.isTransient()) {
			conversation.begin();
			System.out.println(">>>>>>>>>> CONVERSACION INICIADA...");
		}
	}

	public String endConversation() {
		if (!conversation.isTransient()) {
			conversation.end();
			System.out.println(">>>>>>>>>> CONVERSACION TERMINADA...");
		}
		return "orden_ingreso.xhtml?faces-redirect=true";
	}

	private @Inject FichaDetalleProductoRegistration fichaDetalleProductoRegistration;
	/* Action Register*/
	public void register(){
		try {
			if(validarCampos()){
				//verificar si la ficha tecnica fue seleccionada de una anterior
				if(entity.getId()==0){
					//entity = fichaTecnicaRegistration.register(entity,listaFichaProducto,listaFichaInsumoCorte,listaFichaInsumoAcabado);
					entity.setConfeccionista(selectedConfeccionista);
					entity.setOperario(null);
					entity.setTela(null);
					entity.setUsuarioRegistro(usuarioSistema.getLogin());
					entity = fichaTecnicaRegistration.register(entity);
					 for (FichaDetalleProducto p : listaFichaProducto) {
							p.setFechaRegistro(entity.getFechaRegistro());
							p.setUsuarioRegistro(entity.getUsuarioRegistro());
							p.setFichaTecnica(entity);
							p.setCodigoBarra(""+entity.getMarca().getId()+entity.getId()+entity.getColorAtraque().getId()+p.getTalla());
							p.setFichaTecnica(entity);
							fichaDetalleProductoRegistration.register(p);
						}
				}
				//agregar la ficha tecnica creada al proceso de corte
				ProcesoCorte procesoCorte = selectedDetalleOrdenProduccion.getProcesoCorte();
				procesoCorte.setFichaTecnica(entity);
				procesoCorteRegistration.updated(procesoCorte);
				//aqui deberia llevarlo de retorno al flujo de proceso de corte

				//enviando los parametros necesarios para cargar el controller
				String page = "/pages/produccion/produccion-home.xhtml";
				FacesUtil.redirect("http://localhost:8080/buffalo"+page);
				String path = request.getContextPath()+"/pages/produccion/produccion-home.xhtml";
				//FacesUtil.redirect(path);

				//newEntity();
				//FacesUtil.infoMessage("Registrado Correctamente.!");
				//FacesUtil.updateComponets(frms);
			}
		} catch (Exception e) {
			System.out.println("Error en registro Ficha Tecnica: "+e.getMessage());
			FacesUtil.errorMessage("Ocurrio un error en el registo.");
		}
	}

	public void update(){
		try {
			if(validarCampos()){
				listaFichaProducto.addAll(deleteListaFichaProducto);
				listaFichaInsumoCorte.addAll(deleteListaFichaInsumoCorte);
				listaFichaInsumoAcabado.addAll(deleteListaFichaInsumoAcabado);
				fichaTecnicaRegistration.updated(entity,listaFichaProducto,listaFichaInsumoCorte,listaFichaInsumoAcabado);
				//newEntity();
				FacesUtil.infoMessage("Modificado Correctamente.!");
				//FacesUtil.updateComponets(frms);
				String page = "/pages/produccion/produccion-home.xhtml";
				FacesUtil.redirect("http://localhost:8080/buffalo"+page);
			}
		} catch (Exception e) {
			System.out.println("Error en update Ficha Tecnica: "+e.getMessage());
			FacesUtil.errorMessage("Ocurrio un error en el registo.");
		}
	}

	public void delete(){
		try {
			if(validarCampos()){
				entity.setFechaRegistro(new Date());
				entity.setUsuarioRegistro(usuarioSistema.getLogin());
				entity.setBaja(true);
				listaFichaProducto=fichaDetalleProductoRepository.findAllByParameterObjectTwo("fichaTecnica", "baja", entity, false);
				listaFichaInsumoCorte=fichaDetalleInsumoCorteRepository.findAllByParameterObjectTwo("fichaTecnica", "baja", entity, false);
				listaFichaInsumoAcabado=fichaDetalleInsumoAcabadoRepository.findAllByParameterObjectTwo("fichaTecnica", "baja", entity, false);
				listaFichaProducto.addAll(deleteListaFichaProducto);
				listaFichaInsumoCorte.addAll(deleteListaFichaInsumoCorte);
				listaFichaInsumoAcabado.addAll(deleteListaFichaInsumoAcabado);
				fichaTecnicaRegistration.updated(entity,listaFichaProducto,listaFichaInsumoCorte,listaFichaInsumoAcabado);
				newEntity();
				FacesUtil.infoMessage("Eliminado Correctamente.!");
				FacesUtil.updateComponets(frms);
			}
		} catch (Exception e) {
			System.out.println("Error en delete Ficha Tecnica: "+e.getMessage());
			FacesUtil.errorMessage("Ocurrio un error en el registo.");
		}
	}

	public void aprobar(){
		try {
			if(validarCampos()){
				entity.setEstado("AP");
				fichaTecnicaRegistration.updated(entity);
				selectedOrdenProduccion.setProcesoActual("CORTE");
				selectedOrdenProduccion.setPorcentajeTotal(20);
				ordenProduccionRegistration.updated(selectedOrdenProduccion);
				String page = "/pages/produccion/produccion-home.xhtml";
				FacesUtil.redirect("http://localhost:8080/buffalo"+page);
				newEntity();
				FacesUtil.infoMessage("Procesado Correctamente.!");
				FacesUtil.updateComponets(frms);
			}
		} catch (Exception e) {
			System.out.println("Error en update Ficha Tecnica: "+e.getMessage());
			FacesUtil.errorMessage("Ocurrio un error en el registo.");
		}
	}

	private void entregaAlmacen(){
		boolean seguir=true;
		System.out.println("Listas: "+listaFichaProducto.size()+" - "+listaFichaInsumoCorte.size()+" - "+listaFichaInsumoAcabado.size());
		if(listaFichaProducto.size()<1){
			seguir=false;
			FacesUtil.warmMessage("No tiene agregado un Detalle de Produccion.!");
		}
		if(listaFichaInsumoCorte.size()<1){
			seguir=false;
			FacesUtil.warmMessage("No tiene agregado un Detalle de Insumos de Corte.!");
		}
		if(listaFichaInsumoAcabado.size()<1){
			seguir=false;
			FacesUtil.warmMessage("No tiene agregado un Detalle de Insumo de Acabado.!");
		}
		if(seguir){

		}
	}

	public void validarEntregaAlmacen(){
		Usuario us=usuarioRepository.findByLogin(usuario, password);
		if(us!=null){
			FacesUtil.infoMessage("Verificando Datos.!");
			FacesUtil.hideDialog("wv_aprobar");
			entregaAlmacen();
		}else{
			FacesUtil.warmMessage("Usuario o Contraseña Incorrectos.!");
		}
	}

	public void verificarEntregaAlmancen(){
		this.usuario="";
		this.password="";
		FacesUtil.updateComponent("dlg_aprobar");
		FacesUtil.showDialog("wv_aprobar");
	}
	
	private boolean validarCampos(){
		boolean result=true;
		//		if(this.entity.getConfeccionista().get){
		//			FacesUtil.warmMessage("Ingrese un Confeccionista.");
		//			result=false;
		//		}
		if(this.textoAutoCompleteConfeccionista.trim().isEmpty()){
			FacesUtil.warmMessage("Ingrese un Confeccionista.");
			result = false;
		}else{
			if(this.selectedConfeccionista.getId()==0){
				selectedConfeccionista = new Confeccionista();
				selectedConfeccionista.setNombre(textoAutoCompleteConfeccionista);
				selectedConfeccionista.setDireccion("Sin especificar");
				selectedConfeccionista.setEstado("AC");
				selectedConfeccionista.setFechaRegistro(new Date());
				selectedConfeccionista.setTelefono("0");
				selectedConfeccionista.setUsuarioRegistro(usuarioSistema.getLogin());
				selectedConfeccionista = confeccionistaRegistration.register(selectedConfeccionista);
			}
		}
		if(this.textoAutoCompleteOperario.trim().isEmpty()){
			//FacesUtil.warmMessage("Ingrese un Operario.");
			//result = false;
		}else{
//			if(this.selectedOperario.getId()==0){
//				selectedOperario = new Operario();
//				selectedOperario.setNombre(textoAutoCompleteOperario);
//				selectedOperario.setDireccion("Sin especificar");
//				selectedOperario.setEstado("AC");
//				selectedOperario.setFechaRegistro(new Date());
//				selectedOperario.setTelefono("0");
//				selectedOperario.setUsuarioRegistro(usuarioSistema.getLogin());
//				selectedOperario = operarioRegistration.register(selectedOperario);
//			}
		}

		if(this.entity.getMarca()==null){
			FacesUtil.warmMessage("Ingrese una Marca.");
			result=false;
		}
		if(this.entity.getColorAtraque()==null){
			FacesUtil.warmMessage("Ingrese un Color Atraque.");
			result=false;
		}
		if(this.entity.getColorHilo()==null){
			FacesUtil.warmMessage("Ingrese un Color de Hilo.");
			result=false;
		}
		if(this.entity.getPartida()==null){
			FacesUtil.warmMessage("Ingrese una Partida.");
			result=false;
		}
		if(this.entity.getTipoTela()==null){
			FacesUtil.warmMessage("Ingrese un Tipo de Tela.");
			result=false;
		}
		if(this.entity.getFechaSalida()==null){
			FacesUtil.warmMessage("Ingrese una Fecha Salida.");
			result=false;
		}
		if(this.entity.getFechaEntrada()==null){
			FacesUtil.warmMessage("Ingrese una Fecha Entrada.");
			result=false;
		}
		return result;
	}

	/* Action */
	public void nuevoEntity(){
		newEntity();
		nuevo=true;
		modificar=false;
		FacesUtil.updateComponets(frms);
	}

	public void cancelarEntity(){
		nuevo=false;
		modificar=false;
		titulo="Registrar Ficha Tecnica";
		FacesUtil.updateComponets(frms);
	}

	public void modificarEntity(){
		if(entity!=null){
			entity.setFechaRegistro(new Date());
			entity.setUsuarioRegistro(usuarioSistema.getLogin());
			nuevo=true;
			modificar=true;
			titulo="Modificar Ficha Tecnica";
			fichaProducto=new FichaDetalleProducto();
			fichaInsumoCorte=new FichaInsumoCorte();
			fichaInsumoAcabado=new FichaDetalleInsumoAcabado();
			listaFichaProducto=fichaDetalleProductoRepository.findAllByParameterObjectTwo("fichaTecnica", "baja", entity, false);
			listaFichaInsumoCorte=fichaDetalleInsumoCorteRepository.findAllByParameterObjectTwo("fichaTecnica", "baja", entity, false);
			listaFichaInsumoAcabado=fichaDetalleInsumoAcabadoRepository.findAllByParameterObjectTwo("fichaTecnica", "baja", entity, false);
			deleteListaFichaProducto=new ArrayList<>();
			deleteListaFichaInsumoCorte=new ArrayList<>();
			deleteListaFichaInsumoAcabado=new ArrayList<>();
			FacesUtil.updateComponets(frms);
		}else{
			FacesUtil.warmMessage("Seleccione una Ficha Tecnica");
		}
	}

	public void eliminarEntity(){
		if(entity!=null){
			if(entity.getEstado().equals("AC")){
				FacesUtil.showDialog("wv_delete");
			}else{
				FacesUtil.warmMessage("Solo se pueden eliminar Fichas Tecnincas que no estan Aprobadas.!");
			}
		}else{
			FacesUtil.warmMessage("Seleccione una Ficha Tecnica");
		}
	}

	public void verEntity(){
		if(entity!=null){
			armarURLVista();
			FacesUtil.updateComponent("formModalVistaPrevia");
			FacesUtil.showDialog("dlgVistaPrevia");
		}else{
			FacesUtil.warmMessage("Seleccione una Ficha Tecnica");
		}
	}

	/* method fileUpload*/ 

	public void handleFileUpload(FileUploadEvent event) {
		FacesUtil.infoMessage(event.getFile().getFileName() + " is uploaded.");
		try {
			this.entity.setMolde(toByteArrayUsingJava(event.getFile().getInputstream()));
			System.out.println("cargado Correctamente");
			FacesUtil.updateComponent("frm:molde");
			FacesUtil.hideDialog("wv_dlg_molde");
		} catch (Exception e) {
			FacesUtil.warmMessage("No se pudo subir el modelo.!");;
		}
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

	/* method select*/
	public void onRowSelect(SelectEvent event) {
		FacesUtil.infoMessage("Selecionado: "+(FichaTecnica) event.getObject());
		this.buttonCargarFichaTecnica = true;

	}

	//@SuppressWarnings("deprecation")
	public void armarURLVista(){
		try {
			System.out.println("Ingreso a armarURLVentasNSF..."); 
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			urlVista = urlPath+"ficha-tecnica?pId="+entity.getId();
			System.out.println("URL Reporte Vista: "+urlVista);

		} catch (Exception e) {
			System.out.println("Error en armarURLVista: "+e.getMessage());
		}
	}

	/* method detail */
	public void verControlCorte(){
		this.detalle=true;
	}

	public void verMolde(){
		this.detalle=false;
	}

	public void agregarProducto(){
		if(this.usuarioSistema.getAlmacen()!=null){
			System.out.println("Almacen: "+usuarioSistema.getAlmacen());
			this.fichaProducto=new FichaDetalleProducto();
			FacesUtil.updateComponent("frm_producto");
			FacesUtil.showDialog("wv_dlg_producto");
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public void agregarInsumoCorte(){
		if(this.usuarioSistema.getAlmacen()!=null){
			System.out.println("Almacen: "+usuarioSistema.getAlmacen());
			this.fichaInsumoCorte=new FichaInsumoCorte();
			FacesUtil.updateComponent("frm_insumo_corte");
			FacesUtil.showDialog("wv_dlg_insumo_corte");
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public void agregarInsumoAcabado(){
		if(this.usuarioSistema.getAlmacen()!=null){
			System.out.println("Almacen: "+usuarioSistema.getAlmacen());
			this.fichaInsumoAcabado=new FichaDetalleInsumoAcabado();
			FacesUtil.updateComponent("frm_insumo_acabado");
			FacesUtil.showDialog("wv_dlg_insumo_acabado");
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public void modificarProducto(){
		if(this.usuarioSistema.getAlmacen()!=null){
			if(fichaProducto!=null){
				if(fichaProducto.getTalla()!=null){
					FacesUtil.updateComponent("frm_producto");
					FacesUtil.showDialog("wv_dlg_producto");
				}else{
					FacesUtil.warmMessage("Seleccione un detalle.!");
				}
			}else{
				FacesUtil.warmMessage("Seleccione un detalle.!");
			}
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public void modificarInsumoCorte(){
		if(this.usuarioSistema.getAlmacen()!=null){
			if(fichaInsumoCorte!=null){
				if(fichaInsumoCorte.getProducto().getId()!=null){
					FacesUtil.updateComponent("frm_insumo_corte");
					FacesUtil.showDialog("wv_dlg_insumo_corte");
				}else{
					FacesUtil.warmMessage("Seleccione un detalle.!");
				}
			}else{
				FacesUtil.warmMessage("Seleccione un detalle.!");
			}
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public void modificarInsumoAcabado(){
		if(this.usuarioSistema.getAlmacen()!=null){
			if(fichaInsumoAcabado!=null){
				if(fichaInsumoAcabado.getProducto().getId()!=null){
					FacesUtil.updateComponent("frm_insumo_acabado");
					FacesUtil.showDialog("wv_dlg_insumo_acabado");
				}else{
					FacesUtil.warmMessage("Seleccione un detalle.!");
				}
			}else{
				FacesUtil.warmMessage("Seleccione un detalle.!");
			}
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public void eliminarProducto(){
		if(this.usuarioSistema.getAlmacen()!=null){
			if(fichaProducto!=null){
				if(fichaProducto.getTalla()!=null){
					FacesUtil.showDialog("wv_delete_producto");
				}else{
					FacesUtil.warmMessage("Seleccione un detalle.!");
				}
			}else{
				FacesUtil.warmMessage("Seleccione un detalle.!");
			}
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public void eliminarInsumoCorte(){
		if(this.usuarioSistema.getAlmacen()!=null){
			if(fichaInsumoCorte!=null){
				if(fichaInsumoCorte.getProducto().getId()!=null){
					FacesUtil.showDialog("wv_delete_insumo_corte");
				}else{
					FacesUtil.warmMessage("Seleccione un detalle.!");
				}
			}else{
				FacesUtil.warmMessage("Seleccione un detalle.!");
			}
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public void eliminarInsumoAcabado(){
		if(this.usuarioSistema.getAlmacen()!=null){
			if(fichaInsumoAcabado!=null){
				if(fichaInsumoAcabado.getProducto().getId()!=null){
					FacesUtil.showDialog("wv_delete_insumo_acabado");
				}else{
					FacesUtil.warmMessage("Seleccione un detalle.!");
				}
			}else{
				FacesUtil.warmMessage("Seleccione un detalle.!");
			}
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	private void cargarListaTalla(){
		Atributo padre=atributoRepository.findByParameterObject("nombre", "Talla");
		listaTalla=new ArrayList<>();
		if(padre!=null){
			listaTalla=atributoRepository.findAllByParameterObjectOrder("atributoPadre", padre, "nombre");
			if(listaTalla==null){
				listaTalla=new ArrayList<>();
			}
		}
	}

	private void cargarListaColor(){
		Atributo padre=atributoRepository.findByParameterObject("nombre", "Color");
		listaColores=new ArrayList<>();
		if(padre!=null){
			listaColores=atributoRepository.findAllByParameterObjectOrder("atributoPadre", padre, "nombre");
			if(listaColores==null){
				listaColores=new ArrayList<>();
			}
		}
	}

	private void cargarListaMarca(){
		Atributo padre=atributoRepository.findByParameterObject("nombre", "Marca Pantalon");
		listaMarca=new ArrayList<>();
		if(padre!=null){
			listaMarca=atributoRepository.findAllByParameterObjectOrder("atributoPadre", padre, "nombre");
			if(listaMarca==null){
				listaMarca=new ArrayList<>();
			}
		}
	}

	private void cargarListaProductoInsumo(){
		listaProductoInsumo=new ArrayList<>();
		if(usuarioSistema.getAlmacen()!=null){
			List<AlmProducto> ap=almacenProductoRepository.findProductosForAlmacen(usuarioSistema.getAlmacen());
			for (AlmProducto almProducto : ap) {
				listaProductoInsumo.add(almProducto.getProducto());
			}
		}
	}

	private void cargarListaProductoTela(){
		listaProductoTela=new ArrayList<>();
		if(usuarioSistema.getAlmacen()!=null){
			List<AlmProducto> ap=almacenProductoRepository.findProductosForAlmacen(usuarioSistema.getAlmacen());
			TipoProducto tela= tipoProductoRepository.findByParameterObject("descripcion", "TELA");
			if(tela==null){
				tela=new TipoProducto();
				tela.setDescripcion("TELA");
				tela.setEstado("AC");
				tela.setFechaRegistro(new Date());
				tela.setUsuarioRegistro(usuarioSistema.getLogin());
				tela.setSigla("TL");
				try {
					tipoProductoRegistration.register(tela, new ArrayList<AtributoTipoProducto>());
				} catch (Exception e) {
					e.printStackTrace();
				}
				tela= tipoProductoRepository.findByParameterObject("descripcion", "TELA");
			}
			for (AlmProducto almProducto : ap) {

				if(almProducto.getProducto().getTipoProducto().getId()==tela.getId()){
					listaProductoTela.add(almProducto.getProducto());
				}
			}
		}
	}

	public List<Producto> completeProducto(String query){
		List<Producto> result= new ArrayList<>();
		for (Producto producto : listaProductoInsumo) {
			if(producto.getNombreProducto().toUpperCase().startsWith(query.toUpperCase())){
				result.add(producto);
			}
		}
		return result;
	}

	public List<Producto> completeProductoTela(String query){
		List<Producto> result= new ArrayList<>();
		for (Producto producto : listaProductoTela) {
			if(producto.getNombreProducto().toUpperCase().startsWith(query.toUpperCase())){
				result.add(producto);
			}
		}
		return result;
	}

	public List<Atributo> completeMarca(String query){
		List<Atributo> result=new ArrayList<>();
		for (Atributo atributo : listaMarca) {
			if(atributo.getNombre().toUpperCase().startsWith(query.toUpperCase())){
				result.add(atributo);
			}
		}
		return result;
	}

	public List<Atributo> completeColor(String query){
		List<Atributo> result=new ArrayList<>();
		for (Atributo atributo : listaColores) {
			if(atributo.getNombre().toUpperCase().startsWith(query.toUpperCase())){
				result.add(atributo);
			}
		}
		return result;
	}

	public void onItemSelectTela(SelectEvent event) {
		System.out.println("Insumo corte: "+event.getObject());
		Producto pr = ((Producto)event.getObject());
		pr=productoRepository.findById(pr.getId());
		entity.setTela(pr);
	}

	public void onItemSelectMarca(SelectEvent event){
		System.out.println("Atributo Marca: "+event.getObject());
		Atributo at=(Atributo) event.getObject();
		at= atributoRepository.findById(at.getId());
		entity.setMarca(at);
	}

	public void onItemSelectColorAtraque(SelectEvent event){
		System.out.println("Atributo Color Atraque: "+event.getObject());
		Atributo at=(Atributo) event.getObject();
		at= atributoRepository.findById(at.getId());
		entity.setColorAtraque(at);
	}

	public void onItemSelectColorHilo(SelectEvent event){
		System.out.println("Atributo Color Hilo: "+event.getObject());
		Atributo at=(Atributo) event.getObject();
		at= atributoRepository.findById(at.getId());
		entity.setColorHilo(at);
	}

	public void onItemSelectCorte(SelectEvent event) {
		System.out.println("Insumo corte: "+event.getObject());
		Producto pr = ((Producto)event.getObject());
		pr=productoRepository.findById(pr.getId());
		if(pr!=null){
			System.out.println("Producto: "+pr);
			if(fichaInsumoCorte==null){
				fichaInsumoCorte=new FichaInsumoCorte();
			}
			fichaInsumoCorte.setProducto(pr);
		}
	}

	public void onItemSelectAcabado(SelectEvent event) {
		System.out.println("Insumo acabado: "+event.getObject());
		Producto pr = ((Producto)event.getObject());
		pr=productoRepository.findById(pr.getId());
		if(pr!=null){
			System.out.println("Producto: "+pr);
			if(fichaInsumoAcabado==null){
				fichaInsumoAcabado=new FichaDetalleInsumoAcabado();
			}
			fichaInsumoAcabado.setProducto(pr);
		}
	}

	public void accionAgregarProducto(){
		if(validarProducto()){
			listaFichaProducto.add(fichaProducto);
			fichaProducto=new FichaDetalleProducto();
			FacesUtil.updateComponent("frm");
			FacesUtil.updateComponent("frm_producto");
		}
	}

	public void accionAgregarInsumoCorte(){
		if(validarInsumoCorte()){
			fichaInsumoCorte.setProducto(productoRepository.findById(fichaInsumoCorte.getProducto().getId()));
			listaFichaInsumoCorte.add(fichaInsumoCorte);
			System.out.println("Insucmo agregado "+fichaInsumoCorte);
			fichaInsumoCorte=new FichaInsumoCorte();
			FacesUtil.updateComponent("frm");
			//FacesUtil.updateComponent("frm_insumo_corte");	
		}
	}

	public void accionAgregarInsumoAcabado(){
		if(validarInsumoAcabo()){
			fichaInsumoAcabado.setProducto(productoRepository.findById(fichaInsumoAcabado.getProducto().getId()));
			listaFichaInsumoAcabado.add(fichaInsumoAcabado);
			fichaInsumoAcabado=new FichaDetalleInsumoAcabado();
			FacesUtil.updateComponent("frm:tab_detalle");
			FacesUtil.updateComponent("frm_insumo_acabado");
		}
	}

	public void accionEliminarProducto(){
		for (int i = 0; i < listaFichaProducto.size(); i++) {
			FichaDetalleProducto fp=listaFichaProducto.get(i);
			if(fp.getTalla()==fichaProducto.getTalla()){
				listaFichaProducto.remove(i);
				if(fp.getId()!=null){
					fp.setBaja(true);
					deleteListaFichaProducto.add(fp);
				}
				FacesUtil.updateComponent("frm:tab_detalle");
				FacesUtil.hideDialog("wv_delete_producto");
				FacesUtil.infoMessage("Registro Eliminado.!");
				break;
			}
		}
	}

	public void accionEliminarInsumoCorte(){
		for (int i = 0; i < listaFichaInsumoCorte.size(); i++) {
			FichaInsumoCorte fc=listaFichaInsumoCorte.get(i);
			if(fc.getProducto().getId()==fichaInsumoCorte.getProducto().getId()){
				listaFichaInsumoCorte.remove(i);
				if(fc.getId()!=null){
					fc.setBaja(true);
					deleteListaFichaInsumoCorte.add(fc);
				}
				FacesUtil.updateComponent("frm:tab_detalle");
				FacesUtil.hideDialog("wv_delete_insumo_corte");
				FacesUtil.infoMessage("Registro Eliminado.!");
				break;
			}
		}
	}

	public void accionEliminarInsumoAcabado(){
		for (int i = 0; i < listaFichaInsumoAcabado.size(); i++) {
			FichaDetalleInsumoAcabado fa=listaFichaInsumoAcabado.get(i);
			if(fa.getProducto().getId()==fichaInsumoAcabado.getProducto().getId()){
				listaFichaInsumoAcabado.remove(i);
				if(fa.getId()!=null){
					fa.setBaja(true);
					deleteListaFichaInsumoAcabado.add(fa);
				}
				FacesUtil.updateComponent("frm:tab_detalle");
				FacesUtil.hideDialog("wv_delete_insumo_acabado");
				FacesUtil.infoMessage("Registro Eliminado.!");
				break;
			}
		}
	}

	private boolean validarProducto(){
		if(listaFichaProducto==null){
			listaFichaProducto=new ArrayList<>();
		}

		if(fichaProducto.getTalla()!=null){
			if(entity.getId()!=null&&entity.getColorAtraque()!=null&&entity.getMarca()!=null){
				fichaProducto.setCodigoBarra(""+entity.getMarca().getId()+entity.getId()+entity.getColorAtraque().getId()+fichaProducto.getTalla());
			}
			for (FichaDetalleProducto fp : listaFichaProducto) {
				if(fp.getTalla()==fichaProducto.getTalla()){
					if(fp.getId()==fichaProducto.getId()){
						fp=fichaProducto;
						FacesUtil.infoMessage("Modificado Correctametne.!");
						fichaProducto=new FichaDetalleProducto();
						FacesUtil.updateComponent("frm:tab_detalle");
						FacesUtil.updateComponent("frm_producto");
						return false;
					}else{
						FacesUtil.warmMessage("Ya existe la talla.!");
						return false;
					}
				}
			}
		}else{
			FacesUtil.warmMessage("Agregue una talla.!");
			return false;
		}

		return true;
	}

	private boolean validarInsumoCorte(){
		if(fichaInsumoCorte.getProducto().getId()!=null &&fichaInsumoCorte.getProducto().getId()>0){
			if(listaFichaInsumoCorte==null){
				listaFichaInsumoCorte=new ArrayList<>();
				return true;
			}
			if(listaFichaInsumoCorte.size()>0){
				for (FichaInsumoCorte fc : listaFichaInsumoCorte) {
					if(fc.getProducto().getId()==fichaInsumoCorte.getProducto().getId()){
						if(fc.getId()==fichaInsumoCorte.getId()){
							fc=fichaInsumoCorte;
							fichaInsumoCorte=new FichaInsumoCorte();
							FacesUtil.infoMessage("Modificado Correctametne");
							FacesUtil.updateComponent("frm:tab_detalle");
							FacesUtil.updateComponent("frm_insumo_corte");
							return false;
						}else{
							FacesUtil.warmMessage("Ya se agrego el insumo.!");
							return false;
						}

					}
				}
			}
		}else{
			FacesUtil.warmMessage("Seleccione un producto.!");
			return false;
		}

		return true;
	}

	private boolean validarInsumoAcabo(){
		if(fichaInsumoAcabado.getProducto().getId()!=null &&fichaInsumoAcabado.getProducto().getId()>0){
			if(listaFichaInsumoAcabado==null){
				listaFichaInsumoAcabado=new ArrayList<>();
				return true;
			}
			if(listaFichaInsumoAcabado.size()>0){
				for (FichaDetalleInsumoAcabado fc : listaFichaInsumoAcabado) {
					if(fc.getProducto().getId()==fichaInsumoAcabado.getProducto().getId()){
						if(fc.getId()==fichaInsumoAcabado.getId()){
							fc=fichaInsumoAcabado;
							fichaInsumoAcabado=new FichaDetalleInsumoAcabado();
							FacesUtil.infoMessage("Modificado correctamente.!");
							FacesUtil.updateComponent("frm:tab_detalle");
							FacesUtil.updateComponent("frm_insumo_acabado");
							return false;
						}else{
							FacesUtil.warmMessage("Ya se agrego el insumo.!");
							return false;
						}

					}
				}
			}
		}else{
			FacesUtil.warmMessage("Seleccione un producto.!");
			return false;
		}
		return true;
	}

	public void onRowSelectProducto(SelectEvent event) {
		System.out.println("Seleccionado producto: "+event.getObject());
	}

	public void onRowSelectInsumoCorte(SelectEvent event) {
		System.out.println("Seleccionado insumo corte: "+event.getObject());
	}

	public void onRowSelectInsumoAcabado(SelectEvent event) {
		System.out.println("Seleccionado insumo acabado: "+event.getObject());
	}

	//
	public void cargarVerLista(){
		this.buttonVerLista = false;
		this.verLista = true;
		this.nuevo = false;
		this.aprobarFichaTecnica = false;
		//FacesUtil.updateComponets(frms);
	}

	public void volverAtras(){
		this.buttonVerLista = true;
		this.verLista = false;
		this.nuevo = true;
		this.aprobarFichaTecnica = false;
	}

	public void cargarFichaTecnicaSeleccionada(){
		volverAtras();
		//newEntity = selectedEntiry;
		entity = selectedFichaTecnica;
	}

	//autocomplete confeccionista
	public List<String> completeConfeccionista(String query) {
		String upperQuery = query.toUpperCase();
		List<String> results = new ArrayList<String>();
		for (Confeccionista i : listConfeccionista) {
			if(i.getNombre().toUpperCase().startsWith(upperQuery)){
				results.add(i.getNombre());
			}
		}
		return results;
	}

	public void onRowSelectConfeccionistaClick(SelectEvent event) {
		String nombre =  event.getObject().toString();
		for (Confeccionista i : listConfeccionista) {
			if(i.getNombre().equals(nombre)){
				selectedConfeccionista = i;
				//textoAutoCompleteConfeccionista = i.getNombre();
				return;
			}
		}
	}

	//autocomplete operario
	public List<String> completeOperario(String query) {
		String upperQuery = query.toUpperCase();
		List<String> results = new ArrayList<String>();
		for (Operario i : listOperario) {
			if(i.getNombre().toUpperCase().startsWith(upperQuery)){
				results.add(i.getNombre());
			}
		}
		return results;
	}
	
	public void onRowSelectOperarioClick(SelectEvent event) {
		String nombre =  event.getObject().toString();
		for (Operario i : listOperario) {
			if(i.getNombre().equals(nombre)){
				selectedOperario = i;
				//textoAutoCompleteConfeccionista = i.getNombre();
				return;
			}
		}
	}

	/* Get and Set */
	public boolean isNuevo() {
		return nuevo;
	}
	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}
	public FichaTecnica getEntity() {
		return entity;
	}
	public void setEntity(FichaTecnica entity) {
		this.entity = entity;
	}
	public Usuario getUsuarioSistema() {
		return usuarioSistema;
	}
	public void setUsuarioSistema(Usuario usuarioSistema) {
		this.usuarioSistema = usuarioSistema;
	}

	public List<FichaTecnica> getEntitys() {
		return entitys;
	}

	public void setEntitys(List<FichaTecnica> entitys) {
		this.entitys = entitys;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	//	public StreamedContent getStreamedContent() {
	//		streamedContent=null;
	//		try {
	//			if(this.entity.getMolde()!=null){
	//				InputStream is = null;
	//				is = new ByteArrayInputStream(this.entity.getMolde());
	//				System.out.println("Hay imagen: "+is);
	//				streamedContent= new DefaultStreamedContent(new ByteArrayInputStream(
	//						toByteArrayUsingJava(is)));
	//			}else{
	//				String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/gfx");
	//				InputStream inputStream = new FileInputStream(path+ File.separator +  "molde.jpg");
	//				System.out.println("No hay imagen: "+inputStream);
	//				streamedContent = new DefaultStreamedContent(inputStream, "image/jpeg");
	//			}
	//		} catch (Exception e) {
	//			System.out.println("Error obenter Imagen: "+e.getMessage());
	//		}
	//		return streamedContent;
	//	}

	//	@Override
	//	protected StreamedContent buildStreamedContent(FacesContext context) throws Exception {
	//		streamedContent=null;
	//		try {
	//			if(this.entity.getMolde()!=null){
	//				InputStream is = null;
	//				is = new ByteArrayInputStream(this.entity.getMolde());
	//				System.out.println("Hay imagen: "+is);
	//				streamedContent= new DefaultStreamedContent(new ByteArrayInputStream(
	//						toByteArrayUsingJava(is)));
	//			}else{
	//				String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/gfx");
	//				InputStream inputStream = new FileInputStream(path+ File.separator +  "molde.jpg");
	//				System.out.println("No hay imagen: "+inputStream);
	//				streamedContent= new DefaultStreamedContent(inputStream, "image/jpeg");
	//			}
	//		} catch (Exception e) {
	//			System.out.println("Error obenter Imagen: "+e.getMessage());
	//		}
	//		return streamedContent;
	//	}
	//	
	//	public void setStreamedContent(StreamedContent streamedContent) {
	//		this.streamedContent = streamedContent;
	//	}

	public boolean isAdministrador() {
		return administrador;
	}

	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}

	public boolean isDetalle() {
		return detalle;
	}

	public void setDetalle(boolean detalle) {
		this.detalle = detalle;
	}

	public FichaDetalleProducto getFichaProducto() {
		return fichaProducto;
	}

	public void setFichaProducto(FichaDetalleProducto fichaProducto) {
		this.fichaProducto = fichaProducto;
	}

	public FichaInsumoCorte getFichaInsumoCorte() {
		return fichaInsumoCorte;
	}

	public void setFichaInsumoCorte(FichaInsumoCorte fichaInsumoCorte) {
		this.fichaInsumoCorte = fichaInsumoCorte;
	}

	public FichaDetalleInsumoAcabado getFichaInsumoAcabado() {
		return fichaInsumoAcabado;
	}

	public void setFichaInsumoAcabado(FichaDetalleInsumoAcabado fichaInsumoAcabado) {
		this.fichaInsumoAcabado = fichaInsumoAcabado;
	}

	public List<FichaDetalleProducto> getListaFichaProducto() {
		return listaFichaProducto;
	}

	public void setListaFichaProducto(List<FichaDetalleProducto> listaFichaProducto) {
		this.listaFichaProducto = listaFichaProducto;
	}

	public List<FichaInsumoCorte> getListaFichaInsumoCorte() {
		return listaFichaInsumoCorte;
	}

	public void setListaFichaInsumoCorte(List<FichaInsumoCorte> listaFichaInsumoCorte) {
		this.listaFichaInsumoCorte = listaFichaInsumoCorte;
	}

	public List<FichaDetalleInsumoAcabado> getListaFichaInsumoAcabado() {
		return listaFichaInsumoAcabado;
	}

	public void setListaFichaInsumoAcabado(List<FichaDetalleInsumoAcabado> listaFichaInsumoAcabado) {
		this.listaFichaInsumoAcabado = listaFichaInsumoAcabado;
	}

	public List<Atributo> getListaTalla() {
		return listaTalla;
	}

	public void setListaTalla(List<Atributo> listaTalla) {
		this.listaTalla = listaTalla;
	}

	public List<Producto> getListaProducto() {
		return listaProductoInsumo;
	}

	public void setListaProducto(List<Producto> listaProducto) {
		this.listaProductoInsumo = listaProducto;
	}

	public List<FichaDetalleProducto> getDeleteListaFichaProducto() {
		return deleteListaFichaProducto;
	}

	public void setDeleteListaFichaProducto(List<FichaDetalleProducto> deleteListaFichaProducto) {
		this.deleteListaFichaProducto = deleteListaFichaProducto;
	}

	public List<FichaInsumoCorte> getDeleteListaFichaInsumoCorte() {
		return deleteListaFichaInsumoCorte;
	}

	public void setDeleteListaFichaInsumoCorte(
			List<FichaInsumoCorte> deleteListaFichaInsumoCorte) {
		this.deleteListaFichaInsumoCorte = deleteListaFichaInsumoCorte;
	}

	public List<FichaDetalleInsumoAcabado> getDeleteListaFichaInsumoAcabado() {
		return deleteListaFichaInsumoAcabado;
	}

	public void setDeleteListaFichaInsumoAcabado(
			List<FichaDetalleInsumoAcabado> deleteListaFichaInsumoAcabado) {
		this.deleteListaFichaInsumoAcabado = deleteListaFichaInsumoAcabado;
	}

	public String getUrlVista() {
		return urlVista;
	}

	public void setUrlVista(String urlVista) {
		this.urlVista = urlVista;
	}

	public List<Producto> getListaProductoTela() {
		return listaProductoTela;
	}

	public void setListaProductoTela(List<Producto> listaProductoTela) {
		this.listaProductoTela = listaProductoTela;
	}

	public List<Atributo> getListaColores() {
		return listaColores;
	}

	public void setListaColores(List<Atributo> listaColores) {
		this.listaColores = listaColores;
	}

	public List<Atributo> getListaMarca() {
		return listaMarca;
	}

	public void setListaMarca(List<Atributo> listaMarca) {
		this.listaMarca = listaMarca;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Confeccionista getSelectedConfeccionista() {
		return selectedConfeccionista;
	}

	public void setSelectedConfeccionista(Confeccionista selectedConfeccionista) {
		this.selectedConfeccionista = selectedConfeccionista;
	}

	public String getTextConfeccionista() {
		return textConfeccionista;
	}

	public void setTextConfeccionista(String textConfeccionista) {
		this.textConfeccionista = textConfeccionista;
	}

	public String getCodigoPrenda() {
		return codigoPrenda;
	}

	public void setCodigoPrenda(String codigoPrenda) {
		this.codigoPrenda = codigoPrenda;
	}

	public boolean isVerLista() {
		return verLista;
	}

	public void setVerLista(boolean verLista) {
		this.verLista = verLista;
	}

	public boolean isAprobarFichaTecnica() {
		return aprobarFichaTecnica;
	}

	public void setAprobarFichaTecnica(boolean aprobarFichaTecnica) {
		this.aprobarFichaTecnica = aprobarFichaTecnica;
	}

	public boolean isButtonCargarFichaTecnica() {
		return buttonCargarFichaTecnica;
	}

	public void setButtonCargarFichaTecnica(boolean buttonCargarFichaTecnica) {
		this.buttonCargarFichaTecnica = buttonCargarFichaTecnica;
	}

	public boolean isButtonVerLista() {
		return buttonVerLista;
	}

	public void setButtonVerLista(boolean buttonVerLista) {
		this.buttonVerLista = buttonVerLista;
	}

	public FichaTecnica getSelectedFichaTecnica() {
		return selectedFichaTecnica;
	}

	public void setSelectedFichaTecnica(FichaTecnica selectedFichaTecnica) {
		this.selectedFichaTecnica = selectedFichaTecnica;
	}

	public String getTextoAutoCompleteConfeccionista() {
		return textoAutoCompleteConfeccionista;
	}

	public void setTextoAutoCompleteConfeccionista(
			String textoAutoCompleteConfeccionista) {
		this.textoAutoCompleteConfeccionista = textoAutoCompleteConfeccionista;
	}

	public List<Confeccionista> getListConfeccionista() {
		return listConfeccionista;
	}

	public void setListConfeccionista(List<Confeccionista> listConfeccionista) {
		this.listConfeccionista = listConfeccionista;
	}

	public Operario getSelectedOperario() {
		return selectedOperario;
	}

	public void setSelectedOperario(Operario selectedOperario) {
		this.selectedOperario = selectedOperario;
	}

	public String getTextoAutoCompleteOperario() {
		return textoAutoCompleteOperario;
	}

	public void setTextoAutoCompleteOperario(String textoAutoCompleteOperario) {
		this.textoAutoCompleteOperario = textoAutoCompleteOperario;
	}

	public List<Operario> getListOperario() {
		return listOperario;
	}

	public void setListOperario(List<Operario> listOperario) {
		this.listOperario = listOperario;
	}

	public boolean isButtonRegistrar() {
		return buttonRegistrar;
	}

	public void setButtonRegistrar(boolean buttonRegistrar) {
		this.buttonRegistrar = buttonRegistrar;
	}




}
