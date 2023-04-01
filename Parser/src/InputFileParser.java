import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class InputFileParser {
	
	private final byte[] MAGIC = new byte[] {(byte)0xD4, (byte)0xEA, (byte)0x00, (byte)0x00,
											 (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00,
											 (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00,
											 (byte)0xE4, (byte)0xEC, (byte)0x00, (byte)0x00};
	
	private FileInputStream fileData;
	
	public InputFileParser(File input) {
		try {
			File headerOutputFile = new File(input.getAbsolutePath()+"_header");
			FileOutputStream headerData = new FileOutputStream(headerOutputFile);
			fileData = new FileInputStream(input);
			boolean found = false;
			byte[] lastSixteen = new byte[16];
			
			int currentIndex = 0;
			int lastRead = -1;
			while((lastRead = fileData.read()) != -1) {
				headerData.write(lastRead);
				headerData.flush();
				
				for(int i=16;i>1;i--) {
					lastSixteen[16-i] = lastSixteen[16-(i-1)];
				}
				lastSixteen[15] = (byte) lastRead;
				currentIndex++;

				if(Arrays.equals(lastSixteen, MAGIC)) {
					found = true;
					break;
				}
			}
			headerData.close();
			
			if(found) {
				System.out.println("Found end of header file at index #"+currentIndex);
				File parsedDataFile = new File(input.getAbsolutePath()+"_parsed");
				FileOutputStream parsedData = new FileOutputStream(parsedDataFile);
				
				byte[] charBuffer = new byte[528];	//528 bytes is the length of each block of data
				for(int read=fileData.read(charBuffer);read != -1;) {	//While # of bytes read is not -1
					String bodyHeaderData = new String(charBuffer).substring(0, 512);
					//parsedData.write(bodyHeaderData.getBytes());
					//parsedData.flush();
				}
				
				System.out.println("Reached the end of the file");
				parsedData.close();
			}else {
				System.err.println("Reached end of file before finding header.");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
