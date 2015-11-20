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

import bo.buffalo.model.AlmProducto;
import bo.buffalo.model.Almacen;
import bo.buffalo.model.LineasProveedor;
import bo.buffalo.model.Producto;
import bo.buffalo.model.ProductoProveedor;
import bo.buffalo.model.Proveedor;

@ApplicationScoped
public class ProductoProveedorRepository {

	@Inject
	private EntityManager em;

	public ProductoProveedor findById(int id) {
		return em.find(ProductoProveedor.class, id);
	}

	public List<ProductoProveedor> findAllOrderedByProducto() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductoProveedor> criteria = cb
				.createQuery(ProductoProveedor.class);
		Root<ProductoProveedor> producto = criteria
				.from(ProductoProveedor.class);
		criteria.select(producto).orderBy(
				cb.desc(producto.get("nombreProductoProveedor")));
		return em.createQuery(criteria).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ProductoProveedor> findAllOrderedByIDForProducto(
			Producto producto) {
		String query = "select pro from ProductoProveedor pro where (pro.estado='AC' or pro.estado='IN')  and pro.producto.id="
				+ producto.getId() + " order by pro.id desc";
		System.out.println("Query Servicios: " + query);
		return em.createQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ProductoProveedor> traerProductoProveedor(Producto producto) {
		String query = "select pro from ProductoProveedor pro where pro.estado='AC' and pro.producto.id="
				+ producto.getId() + " order by pro.id desc";
		System.out.println("Query traerProductoProveedor: " + query);
		return em.createQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ProductoProveedor> traerProductoProveedorStco(Producto producto) {
		String query = "select pro from ProductoProveedor pro where pro.estado='AC' and pro.producto.id="
				+ producto.getId() + " order by pro.id desc";
		System.out.println("Query traerProductoProveedor: " + query);
		return em.createQuery(query).getResultList();
	}

	public List<ProductoProveedor> findAllOrderedByFechaRegistro() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductoProveedor> criteria = cb
				.createQuery(ProductoProveedor.class);
		Root<ProductoProveedor> producto = criteria
				.from(ProductoProveedor.class);
		criteria.select(producto).orderBy(
				cb.desc(producto.get("fechaRegistro")));
		return em.createQuery(criteria).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ProductoProveedor> findAllProductoProveedorsForDescription(
			String criterio) {
		try {
			String query = "select pro from ProductoProveedor pro where pro.nombreProductoProveedor like '%"
					+ criterio + "%'";
			System.out.println("Consulta: " + query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out
					.println("Error en findAllProductoProveedorsForDescription: "
							+ e.getMessage());
			return null;
		}
	}

	public List<ProductoProveedor> findProductoStock(Producto pro,
			Proveedor prov, LineasProveedor lp) {
		try {
			String query = "select pro from ProductoProveedor pro where pro.producto.id= "
					+ pro.getId()
					+ " and pro.proveedor.id="
					+ prov.getId()
					+ " and pro.lineasProveedor.id=" + lp.getId();
			System.out.println("Consulta: " + query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findProductoStock: " + e.getMessage());
			return null;
		}
	}

	public List<ProductoProveedor> findProductoProveedor(Producto pro) {
		try {
			String query = "select pro from ProductoProveedor pro where pro.producto.id="
					+ pro.getId()
					+ " and pro.producto.id in "
					+ "(select alm.producto.id from  AlmProducto alm where alm.producto.id="
					+ pro.getId() + " and alm.stock>0) ";
			System.out.println("Consulta: " + query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findProductoStock: " + e.getMessage());
			return null;
		}
	}

	public ProductoProveedor findProductoProveedorMaxPrecioCompra(
			Producto pro) {
		try {
			String query = "select pro from ProductoProveedor pro where pro.producto.id ="
					+ pro.getId()
					+ "  and pro.precioUnitarioCompra in( select max(pro1.precioUnitarioCompra) from ProductoProveedor pro1 where pro1.producto.id ="
					+ pro.getId() + "  )";
			System.out.println("findProductoProveedorMaxPrecioCompra : " + query);
			return (ProductoProveedor) em.createQuery(query).getSingleResult();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findProductoStock: " + e.getMessage());
			return null;
		}
	}

	public ProductoProveedor findProductoProveedor(Producto pro,
			Proveedor prov, LineasProveedor lp) {
		try {
			String query = "select pro from ProductoProveedor pro where pro.producto.id= "
					+ pro.getId()
					+ " and pro.proveedor.id="
					+ prov.getId()
					+ " and pro.lineasProveedor.id=" + lp.getId();
			System.out.println("Consulta: " + query);
			return (ProductoProveedor) em.createQuery(query).getSingleResult();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findProductoProveedor: "
					+ e.getMessage());
			return null;
		}
	}

	public ProductoProveedor findProductoProveedor(Integer pro, Integer prov,
			Integer lp) {
		try {
			String query = "select pro from ProductoProveedor pro where pro.producto.id= "
					+ pro
					+ " and pro.proveedor.id="
					+ prov
					+ " and pro.lineasProveedor.id=" + lp;
			System.out.println("Consulta: " + query);
			return (ProductoProveedor) em.createQuery(query).getSingleResult();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en findProductoProveedor: "
					+ e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean deleteAllProductoProveedorsForProducto(Producto producto) {
		try {
			String query = "delete ProductoProveedor  where producto.id="
					+ producto.getId();
			System.out.println("Consulta: " + query);
			int res = em.createQuery(query).executeUpdate();
			System.out.println("res: " + res);
			return res == 1;
		} catch (Exception e) {
			// TODO: handle exception
			System.out
					.println("Error en deleteAllProductoProveedorsForProducto: "
							+ e.getMessage());
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProductoProveedor> traerProductoProveedors() {
		try {
			String query = "select pro from ProductoProveedor pro where pro.estado='AC'  order by pro.id  desc";
			System.out.println("Consulta traerServicios: " + query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProductoProveedors: "
					+ e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProductoProveedor> traerProductoProveedors(Proveedor proveedor,
			Almacen almacen) {
		try {
			String query = "select pro from ProductoProveedor pro where pro.estado='AC' and pro.proveedor.id="
					+ proveedor.getId()
					+ " "
					+ "and pro.proveedor.id in(select ser.proveedor.id from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') "
					+ "  and ser.almacen.id="
					+ almacen.getId()
					+ " and ser.stock>0 )  order by pro.id  desc";
			System.out.println("Consulta traerServicios: " + query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProductoProveedors: "
					+ e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProductoProveedor> traerProductoProveedor(Proveedor proveedor) {
		try {
			String query = "select pro from ProductoProveedor pro where pro.proveedor.estado='AC' and pro.proveedor.id="
					+ proveedor.getId()
					+ " and pro.producto.estado='AC' "
					+ "order by pro.producto.nombreProducto asc";
			System.out.println("Consulta traerProductoProveedor: " + query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			System.out.println("Error en traerProductoProveedors: "
					+ e.getMessage());
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductoProveedor> traerTodosProductoProveedor(Proveedor proveedor) {
		try {
			String query = "select pro from ProductoProveedor pro where pro.proveedor.estado='AC' and pro.proveedor.id="
					+ proveedor.getId()
					+ " and pro.producto.estado='AC'  "
					+ "order by pro.producto.nombreProducto asc";
			System.out.println("Consulta traerProductoProveedor: " + query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			System.out.println("Error en traerProductoProveedors: "
					+ e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<AlmProducto> traerProductoProveedors(Almacen almacen) {
		try {
			String query = "select ser from AlmProducto ser where (ser.estado='AC' or ser.estado='IN') "
					+ "  and ser.almacen.id="
					+ almacen.getId()
					+ " and ser.stock>0   order by ser.producto.nombreProducto asc";
			System.out.println("Consulta traerServicios: " + query);
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en traerProductoProveedors: "
					+ e.getMessage());
			return null;
		}
	}

}
