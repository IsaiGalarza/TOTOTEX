package bo.buffalo.util;

/**
 * Estable el estado de los procesos y subprocesos, de todo el flujo
 * completo en el que se encuenta una orden de Produccion
 * 
 * @author mauriciobejaranorivera
 *
 */
public class EstructuraFlujoProceso {
	/**
	 * ESTADOS:
	 * IN = No fue creado
	 * AC = cuando fue registrado
	 * PR = cuando fue validado y procesado, para seguir con el flujo
	 */
	//DATOS PRINCIPAL
	private double porcentajeTotal;
	private String procesoActual;
	
	//1 PROCESO DE CORTE
	private double porcentajeProcesoCorte;
	private String estadoProcesoCorte;
	//1.1 SUB-PROCESO DE FICHA TECNICA
	private String estadoSubProcesoFichaTecnica;
	//1.2 SUB-PROCESO DE INSUMOCORTE
	private String estadoSubProcesoInsumoCorte;
	
	//2 PROCESO DE MAQUILADOR
	private double porcentajeProcesoMaquilador;
	private String estadoProcesoMaquilador;
	
	//3 PROCESO DE LAVADO
	private double porcentajeProcesoLavado;
	private String estadoProcesoLavado;
	// 3.1 SUB-PROCESO RECIBO LAVANDERIA
	private String estadoSubProcesoReciboLavanderia;
	
	//4 PROCESO DE EMPAQUE FINAL
	private double porcentajeProcesoEmpaqueFinal;
	private String estadoProcesoEmpaqueFinal;
	//4.1 SUB-PROCESO INSUMO FINAL
	private String estadoSubProcesoInsumoFinal;
	//4.2 SUB-PROCESO ALMACEN PRENDA
	private String estadoSubProcesoAlmacenPrenda;
	/**
	 * Constructor
	 */
	public EstructuraFlujoProceso(){
		super();
		//porcentajes inciales
		this.porcentajeProcesoCorte = 0;
		this.porcentajeProcesoMaquilador = 0;
		this.porcentajeProcesoLavado = 0;
		this.porcentajeProcesoEmpaqueFinal = 0;
		//estados procesos iniciales
		this.estadoProcesoCorte = "IN";
		this.estadoProcesoMaquilador = "IN";
		this.estadoProcesoLavado = "IN";
		this.estadoProcesoEmpaqueFinal = "IN";
		//estados sub-procesos iniciales
		this.estadoSubProcesoFichaTecnica = "IN";
		this.estadoSubProcesoInsumoCorte = "IN";
		this.estadoSubProcesoReciboLavanderia = "IN";
		this.estadoSubProcesoInsumoFinal = "IN";
		this.estadoSubProcesoAlmacenPrenda = "IN";
	}

	// ----- GET AND SET -----
	public double getPorcentajeProcesoCorte() {
		return porcentajeProcesoCorte;
	}

	public void setPorcentajeProcesoCorte(double porcentajeProcesoCorte) {
		this.porcentajeProcesoCorte = porcentajeProcesoCorte;
	}

	public double getPorcentajeProcesoMaquilador() {
		return porcentajeProcesoMaquilador;
	}

	public void setPorcentajeProcesoMaquilador(double porcentajeProcesoMaquilador) {
		this.porcentajeProcesoMaquilador = porcentajeProcesoMaquilador;
	}

	public double getPorcentajeProcesoLavado() {
		return porcentajeProcesoLavado;
	}

	public void setPorcentajeProcesoLavado(double porcentajeProcesoLavado) {
		this.porcentajeProcesoLavado = porcentajeProcesoLavado;
	}

	public double getPorcentajeProcesoEmpaqueFinal() {
		return porcentajeProcesoEmpaqueFinal;
	}

	public void setPorcentajeProcesoEmpaqueFinal(
			double porcentajeProcesoEmpaqueFinal) {
		this.porcentajeProcesoEmpaqueFinal = porcentajeProcesoEmpaqueFinal;
	}

	public String getEstadoProcesoCorte() {
		return estadoProcesoCorte;
	}

	public void setEstadoProcesoCorte(String estadoProcesoCorte) {
		this.estadoProcesoCorte = estadoProcesoCorte;
	}

	public String getEstadoSubProcesoFichaTecnica() {
		return estadoSubProcesoFichaTecnica;
	}

	public void setEstadoSubProcesoFichaTecnica(
			String estadoSubProcesoFichaTecnica) {
		this.estadoSubProcesoFichaTecnica = estadoSubProcesoFichaTecnica;
	}

	public String getEstadoProcesoMaquilador() {
		return estadoProcesoMaquilador;
	}

	public void setEstadoProcesoMaquilador(String estadoProcesoMaquilador) {
		this.estadoProcesoMaquilador = estadoProcesoMaquilador;
	}

	public String getEstadoProcesoLavado() {
		return estadoProcesoLavado;
	}

	public void setEstadoProcesoLavado(String estadoProcesoLavado) {
		this.estadoProcesoLavado = estadoProcesoLavado;
	}

	public String getEstadoProcesoEmpaqueFinal() {
		return estadoProcesoEmpaqueFinal;
	}

	public void setEstadoProcesoEmpaqueFinal(String estadoProcesoEmpaqueFinal) {
		this.estadoProcesoEmpaqueFinal = estadoProcesoEmpaqueFinal;
	}

	public double getPorcentajeTotal() {
		return porcentajeTotal;
	}

	public void setPorcentajeTotal(double porcentajeTotal) {
		this.porcentajeTotal = porcentajeTotal;
	}

	public String getProcesoActual() {
		return procesoActual;
	}

	public void setProcesoActual(String procesoActual) {
		this.procesoActual = procesoActual;
	}

	public String getEstadoSubProcesoInsumoCorte() {
		return estadoSubProcesoInsumoCorte;
	}

	public void setEstadoSubProcesoInsumoCorte(
			String estadoSubProcesoInsumoCorte) {
		this.estadoSubProcesoInsumoCorte = estadoSubProcesoInsumoCorte;
	}

	public String getEstadoSubProcesoReciboLavanderia() {
		return estadoSubProcesoReciboLavanderia;
	}

	public void setEstadoSubProcesoReciboLavanderia(
			String estadoSubProcesoReciboLavanderia) {
		this.estadoSubProcesoReciboLavanderia = estadoSubProcesoReciboLavanderia;
	}

	public String getEstadoSubProcesoInsumoFinal() {
		return estadoSubProcesoInsumoFinal;
	}

	public void setEstadoSubProcesoInsumoFinal(
			String estadoSubProcesoInsumoFinal) {
		this.estadoSubProcesoInsumoFinal = estadoSubProcesoInsumoFinal;
	}

	public String getEstadoSubProcesoAlmacenPrenda() {
		return estadoSubProcesoAlmacenPrenda;
	}

	public void setEstadoSubProcesoAlmacenPrenda(
			String estadoSubProcesoAlmacenPrenda) {
		this.estadoSubProcesoAlmacenPrenda = estadoSubProcesoAlmacenPrenda;
	}

}
