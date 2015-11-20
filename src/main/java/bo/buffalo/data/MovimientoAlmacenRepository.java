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

import java.math.BigDecimal;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import bo.buffalo.model.Almacen;
import bo.buffalo.model.LineasProveedor;
import bo.buffalo.model.MovimientoAlmacen;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.Usuario;

@ApplicationScoped
public class MovimientoAlmacenRepository {

	@Inject
	private EntityManager em;

	public MovimientoAlmacen findById(int id) {
		return em.find(MovimientoAlmacen.class, id);
	}

	public List<MovimientoAlmacen> findAllOrderedByDescripcion() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MovimientoAlmacen> criteria = cb.createQuery(MovimientoAlmacen.class);
		Root<MovimientoAlmacen> presentacion = criteria.from(MovimientoAlmacen.class);
		criteria.select(presentacion).orderBy(
				cb.desc(presentacion.get("descripcion")));
		return em.createQuery(criteria).getResultList();
	}


	public Integer numeroDeEntrega() {
		String query = "select max(ser.numeroEntrega) from MovimientoAlmacen ser where (ser.estado='AC' or ser.estado='IN')";
		System.out.println("Query MovimientoAlmacen: " + query);
		Integer  max=(Integer) em.createQuery(query).getSingleResult();
		System.out.println("Entrega Nro: "+max);
		if (max==null) {
			return 1;
		}else{
			return max+1;
		}
	}

	public MovimientoAlmacen findMovimientoAlmacenForUser(Usuario user) {
		String query = "select ser from MovimientoAlmacen ser where (ser.estado='AC' or ser.estado='IN') and ser.usuario.id="
				+ user.getId() + " order by ser.id desc";
		System.out.println("Query MovimientoAlmacen: " + query);
		return (MovimientoAlmacen) em.createQuery(query).getSingleResult();
	}

	public List<MovimientoAlmacen> findMovimientoAlmacensDiferentsUser(Usuario user) {
		String query = "select ser from MovimientoAlmacen ser where (ser.estado='AC' or ser.estado='IN') and ser.usuario.id<>"
				+ user.getId() + " order by ser.id desc";
		System.out.println("Query MovimientoAlmacen: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<MovimientoAlmacen> findAllOrderedByFechaRegistro() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MovimientoAlmacen> criteria = cb.createQuery(MovimientoAlmacen.class);
		Root<MovimientoAlmacen> presentacion = criteria.from(MovimientoAlmacen.class);
		criteria.select(presentacion).orderBy(
				cb.desc(presentacion.get("fechaRegistro")));
		return em.createQuery(criteria).getResultList();
	}

	public List<MovimientoAlmacen> findAllMovimientoAlmacenForDescription(String criterio) {
		try {
			String query = "select ser from MovimientoAlmacen ser where ser.nombre like '%"
					+ criterio + "%'";
			System.out.println("Consulta: " + query);
			List<MovimientoAlmacen> listaMovimientoAlmacen = em.createQuery(query)
					.getResultList();
			return listaMovimientoAlmacen;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllMovimientoAlmacenForDescription: "
					+ e.getMessage());
			return null;
		}
	}

	public List<MovimientoAlmacen> traerMovimientoAlmacenActivas() {
		try {
			String query = "select ser from MovimientoAlmacen ser where ser.estado='AC' order by ser.id desc";
			System.out.println("Consulta traerMovimientoAlmacenActivas: " + query);
			List<MovimientoAlmacen> listaMovimientoAlmacen = em.createQuery(query)
					.getResultList();
			return listaMovimientoAlmacen;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerMovimientoAlmacenActivas: "
					+ e.getMessage());
			return null;
		}
	}


	public List<MovimientoAlmacen> findAll100UltimosMovimientoAlmacen() {
		try {
			String query = "select ser from MovimientoAlmacen ser order by ser.fechaRegistro desc";
			System.out.println("Consulta: " + query);
			List<MovimientoAlmacen> listaMovimientoAlmacen = em.createQuery(query)
					.setMaxResults(100).getResultList();
			return listaMovimientoAlmacen;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosMovimientoAlmacen: "
					+ e.getMessage());
			return null;
		}
	}

}
