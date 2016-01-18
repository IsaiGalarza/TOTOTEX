package bo.buffalo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.richfaces.cdi.push.Push;

import com.ahosoft.utils.FacesUtil;

import bo.buffalo.data.AlmacenProductoRepository;
import bo.buffalo.data.AtributoRepository;
import bo.buffalo.data.DetalleOrdenProduccionRepository;
import bo.buffalo.data.FichaDetalleProductoRepository;
import bo.buffalo.data.FichaInsumoCorteRepository;
import bo.buffalo.data.OperarioRepository;
import bo.buffalo.data.OrdenProduccionRepository;
import bo.buffalo.data.ProductoRepository;
import bo.buffalo.data.TipoProductoRepository;
import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Atributo;
import bo.buffalo.model.AtributoTipoProducto;
import bo.buffalo.model.DetalleOrdenProduccion;
import bo.buffalo.model.FichaCorte;
import bo.buffalo.model.FichaDetalleProducto;
import bo.buffalo.model.FichaInsumoCorte;
import bo.buffalo.model.FichaTecnica;
import bo.buffalo.model.Operario;
import bo.buffalo.model.OrdenProduccion;
import bo.buffalo.model.ProcesoCorte;
import bo.buffalo.model.Producto;
import bo.buffalo.model.TipoProducto;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.FichaCorteRegistration;
import bo.buffalo.service.FichaDetalleInsumoCorteRegistration;
import bo.buffalo.service.FichaDetalleProductoRegistration;
import bo.buffalo.service.FichaInsumoCorteRegistration;
import bo.buffalo.service.OrdenProduccionRegistration;
import bo.buffalo.service.ProcesoCorteRegistration;
import bo.buffalo.service.TipoProductoRegistration;
import bo.buffalo.util.SessionMain;

@Named(value = "fichaCorteController")
@ConversationScoped
public class FichaCorteController implements Serializable{

	private static final long serialVersionUID = -3756873687377670050L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	Conversation conversation;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEventObject;

	//REPOSITORY
	private @Inject ProductoRepository productoRepository;
	private @Inject AlmacenProductoRepository almacenProductoRepository;
	private @Inject OrdenProduccionRepository ordenProduccionRepository;
	private @Inject DetalleOrdenProduccionRepository detalleOrdenProduccionRepository;
	private @Inject FichaInsumoCorteRepository fichaInsumoCorteRepository;
	private @Inject TipoProductoRepository tipoProductoRepository;
	private @Inject OperarioRepository operarioRepository;
	private @Inject FichaDetalleProductoRepository fichaDetalleProductoRepository;
	private @Inject AtributoRepository atributoRepository;

	//REGISTRATION
	private @Inject FichaCorteRegistration fichaCorteRegistration;
	private @Inject FichaInsumoCorteRegistration fichaInsumoCorteRegistration;
	private @Inject ProcesoCorteRegistration procesoCorteRegistration;
	private @Inject FichaDetalleInsumoCorteRegistration fichaDetalleInsumoCorteRegistration;
	private @Inject TipoProductoRegistration tipoProductoRegistration;
	private @Inject FichaDetalleProductoRegistration fichaDetalleProductoRegistration;
	private @Inject OrdenProduccionRegistration ordenProduccionRegistration;

	//ESTADO
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean verLista;
	private boolean aprobarFicha;
	private boolean buttonCargarFichaTecnica;
	private boolean buttonVerLista;
	private boolean modificar; 
	private boolean administrador;
	private boolean detalle;

	//OBJECT
	private FichaCorte fichaCorte;
	private FichaTecnica selectedFichaTecnica;
	private FichaInsumoCorte selectedFichaInsumoCorte;
	private FichaDetalleProducto selectedFichaDetalleProducto;
	private ProcesoCorte selectedProcesoCorte;
	private Operario selectedOperario;

