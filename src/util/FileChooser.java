package util;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Bilal Hussain
 */
public class FileChooser {

	Frame parent;
	JFileChooser chooser;
	
	String title;
	String ext;
	
	FilenameFilter filenameFilter;
	
	public FileChooser(Frame parent,String title, String ext){
		this.parent = parent;
		this.title  = title;
		this.ext    = ext;
		
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("macs") ==-1) {
			chooser  = new JFileChooser();
			if (ext != "" && ext != null){
				chooser.setFileFilter(new FileNameExtensionFilter("*." + ext, ext));
			}
		}else{
			filenameFilter = new CFileNameFilter();
		}
		
	}
	
	public File loadFile(){
		
		if (chooser == null){
		    FileDialog d = new FileDialog(parent, title,FileDialog.LOAD);
		    d.setFilenameFilter(filenameFilter);
			d.setVisible(true);

			String path  = d.getFile();
		    if (path == null) return null;
		    
		    return new File(d.getDirectory() + d.getFile());
		}else{
			int rst = chooser.showOpenDialog(parent);
			if (rst != JFileChooser.APPROVE_OPTION){
				return null;
			}
			return chooser.getSelectedFile();
		}
	}

	public File getDir(){
		if (chooser == null){
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
		    FileDialog d = new FileDialog(parent, title,FileDialog.SAVE);
			d.setVisible(true);
			System.setProperty("apple.awt.fileDialogForDirectories", "false");
			String path  = d.getFile();
		    if (path == null) return null;
		    return new File(d.getDirectory() + d.getFile());
		}else{
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int rst = chooser.showSaveDialog(parent);
			if (rst != JFileChooser.APPROVE_OPTION){
				return null;
			}
			return chooser.getSelectedFile();
		}
	}
	
	class CFileNameFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith("." + ext);
		}
		
	}
	
}
