package bo.buffalo.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;

import javax.swing.JEditorPane;

public class TestPrinting01 {
	
	private static JEditorPane editor;

    public static void main(String[] args) {

        try {

            editor = new JEditorPane();
            editor.setPage(new File("C:/EtiquetaChica3.pdf").toURI().toURL());

            PrinterJob pj = PrinterJob.getPrinterJob();
            if (pj.printDialog()) {
                PageFormat pf = pj.defaultPage();
                Paper paper = pf.getPaper();
                double width = 100;
                double height = 30;
                double margin = 0d * 72d;
                paper.setSize(width, height);
                paper.setImageableArea(
                        margin,
                        margin,
                        width - (margin * 2),
                        height - (margin * 2));
                System.out.println("Before- " + dump(paper));
                pf.setOrientation(PageFormat.PORTRAIT);
                pf.setPaper(paper);
                System.out.println("After- " + dump(paper));
                System.out.println("After- " + dump(pf));
                dump(pf);
                PageFormat validatePage = pj.validatePage(pf);
                System.out.println("Valid- " + dump(validatePage));

                Book pBook = new Book();
                pBook.append(new Page(), pf);
                pj.setPageable(pBook);

                try {
                    pj.print();
                } catch (PrinterException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    protected static String dump(Paper paper) {
        StringBuilder sb = new StringBuilder(64);
        sb.append("HOLA MUNDO!!!");
        return sb.toString();
    }

    protected static String dump(PageFormat pf) {
        Paper paper = pf.getPaper();
        return dump(paper);
    }

    public static class Page implements Printable {

        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            if (pageIndex >= 1) {
                return Printable.NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            // Be careful of clips...
//            g2d.setClip(0, 0, (int) pageFormat.getWidth(), (int) pageFormat.getHeight());
            g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

            double width = pageFormat.getImageableWidth();
            double height = pageFormat.getImageableHeight();

            System.out.println("width = " + width);
            System.out.println("height = " + height);

            editor.setLocation(0, 0);
            editor.setSize((int)width, (int)height);
            editor.printAll(g2d);

            g2d.setColor(Color.BLACK);
            g2d.draw(new Rectangle2D.Double(0, 0, width, height));

            return Printable.PAGE_EXISTS;
        }
    }
	
}
