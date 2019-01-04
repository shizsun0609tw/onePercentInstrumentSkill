package onePercentInstrumentSkill;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;


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
	public void start() throws IOException {
		mergeFiles();
	}
	
	// merge wav and mp4
	private void mergeFiles() throws IOException {
		FFmpeg ffmpeg = new FFmpeg();
		FFprobe ffprobe = new FFprobe();
		
		FFmpegBuilder builder = new FFmpegBuilder()
				
			      .addInput(folderPath + "wavOutput.wav")
				  .addInput(folderPath + "mp4Output.mp4")     // Filename, or a FFmpegProbeResult
				  .overrideOutputFiles(true) // Override the output if it exists

				  .addOutput(outputName)   // Filename for the destination
				    .setFormat("mp4")        // Format is inferred from filename, or can be set
				  
				    .disableSubtitle()       // No subtiles

				    .setAudioChannels(2)         // Mono audio
				    .setAudioCodec("wav")        // using the aac codec
				    .setAudioSampleRate(48000)  // at 48KHz
				    .setAudioBitRate(1536)      // at 32 kbit/s

				    .setVideoCodec("libx264")     // Video using x2-64
				    .setVideoFrameRate(60, 1)     // at 60 frames per second
				    .setVideoResolution(960, 720) // at 640x480 resolution

				    .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
				    .done();

				FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

				// Run a one-pass encode
				executor.createJob(builder).run();

				// Or run a two-pass encode (which is better quality at the cost of being slower)
				executor.createTwoPassJob(builder).run();
	}
}
