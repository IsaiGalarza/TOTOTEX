package bo.buffalo.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Time implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1808620452373692675L;

	private Integer hora;
	private Integer minuto;
	private Integer segundo;

	public Time() {
		super();
		this.hora = 0;
		this.minuto = 0;
		this.segundo = 0;
	}

	public Time(Integer hora, Integer minuto, Integer segundo) {
		super();
		this.hora = hora;
		this.minuto = minuto;
		this.segundo = segundo;
	}

	public Integer getHora() {
		return hora;
	}

	public void setHora(Integer hora) {
		this.hora = hora;
	}

	public Integer getMinuto() {
		return minuto;
	}

	public void setMinuto(Integer minuto) {
		this.minuto = minuto;
	}

	public Integer getSegundo() {
		return segundo;
	}

	public void setSegundo(Integer segundo) {
		this.segundo = segundo;
	}

	static public Time toTime(String time) {
		String[] value = time.split(":");
		Time hora = new Time(Integer.valueOf(value[0]),
				Integer.valueOf(value[1]), Integer.valueOf(value[2]));
		return hora;

	}

	static public String convertTimeToString(Date time) {
		return time.getHours() + ":" + time.getMinutes() + ":"
				+ time.getSeconds();

	}
	
	static public String convertDateToString(Date time) {
		SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
		String fecha= dt.format(time);
		System.out.println("Fecha: "+fecha); 
		return fecha;

	}

	static public boolean mayorT1T2(Time inicio, Time fin) {
		if (inicio.getHora().doubleValue() > fin.getHora().doubleValue()) {
			return true;
		} else if (inicio.getHora().doubleValue() == fin.getHora()
				.doubleValue()) {
			if (inicio.getMinuto().doubleValue() > fin.getMinuto()
					.doubleValue()) {
				return true;
			} else if (inicio.getMinuto().doubleValue() == fin.getMinuto()
					.doubleValue()) {
				if (inicio.getSegundo().doubleValue() > fin.getSegundo()
						.doubleValue()) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	static public boolean bettweenToDate(Time time1, Time time2, Time time3) {
		if (mayorT1T2(time3, time2)) {
			return false;
		} else {

			if (!mayorT1T2(time1, time2)) {
				if (mayorT1T2(time3, time1)) {
					if (mayorT1T2(time3, time2)) {
						return false;
					} else {
						return true;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}

		}
	}

	static public boolean may(Date time1, Date time2) {
		
		long lantes = time1.getTime(); 
		long lahora = time2.getTime(); 
		
		long hora=(lahora - lantes)/3600000;
		if (lantes>lahora) {
			return true;
		}else{
			return false;
		}
		
	}

	@Override
	public String toString() {
		return "Time [hora=" + hora + ", minuto=" + minuto + ", segundo="
				+ segundo + "]";
	}

	public String toConvertToString() {
		return hora + ":" + minuto + ":" + segundo;
	}
	
	

}
