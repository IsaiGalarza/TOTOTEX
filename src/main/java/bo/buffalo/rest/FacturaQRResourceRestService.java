package bo.buffalo.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONObject;

import bo.buffalo.model.Compra;
import bo.buffalo.model.Factura;
import bo.buffalo.model.Proveedor;
import bo.buffalo.model.Usuario;
import bo.buffalo.service.CompraRegistration;
import bo.buffalo.service.ProveedorRegistration;

@Path("/facturaQR")
public class FacturaQRResourceRestService {

	@Inject SyncLink sync;

	@Inject
	private Logger log;

	@Inject
	private CompraRegistration compraRegistration;

	@Inject
	private ProveedorRegistration proveedorRegistration;



	@Inject
	private EntityManager em;

	/*
	 * REST-FUL MEDIPIELMOVIl QR
	 */
	/**
	 * Inicio session login
	 * @method validarLogin
	 * @param String json
	 * @return Response
	 */
	@POST
	@Path("/validarLogin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json; charset=UTF-8")
	public Response validarLogin(String json) {
		sync.getLock().lock();
		System.out.println("validarLogin json: "+json);
		Response.ResponseBuilder builder = null;
		Map<String, String> responseObj = new HashMap<String, String>();

		try {
			JSONObject jsonObject = new JSONObject(json);
			String user = jsonObject.getString("user");
			String password = jsonObject.getString("password");

			Usuario usuario = findByLogin(user);
			if(usuario!= null){
				if(usuario.getPassword().equals(password) ){ //verificar si el user y pass son correcto
					responseObj.put("result", Response.Status.OK.getReasonPhrase());//OK
				}else{
					responseObj.put("result", Response.Status.NOT_FOUND.getReasonPhrase());//Not Found
				}
			}else{
				responseObj.put("result", Response.Status.NOT_FOUND.getReasonPhrase());//Not Found
			}
			builder = Response.status(Response.Status.OK).entity(responseObj);
		}catch(Exception e){
			e.printStackTrace();
			responseObj.put("result",Response.Status.CONFLICT.getReasonPhrase());
			builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
		}
		sync.getLock().unlock();
		return builder.build();
	}

