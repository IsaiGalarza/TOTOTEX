package bo.buffalo.util;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Finishings;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.Sides;

import com.lowagie.text.pdf.PdfDocument;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class printGIF {
    
	public static void main(String[] args){

			PrinterJob job = PrinterJob.getPrinterJob();
			PageFormat pf = job.defaultPage();
			Paper paper = new Paper();
//			paper.setSize(50,20);
//			final double MM_TO_PAPER_UNITS = 1.0/25.4*72.0; //25.4 mm to an inch
//			double width = 1.0/100*72.0;;
//			double height = 1.0/30*72.0;;
//            double margin = 1d * 72d;
			
			double width = 8d * 72d;
			double height = 1d * 72d;
            double margin = 1d * 72d;
            paper.setSize(width, height/2);
//            paper.setImageableArea(
//                    margin,
//                    margin,
//                    width - (margin * 2),
//                    height - (margin * 2));
			
//			  double width = 8d * 72d;
//            double height = 2d * 72d;
//            double margin = 1d * 72d;
//            paper.setSize(width, height);
//            paper.setImageableArea(
//                    margin,
//                    0,
//                    width - (margin * 2),
//                    height);
            paper.setImageableArea(2, 2, width, height);
//			paper.setImageableArea(margin, margin, paper.getWidth() - margin, paper.getHeight() - margin);
            
			pf.setPaper(paper);
			pf.setOrientation(PageFormat.PORTRAIT);
			
			ObjetoDeImpresionEtiqueta objetoImprimir = new ObjetoDeImpresionEtiqueta();
			objetoImprimir.setNombreCliente("nombreCliente");
			objetoImprimir.setCodigoElaborado("codigoElaborado");
			objetoImprimir.setNombrePreparado("nombrePreparado");
			objetoImprimir.setPresentacionPreparado("presentacionPreparado");
			objetoImprimir.setDoctorPreparado("doctorPreparado");
			objetoImprimir.setUsoPreparado("usoPreparado");
			objetoImprimir.setConservacionPreparado("conservacionPreparado");
			objetoImprimir.setFechaElaboracion("fechaElaboracion");
			objetoImprimir.setFechaVencimiento("fechaVencimiento");
		
			
			job.setPrintable(objetoImprimir, pf);
			
			System.out.println("getWidth"+pf.getWidth());
			System.out.println("getHeight"+pf.getHeight());
			
			job.setJobName("EtiquetaChica");
			
			try{
				job.print();
				job.print();
			}catch(PrinterException e){
				System.out.println(e);
			}

	}
	
	public static void printDialog() {
		try {
			
			List<PDDocument> docs = new ArrayList<PDDocument>();
			try {
				String url = "http://localhost:8080/buffalo/factura2?idFactura=68";
				docs.add(PDDocument.load("C:/EtiquetaGrande.pdf"));
				docs.add(PDDocument.load("C:/EtiquetaChica.pdf"));
//				docs.add(PDDocument.load(new URL(url)));
//				docs.add(PDDocument.load("http://localhost:8080/buffalo/factura2?idFactura=68"));
//			    docs.add(PDDocument.load("http://localhost:8080/buffalo/factura2?idFactura=68"));
			} catch (IOException e) {
			    e.printStackTrace();
			}

			try {
			    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
			    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
			    PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
			    aset.add(MediaSizeName.ISO_A4);
			    aset.add(new Copies(1));
			    aset.add(Sides.ONE_SIDED);
			    aset.add(Finishings.STAPLE);

			    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
			    PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
			    PrintService service = ServiceUI.printDialog(null, 200, 200,
			            printService, defaultService, flavor, pras);

			    if (service != null && !docs.isEmpty()) {
			        for (PDDocument doc : docs) {
			            PrinterJob printJob = PrinterJob.getPrinterJob();
			            printJob.setPrintService(service);
			            doc.silentPrint(printJob);
			        }
			    }
			} catch (PrinterException e) {
			    e.printStackTrace();
			} finally {
			    for (PDDocument doc : docs) {
			        if (doc != null) {
			            try {
			                doc.close();
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
			        }
			    }
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void printDialog2() {
		try {
			
			List<PDDocument> docs = new ArrayList<PDDocument>();
			try {
				String url = "http://localhost:8080/buffalo/factura2?idFactura=70";
				docs.add(PDDocument.load(new URL(url)));
				docs.add(PDDocument.load(new URL(url)));
//				docs.add(PDDocument.load("http://localhost:8080/buffalo/factura2?idFactura=68"));
//			    docs.add(PDDocument.load("http://localhost:8080/buffalo/factura2?idFactura=68"));
			} catch (IOException e) {
			    e.printStackTrace();
			}

			try {
			    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
			    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
			    PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
			    aset.add(MediaSizeName.ISO_A4);
			    aset.add(new Copies(1));
			    aset.add(Sides.ONE_SIDED);
			    aset.add(Finishings.STAPLE);
			    
			    
			    PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();

//			    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
//			    PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
//			    PrintService service = ServiceUI.printDialog(null, 200, 200,
//			            printService, defaultService, flavor, pras);

			    if (defaultService != null && !docs.isEmpty()) {
			        for (PDDocument doc : docs) {
			            PrinterJob printJob = PrinterJob.getPrinterJob();
			            printJob.setPrintService(defaultService);
			            doc.silentPrint(printJob);
			        }
			    }
			} catch (PrinterException e) {
			    e.printStackTrace();
			} finally {
			    for (PDDocument doc : docs) {
			        if (doc != null) {
			            try {
			                doc.close();
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
			        }
			    }
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
//	public static void main(String[] args){
//		printDialog();
//	}
	
	public static void main3(String[] args) throws IOException, PrinterException
	{
//	    PDDocument pdf=PDDocument.load(new URL("http://localhost:8080/buffalo/factura2?idFactura=39"));
//	    pdf.print();
	    
//	    URL url = new URL("http://localhost:8080/buffalo/factura2?idFactura=39");
//	    PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
//	    printPDF(url, defaultService);
		
		

      DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
      PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
      patts.add(Sides.DUPLEX);
		
      PrintService[] ps = PrintServiceLookup.lookupPrintServices(flavor, patts);
      if (ps.length == 0) {
          throw new IllegalStateException("No Printer found");
      }
      System.out.println("Available printers: " + Arrays.asList(ps));
      
      //PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
      
      PrintService myService = null;
      for (PrintService printService : ps) {
          if (printService.getName().equals("Canon MG2400 series")) {
        	  System.out.println("Impresora Encontrada: "+printService.getName());
              myService = printService;
              break;
          }
      }

      if (myService == null) {
          throw new IllegalStateException("Printer not found");
      }
		
		/*
		 PrintRequestAttributeSet pras =
			        new HashPrintRequestAttributeSet();
		 //DocFlavor flavor = DocFlavor.INPUT_STREAM.GIF;
		 //DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
			        
			        PrintService printService[] =
			        PrintServiceLookup.lookupPrintServices(flavor, pras);
			        
			        PrintService defaultService =
			        PrintServiceLookup.lookupDefaultPrintService();
			        
			        PrintService service = ServiceUI.printDialog(null, 200, 200,
			        printService, defaultService, flavor, pras);
		*/
      
		PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(myService);
        PDDocument doc = PDDocument.load("C:/factura.pdf");
        doc.silentPrint(job);
	}
	
    public static void main2(String args[]) throws Exception {
        //String filename = args[0];
        String filename = "C:\\test\\test.gif";
//        String filename = "C:/factura.pdf";
        PrintRequestAttributeSet pras =
        new HashPrintRequestAttributeSet();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.GIF;
        
        PrintService printService[] =
        PrintServiceLookup.lookupPrintServices(flavor, pras);
        
        PrintService defaultService =
        PrintServiceLookup.lookupDefaultPrintService();
        
        PrintService service = ServiceUI.printDialog(null, 200, 200,
        printService, defaultService, flavor, pras);
        
        if (service != null) {
            System.out.println("Creating Print Job");
            DocPrintJob job = service.createPrintJob();
            //DocPrintJob job = defaultService.createPrintJob();
            FileInputStream fis = new FileInputStream(filename);
            DocAttributeSet das = new HashDocAttributeSet();
            
            Doc doc = new SimpleDoc(fis, flavor, das);
            System.out.println("Printing");
            job.print(doc, pras);
            System.out.println("Printing Complete?");
            //Thread.sleep(10000);
        }else{
            System.out.println("it's null!");
        }
        System.exit(0);
    }
    
    public static PrintService choosePrinter() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        if(printJob.printDialog()) {
            return printJob.getPrintService();          
        }
        else {
            return null;
        }
    }

    public static void printPDF(URL url, PrintService printer)
            throws IOException, PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(printer);
        PDDocument doc = PDDocument.load(url);
        doc.silentPrint(job);
    }
    
    public static void printPDF(String fileName, PrintService printer)
            throws IOException, PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(printer);
        PDDocument doc = PDDocument.load(fileName);
        doc.silentPrint(job);
    }
    
}