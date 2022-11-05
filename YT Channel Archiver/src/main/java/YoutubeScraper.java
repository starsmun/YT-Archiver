import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class YoutubeScraper {

}

class YTChannel {
    private String channelHandle, channelNick, channelID, channelUrl;
    private String channelDescription;

    private BufferedImage channelIcon, channelBanner;

    private int subscriberCount, videoCount;

    private YTVideo[] videos;

    private static final String[] subPages = {"user", "channel", "c"};

    public YTChannel(String channelID) {
        this.channelID = channelID;
        FuckingOpenGraph og = null;

        for(String subPage : subPages) {
            channelUrl = "https://www.youtube.com/" + subPage + "/" + channelID;
            try {
                og = new FuckingOpenGraph(channelUrl);
                System.out.println(subPage + " Worked, what kind of multi-billion dollar company uses different tags for the same thing?");
                break;
            } catch (IOException e) {
                System.out.println(subPage + " Didn't work");
            }
        }

        if(og == null) {
            System.out.println("Could not find channel");
            return;
        }

        String channelNick = og.get("og:title");

        String path = System.getProperty("user.dir") + "/Youtube Content/" + channelNick;

        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }



    }

}

class YTVideo {
    private String videoID, videoTitle, videoDescription;
    private String videoURL;
    private int videoLength;
    private int viewCount, likeCount, dislikeCount, commentCount;
    private String uploadDate;

    private BufferedImage videoThumbnail;


}