	/**
	 * Registrar Libro de Compras
	 * @method registrarProveedor
	 * @param String json
	 * @return Response
	 */
	@POST
	@Path("/registrarLibroCompra")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json; charset=UTF-8")
	public Response registrarLibroCompra(String json) {
		sync.getLock().lock();
		System.out.println("registrarLibroCompra json: "+json);
		Response.ResponseBuilder builder = null;
		Map<String, String> responseObj = new HashMap<String, String>();
		Usuario usuario = new Usuario();
		Proveedor proveedor = new Proveedor();

		try {
			JSONObject jsonObject = new JSONObject(json);
			//parametros del usuario
			String user = jsonObject.getString("user");
			String password = jsonObject.getString("password");
			//-parametros factura qr
			String nitProveedor = jsonObject.getString("nitProveedor");//1
			String numeroFactura = jsonObject.getString("numeroFactura");//2
			String numeroAutorizacion = jsonObject.getString("numeroAutorizacion");//3
			String fechaFactura = jsonObject.getString("fechaFactura");//4
			String importeTotal = jsonObject.getString("importeTotal");//5
			String importeBaseCreditoFiscal = jsonObject.getString("importeBaseCreditoFiscal");//6
			String codigoControl = jsonObject.getString("codigoControl");//7
			String nitCliente = jsonObject.getString("nitCliente");//8
			String importeICE = jsonObject.getString("importeICE");//9
			String importe_ventas_grabadas_tasa_cero = jsonObject.getString("importe_ventas_grabadas_tasa_cero");//10
			String importeNoSujetoCreditoFiscal = jsonObject.getString("importeNoSujetoCreditoFiscal");//11
			String descuentosBonosRebajas = jsonObject.getString("descuentosBonosRebajas");//12

			//0 verificar usuario y password
			usuario = findByLogin(user);
			if(usuario!= null){
				//1 verificar si el proveedor existe
				proveedor = findProveedorByNit(nitProveedor);
				if(proveedor == null){
					//respuesta - no esta registrado el proveedor
					responseObj.put("result", "No Existe Proveedor");
				}else{
					//2 Verificar si la factura ya fue registrada anteriormente
					Date dateFactura = new SimpleDateFormat("dd/MM/yyyy").parse(fechaFactura.replace("-", "/"));
					Compra compra = existeCompra(dateFactura, nitProveedor, numeroFactura);
					if(compra != null){
						//respuesta - si la factura ya fue registrada anteriormente
						responseObj.put("result", "Ya Registrada");
					}else{

						//3 proceso de registro de libro de compra
						String mCodigoControl = codigoControl;
						String mGestion = fechaFactura.substring(6, fechaFactura.length());
						String mMes = fechaFactura.substring(3, 5);
						int mCorrelativo = traerComprasGestionFiscal(mMes, mGestion).size()+1;//int
						double mCreditoFiscal = Double.parseDouble(importeTotal)*0.13;//IVA
						double mDescuentosBonosRebajas = Double.parseDouble(descuentosBonosRebajas);//de acuerdo a nueva norma
						String mEstado = "AC";
						Date mFechaFactura = dateFactura;
						Date mFechaRegistro = new Date();
						Integer mIdSucursal = usuario.getSucursal().getId();
						double mImporteBaseCreditoFiscal = 0;
						double mImporteExcentos = 0;
						double mImporteICE = Double.parseDouble(importeICE); //de acuerdo a antigua norma
						double mImporteNoSujetoCreditoFiscal = Double.parseDouble(importeNoSujetoCreditoFiscal); //de acuerdo a nueva norma
						double mImporteSubTotal = Double.parseDouble(importeBaseCreditoFiscal); //de acuerdo a nueva norma
						double mImporteTotal = Double.parseDouble(importeTotal);

						String mNitProveedor = nitProveedor;
						String mNumeroAturizacion = numeroAutorizacion;
						String mNumeroDUI = "0";
						String mNumeroFactura = numeroFactura;
						String mRazonSocial = proveedor.getNombre();
						String mTipoCompra = "1";
						String mUsuarioRegistro = user;

						Compra newCompra = new Compra();
						newCompra.setMes(mMes);
						newCompra.setGestion(mGestion);
						newCompra.setCodigoControl(mCodigoControl);
						newCompra.setCorrelativo(mCorrelativo);
						newCompra.setCreditoFiscal(mCreditoFiscal);
						newCompra.setDescuentosBonosRebajas(mDescuentosBonosRebajas);
						newCompra.setEstado(mEstado);
						newCompra.setFechaFactura(mFechaFactura);
						newCompra.setFechaRegistro(mFechaRegistro);
						newCompra.setIdSucursal(mIdSucursal);
						newCompra.setImporteBaseCreditoFiscal(mImporteBaseCreditoFiscal);
						newCompra.setImporteExcentos(mImporteExcentos);
						newCompra.setImporteICE(mImporteICE);
						newCompra.setImporteNoSujetoCreditoFiscal(mImporteNoSujetoCreditoFiscal);
						newCompra.setImporteSubTotal(mImporteSubTotal);
						newCompra.setImporteTotal(mImporteTotal);
						newCompra.setNitProveedor(mNitProveedor);
						newCompra.setNumeroAutorizacion(mNumeroAturizacion);
						newCompra.setNumeroDUI(mNumeroDUI);
						newCompra.setNumeroFactura(mNumeroFactura);
						newCompra.setRazonSocial(mRazonSocial);
						newCompra.setTipoCompra(mTipoCompra);
						newCompra.setUsuarioRegistro(mUsuarioRegistro);
						compraRegistration.register(newCompra);
						//respuesta valida - se registro correctamente
						responseObj.put("result", Response.Status.OK.getReasonPhrase());
					}
				}
			}else{
				//verificar este problema si el usuario no existe
				responseObj.put("result",Response.Status.CONFLICT.getReasonPhrase());
			}

			builder = Response.status(Response.Status.OK).entity(responseObj);
		}catch(Exception e){
			e.printStackTrace();
			responseObj.put("result",Response.Status.CONFLICT.getReasonPhrase());
			builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
		}
		sync.getLock().unlock();
		return builder.build();
	}

