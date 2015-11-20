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

import bo.buffalo.model.HistoriaMargenUtilidad;
import bo.buffalo.model.Proveedor;

@ApplicationScoped
public class MargenUtilidadRepository {

	@Inject
	private EntityManager em;

	public HistoriaMargenUtilidad findById(int id) {
		return em.find(HistoriaMargenUtilidad.class, id);
	}

	public List<HistoriaMargenUtilidad> findAllOrderedByName() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HistoriaMargenUtilidad> criteria = cb
				.createQuery(HistoriaMargenUtilidad.class);
		Root<HistoriaMargenUtilidad> medico = criteria
				.from(HistoriaMargenUtilidad.class);
		criteria.select(medico).orderBy(cb.desc(medico.get("nombre")));
		return em.createQuery(criteria).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<HistoriaMargenUtilidad> findAllOrderedByID() {
		String query = "select med from HistoriaMargenUtilidad med where (med.estado='AC' or med.estado='IN') order by med.id desc";
		System.out.println("Query HistoriaMargenUtilidads: " + query);
		return em.createQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<HistoriaMargenUtilidad> findForProveedorOrderedByID(
			Proveedor pro) {
		String query = "select med from HistoriaMargenUtilidad med where (med.estado='AC' or med.estado='IN')  and med.proveedor.id="
				+ pro.getId() + " order by med.id desc";
		System.out.println("Query HistoriaMargenUtilidads: " + query);
		return em.createQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	public HistoriaMargenUtilidad findAllOrderedByID(Proveedor pro) {
		String query = "select med from HistoriaMargenUtilidad med where med.estado='AC' and med.proveedor.id="
				+ pro.getId() + " order by med.id desc";
		System.out.println("Query HistoriaMargenUtilidads: " + query);
		List<HistoriaMargenUtilidad> lis = em.createQuery(query)
				.getResultList();
		if (lis.size() > 0) {
			return lis.get(0);
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public List<HistoriaMargenUtilidad> findAllProveedorOrderedByID(
			Proveedor pro) {
		String query = "select med from HistoriaMargenUtilidad med where med.estado='AC' and med.proveedor.id="
				+ pro.getId() + " order by med.id desc";
		System.out.println("Query HistoriaMargenUtilidads: " + query);
		return em.createQuery(query).getResultList();

	}

	public List<HistoriaMargenUtilidad> findAllOrderedByFechaRegistro() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HistoriaMargenUtilidad> criteria = cb
				.createQuery(HistoriaMargenUtilidad.class);
		Root<HistoriaMargenUtilidad> medico = criteria
				.from(HistoriaMargenUtilidad.class);
		criteria.select(medico).orderBy(cb.desc(medico.get("fechaRegistro")));
		return em.createQuery(criteria).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<HistoriaMargenUtilidad> findAllHistoriaMargenUtilidadForNombre(
			String criterio) {
		try {
			String query = "select med from HistoriaMargenUtilidad med where upper(med.nombre) like '%"
					+ criterio
					+ "%' or upper(med.especialidad) like '%"
					+ criterio + "%' and med.estado='AC' ";
			System.out
					.println("Consulta findAllHistoriaMargenUtilidadForNombre: "
							+ query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out
					.println("Error en findAllHistoriaMargenUtilidadForNombre: "
							+ e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<HistoriaMargenUtilidad> traerHistoriaMargenUtilidadActivos() {
		try {
			String query = "select med from HistoriaMargenUtilidad med where med.estado='AC' order by med.id desc";
			System.out.println("Consulta traerHistoriaMargenUtilidadActivos: "
					+ query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerHistoriaMargenUtilidadActivos: "
					+ e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<HistoriaMargenUtilidad> traerHistoriaMargenUtilidadNinguno() {
		try {
			String query = "select med from HistoriaMargenUtilidad med where med.estado='AC' and med.nombre='NINGUNO' order by med.id desc";
			System.out.println("Consulta traerHistoriaMargenUtilidadNinguno: "
					+ query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerHistoriaMargenUtilidadActivos: "
					+ e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<HistoriaMargenUtilidad> traerHistoriaMargenUtilidadActivosPrimeros10() {
		try {
			String query = "select med from HistoriaMargenUtilidad med where med.estado='AC' order by med.id desc";
			System.out
					.println("Consulta traerHistoriaMargenUtilidadActivosPrimeros10: "
							+ query);
			return em.createQuery(query).setMaxResults(10).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerHistoriaMargenUtilidadActivos: "
					+ e.getMessage());
			return null;
		}
	}

}
