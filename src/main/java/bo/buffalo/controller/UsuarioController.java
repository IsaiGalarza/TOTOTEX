/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bo.buffalo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.richfaces.cdi.push.Push;

import bo.buffalo.data.AlmacenRepository;
import bo.buffalo.data.CargoRepository;
import bo.buffalo.data.RolesRepository;
import bo.buffalo.data.SucursalRepository;
import bo.buffalo.data.UsuarioRepository;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.Cargo;
import bo.buffalo.model.Roles;
import bo.buffalo.model.Sucursal;
import bo.buffalo.model.Usuario;
import bo.buffalo.model.UsuarioRol;
import bo.buffalo.service.UsuarioRegistration;
import bo.buffalo.structure.EstructuraRoles;

// The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an
// EL name
// Read more about the @Model stereotype in this FAQ:
// http://sfwk.org/Documentation/WhatIsThePurposeOfTheModelAnnotation
@Named(value = "usuarioController")
@ConversationScoped
public class UsuarioController implements Serializable {

	private static final long serialVersionUID = -6562077261902549394L;

	public static final String PUSH_CDI_TOPIC = "pushCdi";

	@Inject
	private FacesContext facesContext;

	@Inject
	private EntityManager em;

	@Inject
	Conversation conversation;
	
	@Inject
	private RolesRepository rolesRepository;

	@Inject
	private UsuarioRegistration userRegistration;

	
	@Inject
    private SucursalRepository sucursalRepository;
	
	private @Inject AlmacenRepository almacenRepository;
	
	@Inject
	private CargoRepository cargoRepository;

	@Inject
	UsuarioRepository usuarioRepository;
	private Usuario usuarioSession;

	private String tituloPanel = "Registrar Usuario";
	private boolean modificar = false;

	@Inject
	@Push(topic = PUSH_CDI_TOPIC)
	Event<String> pushEvent;

	
	private Usuario selectedUsuario;
	private Usuario newUsuario;
	private Usuario usuario;

	private List<Usuario> usuarios;
	private List<Cargo> listaCargos = new ArrayList<Cargo>();
	private List<EstructuraRoles> listaRolesUsuario = new ArrayList<EstructuraRoles>();
	private Integer[] arrayRoles;
	private List<UsuarioRol> listUsuarioRole = new ArrayList<UsuarioRol>();
	
	private List<Sucursal> sucursalesActivas;
	
	private List<Almacen> listaAlmacen= new ArrayList<>();

