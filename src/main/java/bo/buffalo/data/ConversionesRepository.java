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

import bo.buffalo.model.Conversiones;
import bo.buffalo.model.UnidadMedida;

@ApplicationScoped
public class ConversionesRepository {

	@Inject
	private EntityManager em;

	public Conversiones findById(int id) {
		return em.find(Conversiones.class, id);
	}

	public List<Conversiones> findAllOrderedByDescripcion() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Conversiones> criteria = cb
				.createQuery(Conversiones.class);
		Root<Conversiones> conversiones = criteria.from(Conversiones.class);
		criteria.select(conversiones).orderBy(
				cb.desc(conversiones.get("descripcion")));
		return em.createQuery(criteria).getResultList();
	}

	public List<Conversiones> findAllOrderedByID() {
		String query = "select ser from Conversiones ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
		System.out.println("Query Servicios: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<Conversiones> findAllOrderedByFechaRegistro() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Conversiones> criteria = cb
				.createQuery(Conversiones.class);
		Root<Conversiones> conversiones = criteria.from(Conversiones.class);
		criteria.select(conversiones).orderBy(
				cb.desc(conversiones.get("fechaRegistro")));
		return em.createQuery(criteria).getResultList();
	}

	public Conversiones traerEquivalente(UnidadMedida unidadMedida) {
		try {
			String query = "select ser from Conversiones ser where ser.estado='AC' and ser.unidadMedida.id="
					+ unidadMedida.getId()
					+ " and upper(ser.conversion.descripcion) = 'GRAMOS'";
			System.out.println("Consulta traerTipoProducto: " + query);
			return (Conversiones) em.createQuery(query).getSingleResult();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerConversionesActivas: "
					+ e.getMessage());
			return null;
		}
	}

	public List<Conversiones> traerConversionesActivas() {
		try {
			String query = "select ser from Conversiones ser where ser.estado='AC' order by ser.id desc";
			System.out.println("Consulta traerTipoProducto: " + query);
			List<Conversiones> listaConversiones = em.createQuery(query)
					.getResultList();
			return listaConversiones;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerConversionesActivas: "
					+ e.getMessage());
			return null;
		}
	}

	public List<Conversiones> traerConversionesActivas(UnidadMedida unidadMedida) {
		try {
			String query = "select ser from Conversiones ser where ser.estado='AC' and ser.unidadMedida.id="
					+ unidadMedida.getId() + " order by ser.id desc";
			System.out.println("Consulta traerTipoProducto: " + query);
			List<Conversiones> listaConversiones = em.createQuery(query)
					.getResultList();
			return listaConversiones;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerConversionesActivas: "
					+ e.getMessage());
			return null;
		}
	}

	public List<Conversiones> findAll100UltimosConversiones() {
		try {
			String query = "select ser from Conversiones ser order by ser.fechaRegistro desc";
			System.out.println("Consulta: " + query);
			List<Conversiones> listaConversiones = em.createQuery(query)
					.setMaxResults(100).getResultList();
			return listaConversiones;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosConversiones: "
					+ e.getMessage());
			return null;
		}
	}

}
