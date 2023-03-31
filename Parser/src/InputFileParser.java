import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class InputFileParser {
	
	private final byte[] MAGIC = new byte[] {(byte)0xD4, (byte)0xEA, (byte)0x00, (byte)0x00,
											 (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00,
											 (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00,
											 (byte)0xE4, (byte)0xEC, (byte)0x00, (byte)0x00};
	
	private FileInputStream fileData;
	
	public InputFileParser(File input) {
		try {
			fileData = new FileInputStream(input);
			byte[] lastSixteen = new byte[16];
			int currentIndex = 0;
			boolean error = false;
			while(!Arrays.equals(lastSixteen, MAGIC)) {
				int lastRead = fileData.read();
				lastSixteen[currentIndex%16] = (byte) lastRead;
				currentIndex++;
				
				if(lastRead == -1) {
					error = true;
					break;
				}
			}
			
			if(!error) {
				//System.out.println("YIPEE "+new String(MAGIC));
				System.out.println("Found end of header file at index #"+currentIndex+" - "+new String(lastSixteen));
				File parsedDataFile = new File(input.getAbsolutePath()+"_parsed");
				FileOutputStream parsedData = new FileOutputStream(parsedDataFile);
				
				//int chunk = 0;
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
