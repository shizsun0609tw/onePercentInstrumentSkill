package onePercentInstrumentSkill;

import java.io.IOException;

public class MergeFilesTest {
	public static void main(String[] args) throws IOException {
		MergeFiles test = new MergeFiles("src/test/resources/", "MergeTest");
		test.start();
	}
}
