import java.io.*;
import java.util.zip.*;
import java.util.jar.*;

public class Compressor {

	public static void main(String[] args) {
		
		String directory = args[0];
		File dirToComp = new File(directory);
		
		String type = args[1];
		
		if (type.equals("zip")) {
			createZip(dirToComp);
		}
		else if (type.equals("jar")) {
			createJar(dirToComp);
		}
		else {
			System.out.println("Cannot compress to file.");
		}
		
		PrintWriter paths = new PrintWriter(dirToComp + "/report.txt");
		String report = args[2];

	}
	
	private static void createZip(File dirToComp) {
		
		String zipFile = dirToComp + "/out.zip";
		
		try {
			
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
						
			addToZip(zos, dirToComp);
			zos.close();
		}
		catch (IOException e) {
			System.out.println("Error: " + e);
		}
		createCheckSum(zipFile);
	}
	
	private static void addToZip(ZipOutputStream zos, File source) {
		File[] files = source.listFiles();
		for(int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()) {
				addToZip(zos, files[i]);
			}
			try {
				byte[] buffer = new byte[1024];
				FileInputStream fis = new FileInputStream(files[i]);
				ZipEntry newEntry = new ZipEntry(files[i].getName());
				zos.putNextEntry(newEntry);
				
				int length;
				while((length = fis.read(buffer)) > 0) {
					zos.write(buffer,  0 , length);
				}
				
				zos.closeEntry();
				fis.close();
			}
			catch (IOException e) {
				System.out.println("Error: " + e);
			}
			if(files[i].getName().contains(report)) {
				printPath(files[i]);
			}
		}
	}
	
	private static void createJar(File dirToComp) {
		
		String jarFile = dirToComp + "/out.jar";
		try {
			FileOutputStream fos = new FileOutputStream(jarFile);
			JarOutputStream jos = new JarOutputStream(fos);
			
			
			addToJar(jos, dirToComp);
			jos.close();
		}
		catch (IOException e) {
			System.out.println("Error: " + e);
		}
		createCheckSum(jarFile);
	}
	
	private static void addToJar(JarOutputStream jos, File source) {
		File[] files = source.listFiles();
		for(int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()) {
				addToJar(jos, files[i]);
				printPaths(files[i].getName());
			}
			try {
				byte[] buffer = new byte[1024];
				FileInputStream fis = new FileInputStream(files[i]);
				JarEntry newEntry = new JarEntry(files[i].getName());
				jos.putNextEntry(newEntry);
				
				int length;
				while((length = fis.read(buffer)) > 0) {
					jos.write(buffer,  0 , length);
				}
				
				jos.closeEntry();
				fis.close();
			}
			catch (IOException e) {
				System.out.println("Error: " + e);
			}
			if(files[i].getName().contains(report)) {
				printPath(files[i]);
			}
		}
	}
	
	private static void printPath(File file) {
		String toPrint = file.getName();
		boolean atOriginal = false;
		while(atOriginal != true) {
			if(!file.getParentFile().getAbsolutePath().equals(dirToComp)) {
				toPrint = file.getParentFile().getName() + toPrint; 
			}
			else {
				atOriginal = true;
			}
		}		
		paths.println(toPrint);
		paths.close();
	}
	
	private static void checkSum(String fileName) {
		CRC32 crc32 = new CRC32();
		crc32.update(fileName.getBytes());
		printCheckSum(crc32);
	}
	
	private static void printCheckSum(CRC32 crc) {
		PrintWriter csum = new PrintWriter(dirToComp + "/crc.txt");
		csum.println(crc.getValue());
		csum.close();
	}

}
