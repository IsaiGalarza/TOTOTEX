package bo.buffalo.util;

import java.io.Serializable;

import bo.buffalo.model.ProductoProveedor;

public class UtilCostosCalculation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5198737194685528482L;

	static public double calculatePrecioVenta(ProductoProveedor propro,
			double costo, double tipocambio) {
		double utilMax = propro.getUtilidadMax();
	
		double precioventa = 0;
		if (propro.getTipoCambio().equals("BS")) {
			precioventa = ((100 + costo + utilMax) / 100)
					* propro.getPrecioUnitarioCompra();
		} else {
			precioventa = ((100 + costo + utilMax) / 100)
					* (propro.getPrecioUnitarioCompra() * tipocambio);
		}
		System.out.println("costo: "+costo+" utilidad: "+utilMax+" = Precio Venta: "+precioventa);
		return precioventa;
	}
	
	static public double calculatePrecioVenta(ProductoProveedor propro,
			double costo, double tipocambio,double utilMax) {
		double precioventa = 0;
		if (propro.getTipoCambio().equals("BS")) {
			precioventa = ((100 + costo + utilMax) / 100)
					* propro.getPrecioUnitarioCompra();
		} else {
			precioventa = ((100 + costo + utilMax) / 100)
					* (propro.getPrecioUnitarioCompra() * tipocambio);
		}
		System.out.println("costo: "+costo+" utilidad: "+utilMax+" = Precio Venta: "+precioventa);
		return precioventa;
	}
	
	
	static public double calculatePrecioVentaMargen2(ProductoProveedor propro,
			double costo, double tipocambio) {
		double utilMax = propro.getUtilidadMaxReCal();
		double precioventa = 0;
		if (propro.getTipoCambio().equals("BS")) {
			precioventa = ((100 + costo + utilMax) / 100)
					* propro.getPrecioUnitarioCompra();
		} else {
			precioventa = ((100 + costo + utilMax) / 100)
					* (propro.getPrecioUnitarioCompra() * tipocambio);
		}
		System.out.println("costo: "+costo+" utilidad: "+utilMax+" = Precio Venta: "+precioventa);
		return precioventa;
	}

	static public double calculateMargenUtilidad(ProductoProveedor propro,
			double costo, double precioVenta, double tipocambio) {
		double margen = 0;
		if (propro.getTipoCambio().equals("BS")) {
			margen = ((precioVenta * 100) / propro.getPrecioUnitarioCompra())
					- 100 - costo;
		} else {
			margen = ((precioVenta * 100) / (propro.getPrecioUnitarioCompra() * tipocambio))
					- 100 - costo;
		}
		System.out.println("costo: "+costo+" Precio Venta: "+precioVenta+" = utilidad: "+margen);
		return margen;
	}

}
