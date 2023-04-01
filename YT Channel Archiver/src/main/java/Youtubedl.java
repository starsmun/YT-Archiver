import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import com.jfposton.ytdlp.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


public class Youtubedl {
    private static final String apiKey = "AIzaSyCGOypB2LrWuCj9eD5DoNIIe6kGbErPYH8";
    private static Gui gui;

    public static void main(Gui gui) {
        Youtubedl.gui = gui;

    }

    public static void downloadFromURL(String url, String path) {
        // Build request
        YtDlpRequest request = new YtDlpRequest(url, path);
        request.setOption("output", "%(upload_date>%Y-%m-%d)s  %(id)s  %(title)s.%(ext)s");	// --output (upload_date - title.ext)
        request.setOption("ffmpeg-location", "C://ffmpeg/bin/ffmpeg.exe");	// --ffmpeg-location
        request.setOption("retries", 10);		                                        // --retries 10
        request.setOption("compat-options", "no-youtube-unavailable-videos");	        // --compat-options no-youtube-unavailable-videos
        request.setOption("cookies-from-browser", "brave");	                        // --cookies-from-browser chrome
        request.setOption("download-archive FILE");	                                    // --download-archive FILE
        request.setOption("embed-chapters");	                                        // --embed-chapters
        request.setOption("sponsorblock-mark", "sponsor,selfpromo,music_offtopic,poi_highlight");
        request.setOption("concurrent-fragments", 16);	                                // --concurrent-fragments 16
        request.setOption("parse-metadata", "Views - %(view_count)s  Likes - %(like_count)s  Dislikes - %(dislike_count)s:%(meta_purl)s");
        request.setOption("embed-metadata");
        request.setOption("embed-subs");
        request.setOption("use-postprocessor", "ReturnYoutubeDislike:when=pre_process");	        // --extractor-args youtube:player_client=android
        request.setOption("embed-thumbnail");	                                        // --embed-thumbnail
        request.setOption("convert-thumbnail", "jpg");	                                // --convert-thumbnail jpg

        // Make request
        try {
            YtDlp.execute(request, (progress, speed, unit, Title) -> {
                gui.updateProgressBar(progress, speed, unit, Title);
            });

        } catch (YtDlpException e) {
            e.printStackTrace();
        }
    }

    public static void downloadAll(YTChannel channel) {
        System.out.println("Downloading all videos from " + channel.channelNick);
        String path = System.getProperty("user.dir") + "/Youtube Content/" + channel.channelNick;


        String playlistURL = "https://www.youtube.com/playlist?list=UU" + channel.channelID.substring(2);

        downloadFromURL(playlistURL, path);
        //cleanUp(path); //If I end up converting after downloading
    }

    public static void downloadList(List<String> channels) {
        for (String channel : channels) {
            YTChannel c = new YoutubeScraper().phraseChannel(channel, "ALL");
            Youtubedl.downloadAll(c);
        }

    }

    public static void updateChannels(){
        for (YTChannel channel : gui.getArchivedChannels()) {
            System.out.println("Updating " + channel.channelNick);
            if(channel.downloadType.equals("ALL")){
                downloadAll(channel);
            }
        }
    }

    public static List<File> findDuplicates(String handle){
        String path = System.getProperty("user.dir") + "/Youtube Content/" + YTChannel.convertToNick(handle);
        HashMap<String,File> md5Hashes = new HashMap<>();
        List<File> duplicates = new ArrayList<>();

        File[] files = new File(path).listFiles();
        if(files != null) {
            int index = 0;
            for (File fileObject : files) {
                index++;
                if (fileObject.isFile() && fileObject.getName().endsWith(".mkv")) {
                    String md5 = ExtraFunctions.returnMD5(path + "/" + fileObject.getName());
                    if (md5Hashes.containsKey(md5)) {
                        duplicates.add(fileObject);
                        System.out.println("Duplicate found: " + fileObject.getName() + " is a duplicate of " + md5Hashes.get(md5).getName());
                    } else {
                        md5Hashes.put(md5, fileObject);
                    }

                    //System.out.println(md5);
                }
                System.out.println(index + "/" + files.length);
            }
        }

        // Print out the duplicate files
        if (!duplicates.isEmpty()) {
            System.out.println("Duplicate files found:");
            for (File file : duplicates) {
                System.out.println(file.getAbsolutePath());
            }
        }

        return duplicates;
    }

    public static void findDupsTest(String path1, String path2) {

        if(Objects.equals(ExtraFunctions.returnMD5(path1), ExtraFunctions.returnMD5(path2))){
            System.out.println("Files are the same");
        }
        else{
            System.out.println("Files are different");
        }
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