	//LIST
	private List<FichaInsumoCorte> listFichaInsumoCorte;
	private List<FichaInsumoCorte> deleteListFichaInsumoCorte;
	private List<FichaDetalleProducto> listFichaDetalleProducto;
	private List<Producto> listaProductoTela;
	private List<Producto> listaProductoInsumo;
	private List<Operario> listOperario;
	private List<Atributo> listaTalla;
	private List<Atributo> listaColores;
	private List<Atributo> listaMarca;

	//VAR
	private String codigoPrenda;
	private String textConfeccionista;
	private String titulo;
	private String urlVista;
	private String textoAutoCompleteOperario;

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
		System.out.println("init() - FichaCorte");
		request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		usuarioSistema = sessionMain.getUsuarioSistema();
		administrador = sessionMain.isAdministrador();

		nuevo = true;
		fichaCorte = new FichaCorte();
		fichaCorte.setEstado("AC");
		fichaCorte.setFechaCorte(new Date());
		fichaCorte.setFechaProceso(new Date());
		fichaCorte.setFechaRegistro(new Date());
		fichaCorte.setMetroTela(0);
		fichaCorte.setOperario(null);
		fichaCorte.setUsuarioRegistro(usuarioSistema.getLogin());
		
		selectedFichaTecnica = new FichaTecnica();
		selectedFichaInsumoCorte = new FichaInsumoCorte();
		selectedFichaDetalleProducto = new FichaDetalleProducto();
		selectedProcesoCorte = new ProcesoCorte();
		selectedOperario = new Operario();

		listFichaInsumoCorte = new ArrayList<>();
		deleteListFichaInsumoCorte  = new ArrayList<>();
		listFichaDetalleProducto = new ArrayList<FichaDetalleProducto>();
		listOperario = operarioRepository.findAllOrderedByNombre();

		cargarListaTalla();
		cargarListaColor();
		cargarListaMarca();
		cargarListaProductoInsumo();
		cargarListaProductoTela();

		String pIdOrdenProduccion = sessionMain.getAttributeSession("pIdOrdenProduccion");
		System.out.println("pIdOrdenProduccion:"+pIdOrdenProduccion);
		if(pIdOrdenProduccion != null){
			//cargar la orden de produccion
			selectedOrdenProduccion = ordenProduccionRepository.findById(Integer.parseInt(pIdOrdenProduccion));
			System.out.println("selectedOrdenProduccion :"+selectedOrdenProduccion.getId());
			//cargar el detalle orden de produccion
			selectedDetalleOrdenProduccion = detalleOrdenProduccionRepository.findbyOrdenProduccion(selectedOrdenProduccion);
			selectedProcesoCorte = selectedDetalleOrdenProduccion.getProcesoCorte();
			selectedFichaTecnica = selectedProcesoCorte.getFichaTecnica();
			//verificar si se creo la ficha detalleProducto
			
			//VERIFICAR:  
			if(selectedProcesoCorte.getFichaCorte()== null){//SINO SE CREO
				nuevo = true;
			}else {// YA SE CREO
				fichaCorte = selectedProcesoCorte.getFichaCorte();
				listFichaInsumoCorte = fichaInsumoCorteRepository.findByFichaCorte(fichaCorte);
				listFichaDetalleProducto = fichaDetalleProductoRepository.findByFichaCorte(fichaCorte);
				
				selectedOperario = fichaCorte.getOperario()==null?selectedOperario:fichaCorte.getOperario();
				textoAutoCompleteOperario = selectedOperario.getId()==0?"":selectedOperario.getNombre();
				if(fichaCorte.getEstado().equals("AP")){
					nuevo = false;
					aprobarFicha = false;
					listFichaDetalleProducto = fichaDetalleProductoRepository.findByFichaCorte(fichaCorte);
					listFichaInsumoCorte = fichaInsumoCorteRepository.findByFichaCorte(fichaCorte);
					selectedOperario = fichaCorte.getOperario()==null?selectedOperario:fichaCorte.getOperario();
					textoAutoCompleteOperario = selectedOperario.getId()==0?"":selectedOperario.getNombre();
				}else{
					nuevo = false;
					aprobarFicha = true;
				}
			}
		}
		System.out.println("end() - FichaCorte");
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
		return "ficha-tecnica.xhtml?faces-redirect=true";
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

