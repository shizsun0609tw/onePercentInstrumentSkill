package onePercentInstrumentSkill;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;


public class MergeFiles {
	private final String folderPath;
	private final String outputName;
	private final String inputMp4;
	private final String inputWav;
	
	public MergeFiles(String folderPath, String outputName){
		this.inputMp4 = folderPath + "mp4Output.mp4";
		this.inputWav = folderPath + "wavOutput.wav";
		this.folderPath = folderPath;
		this.outputName = outputName;
	}
	
	// start to merge
	public void start() throws IOException {
		mergeFiles();
	}
	
	// merge wav and mp4
	private void mergeFiles() throws IOException {
		System.out.println(System.getProperty("os.name"));
		FFmpeg ffmpeg = null;
		FFprobe ffprobe = null;
		if("Mac OS X".equals(System.getProperty("os.name"))) {
			ffmpeg = new FFmpeg("./ffmpeg_local");
			ffprobe = new FFprobe("./ffprobe_local");
			System.out.println("Find ffmpeg/ffprobe success.");
		}else if("Windows 10".equals(System.getProperty("os.name")) || "Windows 7".equals(System.getProperty("os.name"))) {
			ffmpeg = new FFmpeg("./ffmpeg.exe");
			ffprobe = new FFprobe("./ffprobe.exe");
			System.out.println("Find ffmpeg/ffprobe success.");
		}

		
		Path path = Paths.get(".");
		System.out.println(path.normalize().toAbsolutePath());	
		
		FFmpegBuilder builder = new FFmpegBuilder()
				  .addInput("mp4Output.mp4")     // Filename, or a FFmpegProbeResult
			      .addInput("wavOutput.wav")
				  
				  .overrideOutputFiles(true) // Override the output if it exists

				  .addOutput(outputName+".mp4")   // Filename for the destination
				    .setFormat("mp4")        // Format is inferred from filename, or can be set
				    .setAudioCodec("aac")        // audio using the aac codec
				    .setVideoCodec("copy")     // video using the codec same as input
				    .done();

				FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
				final FFmpegProbeResult in = ffprobe.probe("mp4Output.mp4");

				FFmpegJob job = executor.createJob(builder, new ProgressListener() {

					// Using the FFmpegProbeResult determine the duration of the input
					final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

					
					public void progress(Progress progress) {
						double percentage = progress.out_time_ns / duration_ns;

						// Print out interesting information about the progress
						System.out.println(String.format(
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
				System.out.println("Done!");
		}
}
