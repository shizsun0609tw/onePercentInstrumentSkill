package onePercentInstrumentSkill;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.bramp.


public class MergeFiles {
	private final File mp4File;
	private final File wavFile;
	private final String folderPath;
	private final String outputName;
	
	public MergeFiles(String folderPath, String outputName){
		this.mp4File = new File(folderPath + "mp4Output.mp4");
		this.wavFile = new File(folderPath + "wavOutput.wav");
		this.folderPath = folderPath;
		this.outputName = outputName;
	}
	
	// start to merge
	public void start() {
		mergeFiles();
	}
	
	// merge wav and mp4
	 public boolean doSomething() {

		 String[] exeCmd = new String[]{"ffmpeg", "-i", "audioInput.mp3", "-i", "videoInput.avi" ,"-acodec", "copy", "-vcodec", "copy", "outputFile.avi"};

		 ProcessBuilder pb = new ProcessBuilder(exeCmd);
		 boolean exeCmdStatus = executeCMD(pb);

		 return exeCmdStatus;
		} //End doSomething Function

		private boolean executeCMD(ProcessBuilder pb)
		{
		 pb.redirectErrorStream(true);
		 Process p = null;

		 try {
		  p = pb.start();

		 } catch (Exception ex) {
		 ex.printStackTrace();
		 System.out.println("oops");
		 p.destroy();
		 return false;
		}
		// wait until the process is done
		try {
		 p.waitFor();
		} catch (InterruptedException e) {
		e.printStackTrace();
		System.out.println("woopsy");
		p.destroy();
		return false;
		}
		return true;
		 }// End function executeCMD
}