	public void registrarFichaInsumoCorte(){
		try {
			if(listFichaInsumoCorte.size()==0){
				FacesUtil.infoMessage("Debe agregar Insumos.");
				return;
			}
			//agregar la ficha tecnica creada al proceso de corte
//			ProcesoCorte procesoCorte = selectedDetalleOrdenProduccion.getProcesoCorte();
			fichaCorte.setFechaRegistro(new Date());
			fichaCorte.setOperario(selectedOperario);
			fichaCorte = fichaCorteRegistration.register(fichaCorte);
			selectedProcesoCorte.setFichaCorte(fichaCorte);
			procesoCorteRegistration.updated(selectedProcesoCorte);
			for(FichaInsumoCorte detalle: listFichaInsumoCorte){
				detalle.setFichaCorte(fichaCorte);
				detalle.setFechaRegistro(new Date());
				detalle.setFichaTecnica(null);
				detalle.setUsuarioRegistro(usuarioSistema.getLogin());
				fichaDetalleInsumoCorteRegistration.register(detalle);
			}
			for(FichaDetalleProducto detalle2: listFichaDetalleProducto){
				detalle2.setFichaCorte(fichaCorte);
				detalle2.setFechaRegistro(new Date());
				detalle2.setFichaTecnica(null);
				detalle2.setUsuarioRegistro(usuarioSistema.getLogin());
				fichaDetalleProductoRegistration.register(detalle2);
			}

			//enviando los parametros necesarios para cargar el controller
			String page = "/pages/produccion/produccion-home.xhtml";
			FacesUtil.redirect("http://localhost:8080/buffalo"+page);
		} catch (Exception e) {
			System.out.println("Error en registro Ficha Tecnica: "+e.getMessage());
			FacesUtil.errorMessage("Ocurrio un error en el registo.");
		}
	}
	
	public void aprobarFichaInsumoCorte(){
		try{
			fichaCorte.setEstado("AP");
			fichaCorteRegistration.updated(fichaCorte);
			
			selectedProcesoCorte.setEstado("AP");
			procesoCorteRegistration.updated(selectedProcesoCorte);
			//String path = request.getContextPath()+"/pages/produccion/produccion-home.xhtml";
			//FacesUtil.redirect(path);
			selectedOrdenProduccion.setProcesoActual("LAVANDERIA");
			selectedOrdenProduccion.setPorcentajeTotal(40);
			ordenProduccionRegistration.updated(selectedOrdenProduccion);
			String page = "/pages/produccion/produccion-home.xhtml";
			FacesUtil.redirect("http://localhost:8080/buffalo"+page);
		}catch(Exception e){
			System.out.println("aprobarFichaInsumoCorte() Error: "+e.getMessage());
		}
	}

	private void cargarListaProductoInsumo(){
		listaProductoInsumo=new ArrayList<>();
		if(usuarioSistema.getAlmacen()!=null){
			List<AlmProducto> ap = almacenProductoRepository.findProductosForAlmacen(usuarioSistema.getAlmacen());
			for (AlmProducto almProducto : ap) {
				listaProductoInsumo.add(almProducto.getProducto());
			}
		}
	}

