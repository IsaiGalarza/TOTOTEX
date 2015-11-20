package bo.buffalo.util;

import java.awt.*;
import java.awt.print.*;

public class ObjetoDeImpresion implements Printable{
	public int print(Graphics g, PageFormat f, int pageIndex){
		
		Font font = new Font("Arial", Font.PLAIN, 8);
		
		if(pageIndex == 0){
			g.setFont(font);
//			g.create(2, 2, 100, 30);
			g.drawString("Juan David Silez", 10, 15);
			g.drawString("PRE12345 -2", 170, 15);
			g.drawString("Crema para los Pies", 10, 25);
			g.drawString("Frasco", 170, 25);
			g.drawString("Dr. Pierola Campos David", 10, 35);
			g.drawString("Uso: Todos los dias", 10, 45);
			g.drawString("< 30 Grados C", 170, 45);
			g.drawString("FE:	01/01/2014", 170, 55);
			g.drawString("FV:	01/01/2015", 170, 65);
			return PAGE_EXISTS;
		}else{
			return NO_SUCH_PAGE;
		}
	}
}
