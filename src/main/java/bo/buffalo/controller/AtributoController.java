package bo.buffalo.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.ahosoft.utils.FacesUtil;

import bo.buffalo.data.AtributoRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Atributo;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.AtributoRegistration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Arturo.Herrera
 *
 */
@Named(value="atributoController")
@ConversationScoped
public class AtributoController implements Serializable {

	private static final long serialVersionUID = -7915129014145726307L;
	private static final Logger log = Logger.getLogger(AtributoController.class);
	private static String ACCION_GUARDAR="Guardar Atributo";
	private static String ACCION_MODIFICAR="Modificar Atributo";
	private @Inject Conversation conversation;
	private @Inject AtributoRegistration atributoRegistration;
	private @Inject AtributoRepository atributoRepository;
	private @Inject UsuarioRepository usuarioRepository;
	
	private Atributo entity;
	private Atributo padre;
	private TreeNode root;
	private TreeNode selectNode;
	private String accion;
	private Usuario usuarioSistema;
	
	@PostConstruct 
	public void initAtributo(){
		beginConversation();
		log.info("Iniciando @PostConstruct AtributoController");
		usuarioSistema=usuarioRepository.findByLogin(FacesUtil.getUserSession());
		entity=new Atributo();
		entity.setFechaRegistro(new Date());
		entity.setUsuarioRegistro(usuarioSistema);
		accion=ACCION_GUARDAR;
		root=cargarArbol();
	}
	
	public void register(){
			try {
				if(atributoRegistration.register(entity)!=null){
					clear();
					ArrayList<String> componets=new ArrayList<>();
					componets.add("frm"); componets.add("frm_list");
					FacesUtil.updateComponets(componets);
				}
			} catch (Exception e) {
				FacesUtil.errorMessage("Ocurrio un error");
				e.printStackTrace();   
			}

	}
	
	
	public void registerHijo(){
		try {
			entity.setAtributoPadre(padre);
			if(atributoRegistration.register(entity)!=null){
				clear();
				ArrayList<String> componets=new ArrayList<>();
				componets.add("frm"); componets.add("frm_list");
				FacesUtil.updateComponets(componets);
				FacesUtil.hideDialog("documentDialog");
				FacesUtil.infoMessage("Registrado correctamente.!");
			}
		} catch (Exception e) {
			FacesUtil.errorMessage("Ocurrio un error");
			e.printStackTrace();   
		}

}
	
	public void update(){
		try {
				atributoRegistration.updated(entity);
				clear();
				ArrayList<String> componets=new ArrayList<>();
				componets.add("frm"); componets.add("frm_list");
				FacesUtil.updateComponets(componets);
		} catch (Exception e) {
			System.out.println("Error "+e.getCause());
			String cause=e.getMessage();
			if(cause.equals("org.hibernate.exception.ConstraintViolationException: could not execute statement")){
				FacesUtil.errorMessage("Ya existe un registro con el mismo nombre.!");
			}else{
				FacesUtil.errorMessage("Ocurrio un error en el registro.!");
			}
		}
		
	}
	
	public void delete(){	
		try {
			entity.setBaja(true);
			atributoRegistration.updated(entity);
			clear();
			ArrayList<String> componets=new ArrayList<>();
			componets.add("frm"); componets.add("frm_list");
			FacesUtil.updateComponets(componets);  
			
		} catch (Exception e) {
			FacesUtil.errorMessage("Ocurrio un error");
			e.printStackTrace();
		}		
	}
	
	public void clear(){
		entity=new Atributo();
		entity.setFechaRegistro(new Date());
		entity.setUsuarioRegistro(usuarioSistema);
		accion=ACCION_GUARDAR;
		FacesUtil.resetComponent("frm");
		root=cargarArbol();
	}
	
	private  void cargarHojas(TreeNode n, Atributo atributo) {
		if(atributo!=null){
			List<Atributo> hijo=atributoRepository.findAllByParameterObject("atributoPadre", atributo);
			for (Atributo atributoHijo : hijo) {
				TreeNode treeNode=new DefaultTreeNode(atributoHijo, n);				
				cargarHojas(treeNode, atributoHijo);
			}
		}	
	}
	
	public TreeNode cargarArbol() {
		TreeNode root = new DefaultTreeNode("root", null);
        
        List<Atributo> entitys=atributoRepository.findAllByParameterIsNull("atributoPadre");
        System.out.println("Cantidad de padres: "+entitys.size());
        System.out.println("Padres "+entitys);
        for (Atributo atributo : entitys) {
			TreeNode treeNode=new DefaultTreeNode(atributo, root);
			cargarHojas(treeNode, atributo);
			
		}
         
        return root;
    }

	public void editar(){
		if(selectNode != null){
			try {
				this.entity=(Atributo) selectNode.getData();
				entity.setUsuarioRegistro(usuarioSistema);
				entity.setFechaRegistro(new Date());
				accion=ACCION_MODIFICAR;
				FacesUtil.updateComponent("frm");
			} catch (Exception e) {
				FacesUtil.warmMessage("Ocurrio un Error.!");
			}
			
		}else{
			FacesUtil.warmMessage("Seleccione una Categoria.!");
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

	public Atributo getEntity() {
		return entity;
	}

	public void setEntity(Atributo entity) {
		this.entity = entity;
	}

	public Atributo getPadre() {
		return padre;
	}

	public void setPadre(Atributo padre) {
		this.padre = padre;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getSelectNode() {
		return selectNode;
	}

	public void setSelectNode(TreeNode selectNode) {
		this.selectNode = selectNode;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}
	
	public String getGuardar(){
		return ACCION_GUARDAR;
	}
	
	public String getModificar(){
		return ACCION_MODIFICAR;
	}
	
	
	

}
