package onePercentInstrumentSkill;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import java.io.PrintWriter;

public class MakeTmpFolder {
	private final PrintWriter exPrinter;
	public MakeTmpFolder() throws FileNotFoundException{		
		File f = new File("./tmp/");
		Boolean bool = f.mkdir();
		//Select.setMessage("Make\t./tmp/\tfolder: " + bool);
		
		exPrinter = new PrintWriter(new File("./tmp/MakeTmpFolderException.txt"));
	}
	public Boolean deleteTmpFolder() {
		try {
			FileUtils.deleteDirectory(new File("./tmp/"));
			Select.setMessage("Delete\t./tmp/\tfolder: true");
			return true;
		} catch (IOException e) {
			exPrinter.println("Delete\t./tmp/\tfolder: false");
			e.printStackTrace(exPrinter);
			exPrinter.flush();
			return false;
		}
	}
}
