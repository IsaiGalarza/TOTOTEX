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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.LineasProveedor;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.TipoProducto;
import bo.buffalo.model.Usuario;
import bo.buffalo.structure.StructureCatalogo;
import bo.buffalo.structure.StructureCatalogoPrecios;

@ApplicationScoped
public class AlmacenProductoRepository {

	@Inject
	private EntityManager em;



	public AlmProducto findById(int id) {
		return em.find(AlmProducto.class, id);
	}

	public List<AlmProducto> findAllOrderedByDescripcion() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AlmProducto> criteria = cb.createQuery(AlmProducto.class);
		Root<AlmProducto> presentacion = criteria.from(AlmProducto.class);
		criteria.select(presentacion).orderBy(
				cb.desc(presentacion.get("descripcion")));
		return em.createQuery(criteria).getResultList();
	}

	@SuppressWarnings("unchecked")
	public boolean existProducto(Producto prod, Almacen alm,
			LineasProveedor lp, Proveedor prov) {
		String query = "select ser from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') and ser.producto.id="
				+ prod.getId()
				+ "  and ser.almacen.id="
				+ alm.getId()
				+ " and ser.lineaProvedor.id="
				+ lp.getId()
				+ " and ser.proveedor.id="
				+ prov.getId()
				+ " order by ser.id desc";
		System.out.println("Query AlmProducto: " + query);
		List<AlmProducto> lis = (List<AlmProducto>) em.createQuery(query)
				.getResultList();
		if (lis.size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	public AlmProducto findByProducto(Producto prod, Almacen alm,
			LineasProveedor lp, Proveedor prov) {
		String query = "select ser from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') and ser.producto.id="
				+ prod.getId()
				+ "  and ser.almacen.id="
				+ alm.getId()
				+ " and ser.lineaProvedor.id="
				+ lp.getId()
				+ " and ser.proveedor.id="
				+ prov.getId()
				+ " order by ser.id desc";
		System.out.println("Query AlmProducto: " + query);
		return (AlmProducto) em.createQuery(query).getResultList().get(0);
	}

	public AlmProducto findByAlmacenProducto(Producto prod, Almacen alm) {
		String query = "select con from AlmProducto con where (con.estado='AC' or con.estado='IN') and con.producto.id="
				+ prod.getId()
				+ " and con.almacen.id="
				+ alm.getId()
				+ " and con.stock in(  select max(ser.stock) as stock from AlmProducto ser where  ser.producto.id="
				+ prod.getId() + "  and ser.almacen.id=" + alm.getId() + ") ";
		System.out.println("Query AlmProducto: " + query);
		return (AlmProducto) em.createQuery(query).getResultList().get(0);
	}

	@SuppressWarnings("unchecked")
	public List<AlmProducto> findProductosForAlmacen(Almacen alm) {
		String query = "select ser from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') "
				+ "  and ser.almacen.id="
				+ alm.getId()
				+ " order by ser.producto.nombreProducto asc";
		System.out.println("Query AlmProducto: " + query);
		return em.createQuery(query).getResultList();
	}

	// select ser.stock from AlmProducto ser where ser.almacen.id=8 and
	// ser.producto.id=4 and ser.stock>0

	@SuppressWarnings("unchecked")
	public List<AlmProducto> obteberProducto(Almacen alm, Producto pro) {
		String query = "select ser from AlmProducto ser where ser.almacen.id="
				+ alm.getId()
				+ " and ser.producto.id="
				+ pro.getId()
				+ " and ser.stock>0  order by ser.stock desc";
		System.out.println("Query AlmProducto: " + query);
		return em.createQuery(query).getResultList();
	}

	
	public double obtenerProductoStock(Almacen alm, Producto pro) {
		String query = "select sum(ser.stock) from AlmProducto ser where ser.almacen.id="
				+ alm.getId()
				+ " and ser.producto.id="
				+ pro.getId()
				+ " and ser.stock>0";
		System.out.println("Query AlmProducto: " + query);
		Object res= em.createQuery(query).getSingleResult();
		System.out.println("res: "+res);
		if (res==null) {
			return -1;
		}else{
			return (double)res;
		}
	}
	@SuppressWarnings("rawtypes")
	public List<StructureCatalogoPrecios> findResumenProductosForAlmacen(
			Almacen alm) {
		String query = "select ser.producto.id,ser.producto.nombreProducto,sum(ser.stock) from AlmProducto ser where (ser.estado='AC' or ser.estado='IN')   and ser.almacen.id="
				+ alm.getId()
				+ " group by ser.producto.id,ser.producto.nombreProducto order by ser.producto.nombreProducto asc";
		System.out.println("Query AlmProducto: " + query);

		Query quer = em.createQuery(query);
		System.out.println("entro");
		List<StructureCatalogoPrecios> listCatalogo = new ArrayList<StructureCatalogoPrecios>();
		for (Iterator it = quer.getResultList().iterator(); it.hasNext();) {
			Object[] row = (Object[]) it.next();
			StructureCatalogoPrecios structureCatalogoPrecios = new StructureCatalogoPrecios(
					(Integer) row[0], row[1].toString(), (double) row[2]);

			listCatalogo.add(structureCatalogoPrecios);
		}
		System.out.println("paso  : " + listCatalogo.size());

		return listCatalogo;
	}

	@SuppressWarnings("rawtypes")
	public List<StructureCatalogoPrecios> findTipoProductosForAlmacen(
			Almacen alm, TipoProducto tipo, Date fechIni, Date fechFin) {
		String query = "select ser.producto.id,ser.producto.nombreProducto,sum(ser.stock) from AlmProducto ser where (ser.estado='AC' or ser.estado='IN')   and ser.almacen.id="
				+ alm.getId()
				+ " and ser.producto.tipoProducto.id="
				+ tipo.getId()
				+ " and ser.fechaRegistro between '"
				+ fechIni
				+ "' and '"
				+ fechFin
				+ "' group by ser.producto.id,ser.producto.nombreProducto order by ser.producto.nombreProducto asc";
		System.out.println("Query AlmProducto: " + query);

		Query quer = em.createQuery(query);
		System.out.println("entro");
		List<StructureCatalogoPrecios> listCatalogo = new ArrayList<StructureCatalogoPrecios>();
		for (Iterator it = quer.getResultList().iterator(); it.hasNext();) {
			Object[] row = (Object[]) it.next();
			StructureCatalogoPrecios structureCatalogoPrecios = new StructureCatalogoPrecios(
					(Integer) row[0], row[1].toString(), (double) row[2]);

			listCatalogo.add(structureCatalogoPrecios);
		}
		System.out.println("paso  : " + listCatalogo.size());

		return listCatalogo;
	}

	public List<Producto> findProductoForAlmacen(Almacen alm) {
		String query = "select distinct(ser.producto) from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') "
				+ "  and ser.almacen.id=" + alm.getId() + " ";
		System.out.println("Query AlmProducto: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<AlmProducto> findForAlmacen(Almacen alm) {
		String query = "select ser from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') "
				+ "  and ser.almacen.id="
				+ alm.getId()
				+ " order by ser.producto.nombreProducto asc";
		System.out.println("Query AlmProducto: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<AlmProducto> findForAlmacenAndTipoProducto(Almacen alm,
			TipoProducto tipo) {
		String query = "select ser from AlmProducto ser where ser.estado='AC' and ser.almacen.id="
				+ alm.getId()
				+ " and ser.producto.tipoProducto.id= "
				+ tipo.getId() + " order by ser.producto.nombreProducto asc";
		System.out.println("Query AlmProducto: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<Proveedor> findProveedorProductoForAlmacen(Almacen alm,
			Producto p) {
		String query = "select distinct(ser.proveedor) from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') "
				+ "  and ser.almacen.id="
				+ alm.getId()
				+ "  and ser.producto.id=" + p.getId();
		System.out.println("Query AlmProducto: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<Proveedor> findProveedorForAlmacen(Almacen alm) {
		String query = "select distinct(ser.proveedor) from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') "
				+ "  and ser.almacen.id=" + alm.getId() + " and ser.stock>0 ";
		System.out.println("Query AlmProducto: " + query);
		return em.createQuery(query).getResultList();
	}

	public AlmProducto findAlmacenProductoForAlmacenAndProducto(Almacen alm,
			Producto p) {
		String query = "select pro from AlmProducto pro where pro.producto.id ="
				+ p.getId()
				+ " and pro.almacen.id="
				+ alm.getId()
				+ "  and pro.stock in( select max(pro1.stock) from AlmProducto pro1 where pro1.producto.id ="
				+ p.getId() + " and pro1.almacen.id=" + alm.getId() + "  )";
		System.out.println("Query AlmProducto: " + query);
		return (AlmProducto) em.createQuery(query).getSingleResult();
	}

	//

	public List<AlmProducto> findProductosForAlmacen(Almacen alm,
			Proveedor proveedor) {
		String query = "select ser from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') "
				+ "  and ser.almacen.id="
				+ alm.getId()
				+ "  and ser.proveedor.id="
				+ proveedor.getId()
				+ " and ser.stock>0 order by ser.producto.nombre asc";
		System.out.println("Query AlmProducto: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<LineasProveedor> findLineaProveedorProductoForAlmacen(
			Almacen alm, Producto p, Proveedor pv) {
		String query = "select distinct(ser.lineaProvedor) from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') "
				+ "  and ser.almacen.id="
				+ alm.getId()
				+ "  and ser.producto.id="
				+ p.getId()
				+ "  and ser.proveedor.id="
				+ pv.getId()
				+ " order by ser.lineaProvedor.nombre asc";
		System.out.println("Query AlmProducto: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<LineasProveedor> findAllLineaProveedorProductoForAlmacen(
			Almacen alm, Producto p, Proveedor pv, LineasProveedor

			lp) {
		String query = "select distinct(ser.lineaProvedor) from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') "
				+ "  and ser.almacen.id="
				+ alm.getId()
				+ "  and ser.producto.id="
				+ p.getId()
				+ "  and ser.proveedor.id="
				+ pv.getId()
				+ "  and ser.lineaProvedor.id="
				+ lp.getId()
				+ " order by ser.lineaProvedor.nombre asc";
		System.out.println("Query AlmProducto: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<ProductoProveedor> findAllAlmProductoActivos() {

		String query = "select  ser.producto.nombreProducto,ser.proveedor.nombre, avg(ser.producto.id) as idpro, avg(ser.proveedor.id) as idprov, avg(ser.lineaProvedor.id) as idli"
				+ "  from AlmProducto ser, ProductoProveedor pp where ser.estado='AC' and ser.producto.id=pp.producto.id"
				+ " group by ser.producto.nombreProducto, ser.proveedor.nombre order by ser.producto.nombreProducto asc";
		System.out.println("Query AlmProducto: " + query);

		Query quer = em.createQuery(query);
		System.out.println("entro");
		List<ProductoProveedor> listCatalogo = new ArrayList<ProductoProveedor>();
		for (Iterator it = quer.getResultList().iterator(); it.hasNext();) {
			Object[] row = (Object[]) it.next();
			ProductoProveedor productoProveedor = new ProductoProveedor();

			query = "select pro from ProductoProveedor pro where pro.producto.id= "
					+ row[3] + " and pro.estado='AC'";

			System.out.println(query);
			productoProveedor = (ProductoProveedor) em.createQuery(query)
					.getSingleResult();
			listCatalogo.add(productoProveedor);
		}
		System.out.println("paso  : " + listCatalogo.size());

		return listCatalogo;
	}

	public List<AlmProducto> findAllAlmProductoCatalogo() {
		String query = "select order by ser.producto.nombreProducto,ser.proveedor.nombre,ser.lineaProvedor.nombre,"
				+ "  concat(ser.producto.cantidadUnidadPresentacion.presentacion.descripcion,' ', ser.producto.cantidadUnidadPresentacion.cantidad,' ',ser.producto.cantidadUnidadPresentacion.unidadMedida.descripcion) "
				+ "  from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') "
				+ " group by ser.producto.nombreProducto,ser.proveedor.nombre,ser.lineaProvedor.nombre";
		System.out.println("Query AlmProducto: " + query);

	
		return em.createQuery(query).getResultList();
	}

	public List<AlmProducto> findAlmProductosDiferentsUser(Usuario user) {
		String query = "select ser from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') and ser.usuario.id<>"
				+ user.getId() + " order by ser.id desc";
		System.out.println("Query AlmProducto: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<AlmProducto> findAllOrderedByFechaRegistro() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AlmProducto> criteria = cb.createQuery(AlmProducto.class);
		Root<AlmProducto> presentacion = criteria.from(AlmProducto.class);
		criteria.select(presentacion).orderBy(
				cb.desc(presentacion.get("fechaRegistro")));
		return em.createQuery(criteria).getResultList();
	}

	public List<AlmProducto> findAllAlmProductoForDescription(String criterio) {
		try {
			String query = "select ser from AlmProducto ser where ser.nombre like '%"
					+ criterio + "%'";
			System.out.println("Consulta: " + query);
			List<AlmProducto> listaAlmProducto = em.createQuery(query)
					.getResultList();
			return listaAlmProducto;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAllAlmProductoForDescription: "
					+ e.getMessage());
			return null;
		}
	}

	public List<AlmProducto> traerAlmProductoActivas() {
		try {
			String query = "select ser from AlmProducto ser where ser.estado='AC' order by ser.id desc";
			System.out.println("Consulta traerAlmProductoActivas: " + query);
			List<AlmProducto> listaAlmProducto = em.createQuery(query)
					.getResultList();
			return listaAlmProducto;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerAlmProductoActivas: "
					+ e.getMessage());
			return null;
		}
	}

	public List<AlmProducto> traerAlmProductoForAlmacenAndProducto(
			Producto pro, Almacen alm) {
		try {
			String query = "select pro1 from AlmProducto pro1 where pro1.producto.id ="
					+ pro.getId()
					+ " and pro1.almacen.id="
					+ alm.getId()
					+ " and pro1.stock>0  order by pro1.stock desc";
			System.out
					.println("Consulta traerAlmProductoForAlmacenAndProducto: "
							+ query);
			List<AlmProducto> listaAlmProducto = em.createQuery(query)
					.getResultList();
			return listaAlmProducto;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerAlmProductoActivas: "
					+ e.getMessage());
			return null;
		}
	}

	public AlmProducto findProductoStock(Almacen alm, Producto pro,
			Proveedor prov, LineasProveedor lp) {
		try {
			String query = "select pro from AlmProducto pro where pro.producto.id= "
					+ pro.getId()
					+ "  and pro.almacen.id="
					+ alm.getId()
					+ "  and pro.proveedor.id="
					+ prov.getId()
					+ " and pro.lineaProvedor.id=" + lp.getId();
			System.out.println("Consulta: " + query);
			List<AlmProducto> lis = em.createQuery(query).getResultList();
			if (lis.size() > 0) {
				System.out.println("Stock : " + lis.get(0).getStock());
				return lis.get(0);
			} else {
				AlmProducto prod = new AlmProducto();
				prod.setStock(0);
				return prod;
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findProductoStock: " + e.getMessage());
			return null;
		}
	}

	public List<AlmProducto> findAll100UltimosAlmProducto() {
		try {
			String query = "select ser from AlmProducto ser order by ser.fechaRegistro desc";
			System.out.println("Consulta: " + query);
			List<AlmProducto> listaAlmProducto = em.createQuery(query)
					.setMaxResults(100).getResultList();
			return listaAlmProducto;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findAll100UltimosAlmProducto: "
					+ e.getMessage());
			return null;
		}
	}

}