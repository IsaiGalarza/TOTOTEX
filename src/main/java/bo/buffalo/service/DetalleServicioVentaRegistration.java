package bo.buffalo.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import bo.buffalo.model.DetalleServicioVenta;
import bo.buffalo.model.Proforma;

//The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class DetalleServicioVentaRegistration {

	@Inject
	private Logger log;

	@Inject
	private EntityManager em;

	@Inject
	private Event<DetalleServicioVenta> detalleFarmacoVentaEventSrc;

	public void register(DetalleServicioVenta detalleFarmacoVenta)
			throws Exception {
		log.info("Registering DetalleServicioVenta: ");
		em.persist(detalleFarmacoVenta);
		detalleFarmacoVentaEventSrc.fire(detalleFarmacoVenta);
	}

	public void updated(DetalleServicioVenta detalleFarmacoVenta)
			throws Exception {
		log.info("Updated DetalleServicioVenta: " + detalleFarmacoVenta.getId());
		em.merge(detalleFarmacoVenta);
		detalleFarmacoVentaEventSrc.fire(detalleFarmacoVenta);
	}

	public void remover(DetalleServicioVenta detalleFarmacoVenta) {
		log.info("Remover DetalleServicioVenta: " + detalleFarmacoVenta.getId());
		detalleFarmacoVenta.setEstado("RM");
		em.merge(detalleFarmacoVenta);
		detalleFarmacoVentaEventSrc.fire(detalleFarmacoVenta);
	}

	public void removerForProforma(Proforma proforma) {
		log.info("Remover DetalleServicioVenta: " + proforma.getId());

		String query = "delete from DetalleServicioVenta pro where  pro.proforma.id="
				+ proforma.getId();
		System.out.println("Query Servicios: " + query);
		em.createQuery(query).executeUpdate();

	}

	/*
	 * public void deleteAll(Proveedor proveedor){
	 * log.info("Update GastosProveedor: " + proveedor.getNombre()); String
	 * query
	 * ="delete from GastosProveedor gp where gp.proveedor.id="+proveedor.getId
	 * (); Query queries= (Query) em.createQuery(query); int result =
	 * queries.executeUpdate(); }
	 */

}
