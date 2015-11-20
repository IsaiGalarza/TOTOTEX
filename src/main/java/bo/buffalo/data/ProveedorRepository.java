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

import bo.buffalo.model.Proveedor;

@ApplicationScoped
public class ProveedorRepository {

	@Inject
	private EntityManager em;

	public Proveedor findById(int id) {
		return em.find(Proveedor.class, id);
	}

	public List<Proveedor> findAllOrderedByDescripcion() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Proveedor> criteria = cb.createQuery(Proveedor.class);
		Root<Proveedor> Proveedor = criteria.from(Proveedor.class);
		criteria.select(Proveedor).orderBy(cb.desc(Proveedor.get("nombre")));
		return em.createQuery(criteria).getResultList();
	}

	public List<Proveedor> findAllOrderedByID() {
		String query = "select ser from Proveedor ser where ser.estado='AC' or ser.estado='IN' order by ser.id desc";
		System.out.println("Query Proveedor: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<Proveedor> findAllOrderedByFechaRegistro() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Proveedor> criteria = cb.createQuery(Proveedor.class);
		Root<Proveedor> proveedor = criteria.from(Proveedor.class);
		criteria.select(proveedor).orderBy(
				cb.desc(proveedor.get("fechaRegistro")));
		return em.createQuery(criteria).getResultList();
	}

	public List<Proveedor> findAllProveedoresForDescription(String criterio) {
		try {
			String query = "select ser from Proveedor ser where ser.nombre like '%"
					+ criterio + "%'";
			System.out.println("Consulta: " + query);
			List<Proveedor> listaProvedores = em.createQuery(query)
					.getResultList();
			return listaProvedores;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllProveedorForDescription: "
					+ e.getMessage());
			return null;
		}
	}

	public double obtenerCosto(Proveedor proveedor) {
		try {
			String query = "select sum(ser.porcentaje) from GastosProveedor ser where ser.proveedor.id="
					+ proveedor.getId();
			System.out.println("Consulta: " + query);
			Object obj = em.createQuery(query).getSingleResult();
			if (obj == null) {
				return 0;
			} else {
				return (double) obj;
			}

		} catch (Exception e) {
			System.out.println("Error en obtenerCosto: " + e.getMessage());
			return 0;
		}
	}

	public Integer obtenerMargenUtilidadMax(Proveedor proveedor) {
		try {
			String query = "select max(ser.utilidadMax) from HistoriaMargenUtilidad ser where ser.proveedor.id="
					+ proveedor.getId();
			System.out.println("Consulta: " + query);
			return (Integer) em.createQuery(query).getSingleResult();
		} catch (Exception e) {
			System.out.println("Error en obtenerMargenUtilidadMax: "
					+ e.getMessage());
			return 0;
		}
	}

	public List<Proveedor> traerProveedoresActivas() {
		try {
			String query = "select ser from Proveedor ser where ser.estado='AC' order by ser.id desc";
			System.out.println("Consulta traerProveedoresActivas: " + query);
			List<Proveedor> listaProvedores = em.createQuery(query)
					.getResultList();
			return listaProvedores;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProveedorActivas: "
					+ e.getMessage());
			return null;
		}
	}

	public List<Proveedor> traerProveedoresSinCentralCosto() {
		try {
			String query = "select pro from Proveedor pro where pro.estado='AC' and pro.id not in (select cen.idProveedor from CentralDeCosto cen)";
			System.out.println("Consulta traerProveedoresSinCentralCosto: "
					+ query);
			List<Proveedor> listaProvedores = em.createQuery(query)
					.getResultList();
			return listaProvedores;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProveedoresSinCentralCosto: "
					+ e.getMessage());
			return null;
		}
	}

	public List<Proveedor> findAll100UltimosProveedores() {
		try {
			String query = "select ser from Proveedor ser order by ser.fechaRegistro desc";
			System.out.println("Consulta: " + query);
			List<Proveedor> listaProvedores = em.createQuery(query)
					.setMaxResults(100).getResultList();
			return listaProvedores;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosProveedor: "
					+ e.getMessage());
			return null;
		}
	}

}
