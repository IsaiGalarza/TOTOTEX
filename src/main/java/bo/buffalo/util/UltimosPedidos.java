package bo.buffalo.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class UltimosPedidos implements Serializable{

	private static final long serialVersionUID = -6348168922942993286L;

	private List<EstructuraDetallePedido> listaUltimosPedidos = new ArrayList<EstructuraDetallePedido>();
	
	@PostConstruct
	public void init(){
//		System.out.println("Ingreso a Cargar Ultimos Pedidos.....");
//		MSAccessPedido access = new MSAccessPedido();
//		listaUltimosPedidos = access.traerUltimasNotasVenta();
	}
	
	public UltimosPedidos(){
		
	}

	public List<EstructuraDetallePedido> getListaUltimosPedidos() {
		return listaUltimosPedidos;
	}

	public void setListaUltimosPedidos(List<EstructuraDetallePedido> listaUltimosPedidos) {
		this.listaUltimosPedidos = listaUltimosPedidos;
	}
	
}