	public void armarURLVista(){
		try {
			System.out.println("Ingreso a armarURLVentasNSF..."); 
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
			String urlPath = request.getRequestURL().toString();
			urlPath = urlPath.substring(0, urlPath.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			System.out.println("urlPath >> "+urlPath);
			urlVista = urlPath+"ficha-tecnica?pId="+selectedFichaTecnica.getId();
			System.out.println("URL Reporte Vista: "+urlVista);

		} catch (Exception e) {
			System.out.println("Error en armarURLVista: "+e.getMessage());
		}
	}

	public void onRowSelectInsumoCorte(SelectEvent event) {
		System.out.println("Seleccionado insumo corte: "+event.getObject());
	}

	public List<Producto> completeProducto(String query){
		System.out.println("completeProducto() query: "+query);
		List<Producto> result= new ArrayList<>();
		for (Producto producto : listaProductoInsumo) {
			if(producto.getNombreProducto().toUpperCase().startsWith(query.toUpperCase())){
				result.add(producto);
			}
		}
		return result;
	}

	public void onItemSelectCorte(SelectEvent event) {
		System.out.println("pre - fichaInsumoCorte: ");
		System.out.println("producto: "+event.getObject());
		Producto pr = ((Producto)event.getObject());
		pr=productoRepository.findById(pr.getId());
		if(pr!=null){
			System.out.println("Producto: "+pr);
			if(selectedFichaInsumoCorte == null){
				selectedFichaInsumoCorte = new FichaInsumoCorte();
			}
			selectedFichaInsumoCorte.setProducto(pr);
			System.out.println("post - fichaInsumoCorte: "+selectedFichaInsumoCorte.getProducto());
		}
	}

	//insumo corte
	public void agregarInsumoCorte(){
		if(this.usuarioSistema.getAlmacen()!=null){
			System.out.println("Almacen: "+usuarioSistema.getAlmacen());
			this.selectedFichaInsumoCorte=new FichaInsumoCorte();
			FacesUtil.updateComponent("frm_insumo_corte");
			FacesUtil.showDialog("wv_dlg_insumo_corte");
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public void modificarInsumoCorte(){
		if(this.usuarioSistema.getAlmacen()!=null){
//			if(fichaDetalleInsumoCorte!=null){
//				if(fichaDetalleInsumoCorte.getProducto().getId()!=null){
//					FacesUtil.updateComponent("frm_insumo_corte");
//					FacesUtil.showDialog("wv_dlg_insumo_corte");
//				}else{
//					FacesUtil.warmMessage("Seleccione un detalle.!");
//				}
//			}else{
//				FacesUtil.warmMessage("Seleccione un detalle.!");
//			}
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}

	public void eliminarInsumoCorte(){
		if(this.usuarioSistema.getAlmacen()!=null){
//			if(fichaDetalleInsumoCorte!=null){
//				if(fichaDetalleInsumoCorte.getProducto().getId()!=null){
//					FacesUtil.showDialog("wv_delete_insumo_corte");
//				}else{
//					FacesUtil.warmMessage("Seleccione un detalle.!");
//				}
//			}else{
//				FacesUtil.warmMessage("Seleccione un detalle.!");
//			}
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}
	// FICHA INSUMO CORTE

	//action insumo
	public void accionAgregarInsumoCorte(){
	//	System.out.println("fichaInsumoCorte: "+fichaDetalleInsumoCorte);
		if(validarInsumoCorte()){
			selectedFichaInsumoCorte.setProducto(productoRepository.findById(selectedFichaInsumoCorte.getProducto().getId()));
			listFichaInsumoCorte.add(selectedFichaInsumoCorte);
			selectedFichaInsumoCorte = new FichaInsumoCorte();
//
			FacesUtil.updateComponent("frm_list:table01");	
			System.out.println("Insumo agregado "+selectedFichaInsumoCorte);
		}
	}

	private boolean validarInsumoCorte(){
//		if(fichaDetalleInsumoCorte.getProducto().getId()!=null &&fichaDetalleInsumoCorte.getProducto().getId()>0){
//			if(listaFichaInsumoCorte==null){
//				listaFichaInsumoCorte=new ArrayList<>();
//				return true;
//			}
//			if(listaFichaInsumoCorte.size()>0){
//				for (FichaDetalleInsumoCorte fc : listaFichaInsumoCorte) {
//					if(fc.getProducto().getId()==fichaDetalleInsumoCorte.getProducto().getId()){
//						if(fc.getId()==fichaDetalleInsumoCorte.getId()){
//							fc=fichaDetalleInsumoCorte;
//							fichaDetalleInsumoCorte=new FichaDetalleInsumoCorte();
//							FacesUtil.infoMessage("Modificado Correctametne");
//							FacesUtil.updateComponent("frm:tab_detalle");
//							FacesUtil.updateComponent("frm_insumo_corte");
//							return false;
//						}else{
//							FacesUtil.warmMessage("Ya se agrego el insumo.!");
//							return false;
//						}
//					}
//				}
//			}
//		}else{
//			FacesUtil.warmMessage("Seleccione un producto.!");
//			return false;
//		}
		return true;
	}
	public void accionEliminarInsumoCorte(){
//		for (int i = 0; i < listaFichaInsumoCorte.size(); i++) {
//			FichaDetalleInsumoCorte fc=listaFichaInsumoCorte.get(i);
//			if(fc.getProducto().getId()==fichaDetalleInsumoCorte.getProducto().getId()){
//				listaFichaInsumoCorte.remove(i);
//				if(fc.getId()!=null){
//					fc.setBaja(true);
//					deleteListaFichaInsumoCorte.add(fc);
//				}
//				FacesUtil.updateComponent("frm:tab_detalle");
//				FacesUtil.hideDialog("wv_delete_insumo_corte");
//				FacesUtil.infoMessage("Registro Eliminado.!");
//				break;
//			}
//		}
	}
	
	// FICHA INSUMO CORTE
	public void accionAgregarProducto(){
//		if(validarProducto()){
			listFichaDetalleProducto.add(selectedFichaDetalleProducto);
			selectedFichaDetalleProducto =new FichaDetalleProducto();
			FacesUtil.updateComponent("frm_list");
			FacesUtil.updateComponent("frm_producto");
//		}
	}
	
	private boolean validarProducto(){
//		if(listaFichaProducto==null){
//			listaFichaProducto=new ArrayList<>();
//		}
//
//		if(fichaProducto.getTalla()!=null){
//			if(entity.getId()!=null&&entity.getColorAtraque()!=null&&entity.getMarca()!=null){
//				fichaProducto.setCodigoBarra(""+entity.getMarca().getId()+entity.getId()+entity.getColorAtraque().getId()+fichaProducto.getTalla());
//			}
//			for (FichaDetalleProducto fp : listaFichaProducto) {
//				if(fp.getTalla()==fichaProducto.getTalla()){
//					if(fp.getId()==fichaProducto.getId()){
//						fp=fichaProducto;
//						FacesUtil.infoMessage("Modificado Correctametne.!");
//						fichaProducto=new FichaDetalleProducto();
//						FacesUtil.updateComponent("frm:tab_detalle");
//						FacesUtil.updateComponent("frm_producto");
//						return false;
//					}else{
//						FacesUtil.warmMessage("Ya existe la talla.!");
//						return false;
//					}
//				}
//			}
//		}else{
//			FacesUtil.warmMessage("Agregue una talla.!");
//			return false;
//		}

		return true;
	}
	
	public void agregarProducto(){
		if(this.usuarioSistema.getAlmacen()!=null){
			System.out.println("Almacen: "+usuarioSistema.getAlmacen());
			this.selectedFichaDetalleProducto =new FichaDetalleProducto();
			FacesUtil.updateComponent("frm_producto");
			FacesUtil.showDialog("wv_dlg_producto");
		}else{
			FacesUtil.warmMessage("No tiene asignano un almacen, Contactese con el administrador.!");
		}
	}
	
	public void modificarProducto(){
		if(this.usuarioSistema.getAlmacen()!=null){
			if(selectedFichaDetalleProducto!=null){
				if(selectedFichaDetalleProducto.getTalla()!=null){
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
	public void onRowSelectProducto(SelectEvent event) {
		System.out.println("Seleccionado producto: "+event.getObject());
	}
	
	public void onItemSelectTela(SelectEvent event) {
		System.out.println("Insumo corte: "+event.getObject());
		Producto pr = ((Producto)event.getObject());
		pr=productoRepository.findById(pr.getId());
		fichaCorte.setTela(pr);
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

	public Usuario getUsuarioSistema() {
		return usuarioSistema;
	}

	public void setUsuarioSistema(Usuario usuarioSistema) {
		this.usuarioSistema = usuarioSistema;
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


	public String getUrlVista() {
		return urlVista;
	}

	public void setUrlVista(String urlVista) {
		this.urlVista = urlVista;
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

	public boolean isAprobarFicha() {
		return aprobarFicha;
	}

	public void setAprobarFicha(boolean aprobarFicha) {
		this.aprobarFicha = aprobarFicha;
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

	public OrdenProduccion getSelectedOrdenProduccion() {
		return selectedOrdenProduccion;
	}

	public void setSelectedOrdenProduccion(OrdenProduccion selectedOrdenProduccion) {
		this.selectedOrdenProduccion = selectedOrdenProduccion;
	}

	public DetalleOrdenProduccion getSelectedDetalleOrdenProduccion() {
		return selectedDetalleOrdenProduccion;
	}

	public void setSelectedDetalleOrdenProduccion(
			DetalleOrdenProduccion selectedDetalleOrdenProduccion) {
		this.selectedDetalleOrdenProduccion = selectedDetalleOrdenProduccion;
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

	public Operario getSelectedOperario() {
		return selectedOperario;
	}

	public void setSelectedOperario(Operario selectedOperario) {
		this.selectedOperario = selectedOperario;
	}

	public FichaCorte getFichaCorte() {
		return fichaCorte;
	}

	public void setFichaCorte(FichaCorte fichaCorte) {
		this.fichaCorte = fichaCorte;
	}

	public FichaInsumoCorte getSelectedFichaInsumoCorte() {
		return selectedFichaInsumoCorte;
	}

	public void setSelectedFichaInsumoCorte(
			FichaInsumoCorte selectedFichaInsumoCorte) {
		this.selectedFichaInsumoCorte = selectedFichaInsumoCorte;
	}

	public FichaDetalleProducto getSelectedFichaDetalleProducto() {
		return selectedFichaDetalleProducto;
	}

	public void setSelectedFichaDetalleProducto(FichaDetalleProducto selectedFichaDetalleProducto) {
		this.selectedFichaDetalleProducto = selectedFichaDetalleProducto;
	}

	public ProcesoCorte getSelectedProcesoCorte() {
		return selectedProcesoCorte;
	}

	public void setSelectedProcesoCorte(ProcesoCorte selectedProcesoCorte) {
		this.selectedProcesoCorte = selectedProcesoCorte;
	}

	public List<FichaDetalleProducto> getListFichaDetalleProducto() {
		return listFichaDetalleProducto;
	}

	public void setListFichaDetalleProducto(
			List<FichaDetalleProducto> listaFichaDetalleProducto) {
		this.listFichaDetalleProducto = listaFichaDetalleProducto;
	}

	public FichaTecnica getSelectedFichaTecnica() {
		return selectedFichaTecnica;
	}

	public void setSelectedFichaTecnica(FichaTecnica selectedFichaTecnica) {
		this.selectedFichaTecnica = selectedFichaTecnica;
	}

	public List<FichaInsumoCorte> getListFichaInsumoCorte() {
		return listFichaInsumoCorte;
	}

	public void setListFichaInsumoCorte(
			List<FichaInsumoCorte> listFichaInsumoCorte) {
		this.listFichaInsumoCorte = listFichaInsumoCorte;
	}

	public List<FichaInsumoCorte> getDeleteListFichaInsumoCorte() {
		return deleteListFichaInsumoCorte;
	}

	public void setDeleteListFichaInsumoCorte(
			List<FichaInsumoCorte> deleteListFichaInsumoCorte) {
		this.deleteListFichaInsumoCorte = deleteListFichaInsumoCorte;
	}

	public List<Atributo> getListaTalla() {
		return listaTalla;
	}

	public void setListaTalla(List<Atributo> listaTalla) {
		this.listaTalla = listaTalla;
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

}
