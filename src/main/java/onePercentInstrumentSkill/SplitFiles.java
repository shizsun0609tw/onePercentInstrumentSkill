package onePercentInstrumentSkill;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.tools.ant.Main;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;


public class SplitFiles {
	private final String folderPath;
	private final String outputPath = "./tmp/";
	private MidiHandler midi;
	private ArrayList<String> inputFileNames = new ArrayList<String>();
	public SplitFiles(String folderPath, MidiHandler midi){
		this.folderPath = folderPath;
		this.midi = midi;
	}
	
	// start to merge
	public void start() throws IOException {
		splitFiles();
	}
	
	// merge wav and mp4
	private void splitFiles() throws IOException {
		int counter = 0;
		Select.setMessage(System.getProperty("os.name"));
		FFmpeg ffmpeg = null;
		FFprobe ffprobe = null;
		Path currentPath = Paths.get("");

		try {
			if("Mac OS X".equals(System.getProperty("os.name"))) {
				ffmpeg = new FFmpeg(currentPath.toAbsolutePath().toString() + "/ffmpeg/ffmpeg");
				ffprobe = new FFprobe(currentPath.toAbsolutePath().toString() + "/ffmpeg/ffprobe");
				Select.setMessage("Find ffmpeg & ffprobe success.");
			}else if("Windows 10".equals(System.getProperty("os.name")) || "Windows 7".equals(System.getProperty("os.name"))) {
				ffmpeg = new FFmpeg(currentPath.toAbsolutePath().toString() + "/ffmpeg/ffmpeg.exe");
				ffprobe = new FFprobe(currentPath.toAbsolutePath().toString() + "/ffmpeg/ffprobe.exe");
				Select.setMessage("Find ffmpeg & ffprobe success.");
			}
		}
		catch (Exception e) {
			Select.setMessage(e.toString());
		}
		// if folderPath exists
		File folder = new File(folderPath);
		if(folder.exists() && folder.isDirectory()) {
			// for all notes' name
	    	for(int i = 0; i < 128; i++) {
				if(NoteTable.NOTE_TABLE[i] != null) {
	    			// open wav data file
	    			File tempFile = new File(folderPath+NoteTable.NOTE_TABLE[i]+".mp4");
	    			if(tempFile.exists()) {
	    				inputFileNames.add(NoteTable.NOTE_TABLE[i]);
	    			}
				}
	    	}
		}
		
		for(int index = 0; index < inputFileNames.size(); index++) {
			counter++;
			FFmpegBuilder builder = new FFmpegBuilder()
				  .addInput(folderPath+inputFileNames.get(index)+".mp4")     // Filename, or a FFmpegProbeResult
				  
				  .overrideOutputFiles(true) // Override the output if it exists

				  .addOutput(outputPath+inputFileNames.get(index)+".wav")   // Filename for the destination
				    .setFormat("wav")        // Format is inferred from filename, or can be set
				    //.setAudioCodec("aac")        // audio using the aac codec
				    //.setVideoCodec("copy")     // video using the codec same as input
				    .done();

			FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
			final FFmpegProbeResult in = ffprobe.probe(folderPath+inputFileNames.get(index)+".mp4");

			FFmpegJob job = executor.createJob(builder, new ProgressListener() {

				// Using the FFmpegProbeResult determine the duration of the input
				final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);
	
				
				public void progress(Progress progress) {
					double percentage = progress.out_time_ns / duration_ns;
	
					// Print out interesting information about the progress
					Select.setMessage(String.format(
						"[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
						percentage * 100,
						progress.status,
						progress.frame,
						FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
						progress.fps.doubleValue(),
						progress.speed
					));
				}
			});
			job.run();
			Select.setMessage("Done!");
		}
		Select.setMessage(String.format("%d", counter));
	}
}
