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

import bo.buffalo.model.PedidoMov;
import bo.buffalo.model.Usuario;

@ApplicationScoped
public class PedidoMovRepository {

	@Inject
	private EntityManager em;

	public PedidoMov findById(int id) {
		return em.find(PedidoMov.class, id);
	}

	public List<PedidoMov> findAllOrderedByDescripcion() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PedidoMov> criteria = cb.createQuery(PedidoMov.class);
		Root<PedidoMov> presentacion = criteria.from(PedidoMov.class);
		criteria.select(presentacion).orderBy(
				cb.desc(presentacion.get("nombre")));
		return em.createQuery(criteria).getResultList();
	}

	public List<PedidoMov> findAllOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where (ser.estado='AC' or ser.estado='IN' or ser.estado='PR') and ser.usuario.id="
				+ user.getId() + " order by ser.id desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}

	public Integer maxOrdenTraspaso() {
		String query = "select max(ser)+1 from PedidoMov ser where (ser.estado='AC' or ser.estado='IN' or ser.estado='PR') and ser.tipoPedido='INT'";
		System.out.println("Query PedidoMov: " + query);
		Object res=(Object) em.createQuery(query).getSingleResult();
		if (res==null) {
			return 1;
		}else{
			return (int)res;	
		}
		
	}

	public Integer maxOrdenCompra() {
		String query = "select max(ser.correlativo)+1 from PedidoMov ser where (ser.estado='AC' or ser.estado='IN' or ser.estado='PR') and ser.tipoPedido='EXT'";
		System.out.println("Query maxOrdenCompra: " + query);
		Object obj= em.createQuery(query).getSingleResult();
		if (obj!=null) {
			return (int) obj;
		}else{
			return 1;
		}
		
	}

	public List<PedidoMov> findAllExternoOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where (ser.estado='AC' or ser.estado='IN'  or ser.estado='RM'  or ser.estado='PR') and ser.tipoPedido='EXT' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query findAllExternoOrderedByID: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<PedidoMov> findExternoNuevoOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where ser.estado='AC' and ser.tipoPedido='EXT' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<PedidoMov> findExternoProcesadasOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where ser.estado='PR' and ser.tipoPedido='EXT' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<PedidoMov> findExternoAnuladasOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where (ser.estado='RM' or ser.estado='IN' ) and ser.tipoPedido='EXT' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<PedidoMov> findInternoNuevoOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where ser.estado='AC'  and ser.tipoPedido='INT' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<PedidoMov> findInternoProcesadasOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where ser.estado='PR'  and ser.tipoPedido='INT' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}
	
	public List<PedidoMov> findInternoPreElaboradoProcesadasOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where ser.estado='PR' and ser.tipoPedido='INT' and ser.preelaborado='PRE-ELABORADO' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<PedidoMov> findInternoAnuladasOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where (ser.estado='RM' or ser.estado='IN' ) and and ser.tipoPedido='INT' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}
	
	public List<PedidoMov> findInternoPreElaboradoAnuladasOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where (ser.estado='RM' or ser.estado='IN' ) and ser.tipoPedido='INT' and ser.preelaborado='PRE-ELABORADO' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}


	public List<PedidoMov> findAllInternoOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where (ser.estado='AC') and ser.tipoPedido='INT' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}
	
	public List<PedidoMov> findAllInternoNuevoOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where (ser.estado='AC' or ser.estado='PR') and ser.tipoPedido='INT' and ser.preelaborado='PRE-ELABORADO'  and ser.usuarioOut.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}
	
	public List<PedidoMov> findAllInternoPreElaboradosOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where (ser.estado='AC') and ser.tipoPedido='INT' and ser.preelaborado='PRE-ELABORADO' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<PedidoMov> findAllInternOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where (ser.estado='AC' or ser.estado='IN'  or ser.estado='RM'  or ser.estado='PR') and ser.tipoPedido='INT' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}
	
	public List<PedidoMov> findAllInternPreElaboradoOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where (ser.estado='AC' or ser.estado='IN'  or ser.estado='RM'  or ser.estado='PR') and ser.preelaborado='PRE-ELABORADO' and ser.tipoPedido='INT' and ser.usuario.id="
				+ user.getId() + " order by ser.fechaRegistro desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<PedidoMov> findAllOrderedByID() {
		String query = "select ser from PedidoMov ser where (ser.estado='AC' or ser.estado='IN' or ser.estado='PR')  order by ser.id desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<PedidoMov> findAllPedidoInternoOrderedByID() {
		String query = "select ser from PedidoMov ser where (ser.estado='AC' or ser.estado='IN' or ser.estado='PR') and ser.tipoPedido='INT'  order by ser.id desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<PedidoMov> findAllPedidoAprovedOrderedByID(Usuario user) {
		String query = "select ser from PedidoMov ser where ser.estado='PR'  and ( ser.entrega='NEW' or  ser.entrega='PEN' or  ser.entrega='COM') and ser.usuarioOut.id="
				+ user.getId()
				+ " and ser.tipoPedido='INT'  order by ser.id desc";
		// +"UNOIN (select ser from PedidoMov ser where ser.estado='PR'and  ser.usuario.id="+user.getId()+" and ser.tipoPedido='EXT' ) order by ser.id desc";
		System.out.println("Query PedidoMov: " + query);
		List<PedidoMov> lista = em.createQuery(query).getResultList();
		query = "select ser from PedidoMov ser where ser.estado='PR' and ( ser.entrega='NEW' or  ser.entrega='PEN' or  ser.entrega='COM') and  ser.usuario.id="
				+ user.getId()
				+ " and ser.tipoPedido='EXT' order by ser.id desc";
		List<PedidoMov> lista2 = em.createQuery(query).getResultList();
		for (PedidoMov value : lista2) {
			lista.add(value);
		}
		return lista;
	}

	public List<PedidoMov> findAllPedidoAprovedInterno(Usuario user) {
		String query = "select ser from PedidoMov ser where (ser.estado='AC' or ser.estado='PR'or ser.estado='TE')  and ( ser.entrega='NEW' or  ser.entrega='PEN' or  ser.entrega='COM') and ser.usuarioOut.id="
				+ user.getId()
				+ " and ser.tipoPedido='INT'  order by ser.fechaRegistro desc";
		// +"UNOIN (select ser from PedidoMov ser where ser.estado='PR'and  ser.usuario.id="+user.getId()+" and ser.tipoPedido='EXT' ) order by ser.id desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();

	}

	public List<PedidoMov> findAllPedidoAprovedExterno(Usuario user) {
		String query = "select ser from PedidoMov ser where (ser.estado='AC' or ser.estado='PR' or ser.estado='TE') and ( ser.entrega='NEW' or  ser.entrega='PEN' or  ser.entrega='COM') and  ser.usuario.id="
				+ user.getId()
				+ " and ser.tipoPedido='EXT' order by ser.fechaRegistro desc";
		// +"UNOIN (select ser from PedidoMov ser where ser.estado='PR'and  ser.usuario.id="+user.getId()+" and ser.tipoPedido='EXT' ) order by ser.id desc";
		System.out.println("Query PedidoMov: " + query);
		return em.createQuery(query).getResultList();

	}

	public List<PedidoMov> findAllOrderedByFechaRegistro() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PedidoMov> criteria = cb.createQuery(PedidoMov.class);
		Root<PedidoMov> presentacion = criteria.from(PedidoMov.class);
		criteria.select(presentacion).orderBy(
				cb.desc(presentacion.get("fechaRegistro")));
		return em.createQuery(criteria).getResultList();
	}

	public List<PedidoMov> findAllPedidoMovForDescription(String criterio) {
		try {
			String query = "select ser from PedidoMov ser where ser.nombre like '%"
					+ criterio + "%'";
			System.out.println("Consulta: " + query);
			List<PedidoMov> listaPedidoMov = em.createQuery(query)
					.getResultList();
			return listaPedidoMov;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllPedidoMovForDescription: "
					+ e.getMessage());
			return null;
		}
	}

	public List<PedidoMov> traerPedidoMovActivas() {
		try {
			String query = "select ser from PedidoMov ser where ser.estado='AC' order by ser.id desc";
			System.out.println("Consulta traerPedidoMovActivas: " + query);
			List<PedidoMov> listaPedidoMov = em.createQuery(query)
					.getResultList();
			return listaPedidoMov;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerPedidoMovActivas: "
					+ e.getMessage());
			return null;
		}
	}

	public List<PedidoMov> findAll100UltimosPedidoMov() {
		try {
			String query = "select ser from PedidoMov ser order by ser.fechaRegistro desc";
			System.out.println("Consulta: " + query);
			List<PedidoMov> listaPedidoMov = em.createQuery(query)
					.setMaxResults(100).getResultList();
			return listaPedidoMov;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosPedidoMov: "
					+ e.getMessage());
			return null;
		}
	}

}
