package bo.buffalo.util;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class SimpleTreeBean {
	private static final String SRC_PATH = "/WEB-INF";

	   private List<FileSystemNode> srcRoots;

	   public synchronized List<FileSystemNode> getSourceRoots() {
	      if (srcRoots == null) {
	         srcRoots = new FileSystemNode(SRC_PATH).getDirectories();
	      }
	      return srcRoots;
	   }
}