	/**
	 * Registrar Proveedor
	 * @method registrarProveedor
	 * @param String json
	 * @return Response
	 */
	@POST
	@Path("/registrarProveedor")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json; charset=UTF-8")
	public Response registrarProveedor(String json) {
		sync.getLock().lock();
		System.out.println("registrarProveedor json: "+json);
		Response.ResponseBuilder builder = null;
		Map<String, String> responseObj = new HashMap<String, String>();

		try {
			JSONObject jsonObject = new JSONObject(json);
			//parametros del usuario
			String user = jsonObject.getString("user");

			String nitProveedor = jsonObject.getString("nitProveedor");
			String nombreProveedor = jsonObject.getString("nombreProveedor");
			String numeroAutorizacion = jsonObject.getString("numeroAutorizacion");

			Usuario usuario = findByLogin(user);
			if(usuario!= null){
				//aqui proceso de registro del proveedor

				//1 verificar si existe el proveedor
				Proveedor proveedor = findProveedorByNit(nitProveedor);
				if(proveedor == null){
					proveedor = new Proveedor();
					proveedor.setNit(nitProveedor);
					proveedor.setNombre(nombreProveedor);
					proveedor.setNumeroAutorizacion(numeroAutorizacion);
					proveedorRegistration.register(proveedor);
					responseObj.put("result", Response.Status.OK.getReasonPhrase());//OK
				}else{
					responseObj.put("result", "Ya Registrado");
				}
			}else{
				responseObj.put("result", "User Not Found");
			}
			builder = Response.status(Response.Status.OK).entity(responseObj);
		}catch(Exception e){
			e.printStackTrace();
			responseObj.put("result",Response.Status.CONFLICT.getReasonPhrase());
			builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
		}
		sync.getLock().unlock();
		return builder.build();
	}

