package bo.buffalo.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import bo.buffalo.data.AlmacenProductoRepository;
import bo.buffalo.data.AtributoRepository;
import bo.buffalo.data.FichaDetalleInsumoAcabadoRepository;
import bo.buffalo.data.FichaDetalleInsumoCorteRepository;
import bo.buffalo.data.FichaDetalleProductoRepository;
import bo.buffalo.data.FichaTecnicaRepository;
import bo.buffalo.data.ProductoRepository;
import bo.buffalo.data.TipoProductoRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Atributo;
import bo.buffalo.model.AtributoTipoProducto;
import bo.buffalo.model.FichaDetalleInsumoAcabado;
import bo.buffalo.model.FichaDetalleInsumoCorte;
import bo.buffalo.model.FichaDetalleProducto;
import bo.buffalo.model.FichaTecnica;
import bo.buffalo.model.Producto;
import bo.buffalo.model.TipoProducto;
import bo.buffalo.model.Usuario;
import bo.buffalo.model.UsuarioRol;
import bo.buffalo.service.FichaTecnicaRegistration;
import bo.buffalo.service.TipoProductoRegistration;

import com.ahosoft.utils.FacesUtil;

/**
 * @author Arturo.Herrera
 *
 */
@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class FichaTecnicaController implements Serializable{
	private @Inject UsuarioRepository usuarioRepository;
	private @Inject FichaTecnicaRegistration fichaTecnicaRegistration;
	private @Inject FichaTecnicaRepository fichaTecnicaRepository;
	private @Inject AtributoRepository atributoRepository;
	private @Inject AlmacenProductoRepository almacenProductoRepository;
	private @Inject ProductoRepository productoRepository;
	private @Inject FichaDetalleProductoRepository fichaDetalleProductoRepository;
	private @Inject FichaDetalleInsumoCorteRepository fichaDetalleInsumoCorteRepository;
	private @Inject FichaDetalleInsumoAcabadoRepository fichaDetalleInsumoAcabadoRepository;
	private @Inject TipoProductoRepository tipoProductoRepository;
	private @Inject TipoProductoRegistration tipoProductoRegistration;
	
	private FichaTecnica entity;
	private List<FichaTecnica> entitys;
	private Usuario usuarioSistema;
	private boolean nuevo; /*Control para renderizar la vista*/
	private boolean modificar; 
	private boolean administrador;
	private boolean detalle;
	private ArrayList<String> frms;
	private String titulo;
	private StreamedContent streamedContent;
	private FichaDetalleProducto fichaProducto;
	private FichaDetalleInsumoCorte fichaInsumoCorte;
	private FichaDetalleInsumoAcabado fichaInsumoAcabado;
	private List<FichaDetalleProducto> listaFichaProducto;
	private List<FichaDetalleInsumoCorte> listaFichaInsumoCorte;
	private List<FichaDetalleInsumoAcabado> listaFichaInsumoAcabado;
	private List<FichaDetalleProducto> deleteListaFichaProducto;
	private List<FichaDetalleInsumoCorte> deleteListaFichaInsumoCorte;
	private List<FichaDetalleInsumoAcabado> deleteListaFichaInsumoAcabado;
	private List<Atributo> listaTalla;
	private List<Producto> listaProductoInsumo;
	private List<Producto> listaProductoTela;
	private List<Atributo> listaColores;
	private List<Atributo> listaMarca;

	private String urlVista;
	private String usuario;
	private String password;
	
	@PostConstruct
	public void init(){
		usuarioSistema= usuarioRepository.findByLogin(FacesUtil.getUserSession());
		administrador=esAdministrador();
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
	}
	
	private void newEntity(){
		entity=new FichaTecnica();
		entity.setEstado("Nuevo");
		entity.setFechaRegistro(new Date());
		entity.setUsuarioRegistro(usuarioSistema.getLogin());
		entitys=fichaTecnicaRepository.findAllByParameterObject("baja", false);
		nuevo=false;
		modificar=false;
		detalle=false;
		titulo="Registrar Ficha Tecnica";
		fichaProducto=new FichaDetalleProducto();
		fichaInsumoCorte=new FichaDetalleInsumoCorte();
		fichaInsumoAcabado=new FichaDetalleInsumoAcabado();
		listaFichaProducto=new ArrayList<>();
		listaFichaInsumoCorte=new ArrayList<>();
		listaFichaInsumoAcabado=new ArrayList<>();
		deleteListaFichaProducto=new ArrayList<>();
		deleteListaFichaInsumoCorte=new ArrayList<>();
		deleteListaFichaInsumoAcabado=new ArrayList<>();
	}
	
	
	/* Action Register*/
	public void register(){
		try {
			if(validarCampos()){
				fichaTecnicaRegistration.register(entity,listaFichaProducto,listaFichaInsumoCorte,listaFichaInsumoAcabado);
				newEntity();
				FacesUtil.infoMessage("Registrado Correctamente.!");
				FacesUtil.updateComponets(frms);
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
				newEntity();
				FacesUtil.infoMessage("Modificado Correctamente.!");
				FacesUtil.updateComponets(frms);
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
				entity.setEstado("Aprobado");
				fichaTecnicaRegistration.updated(entity);
				newEntity();
				FacesUtil.infoMessage("Modificado Correctamente.!");
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
		if(this.entity.getConfeccionista()==null){
			FacesUtil.warmMessage("Ingrese un Confeccionista.");
			result=false;
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
			fichaInsumoCorte=new FichaDetalleInsumoCorte();
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
			if(entity.getEstado().equals("Nuevo")){
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
        
    }
	
	/* method useful */
	private boolean esAdministrador(){
		boolean resultado=false;
		try {
			List<UsuarioRol> ur=usuarioRepository.buscarUsuarioRol(usuarioSistema);
			if(!ur.isEmpty()){
				for (UsuarioRol usuarioRol : ur) {
					if(usuarioRol.getRoles().getName().equals("administracion")){
						resultado=true;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error verifiacar si es administrador: "+e.getMessage());
		}
		return resultado;
	}
	
	@SuppressWarnings("deprecation")
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
			this.fichaInsumoCorte=new FichaDetalleInsumoCorte();
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
					// TODO Auto-generated catch block
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
        		fichaInsumoCorte=new FichaDetalleInsumoCorte();
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
			FacesUtil.updateComponent("frm:tab_detalle");
			FacesUtil.updateComponent("frm_producto");
		}
	}
	
	public void accionAgregarInsumoCorte(){
		if(validarInsumoCorte()){
			fichaInsumoCorte.setProducto(productoRepository.findById(fichaInsumoCorte.getProducto().getId()));
			listaFichaInsumoCorte.add(fichaInsumoCorte);
			System.out.println("Insucmo agregado "+fichaInsumoCorte);
			fichaInsumoCorte=new FichaDetalleInsumoCorte();
			FacesUtil.updateComponent("frm:tab_detalle");
			FacesUtil.updateComponent("frm_insumo_corte");	
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
			FichaDetalleInsumoCorte fc=listaFichaInsumoCorte.get(i);
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
				for (FichaDetalleInsumoCorte fc : listaFichaInsumoCorte) {
					if(fc.getProducto().getId()==fichaInsumoCorte.getProducto().getId()){
						if(fc.getId()==fichaInsumoCorte.getId()){
							fc=fichaInsumoCorte;
							fichaInsumoCorte=new FichaDetalleInsumoCorte();
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

	public StreamedContent getStreamedContent() {
		streamedContent=null;
		try {
			if(this.entity.getMolde()!=null){
				InputStream is = null;
				is = new ByteArrayInputStream(this.entity.getMolde());
				System.out.println("Hay imagen: "+is);
				streamedContent= new DefaultStreamedContent(new ByteArrayInputStream(
						toByteArrayUsingJava(is)));
			}else{
				String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/gfx");
				InputStream inputStream = new FileInputStream(path+ File.separator +  "molde.jpg");
				System.out.println("No hay imagen: "+inputStream);
				streamedContent= new DefaultStreamedContent(inputStream, "image/jpeg");
			}
		} catch (Exception e) {
			System.out.println("Error obenter Imagen: "+e.getMessage());
		}
		return streamedContent;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
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

	public FichaDetalleProducto getFichaProducto() {
		return fichaProducto;
	}

	public void setFichaProducto(FichaDetalleProducto fichaProducto) {
		this.fichaProducto = fichaProducto;
	}

	public FichaDetalleInsumoCorte getFichaInsumoCorte() {
		return fichaInsumoCorte;
	}

	public void setFichaInsumoCorte(FichaDetalleInsumoCorte fichaInsumoCorte) {
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

	public List<FichaDetalleInsumoCorte> getListaFichaInsumoCorte() {
		return listaFichaInsumoCorte;
	}

	public void setListaFichaInsumoCorte(List<FichaDetalleInsumoCorte> listaFichaInsumoCorte) {
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

	public List<FichaDetalleInsumoCorte> getDeleteListaFichaInsumoCorte() {
		return deleteListaFichaInsumoCorte;
	}

	public void setDeleteListaFichaInsumoCorte(
			List<FichaDetalleInsumoCorte> deleteListaFichaInsumoCorte) {
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
	
	
	
	
}
