package bo.buffalo.util;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "documentService")
@ApplicationScoped
public class DocumentService implements Serializable{
     
    public TreeNode createDocuments() {
        TreeNode root = new DefaultTreeNode(new Document("Files", "-", "Folder"), null);
         
        TreeNode venta = new DefaultTreeNode(new Document("Venta", "-", "Folder","/buffalo/pages/ventas/home.xhtml"), root);//Parent
   
        
        //Venta sub-nodes
        TreeNode facturacion = new DefaultTreeNode("print",new Document("Facturacion", "-", "Folder","/buffalo/pages/ventas/facturacion.xhtml"), venta);
        TreeNode cuentas = new DefaultTreeNode(new Document("Cuentas/Cobrar", "-", "Folder",""), venta);
        TreeNode proforma = new DefaultTreeNode(new Document("Proforma", "-", "Folder","/buffalo/pages/ventas/proforma-home.xhtml"), venta);
        
        TreeNode libroIva = new DefaultTreeNode(new Document("Vademecum", "-", "Folder","/buffalo/pages/contabilidad/gestion-contable.xhtml"), venta);
        TreeNode libroVenta = new DefaultTreeNode("document",new Document("Libro de Venta", "-", "Folder","/buffalo/pages/contabilidad/facturas.xhtml"), libroIva);
        TreeNode libroCompra = new DefaultTreeNode("document",new Document("Libro de Compra", "-", "Folder","/buffalo/pages/contabilidad/lc-compras.xhtml"), libroIva);
        TreeNode exportarVenta = new DefaultTreeNode("document",new Document("Exportar Venta", "-", "Folder","/buffalo/pages/contabilidad/exportacion-ventas.xhtml"), libroIva);
        TreeNode exportarCompra = new DefaultTreeNode("document",new Document("Exportar Compra", "-", "Folder","/buffalo/pages/contabilidad/exportacion-compras.xhtml"), libroIva);
       
        
        TreeNode cliente = new DefaultTreeNode("document",new Document("Cliente", "-", "Folder","/buffalo/pages/ventas/cliente.xhtml"), venta);
        
        TreeNode ventas = new DefaultTreeNode(new Document("Ventas", "-", "Folder"), venta);
         
        
        TreeNode preparado = new DefaultTreeNode("document", new Document("Proforma Preparado", "40 KB", "Pages Document","/buffalo/pages/ventas/proforma-preparado.xhtml"), proforma);
        TreeNode farmaco = new DefaultTreeNode("document", new Document("Proforma Farmaco", "40 KB", "Pages Document","/buffalo/pages/ventas/proforma-farmaco.xhtml"), proforma);
         
        TreeNode proformaVenta = new DefaultTreeNode("document", new Document("Proforma Venta", "40 KB", "Pages Document","/buffalo/pages/ventas/proforma-ventas.xhtml"), ventas);
        TreeNode reporteVenta = new DefaultTreeNode("document", new Document("Reporte Venta", "40 KB", "Pages Document","/buffalo/pages/ventas/ventas.xhtml"), ventas);
       
        //Modulo Compra
        TreeNode compra = new DefaultTreeNode(new Document("Compra", "-", "Folder","/buffalo/pages/inventario/compra/home.xhtml"), root);//Parent
        
        TreeNode pedidos = new DefaultTreeNode("document", new Document("Pedidos", "40 KB", "Pages Document","/buffalo/pages/inventario/almacen/register-pedido.xhtml"), compra);
        TreeNode compras = new DefaultTreeNode("document", new Document("Compras", "40 KB", "Pages Document"), compra);
        TreeNode traspaso = new DefaultTreeNode("document", new Document("Trapaso", "40 KB", "Pages Document","/buffalo/pages/inventario/almacen/entrega-pedido-interno.xhtml"), compra);
        TreeNode proveedores = new DefaultTreeNode("document", new Document("Proveedores", "40 KB", "Pages Document","/buffalo/pages/inventario/compra/lista-proveedor.xhtml"), compra);
        
        //Modulo Inventario
        TreeNode inventario = new DefaultTreeNode(new Document("Inventario", "-", "Folder","/buffalo/pages/inventario/home.xhtml"), root);
        
        TreeNode catalogo = new DefaultTreeNode(new Document("Catalogo Producto", "30 KB", "Folder","/buffalo/pages/parametrizacion/prueba.xhtml"), inventario);
        TreeNode catalogo1 = new DefaultTreeNode("document",new Document("Catalogo", "30 KB", "Pages Document","/buffalo/pages/inventario/almacen/catalogo.xhtml"), catalogo);
        TreeNode listaProductos = new DefaultTreeNode("document",new Document("Lista de Productos", "30 KB", "Pages Document","/buffalo/pages/inventario/almacen/almacen-stock-list.xhtml"), catalogo);
        
        TreeNode entregaPedidos = new DefaultTreeNode( new Document("Entrega de Pedidos", "45 KB", "Folder","/buffalo/pages/parametrizacion/prueba.xhtml"), inventario);
        TreeNode aprovacioPedido = new DefaultTreeNode("document",new Document("Aprovacion de Pedidos", "30 KB", "Pages Document","/buffalo/pages/inventario/almacen/aprobar-pedido.xhtml"), entregaPedidos);
        TreeNode RecepcioPedidos = new DefaultTreeNode("document",new Document("Recepcion de Pedidos Externos", "30 KB", "Pages Document","/buffalo/pages/inventario/almacen/entrega-pedido.xhtml"), entregaPedidos);

        
        TreeNode cardex = new DefaultTreeNode("document", new Document("Cardex", "96 KB", "Pages Document","/buffalo/pages/inventario/almacen/cardex-producto.xhtml"), inventario);
        
        TreeNode salidaProducto = new DefaultTreeNode( new Document("Salida de Productos", "96 KB", "Folder"), inventario);
        TreeNode baja = new DefaultTreeNode("document", new Document("Baja de Producto", "96 KB", "Pages Document","/buffalo/pages/inventario/almacen/register-bajas.xhtml"), salidaProducto);
        TreeNode salida1 = new DefaultTreeNode( "document",new Document("Salida 1", "96 KB", "Pages Document",""), salidaProducto);
        TreeNode salida2 = new DefaultTreeNode( "document",new Document("Salida 2", "96 KB", "Pages Document",""), salidaProducto);
        TreeNode salida3 = new DefaultTreeNode( "document",new Document("Salida 3", "96 KB", "Pages Document",""), salidaProducto);
         
        //Modulo Produccion/Laboratorio
        TreeNode produccion = new DefaultTreeNode(new Document("Produccion/Laboratorio", "-", "Folder","/buffalo/pages/produccion/home.xhtml"), root);
        TreeNode certificacion = new DefaultTreeNode("document",new Document("Certificacion Codigo Control V7", "-", "Folder","/buffalo/pages/administracion/certificacion.xhtml"), produccion);
        TreeNode especialidades = new DefaultTreeNode("document",new Document("Especilidades Medicas", "-", "Folder","/buffalo/pages/administracion/register-especialidad.xhtml"), produccion);
        TreeNode preelaborado = new DefaultTreeNode( "document",new Document("Pre-Elaborado", "96 KB", "Pages Document",""), produccion);
        TreeNode etiqueta = new DefaultTreeNode( "document",new Document("Etiqueta", "96 KB", "Pages Document","/buffalo/pages/produccion/etiquetas.xhtml"), produccion);
        TreeNode vadimecum = new DefaultTreeNode( "document",new Document("Vadimecum", "96 KB", "Pages Document","/buffalo/pages/produccion/register-receta.xhtml"), produccion);
        TreeNode medicos = new DefaultTreeNode( "document",new Document("Medicos", "96 KB", "Pages Document","/buffalo/pages/produccion/register-medico.xhtml"), produccion);
        
        //MOdulo Contabilidad
        TreeNode contabilidad = new DefaultTreeNode(new Document("Contabilidad", "-", "Folder",""), root);
       
        
        //Modulo Parametrizacion
        TreeNode parametrizacion = new DefaultTreeNode(new Document("Parametrizacion", "-", "Folder","/buffalo/pages/parametrizacion/home.xhtml"), root);
        
        TreeNode inventarioparamentrizacion = new DefaultTreeNode(new Document("Inventario", "-", "Folder"), parametrizacion);
        
        TreeNode gestionProducto = new DefaultTreeNode(new Document("Gestion Producto", "-", "Folder"), inventarioparamentrizacion);
        TreeNode producto = new DefaultTreeNode("document",new Document("Producto", "-", "Pages Document","/buffalo/pages/inventario/register-productop.xhtml"), gestionProducto);
        TreeNode tipoProducto = new DefaultTreeNode("document",new Document("Tipo Producto", "-", "Pages Document","/buffalo/pages/inventario/register-tipo_producto.xhtml"), gestionProducto);
        TreeNode areaProducto = new DefaultTreeNode("document",new Document("Area Producto", "-", "Pages Document","/buffalo/pages/inventario/register-area.xhtml"), gestionProducto);
        TreeNode grupoProducto = new DefaultTreeNode("document",new Document("Grupo Producto", "-", "Pages Document","/buffalo/pages/inventario/register-grupo.xhtml"), gestionProducto);
        
        TreeNode gestionProveedor = new DefaultTreeNode(new Document("Gestion Proveedores", "-", "Folder"), inventarioparamentrizacion);
        TreeNode Proveedor = new DefaultTreeNode("document",new Document("Proveedore", "-", "Pages Document","/buffalo/pages/inventario/proveedor.xhtml"), gestionProveedor);
        TreeNode lineaProveedor = new DefaultTreeNode("document",new Document("Linea Proveedores", "-", "Pages Document","/buffalo/pages/inventario/linea-proveedor.xhtml"), gestionProveedor);
        TreeNode historiaProveedor = new DefaultTreeNode("document",new Document("Historia de Utilidad Proveedores", "-", "Pages Document","/buffalo/pages/inventario/historia-utilidad-proveedor.xhtml"), gestionProveedor);
        
        TreeNode gestionAlmacen = new DefaultTreeNode("document",new Document("Gestion Almacenes", "-", "Pages Document","/buffalo/pages/inventario/almacen/register-almacen.xhtml"), inventarioparamentrizacion);
        
        TreeNode gestionPresentacion = new DefaultTreeNode(new Document("Gestion Presentacion", "-", "Pages Document"), inventarioparamentrizacion);
        TreeNode unidadMedida = new DefaultTreeNode("document",new Document("Unidad de Medida", "-", "Pages Document","/buffalo/pages/inventario/register-unidades.xhtml"), gestionPresentacion);
        TreeNode cantUnitMedida = new DefaultTreeNode("document",new Document("Cantidad Ud. Presentacion", "-", "Pages Document","/buffalo/pages/administracion/register-cantidad-unidad-presentacion.xhtml"), gestionPresentacion);
        TreeNode presentacion = new DefaultTreeNode("document",new Document("Presentacion", "-", "Pages Document","/buffalo/pages/inventario/register-presentacion.xhtml"), gestionPresentacion);
        
        TreeNode gestionEmpresa = new DefaultTreeNode("document",new Document("Gestion Empresa", "-", "Pages Document","/buffalo/pages/administracion/register-empresa.xhtml"), inventarioparamentrizacion);
       
        TreeNode producionparamentrizacion = new DefaultTreeNode(new Document("Produccion", "-", "Folder"), parametrizacion);
        
        TreeNode contabilidadparamentrizacion = new DefaultTreeNode(new Document("Contabilidad", "-", "Folder"), parametrizacion);
         
        TreeNode compraVentaparamentrizacion = new DefaultTreeNode(new Document("Compra/Venta", "-", "Folder"), parametrizacion);
        TreeNode centralcostos = new DefaultTreeNode("document",new Document("Central de Costos", "-", "Pages Document","/buffalo/pages/inventario/central-costos.xhtml"), compraVentaparamentrizacion);
        TreeNode ciudad = new DefaultTreeNode("document",new Document("Gestion Ciudad", "-", "Pages Document","/buffalo/pages/inventario/register-ciudad.xhtml"), compraVentaparamentrizacion);
        TreeNode sucursal = new DefaultTreeNode("document",new Document("Gestion Sucursal", "-", "Pages Document","/buffalo/pages/administracion/register-sucursal.xhtml"), compraVentaparamentrizacion);
        TreeNode usuarioparamentrizacion = new DefaultTreeNode(new Document("Gestion Usuario", "-", "Folder"), parametrizacion);
        TreeNode usuario = new DefaultTreeNode("document",new Document("Usuario", "-", "Pages Document","/buffalo/pages/administracion/register-usuario.xhtml"), usuarioparamentrizacion);
         
        return root;
    }
}