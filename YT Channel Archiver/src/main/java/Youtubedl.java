import com.jfposton.ytdlp.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class Youtubedl {
    private static final String apiKey = "AIzaSyCGOypB2LrWuCj9eD5DoNIIe6kGbErPYH8";

    public static void main() {
        downloadAll(new YoutubeScraper().phraseChannel("NerdsInc"));

    }

    public static void downloadFromURL(String url, String path) {
        // Build request
        YtDlpRequest request = new YtDlpRequest(url, path);
        request.setOption("output", "%(upload_date>%Y-%m-%d)s - %(title)s.%(ext)s");	// --output (upload_date - title.ext)
        request.setOption("ffmpeg-location", "C://ffmpeg/bin/ffmpeg.exe");	// --ffmpeg-location
        request.setOption("retries", 10);		                                        // --retries 10
        request.setOption("compat-options", "no-youtube-unavailable-videos");	        // --compat-options no-youtube-unavailable-videos
        request.setOption("cookies-from-browser", "brave");	                            // --cookies-from-browser brave
        request.setOption("download-archive FILE");	                                    // --download-archive FILE
        request.setOption("concurrent-fragments", 16);	                                // --concurrent-fragments 16
        request.setOption("embed-metadata");                                         // --embed-metadata
        request.setOption("embed-thumbnail");	                                        // --embed-thumbnail
        request.setOption("convert-thumbnail", "jpg");	                                // --convert-thumbnail jpg

        // Make request
        try {
            YtDlp.execute(request, (progress, eta) -> System.out.println("Progress: " + progress + "% ETA: " + eta));
        } catch (YtDlpException e) {
            e.printStackTrace();
        }
    }

    public static void downloadAll(YTChannel channel) {

        String path = System.getProperty("user.dir") + "/Youtube Content/" + channel.channelNick;
        path = path.replace(".", "");

        String playlistURL = "https://www.youtube.com/playlist?list=UU" + channel.channelID.substring(2);

        downloadFromURL(playlistURL, path);
        //cleanUp(path); //If I end up converting after downloading
    }

    public static void cleanUp(String path) {
        File filePath = new File(path);
        File descriptionPath = new File(path + "/description");
        File thumbnailPath = new File(path + "/thumbnail");

        if (!descriptionPath.exists()) descriptionPath.mkdirs();

        if (!thumbnailPath.exists()) thumbnailPath.mkdirs();

        File[] files = filePath.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.getName().endsWith(".description")) {
                try {
                    if(!new File(path + "/description/" + file.getName()).exists()) {
                        Files.move(file.toPath(), new File(descriptionPath + "/" + file.getName()).toPath());
                    }else {
                        file.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (file.getName().endsWith(".jpg")) {
                try {
                    if(!new File(path + "/thumbnail/" + file.getName()).exists()) {
                        Files.move(file.toPath(), new File(thumbnailPath + "/" + file.getName()).toPath());
                    }else {
                        file.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}


