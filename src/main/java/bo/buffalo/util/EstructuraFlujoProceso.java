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
	
	//1 PROCESO DE CORTE
	private double porcentajeProcesoCorte;
	private String estadoProcesoCorte;
	//2 SUB-PROCESO DE FICHA TECNICA
	private String estadoSubProcesoFichaTecnica;
	
	//2 PROCESO DE MAQUILADOR
	private double porcentajeProcesoMaquilador;
	private String estadoProcesoMaquilador;
	
	//3 PROCESO DE LAVADO
	private double porcentajeProcesoLavado;
	private String estadoProcesoLavado;
	
	//4 PROCESO DE EMPAQUE FINAL
	private double porcentajeProcesoEmpaqueFinal;
	private String estadoProcesoEmpaqueFinal;
	
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

}
