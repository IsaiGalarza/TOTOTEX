/*
 * ProformaVentaRepository.java	1.0 2014/09/19
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

import bo.buffalo.model.Proforma;
import bo.buffalo.model.ProformaVenta;

@ApplicationScoped
public class ProformaVentaRepository {

	@Inject
	private EntityManager em;

	public Proforma findById(int id) {
		return em.find(Proforma.class, id);
	}

	public List<ProformaVenta> findAllOrderedByName() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProformaVenta> criteria = cb
				.createQuery(ProformaVenta.class);
		Root<ProformaVenta> entity = criteria.from(ProformaVenta.class);
		criteria.select(entity).orderBy(cb.desc(entity.get("fechaRegistro")));
		return em.createQuery(criteria).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ProformaVenta> findAllOrderedByID() {
		String query = "select emp from ProformaVenta emp where (emp.estado='AC' or emp.estado='IN' or emp.estado='CF' or emp.estado='RE' or emp.estado='TE' or emp.estado='EN' or emp.estado='PR') "
				+ "	order by emp.fechaRegistro desc";
		System.out.println("Query ProformaVenta: " + query);
		return em.createQuery(query).getResultList();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ProformaVenta> findAllOrderedDateByID() {
		String query = "select emp from ProformaVenta emp where (emp.estado='AC' or emp.estado='IN' or emp.estado='CF' or emp.estado='RE' or emp.estado='TE' or emp.estado='EN') "
				+ " and emp.totalPagado>0	order by emp.fechaRegistro desc";
		System.out.println("Query ProformaVenta: " + query);
		return em.createQuery(query).getResultList();
	}

	
	@SuppressWarnings("unchecked")
	public List<ProformaVenta> findAllAsignacionOrderedByID() {
		String query = "select emp from ProformaVenta emp where emp.estado<>'IN' order by emp.fechaRegistro desc";
		System.out.println("Query ProformaVenta: " + query);
		return em.createQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Proforma> findAllProformaVentaClienteNombre(String criterio) {
		try {
			String query = "select emp from Proforma emp"
					+ " where upper(emp.cliente.nombreCompleto) like '%" + criterio + "%' and (emp.estado='AC' or emp.estado='PR' or emp.estado='CF' or emp.estado='TE') order by emp.fechaRegistro desc";
			System.out.println("Consulta findAllProformaForNombre: " + query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllProformaVentaClienteNombre: "
					+ e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProformaVenta> traerProformaVentaPrimeros10() {
		try {
			String query = "select emp from ProformaVenta emp where emp.estado='AC' or emp.estado='IN' order by emp.id desc";
			System.out.println("Consulta traerProformaVentaPrimeros10: "
					+ query);
			return em.createQuery(query).setMaxResults(10).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProformaVentaPrimeros10: "
					+ e.getMessage());
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Proforma> traerProformaVentaPendientes() {
		try {
			String query = "select pv from Proforma pv where pv.estado='PR' order by pv.fechaRegistro desc";
			System.out.println("Consulta traerProformaVentaPendientes: "
					+ query);
			return em.createQuery(query).setMaxResults(10).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProformaVentaPendientes: "
					+ e.getMessage());
			return null;
		}
	}

}
