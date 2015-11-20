/*
 * ServicioRepository.java	1.0 2014/09/19
 *
 * Copyright (c) 2014 by QBIT SRL, Inc. All Rights Reserved.
 * 
 * QBIT SRL grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to QBIT SRL.
 * 
 * Autor: Isai Galarza
 * 
 */

package bo.buffalo.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import bo.buffalo.model.OrdenCompra;
import bo.buffalo.model.Usuario;

@ApplicationScoped
public class OrdenCompraRepository {

	@Inject
	private EntityManager em;

	public OrdenCompra findById(int id) {
		return em.find(OrdenCompra.class, id);
	}

	public List<OrdenCompra> findAllOrderedByDescripcion() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OrdenCompra> criteria = cb.createQuery(OrdenCompra.class);
		Root<OrdenCompra> presentacion = criteria.from(OrdenCompra.class);
		criteria.select(presentacion).orderBy(
				cb.desc(presentacion.get("nombre")));
		return em.createQuery(criteria).getResultList();
	}

	public List<OrdenCompra> findAllOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where (ser.estado='AC' or ser.estado='IN' or ser.estado='PR') and ser.usuario.id="
				+ user.getId() + " order by ser.id desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}

	public Integer maxOrdenTraspaso() {
		String query = "select max(ser)+1 from OrdenCompra ser where (ser.estado='AC' or ser.estado='IN' or ser.estado='PR')  ";
		System.out.println("Query OrdenCompra: " + query);
		Object res=(Object) em.createQuery(query).getSingleResult();
		if (res==null) {
			return 1;
		}else{
			return (int)res;	
		}
		
	}

	public Integer maxOrdenCompra() {
		String query = "select max(ser.correlativo)+1 from OrdenCompra ser where (ser.estado='AC' or ser.estado='IN' or ser.estado='PR')  ";
		System.out.println("Query maxOrdenCompra: " + query);
		Object obj= em.createQuery(query).getSingleResult();
		if (obj!=null) {
			return (int) obj;
		}else{
			return 1;
		}
		
	}

	public List<OrdenCompra> findAllExternoOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where (ser.estado='AC' or ser.estado='IN'  or ser.estado='RM'  or ser.estado='PR')   and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("[findAllExternoOrderedByID]Query findAllExternoOrderedByID: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<OrdenCompra> findExternoNuevoOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where ser.estado='AC'  and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("[findExternoNuevoOrderedByID]Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<OrdenCompra> findExternoProcesadasOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where ser.estado='PR'   and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("[findExternoNuevoOrderedByID]Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<OrdenCompra> findExternoAnuladasOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where (ser.estado='RM' or ser.estado='IN' )   and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<OrdenCompra> findInternoNuevoOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where ser.estado='AC'    and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<OrdenCompra> findInternoProcesadasOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where ser.estado='PR'    and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}
	
	public List<OrdenCompra> findInternoPreElaboradoProcesadasOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where ser.estado='PR'   and ser.preelaborado='PRE-ELABORADO' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<OrdenCompra> findInternoAnuladasOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where (ser.estado='RM' or ser.estado='IN' ) and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}
	
	public List<OrdenCompra> findInternoPreElaboradoAnuladasOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where (ser.estado='RM' or ser.estado='IN' )   and ser.preelaborado='PRE-ELABORADO' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}


	public List<OrdenCompra> findAllInternoOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where (ser.estado='AC')   and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}
	
	public List<OrdenCompra> findAllInternoNuevoOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where (ser.estado='AC' or ser.estado='PR')   and ser.preelaborado='PRE-ELABORADO'  and ser.usuarioOut.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}
	
	public List<OrdenCompra> findAllInternoPreElaboradosOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where (ser.estado='AC')   and ser.preelaborado='PRE-ELABORADO' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<OrdenCompra> findAllInternOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where (ser.estado='AC' or ser.estado='IN'  or ser.estado='RM'  or ser.estado='PR')   and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}
	
	public List<OrdenCompra> findAllInternPreElaboradoOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where (ser.estado='AC' or ser.estado='IN'  or ser.estado='RM'  or ser.estado='PR') and ser.preelaborado='PRE-ELABORADO'   and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<OrdenCompra> findAllOrderedByID() {
		String query = "select ser from OrdenCompra ser where (ser.estado='AC' or ser.estado='IN' or ser.estado='PR')  order by ser.id desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<OrdenCompra> findAllPedidoInternoOrderedByID() {
		String query = "select ser from OrdenCompra ser where (ser.estado='AC' or ser.estado='IN' or ser.estado='PR')    order by ser.id desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<OrdenCompra> findAllPedidoAprovedOrderedByID(Usuario user) {
		String query = "select ser from OrdenCompra ser where ser.estado='PR'  and ( ser.entrega='NEW' or  ser.entrega='PEN' or  ser.entrega='COM') and ser.usuarioOut.id="
				+ user.getId()
				+ "    order by ser.id desc";
		// +"UNOIN (select ser from OrdenCompra ser where ser.estado='PR'and  ser.usuario.id="+user.getId()+"   ) order by ser.id desc";
		System.out.println("Query OrdenCompra: " + query);
		List<OrdenCompra> lista = em.createQuery(query).getResultList();
		query = "select ser from OrdenCompra ser where ser.estado='PR' and ( ser.entrega='NEW' or  ser.entrega='PEN' or  ser.entrega='COM') and  ser.usuario.id="
				+ user.getId()
				+ "   order by ser.id desc";
		List<OrdenCompra> lista2 = em.createQuery(query).getResultList();
		for (OrdenCompra value : lista2) {
			lista.add(value);
		}
		return lista;
	}

	public List<OrdenCompra> findAllPedidoAprovedInterno(Usuario user) {
		String query = "select ser from OrdenCompra ser where (ser.estado='AC' or ser.estado='PR'or ser.estado='TE')  and ( ser.entrega='NEW' or  ser.entrega='PEN' or  ser.entrega='COM') and ser.usuarioOut.id="
				+ user.getId()
				+ "    order by ser.fechaRegistro desc";
		// +"UNOIN (select ser from OrdenCompra ser where ser.estado='PR'and  ser.usuario.id="+user.getId()+"   ) order by ser.id desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();

	}

	public List<OrdenCompra> findAllPedidoAprovedExterno(Usuario user) {
		String query = "select ser from OrdenCompra ser where (ser.estado='AC' or ser.estado='PR' or ser.estado='TE') and ( ser.entrega='NEW' or  ser.entrega='PEN' or  ser.entrega='COM') and  ser.usuario.id="
				+ user.getId()
				+ "   order by ser.fechaRegistro desc";
		// +"UNOIN (select ser from OrdenCompra ser where ser.estado='PR'and  ser.usuario.id="+user.getId()+"   ) order by ser.id desc";
		System.out.println("Query OrdenCompra: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<OrdenCompra> findAllOrderedByFechaRegistro() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OrdenCompra> criteria = cb.createQuery(OrdenCompra.class);
		Root<OrdenCompra> presentacion = criteria.from(OrdenCompra.class);
		criteria.select(presentacion).orderBy(
				cb.desc(presentacion.get("fechaRegistro")));
		return em.createQuery(criteria).getResultList();
	}

	public List<OrdenCompra> findAllOrdenCompraForDescription(String criterio) {
		try {
			String query = "select ser from OrdenCompra ser where ser.nombre like '%"
					+ criterio + "%'";
			System.out.println("Consulta: " + query);
			List<OrdenCompra> listaOrdenCompra = em.createQuery(query)
					.getResultList();
			return listaOrdenCompra;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllOrdenCompraForDescription: "
					+ e.getMessage());
			return null;
		}
	}

	public List<OrdenCompra> traerOrdenCompraActivas() {
		try {
			String query = "select ser from OrdenCompra ser where ser.estado='AC' order by ser.id desc";
			System.out.println("Consulta traerOrdenCompraActivas: " + query);
			List<OrdenCompra> listaOrdenCompra = em.createQuery(query)
					.getResultList();
			return listaOrdenCompra;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerOrdenCompraActivas: "
					+ e.getMessage());
			return null;
		}
	}

	public List<OrdenCompra> findAll100UltimosOrdenCompra() {
		try {
			String query = "select ser from OrdenCompra ser order by ser.fechaRegistro desc";
			System.out.println("Consulta: " + query);
			List<OrdenCompra> listaOrdenCompra = em.createQuery(query)
					.setMaxResults(100).getResultList();
			return listaOrdenCompra;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosOrdenCompra: "
					+ e.getMessage());
			return null;
		}
	}

}
