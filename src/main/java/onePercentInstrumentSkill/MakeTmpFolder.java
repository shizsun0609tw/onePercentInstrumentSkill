package onePercentInstrumentSkill;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class MakeTmpFolder {
	public MakeTmpFolder(){
		File f = new File("./tmp/");
		Boolean bool = f.mkdir();
		System.out.println("Make\t./tmp/\tfolder: " + bool);
	}
	public Boolean deleteTmpFolder() {
		try {
			FileUtils.deleteDirectory(new File("./tmp/"));
			System.out.println("Delete\t./tmp/\tfolder: true");
			return true;
		} catch (IOException e) {
			System.out.println("Delete\t./tmp/\tfolder: false");
			e.printStackTrace();
			return false;
		}
	}
}
