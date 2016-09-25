import java.io.*;
import java.util.zip.*;
import java.util.jar.*;
import java.nio.file.Path;

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

	}
	
	private static void createZip(File dirToComp) {
		
		String zipFile = dirToComp + "/../out.zip";
		
		try {
			
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
						
			addToZip(zos, dirToComp);
			zos.close();
		}
		catch (IOException e) {
			System.out.println("Error: " + e);
		}
		createZipCheckSum(zos);
	}
	
	private static void addToZip(ZipOutputStream zos, File source) {
		File[] files = source.listFiles();
		for(int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()) {
				addToZip(zos, files[i]);
			}
			try {
				Path filePath = files[i].toPath();
				printPath(filePath);
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
		}
	}
	
	private static void createZipCheckSum(ZipOutputStream zos) {
		CheckedOutputStream checksum = new CheckedOutputStream(fos, new CRC32());
		
		PrintWriter crc = new PrintWriter(dir + "/../crc.txt");
        crc.print("Checksum: " + checksum.getChecksum().getValue());
        crc.close();
	}
	
	private static void createJar(File dirToComp) {
		
		String jarFile = dirToComp + "/../out.jar";
		try {
			FileOutputStream fos = new FileOutputStream(jarFile);
			JarOutputStream jos = new JarOutputStream(fos);
			
			
			addToJar(jos, dirToComp);
			jos.close();
		}
		catch (IOException e) {
			System.out.println("Error: " + e);
		}
		createJarCheckSum(jos);
	}
	
	private static void addToJar(JarOutputStream jos, File source) {
		File[] files = source.listFiles();
		for(int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()) {
				addToJar(jos, files[i]);
			}
			try {
				Path filePath = files[i].toPath();
				printPath(filePath);
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
		}
	}
	
	private static void createJarCheckSum(JarOutputStream jos) {
		CheckedOutputStream checksum = new CheckedOutputStream(jos, new CRC32());
		
		PrintWriter crc = new PrintWriter(dir + "/../crc.txt");
        crc.print("Checksum: " + checksum.getChecksum().getValue());
        crc.close();
	}
	
	private static void printPath(Path path) {
		PrintWriter writer = new PrintWriter("report.txt");
		writer.println(path);
	}
	
	private static void checkSum() {
		
	}

}