    // @Named provides access the return value via the EL variable name "sucursales" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Sucursal> getSucursalesActivas() {
        return sucursalesActivas;
    }
    
	@Produces
	@Named
	public Usuario getNewUsuario() {
		return newUsuario;
	}

	@Produces
	@Named
	public Usuario getUsuario() {
		return usuario;
	}

	@PostConstruct
	public void initNewUsuario() {

		// initConversation();
		beginConversation();

		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		System.out.println("init Sucursal*********************************");
		System.out.println("request.getClass().getName():"
				+ request.getClass().getName());
		System.out.println("isVentas:" + request.isUserInRole("ventas"));
		System.out.println("remoteUser:" + request.getRemoteUser());
		System.out.println("userPrincipalName:"
				+ (request.getUserPrincipal() == null ? "null" : request
						.getUserPrincipal().getName()));
		
		// cargar roles para usuario nuevo
		listaRolesUsuario.clear();
		List<Roles> listaRolesRegistrados = rolesRepository.findAllOrderedByName();
		for (Roles rol : listaRolesRegistrados) {
			listaRolesUsuario.add(0, new EstructuraRoles(rol.getId(), false,
					rol));
		}
		
		selectedUsuario = null;
		tituloPanel = "Registrar Personal";
		usuarioSession = usuarioRepository.findByLogin(request
				.getUserPrincipal().getName());
		System.out.println("Sucursal Usuario: "
				+ usuarioSession.getSucursal().getNombre());
		
		newUsuario = new Usuario();
		newUsuario.setEstado("AC");
		newUsuario.setFechaRegistro(new Date());
		newUsuario.setUsuarioRegistro(usuarioSession.getLogin());
		newUsuario.setImpresionSilencionsa(true);
		newUsuario.setAlmacen(new Almacen());

		// buscar lista UsuarioRol
		usuarios = usuarioRepository.traerUsuariosActivosInactivos();
		sucursalesActivas = sucursalRepository.traerSucursalesActivas();
		setListaCargos(cargoRepository.findActivosOrderedByFechaRegistro());
		modificar = false;

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

	// SELECT SUCURSAL CLICK
		public void onRowSelectUsuarioClick(SelectEvent event) {
			try {
				Usuario usuario = (Usuario) event.getObject();
				System.out.println("onRowSelectUsuarioClick  " + usuario.getId());
				selectedUsuario = usuario;
				
				newUsuario = selectedUsuario;
				newUsuario.setFechaRegistro(new Date());
				newUsuario.setUsuarioRegistro(usuarioSession.getLogin());

				tituloPanel = "Modificar Personal";
				modificar = true;
				if(newUsuario.getSucursal()==null){
					newUsuario.setSucursal(new Sucursal());
				}
				/*cargoID = newUsuario.getCargo().getId();
				sucursalID = newUsuario.getSucursal().getId();*/
				
				//cargar roles del usuario en la estructura
				listaRolesUsuario.clear();
				List<Roles> listaRolesRegistrados = rolesRepository.findAllOrderedByName();
				for(Roles rol : listaRolesRegistrados){
					boolean existeRolAsignado = usuarioRepository.buscarUsuarioRol(usuario, rol);
						System.out.println("Existe Rol: "+rol.getName() + "....: "+existeRolAsignado);
						if(existeRolAsignado){
							listaRolesUsuario.add(0, new EstructuraRoles(rol.getId(), true, rol));
						}else{
							listaRolesUsuario.add(0, new EstructuraRoles(rol.getId(), false, rol));
						}
				}

				System.out.println("arrayRoles: " + arrayRoles);
				// buscar roles y poner en la lista arrayroles
//				try {
//					List<UsuarioRol> listaRoles = usuarioRepository
//							.buscarUsuarioRol(usuario);
//					for (int i = 0; i < listaRoles.size(); i++) {
//						arrayRoles[i] = listaRoles.get(i).getRoles().getId(); //AQUI ERROR
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					System.out.println("Error al Recorrer Roles: "+e.getMessage());
//				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println("Error in onRowSelectUsuarioClick: "
						+ e.getMessage());
			}
		}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	// @Named provides access the return value via the EL variable name
	// "sucursales" in the UI (e.g.
	// Facelets or JSP view)
	@Produces
	@Named
	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	// Generated by Map
	private static Map<String, Object> rolesValue;
	static {
		rolesValue = new LinkedHashMap<String, Object>();
		rolesValue.put("ventas", new Long(1)); // label, value
		rolesValue.put("administracion", new Long(2));
		rolesValue.put("laboratorio", new Long(3));
		rolesValue.put("visitador", new Long(4));
	}

	public Map<String, Object> getRolesValue() {
		return rolesValue;
	}

	public void registrarUsuario() {
		try {
			System.out.println("Ingreso a registrarUsuario "
					+ newUsuario.getId());
			
//			List<Roles> listRole = new ArrayList<Roles>();
//			for (Integer roles : arrayRoles) {
//				// System.out.println("Role ID: "+roles);
//				Roles role = em.find(Roles.class, roles);
//				// System.out.println("Role: "+role.getName());
//				listRole.add(role);
//			}

		/*	Sucursal sucursal = (Sucursal) em.find(Sucursal.class,
					this.getSucursalID());
			System.out.println("Sucursal: " + sucursal.getNombre());
			newUsuario.setCargo(em.find(Cargo.class, this.getCargoID()));
			newUsuario.setSucursal(sucursal);
*/
			userRegistration.registerUserAndRoles(newUsuario, listaRolesUsuario);	
			//userRegistration.register(newUsuario, listRole);
			
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Personal Registrado!", newUsuario.getName());
			facesContext.addMessage(null, m);
			initNewUsuario();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error en UsuarioController.register: "
					+ e.getMessage());
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Registration unsuccessful");
			facesContext.addMessage(null, m);
		}
	}

	public void modificarUsuario() {
		try {
			System.out.println("Ingreso a modificarUsuario "
					+ newUsuario.getId());
//			List<Roles> listRole = new ArrayList<Roles>();
//			for (Integer roles : arrayRoles) {
//				// System.out.println("Role ID: "+roles);
//				Roles role = em.find(Roles.class, roles);
//				// System.out.println("Role: "+role.getName());
//				listRole.add(role);
//			}
/*
			Sucursal sucursal = (Sucursal) em.find(Sucursal.class,
					this.getSucursalID());*/
			/*System.out.println("Sucursal: " + sucursal.getNombre());*/
			newUsuario.setFechaRegistro(new Date());
			newUsuario.setUsuarioRegistro(usuarioSession.getLogin());
			if(newUsuario.getSucursal()==null){
				newUsuario.setSucursal(new Sucursal());
			}
		/*	newUsuario.setCargo(em.find(Cargo.class, this.getCargoID()));
			newUsuario.setSucursal(sucursal);*/

			userRegistration.updateUserAndRoles(newUsuario, listaRolesUsuario);
			
			//userRegistration.update(newUsuario, listRole);

			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Personal Modificado!", newUsuario.getName());
			facesContext.addMessage(null, m);
			initNewUsuario();

		} catch (Exception e) {
			System.err.println("Error en UsuarioController.modificarUsuario: "
					+ e.getMessage());
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Modificar Usuario unsuccessful");
			facesContext.addMessage(null, m);
		}
	}
	
	public void eliminarUsuario() {
		try {
			System.out.println("Ingreso a eliminarUsuario"
					+ newUsuario.getId());

			userRegistration.remover(newUsuario);
			
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Personal Borrado!", newUsuario.getName());
			facesContext.addMessage(null, m);
			initNewUsuario();

		} catch (Exception e) {
			System.err.println("Error en UsuarioController.eliminarUsuario: "
					+ e.getMessage());
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Eliminar Usuario unsuccessful");
			facesContext.addMessage(null, m);
		}
	}

	@SuppressWarnings("unchecked")
	public List<UsuarioRol> getListUserRole(String email) {
		String query = "select ur from UsuarioRol ur where ur.usuario.email='"
				+ email + "'";
		List<UsuarioRol> lista = em.createQuery(query).getResultList();
		for (UsuarioRol ur : lista) {
			System.out.println("Rol: " + ur.getRoles().getName());
		}
		return lista;
		// return userRepository.findByEmail(email);
	}

	public List<UsuarioRol> listarRoles(String email) {
		String query = "select ur from UsuarioRol ur where ur.usuario.email='"
				+ email + "'";
		List<UsuarioRol> lista = em.createQuery(query).getResultList();
		for (UsuarioRol ur : lista) {
			System.out.println("Rol: " + ur.getRoles().getName());
		}
		return lista;
		// return userRepository.findByEmail(email);
	}
	
	public void sucursalSeleccionada(){
		try {
			System.out.println("Sucursal seleccionada: "+this.newUsuario.getSucursal().getId());
			Sucursal su=sucursalRepository.findById(newUsuario.getSucursal().getId());
			this.listaAlmacen=almacenRepository.findAlmacenForSucursal(su);
		} catch (Exception e) {
			System.out.println("Error mensaje: "+e.getMessage());
		}
		
	}
	
	public void almacenSeleccionado(){
		try {
			System.out.println("Almacen seleccionada: "+this.newUsuario.getAlmacen().getId());
			this.newUsuario.setAlmacen(almacenRepository.findById(newUsuario.getAlmacen().getId()));
		} catch (Exception e) {
			this.newUsuario.setAlmacen(new Almacen());
			System.out.println("Error mensaje: "+e.getMessage());
		}
	}

	private String getRootErrorMessage(Exception e) {
		// Default to general error message that registration failed.
		String errorMessage = "Registration failed. See server log for more information";
		if (e == null) {
			// This shouldn't happen, but return the default messages
			return errorMessage;
		}

		// Start with the exception and recurse to find the root cause
		Throwable t = e;
		while (t != null) {
			// Get the message from the Throwable class instance
			errorMessage = t.getLocalizedMessage();
			t = t.getCause();
		}
		// This is the root cause message
		return errorMessage;
	}

	public List<UsuarioRol> getListUsuarioRole() {
		return listUsuarioRole;
	}

	public void setListUsuarioRole(List<UsuarioRol> listUsuarioRole) {
		this.listUsuarioRole = listUsuarioRole;
	}

	public Integer[] getArrayRoles() {
		return arrayRoles;
	}

	public void setArrayRoles(Integer[] arrayRoles) {
		this.arrayRoles = arrayRoles;
	}

	public void setNewUsuario(Usuario newUsuario) {
		this.newUsuario = newUsuario;
	}


	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public Usuario getSelectedUsuario() {
		return selectedUsuario;
	}

	public void setSelectedUsuario(Usuario selectedUsuario) {
		this.selectedUsuario = selectedUsuario;
	}

	public List<EstructuraRoles> getListaRolesUsuario() {
		return listaRolesUsuario;
	}

	public void setListaRolesUsuario(List<EstructuraRoles> listaRolesUsuario) {
		this.listaRolesUsuario = listaRolesUsuario;
	}



	public List<Cargo> getListaCargos() {
		return listaCargos;
	}

	public void setListaCargos(List<Cargo> listaCargos) {
		this.listaCargos = listaCargos;
	}

	public List<Almacen> getListaAlmacen() {
		return listaAlmacen;
	}

	public void setListaAlmacen(List<Almacen> listaAlmacen) {
		this.listaAlmacen = listaAlmacen;
	}

}
