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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import bo.buffalo.model.Almacen;
import bo.buffalo.model.CardexProducto;
import bo.buffalo.model.LineasProveedor;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.TipoProducto;
import bo.buffalo.structure.StructureCardexProducto;
import bo.buffalo.structure.StructureCatalogoPrecios;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@ApplicationScoped
public class CardexProductoRepository {

	@Inject
	private EntityManager em;

	public CardexProducto findById(int id) {
		return em.find(CardexProducto.class, id);
	}

	public CardexProducto findByID(int usuarioId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CardexProducto> criteria = cb
				.createQuery(CardexProducto.class);
		Root<CardexProducto> user = criteria.from(CardexProducto.class);
		criteria.select(user).where(cb.equal(user.get("id"), usuarioId));
		return em.createQuery(criteria).getSingleResult();
	}

	public List<CardexProducto> findAllOrderedByName() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CardexProducto> criteria = cb
				.createQuery(CardexProducto.class);
		Root<CardexProducto> user = criteria.from(CardexProducto.class);
		criteria.select(user).orderBy(cb.asc(user.get("name")));
		return em.createQuery(criteria).getResultList();
	}

	public List<CardexProducto> traerCardexProductosActivosInactivos() {
		String query = "select usr from CardexProducto usr where usr.estado='AC' or usr.estado='IN' order by usr.id desc";
		System.out.println("Query Traer CardexProductos Activos/Inactivos: "
				+ query);
		return em.createQuery(query).getResultList();
	}

	public List<CardexProducto> findForProductoProveedorLinea(Almacen a,
			Producto p, Date fechIni, Date fechFin) {
		String query = "select usr from CardexProducto usr where (usr.estado='AC' or usr.estado='IN') "
				+ " and usr.almacen.id="
				+ a.getId()
				+ " and usr.producto.id="
				+ p.getId()
				+ " and fechaRegistro between '"
				+ fechIni
				+ "' and '" + fechFin + "' " + " order by usr.id desc";
		System.out.println("Query Traer CardexProductos Activos/Inactivos: "
				+ query);
		return em.createQuery(query).getResultList();
	}

	public List<CardexProducto> findForAlmacenTipoProducto(Almacen a,
			TipoProducto p, Date fechIni, Date fechFin) {
		String query = "select usr from CardexProducto usr where (usr.estado='AC' or usr.estado='IN') "
				+ " and usr.almacen.id="
				+ a.getId()
				+ " and usr.producto.tipoProducto.id="
				+ p.getId()
				+ " and fechaRegistro between '"
				+ fechIni
				+ "' and '"
				+ fechFin + "' " + " order by usr.id desc";
		System.out.println("Query Traer CardexProductos Activos/Inactivos: "
				+ query);
		return em.createQuery(query).getResultList();
	}

	public List<StructureCardexProducto> findForAlmacenCardex(Almacen a,
			Integer idProducto, Date fechIni, Date fechFin) {
		String query = "select con.producto.id,con.producto.nombreProducto, case when con.tipoMovimiento='INGRESO' then con.cantidad else 0 end  as ingreso,case when con.tipoMovimiento='SALIDA' then con.cantidad else 0 end  as egreso ,con.fechaRegistro"
				+ " ,con.movimiento,con.usuarioRegistro from CardexProducto con where (con.estado='AC' or con.estado='IN') "
				+ " and con.almacen.id="
				+ a.getId()
				+ " and con.producto.id="
				+ idProducto
				+ " and con.fechaRegistro between '"
				+ fechIni
				+ "' and '" + fechFin + "' " + " order by con.fechaRegistro desc";
		
		System.out.println("Query findForAlmacenCardex: " + query);

		Query quer = em.createQuery(query);
		System.out.println("entro");
		List<StructureCardexProducto> listCatalogo = new ArrayList<StructureCardexProducto>();
		double total=0;
		for (Iterator it = quer.getResultList().iterator(); it.hasNext();) {
			Object[] row = (Object[]) it.next();
			total+=((double) row[2]) - ((double) row[3]);
			StructureCardexProducto structureCatalogoPrecios = new StructureCardexProducto((Integer)row[0],
					row[1].toString(), (double) row[2], (double) row[3],
					total,(Date) row[4],row[5].toString(),row[6].toString());

			listCatalogo.add(structureCatalogoPrecios);
		}
		System.out.println("paso  : " + listCatalogo.size());

		return listCatalogo;
	}

	public List<CardexProducto> findForProductoProveedorLinea(Almacen a,
			Producto p, Proveedor prov, Date fechIni, Date fechFin) {
		String query = "select usr from CardexProducto usr where (usr.estado='AC' or usr.estado='IN') "
				+ " and usr.almacen.id="
				+ a.getId()
				+ " and usr.producto.id="
				+ p.getId()
				+ " and usr.proveedor.id="
				+ prov.getId()
				+ " and fechaRegistro between '"
				+ fechIni
				+ "' and '"
				+ fechFin + "' " + " order by usr.id desc";
		System.out.println("Query Traer CardexProductos Activos/Inactivos: "
				+ query);
		return em.createQuery(query).getResultList();
	}

	public List<CardexProducto> findForProductoProveedorLinea(Almacen a,
			Producto p, Proveedor prov, LineasProveedor lp, Date fechIni,
			Date fechFin) {
		String query = "select usr from CardexProducto usr where (usr.estado='AC' or usr.estado='IN') "
				+ " and usr.almacen.id="
				+ a.getId()
				+ " and usr.producto.id="
				+ p.getId()
				+ " and usr.proveedor.id="
				+ prov.getId()
				+ " and usr.lineaProveedor.id="
				+ lp.getId()
				+ " and fechaRegistro between '"
				+ fechIni
				+ "' and '"
				+ fechFin + "' " + " order by usr.id desc";
		System.out.println("Query Traer CardexProductos Activos/Inactivos: "
				+ query);
		return em.createQuery(query).getResultList();
	}

}