	/**
	 * obtenerLibroCompra
	 * @param login
	 * @return
	 */
	@POST
	@Path("/obtenerLibroCompra")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json; charset=UTF-8")
	public Response obtenerLibroCompra(String json) {
		sync.getLock().lock();
		System.out.println("obtenerLibroCompra json: "+json);
		Response.ResponseBuilder builder = null;
		JSONArray jsonList = new JSONArray();
		try{
			JSONObject jsonObject = new JSONObject(json);
			//parametros del usuario
			String user = jsonObject.getString("user");
			//parametros libroCompra
			String fechaInicio = jsonObject.getString("fechaInicio");
			String fechaFinal = jsonObject.getString("fechaFinal");
			//validar user
			Usuario usuario = findByLogin(user);
			if(usuario!= null){
				Date dateFechaInico = new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicio.replace("-", "/"));
				Date dateFechaFin = new SimpleDateFormat("dd/MM/yyyy").parse(fechaFinal.replace("-", "/"));
				//aqui proceso para obtener libro de compra
				List<Compra> listCompra = buscarCompras(dateFechaInico, dateFechaFin);
				if(listCompra.size()>0){
					for(Compra compra : listCompra){
						JSONObject jsonObjectList = new JSONObject();
						jsonObjectList.put("campo0", compra.getNitProveedor());
						jsonObjectList.put("campo1", compra.getRazonSocial());
						jsonObjectList.put("campo2", compra.getFechaFactura().toString());
						jsonObjectList.put("campo3", compra.getNumeroFactura());
						jsonObjectList.put("campo4", String.valueOf(compra.getImporteTotal()));
						jsonList.put(jsonObjectList);
					}
				}
				System.out.println("jsonList: "+jsonList.toString());
				builder = Response.status(Response.Status.OK).entity(
						jsonList.toString());
			}else{
				jsonList = new JSONArray();
				builder = Response.status(Response.Status.NOT_FOUND);
			}
		}catch(Exception e){
			e.printStackTrace();
			jsonList = new JSONArray();
			builder = Response.status(Response.Status.CONFLICT);
		}
		sync.getLock().unlock();
		return builder.build();
	}

	/**
	 * obtenerLibroVenta
	 * @param String user, String mes, String gestion
	 * @return
	 */
	@POST
	@Path("/obtenerLibroVenta")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json; charset=UTF-8")
	public Response obtenerLibroVenta(String json) {
		sync.getLock().lock();
		System.out.println("obtenerLibroCompra ");
		Response.ResponseBuilder builder = null;
		JSONArray jsonList = new JSONArray();
		try{
			JSONObject jsonObject = new JSONObject(json);
			//parametros del usuario
			String user = jsonObject.getString("user");
			//parametros libroCompra
			String fechaInicio = jsonObject.getString("fechaInicio");
			String fechaFinal = jsonObject.getString("fechaFinal");
			//validar user
			Usuario usuario = findByLogin(user);
			if(usuario!= null){
				Date dateFechaInico = new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicio.replace("-", "/"));
				Date dateFechaFin = new SimpleDateFormat("dd/MM/yyyy").parse(fechaFinal.replace("-", "/"));
				List<Factura> listVenta=buscarFacturas(dateFechaInico, dateFechaFin);
				if(listVenta.size()>0){
					for (Factura factura : listVenta) {
						JSONObject jsonObjectList = new JSONObject();
						jsonObjectList.put("campo0", factura.getNitCi());
						jsonObjectList.put("campo1", factura.getNumeroFactura());
						jsonObjectList.put("campo2", factura.getTotalFacturado());
						jsonObjectList.put("campo3", factura.getMes());
						jsonObjectList.put("campo4", factura.getGestion());
						jsonList.put(jsonObjectList);
					}
				}
				System.out.println("jsonList: "+jsonList.toString());
				builder = Response.status(Response.Status.OK).entity(
						jsonList.toString());
			}else{
				jsonList = new JSONArray();
				builder = Response.status(Response.Status.NOT_FOUND);
			}

		}catch(Exception e){
			e.printStackTrace();
			jsonList = new JSONArray();
			builder = Response.status(Response.Status.CONFLICT);
		}
		sync.getLock().unlock();
		return builder.build();
	}


	/**
	 * obtenerLibroVenta
	 * @param String user, String mes, String gestion
	 * @return
	 */
	@POST
	@Path("/obtenerListaProveedores")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json; charset=UTF-8")
	public Response obtenerListaProveedores(String json) {
		sync.getLock().lock();
		System.out.println("obtenerLibroCompra ");
		Response.ResponseBuilder builder = null;
		JSONArray jsonList = new JSONArray();

		try{
			JSONObject jsonObject = new JSONObject(json);
			//parametros del usuario
			String user = jsonObject.getString("user");
			//parametros libroCompra
			System.out.println("Lista Proveedores");
			//validar user
			Usuario usuario = findByLogin(user);
			if(usuario!= null){
				try {
					List<Proveedor> prov=getListProveedor();
					if(prov.size()>0){
						for (Proveedor proveedor : prov) {
							JSONObject jsonObjectList = new JSONObject();
							jsonObjectList.put("campo0", proveedor.getId());
							jsonObjectList.put("campo1", proveedor.getNombre());
							jsonObjectList.put("campo2", proveedor.getNit());
							jsonObjectList.put("campo3", proveedor.getNumeroAutorizacion());
							jsonObjectList.put("campo4", (proveedor.getTelefono()==null)?"":proveedor.getTelefono());
							jsonList.put(jsonObjectList);

						}
					}
					System.out.println("jsonList: "+jsonList.toString());
					builder = Response.status(Response.Status.OK).entity(
							jsonList.toString());
				} catch (Exception e) {
					System.out.println(e.toString());
					builder = Response.status(Response.Status.CONFLICT);
				}
			}else{
				jsonList = new JSONArray();
				builder = Response.status(Response.Status.NOT_FOUND);
			}

		}catch(Exception e){
			e.printStackTrace();
			jsonList = new JSONArray();
			builder = Response.status(Response.Status.CONFLICT);
		}
		sync.getLock().unlock();
		return builder.build();
	}


	//--------------------METODOS-------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<Factura> buscarFacturas(Date fechaIni, Date fechaFin) {
		try {
			System.out.println("Ingreso a buscarFacturasSucursal....");

			return em.createQuery("select fac FROM Factura fac WHERE fac.fechaFactura>=:stDate and fac.fechaFactura<=:edDate "
					+ " order by fac.fechaRegistro desc")
					.setParameter("stDate", fechaIni)
					.setParameter("edDate", fechaFin)
					.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error en buscar  Facturas: "+e.getMessage());
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	public List<Compra> buscarCompras(Date fechaIni, Date fechaFin) {
		try {
			System.out.println("Ingreso a buscar Libro Compras....");

			return em.createQuery("select fac FROM Compra fac WHERE fac.fechaFactura>=:stDate and fac.fechaFactura<=:edDate "
					+ " order by fac.fechaRegistro desc")
					.setParameter("stDate", fechaIni)
					.setParameter("edDate", fechaFin)
					.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error en buscarFacturasSucursal: "+e.getMessage());
			return null;
		}
	}

	public Usuario findByLogin(String login) {
		try{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Usuario> criteria = cb.createQuery(Usuario.class);
			Root<Usuario> user = criteria.from(Usuario.class);
			criteria.select(user).where(cb.equal(user.get("login"), login));
			return em.createQuery(criteria).getSingleResult();
		}catch(NoResultException e2){
			e2.printStackTrace();
			return null;
		}
	}

	private Proveedor findProveedorByNit(String nit){
		try{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Proveedor> criteria = cb.createQuery(Proveedor.class);
			Root<Proveedor> Proveedor = criteria.from(Proveedor.class);
			criteria.select(Proveedor).where(cb.equal(Proveedor.get("nit"), nit));
			return em.createQuery(criteria).getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}

	private List<Proveedor> getListProveedor(){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Proveedor> criteria = cb.createQuery(Proveedor.class);
		Root<Proveedor> detailCall = criteria.from(Proveedor.class);
		criteria.select(detailCall);
		return em.createQuery(criteria).getResultList();
	}

	private Compra existeCompra(Date fechaFactura , String nitProveedor, String numeroFactura){
		try{
			System.out.println("existeCompra fechaFactura: "+fechaFactura.toString());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Compra> criteria = cb.createQuery(Compra.class);
			Root<Compra> compra = criteria.from(Compra.class);
			criteria.select(compra).where(cb.equal(compra.get("nitProveedor"), nitProveedor),
					cb.equal(compra.get("numeroFactura"),numeroFactura),
					cb.equal(compra.get("fechaFactura"),fechaFactura));
			return em.createQuery(criteria).getSingleResult();

		}catch(NoResultException e){
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Compra> traerComprasGestionFiscal(String mes, String gestion) {
		try {
			String query = "select com from Compra com where com.estado='AC' and com.mes='"+mes+"' and com.gestion='"+gestion+"' order by com.id desc";
			System.out.println("Consulta traerComprasGestionFiscal: "+query);
			List<Compra> listaCompra = em.createQuery(query).getResultList();
			System.out.println("Resultado: "+listaCompra.size());
			return listaCompra;        	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error en traerServiciosUltimas10Compras: "+e.getMessage());
			return null;
		}
	}
}