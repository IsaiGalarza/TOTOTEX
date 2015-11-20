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

import bo.buffalo.model.BajaProductos;
import bo.buffalo.model.Proveedor;

@ApplicationScoped
public class BajaProductoRepository {

	@Inject
	private EntityManager em;

	public BajaProductos findById(int id) {
		return em.find(BajaProductos.class, id);
	}

	public List<BajaProductos> findAllOrderedByName() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BajaProductos> criteria = cb
				.createQuery(BajaProductos.class);
		Root<BajaProductos> medico = criteria
				.from(BajaProductos.class);
		criteria.select(medico).orderBy(cb.desc(medico.get("nombre")));
		return em.createQuery(criteria).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<BajaProductos> findAllOrderedByID() {
		String query = "select med from BajaProductos med where med.estado='AC' or med.estado='IN' order by med.id desc";
		System.out.println("Query BajaProductoss: " + query);
		return em.createQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	public BajaProductos findAllOrderedByID(Proveedor pro) {
		String query = "select med from BajaProductos med where med.estado='AC' and med.proveedor.id="
				+ pro.getId() + " order by med.id desc";
		System.out.println("Query BajaProductoss: " + query);
		List<BajaProductos> lis = em.createQuery(query)
				.getResultList();
		if (lis.size() > 0) {
			return lis.get(0);
		} else
			return null;
	}

	public List<BajaProductos> findAllOrderedByFechaRegistro() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BajaProductos> criteria = cb
				.createQuery(BajaProductos.class);
		Root<BajaProductos> medico = criteria
				.from(BajaProductos.class);
		criteria.select(medico).orderBy(cb.desc(medico.get("fechaRegistro")));
		return em.createQuery(criteria).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<BajaProductos> findAllBajaProductosForNombre(
			String criterio) {
		try {
			String query = "select med from BajaProductos med where upper(med.nombre) like '%"
					+ criterio
					+ "%' or upper(med.especialidad) like '%"
					+ criterio + "%' and med.estado='AC' ";
			System.out
					.println("Consulta findAllBajaProductosForNombre: "
							+ query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out
					.println("Error en findAllBajaProductosForNombre: "
							+ e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<BajaProductos> traerBajaProductosActivos() {
		try {
			String query = "select med from BajaProductos med where med.estado='AC' order by med.id desc";
			System.out.println("Consulta traerBajaProductosActivos: "
					+ query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerBajaProductosActivos: "
					+ e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<BajaProductos> traerBajaProductosNinguno() {
		try {
			String query = "select med from BajaProductos med where med.estado='AC' and med.nombre='NINGUNO' order by med.id desc";
			System.out.println("Consulta traerBajaProductosNinguno: "
					+ query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerBajaProductosActivos: "
					+ e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<BajaProductos> traerBajaProductosActivosPrimeros10() {
		try {
			String query = "select med from BajaProductos med where med.estado='AC' order by med.id desc";
			System.out
					.println("Consulta traerBajaProductosActivosPrimeros10: "
							+ query);
			return em.createQuery(query).setMaxResults(10).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerBajaProductosActivos: "
					+ e.getMessage());
			return null;
		}
	}

}
