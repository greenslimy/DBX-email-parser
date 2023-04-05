import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class InputFileParser {

	/** Length of each block's header */
	public static final int BLOCK_HEADER_LENGTH = 16;
	public static final int BLOCK_BODY_LENGTH = 512;

	/** 4 byte magic string that denotes the initial email data header block */
	private final int HEADER_DATA_END_MAGIC = 0x0000EAD4;	//60116; TODO: Does the header always end here?

	private final int BLOCK_HEADER_END_MAGIC = 0x00000000;

	/** 16 null bytes denotes this is the end of email data and begins the footer */
	private final byte[] EMAIL_DATA_END_MAGIC = new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
															(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
															(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
															(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};

	private String currentDirectory;
	private String fileName;
	
	public InputFileParser(String path) throws FileNotFoundException {
		try {
			File fd = new File(path);	//This is mainly to retrieve metadata from the file itself
			this.currentDirectory = fd.getParent();
			this.fileName = fd.getName().split("\\.")[0];	//The name sans the file extension
			System.out.println("File name: "+fileName);

			RandomAccessFile inputFile = new RandomAccessFile(path, "r");	//Open this file in read-only mode
			int headerEndIndex = 60116;

			File headerOutputFile = new File(path+"_header");
			FileOutputStream headerOutputStream = new FileOutputStream(headerOutputFile);

			byte[] headerData = new byte[headerEndIndex];
			inputFile.seek(0);		//Return to the beginning of the input file
			inputFile.read(headerData);	//Read headerData.length bytes. Our file pointer will now be positioned at the beginning of the first block

			headerOutputStream.write(headerData);
			headerOutputStream.flush();
			headerOutputStream.close();
			
			if(headerEndIndex != -1) {	//We can start processing the email data now
				File namedDirectory = new File(this.currentDirectory+"/"+this.fileName);	//We will be creating a directory with the same name as the input file to store found emails
				if(!namedDirectory.exists()) {
					System.out.println("Creating directory "+fileName);
					namedDirectory.mkdir();
				}

				System.out.println("Found end of header at byte #"+headerEndIndex+". Parsing email data...");	//TODO: Seems like the header always ends at byte 60116...

				boolean reachedFooter = false;

				byte[] block_headerBytes = new byte[BLOCK_HEADER_LENGTH];
				byte[] block_bodyBytes = new byte[BLOCK_BODY_LENGTH];
				int block_currentHeaderId = 0x00000000;		//First 4 bytes of the header is the identifier; 32 bits
				int block_bodyDataBytesCount = 0;
				int block_expectedHeaderId = HEADER_DATA_END_MAGIC;	//Identifier of the next block

				boolean beginNewEmail = false;
				int emailIndex = 0;
				long totalFileSize = 0;
				File emailFile = new File(this.currentDirectory+"/"+this.fileName+"/email_"+emailIndex);
				FileOutputStream dataOutput = new FileOutputStream(emailFile);

				while(inputFile.read(block_headerBytes) != -1) {
					//System.out.println("Block header: "+Helpers.humanize(block_headerBytes));
					block_currentHeaderId = Helpers.bytesToInt(block_headerBytes, 4, true) & 0xFFFFFF;	//4-byte Integer can only index 3 bytes of blocks?
					//System.out.println("Header block id: "+block_currentHeaderId+"; Expected: "+block_expectedHeaderId);

					if (block_currentHeaderId == block_expectedHeaderId) {
						block_bodyDataBytesCount = Helpers.bytesToInt(block_headerBytes, 8, 10, true);    //# of bytes within the next 512 byte body is actual email data; Little endian
						block_expectedHeaderId = Helpers.bytesToInt(block_headerBytes, 12, 15, true) & 0xFFFFFF;
						//System.out.println("Block body bytes length " + block_bodyDataBytesCount);

						inputFile.read(block_bodyBytes);
						dataOutput.write(Arrays.copyOfRange(block_bodyBytes, 0, block_bodyDataBytesCount));
						dataOutput.flush();

						totalFileSize += block_bodyDataBytesCount;
						if (block_expectedHeaderId == BLOCK_HEADER_END_MAGIC) {
							block_expectedHeaderId = block_currentHeaderId+528;
							System.out.println("Wrote email data file (email_"+emailIndex+") with length " + totalFileSize);

							//Begin parsing readable email header data
							JSONHelpers json = new JSONHelpers();
							json.separateEmailHeader(emailFile.toString(), this.currentDirectory + "/" + this.fileName + " metadata");

							//beginNewEmail = true;
							emailIndex++;
							dataOutput.close();    //Close the current file's data stream before beginning the new one
							totalFileSize = 0;    //Reset the file size

							emailFile = new File(this.currentDirectory + "/" + this.fileName + "/email_" + emailIndex);
							dataOutput = new FileOutputStream(emailFile);
						}
					}else{
						System.err.println("Block Header Id mismatch at "+(inputFile.getFilePointer()-16));
						break;
					}
				}

				if(reachedFooter) {
					System.out.println("Reached the end of email data.");
				}else{
					System.err.println("Malformed .dbx; Reached end of the file before finding the footer.");
				}
			}else {
				System.err.println("Malformed .dbx; Reached end of file before finding the header.");	//Magic header data was not found
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getCurrentDirectory() {
		return currentDirectory;
	}

	public String getFileName() {
		return fileName;
	}

}
