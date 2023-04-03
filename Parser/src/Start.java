import java.io.File;
import java.io.FileNotFoundException;

public class Start {
	
	private static InputFileParser parser;

	public static void main(String[] args) {
		if(args[0] != null && !args[0].equalsIgnoreCase("")) {
			try {
				parser = new InputFileParser(args[0]);
			} catch (FileNotFoundException e) {
				System.err.println("File "+args[0]+" not found!");
			}
			/*File input = new File(args[0]);
			
			if(input.exists()) {
				parser = new InputFileParser(input);
			}else {
				System.err.println(args[0]+" doesnt exist");
			}*/
		}
	}

}
