import java.io.File;

public class Start {
	
	private static InputFileParser parser;

	public static void main(String[] args) {
		if(args[0] != null && !args[0].equalsIgnoreCase("")) {
			File input = new File(args[0]);
			
			if(input.exists()) {
				parser = new InputFileParser(input);
			}else {
				System.err.println(args[0]+" doesn't exist");
			}
		}
	}

}
