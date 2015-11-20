package bo.buffalo.util;
//package bo.medipiel.util;
//
//import java.util.ArrayList;
//import java.util.List;
//import javax.annotation.PostConstruct;
//import javax.enterprise.context.ApplicationScoped;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//
//@ApplicationScoped
//public class UltimosPedidosJob implements Job{
//	
//	private String name = "Soup of the day";
//
//    @PostConstruct
//    public void afterCreate() {
//        System.out.println("Soup created >>>>>> UltimosPedidosJob");
//        listaPedidos.clear();
//        MSAccessPedido access = new MSAccessPedido();
//        listaPedidos = access.traerUltimasNotasVenta();
//		System.out.println("Size Lista Pedidos: "+listaPedidos.size());
//		name="name modificado";
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name){
//        this.name = name;
//    }
//	
//	private List<EstructuraDetallePedido> listaPedidos = new ArrayList<EstructuraDetallePedido>();
//	
//	public void execute(JobExecutionContext jobExecutionContext){
//		try {
//			MSAccessPedido access = new MSAccessPedido();
////			listaPedidos = access.traerUltimasNotasVenta();
////			System.out.println("Size Lista Pedidos: "+listaPedidos.size());
//
////			ultimosPedidos.setListaUltimosPedidos(listaPedidos);
////			ultimosPedidos.setCadena("Cadena Jok Ok...");
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.out.println("Error en UltimosPedidosJob: " + e.getMessage());
//		}
//	}
//	
//	public List<EstructuraDetallePedido> getListaPedidos() {
//		return listaPedidos;
//	}
//	public void setListaPedidos(List<EstructuraDetallePedido> listaPedidos) {
//		this.listaPedidos = listaPedidos;
//	}
//	
//}
