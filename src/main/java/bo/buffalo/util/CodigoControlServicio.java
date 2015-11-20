
package bo.buffalo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class CodigoControlServicio {
	
	public static void main(String[] args){
		try {
			CodigoControlServicio obj = new CodigoControlServicio();
			//EJEMPLO 1
//			String autorizacion="79040011859";
//			String factura = "152";
//			String nit = "1026469026";
//			String fecha = "20070728";
//			String monto = "135";
//			String llave = "A3Fs4s$)2cvD(eY667A5C4A2rsdf53kw9654E2B23s24df35F5";
//			
//			String cc = generaCC(autorizacion, factura, nit, fecha, monto, llave);
			
			//EJEMPLO 3
			String autorizacion="600400938666"; //29040011007
			String factura = "342898";
			String nit = "1706717"; //215604026 // 953387014
			String fecha = "2007/07/08"; //28/11/2007     
			String monto = "41789";
			String llave = "(r(gIs)D4MVe%f#KeYGA(fbwW_8PPmnLGbI5zWa7J9sVLnX-D)HF*_dKh{(fNcYI";
			
			//X#WCu-CKhVa-a4diIRxH3I#7]T8zg\\}5r4qYsyU-IaxTdAv7-Bg5jG#RGb]J-8PI     ARREGLAR CADENA / caracter especial
			String cc = obj.generaCC(autorizacion, factura, nit, fecha, monto, llave);
			
			System.out.println("Codigo Control Generado: " + cc);
//			System.out.println("Codigo Control Correcto: 62-12-AF-1B");
			
		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("Error al generar Codigo de Control: "+ e.getMessage());
		}
		
	}
	
	
	private static final void intercambiaValor(int[] a, int b, int c) {
		int temporal = a[b];
		a[b] = a[c];
		a[c] = temporal;
	}

	private static final String invierteCadena(String cadena) {
		int largo = cadena.length();
		char[] tempCharArray = new char[largo], charArray = new char[largo];
		cadena.getChars(0, largo, tempCharArray, 0);
		for (int j = 0; j < largo; j++) {
			charArray[j] = tempCharArray[largo - 1 - j];
		}
		return String.valueOf(charArray);
	}

	private static final String arc4(String mensaje, String key, int separador) {
		int[] state = new int[256];
		int x = 0, y = 0, index1 = 0, index2 = 0, nmen = 0;
		String mensajeCifrado = "", subcadena = "";
		for (int i = 0; i <= 255; i++) {
			state[i] = i;
		}
		for (int i = 0; i <= 255; i++) {
			index2 = ((int) key.charAt(index1) + state[i] + index2) % 256;
			intercambiaValor(state, i, index2);
			index1 = (++index1) % key.length();
		}
		for (int i = 0; i < mensaje.length(); i++) {
			x = (++x) % 256;
			y = (state[x] + y) % 256;
			intercambiaValor(state, x, y);
			nmen = (int) mensaje.charAt(i) ^ state[(state[x] + state[y]) % 256];
			subcadena = Integer.toHexString(nmen);
			if (subcadena.length() == 1)
				subcadena = "0".concat(subcadena);
			if (separador == 1) {
				mensajeCifrado = mensajeCifrado.concat("-" + subcadena);
			} else {
				mensajeCifrado = mensajeCifrado.concat(subcadena);
			}
		}
		if (separador == 1) {
			return mensajeCifrado.substring(1).toUpperCase();
		} else {
			return mensajeCifrado.toUpperCase();
		}
	}

	private static final String base64(int numero) {
		String diccionario = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+/";
		String palabra = "";
		int cociente = 1, resto = 0;
		while (cociente > 0) {
			cociente = numero / 64;
			resto = numero % 64;
			palabra = Character.toString(diccionario.charAt(resto)).concat(palabra);
			numero = cociente;
		}
		return palabra;
	}

	private static final String verhoeff(String cifra) {
		int[][] mul = { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
				{ 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 },
				{ 2, 3, 4, 0, 1, 7, 8, 9, 5, 6 },
				{ 3, 4, 0, 1, 2, 8, 9, 5, 6, 7 },
				{ 4, 0, 1, 2, 3, 9, 5, 6, 7, 8 },
				{ 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 },
				{ 6, 5, 9, 8, 7, 1, 0, 4, 3, 2 },
				{ 7, 6, 5, 9, 8, 2, 1, 0, 4, 3 },
				{ 8, 7, 6, 5, 9, 3, 2, 1, 0, 4 },
				{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 } };
		int[][] per = { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
				{ 1, 5, 7, 6, 2, 8, 3, 0, 9, 4 },
				{ 5, 8, 0, 3, 7, 9, 6, 1, 4, 2 },
				{ 8, 9, 1, 6, 0, 4, 3, 5, 2, 7 },
				{ 9, 4, 5, 3, 1, 2, 6, 8, 7, 0 },
				{ 4, 2, 8, 6, 5, 7, 3, 9, 0, 1 },
				{ 2, 7, 9, 3, 8, 0, 6, 4, 1, 5 },
				{ 7, 0, 4, 6, 9, 1, 3, 2, 5, 8 } };
		int[] inv = { 0, 4, 3, 2, 1, 5, 6, 7, 8, 9 };
		int check = 0;
		String numeroInvertido = invierteCadena(cifra);
		for (int i = 0; i < numeroInvertido.length(); i++) {
			check = mul[check][per[(i + 1) % 8][Integer.parseInt(String
					.valueOf(numeroInvertido.charAt(i)))]];
		}
		return String.valueOf(inv[check]);
	}
	
	
	public  String generaCC(String autorizacion, String numerofactura,
			String nit, String dateInString, String monto, String llave) {
		
		try {
	    	
	    	CodigoControl7 cc = new CodigoControl7();
	    	cc.setNumeroAutorizacion(autorizacion);
	    	cc.setNumeroFactura(Integer.valueOf(numerofactura));
	    	cc.setNitci(nit);
	    	
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	    	//String dateInString = "2008/05/19";
	    	Date date;
			date = sdf.parse(dateInString);
	    	cc.setFechaTransaccion(date);
	    	//35.958,60
	    	cc.setMonto(Integer.valueOf(monto));
	    	cc.setLlaveDosificacion(llave);
	    	String codigoControl = cc.obtener();
	    	
	    	System.out.println("CC: "+codigoControl);
	    	
	    	return codigoControl;
	    	
	    	} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "ErrorCC";
			}
	}
	
	public  String generaCC2(String autorizacion, String numerofactura,
			String nit, String fecha, String monto, String llave) {
		
		System.out.println("autorizacion: "+autorizacion);
		System.out.println("numerofactura: "+numerofactura);
		System.out.println("nit: "+nit);
		System.out.println("fecha: "+fecha);
		System.out.println("monto: "+monto);
		System.out.println("llave: "+llave);
		
		if(nit.length()==7){
			nit = "00".concat(nit);
		}
		
		// Paso 1
		for (int i = 0; i < 2; i++) {
			numerofactura = numerofactura.concat(verhoeff(numerofactura));
			nit = nit.concat(verhoeff(nit));
			fecha = fecha.concat(verhoeff(fecha));
			monto = monto.concat(verhoeff(monto));
		}
		String suma1 = String.valueOf(Long.valueOf(numerofactura) + Long.valueOf(nit)
				+ Long.valueOf(fecha) + Long.valueOf(monto));
		for (int i = 0; i < 5; i++) {
			suma1 = suma1.concat(verhoeff(suma1));
		}
		String cincoDigitos = suma1.substring(suma1.length() - 5);
		// System.out.println("llave:"+llave+" cincoDigitos:"+cincoDigitos);

		// Paso 2
		String[] cadenas = new String[5];
		int ptrLlave = 0, largoDigito = 0;
		for (int i = 0; i < 5; i++) {
			largoDigito = Integer.parseInt(String.valueOf(cincoDigitos.charAt(i))) + 1;
			cadenas[i] = llave.substring(ptrLlave, (ptrLlave + largoDigito));
			ptrLlave = ptrLlave + largoDigito;
		}
		autorizacion = autorizacion.concat(cadenas[0]);
		numerofactura = numerofactura.concat(cadenas[1]);
		nit = nit.concat(cadenas[2]);
		fecha = fecha.concat(cadenas[3]);
		monto = monto.concat(cadenas[4]);
		// System.out.println("aut:"+autorizacion+" factura:"+factura+" nit:"+nit+" fecha:"+fecha+" monto:"+monto);

		// Paso 3
		suma1 = autorizacion.concat(numerofactura).concat(nit).concat(fecha)
				.concat(monto);
		suma1 = arc4(suma1, llave.concat(cincoDigitos), 0);
		// System.out.println(suma1);

		// Paso 4
		largoDigito = suma1.length();
		char[] temporal = suma1.toCharArray();
		long[] suma = new long[7];
		suma[0] = 0;
		for (int i = 0; i < largoDigito; i++) {
			suma[0] += (long) temporal[i];
		}
		// System.out.println(suma[0]);
		for (int i = 0; i < 5; i++) {
			for (int j = i; j < temporal.length; j += 5) {
				suma[i + 1] += (long) temporal[j];
			}
			// System.out.println("suma["+(i+1)+"]:"+suma[i+1]);
		}

		// Paso 5
		suma[6] = 0;
		for (int i = 0; i < 5; i++) {
			suma[i + 1] *= suma[0];
			// System.out.print("suma["+(i+1)+"]:"+suma[i+1]);
			// Se supone que no le ingresan valores decimales, no hay necesidad
			// de truncar el resultado
			suma[i + 1] /= (Long.parseLong(String.valueOf(cincoDigitos
					.charAt(i))) + 1);
			// System.out.println(" dividido 8 "+suma[i+1]);
			suma[6] += suma[i + 1];
		}
		// System.out.println("suma[6]:"+suma[6]);
		String paso5 = base64((int) suma[6]);
		// System.out.println("base64:"+paso5);

		// Paso6
		return arc4(paso5, llave.concat(cincoDigitos), 1);
	}
}
