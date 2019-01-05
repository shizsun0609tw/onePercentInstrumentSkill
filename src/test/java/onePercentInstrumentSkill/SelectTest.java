package onePercentInstrumentSkill;

import java.io.FileNotFoundException;

public class SelectTest {
    public static void main(String[] args) {
        Select select = null;
		try {
			select = new Select();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        select.GUI();
    }
}