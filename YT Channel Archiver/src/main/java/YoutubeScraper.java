import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import org.yaml.snakeyaml.*;

import com.luciad.imageio.webp.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;

import static javax.imageio.ImageIO.getImageReader;

public class YoutubeScraper {
    public YTChannel phraseChannel(String channelHandle){
        YTChannel channel = new YTChannel(channelHandle, false);

        try {
            channel.setChannelBanner("https://www.banner.yt/" + channel.channelID + "/tv");
            Document doc = Jsoup.connect(channel.channelUrl).get();

            channel.SetSubscriberCount(doc.select("subscriber-count").attr("aria-label")); //TODO: Fix this

            File directory = new File(System.getProperty("user.dir")
                           + "/Youtube Content/" + channel.channelNick);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            Map<String, Object> data = new HashMap<>();
            data.put("Channel Handle", channel.channelHandle);
            data.put("Channel Nick", channel.channelNick);
            data.put("Channel ID", channel.channelID);
            data.put("Channel URL", channel.channelUrl);

            data.put("Subscriber Count", channel.subscriberCount);
            data.put("Channel Description", channel.channelDescription);

            Yaml yaml = new Yaml();
            directory = new File(System.getProperty("user.dir")
                    + "/Youtube Content/" + channel.channelNick + "/ChannelInfo");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            if (!new File(directory + "profile.jpg").exists()) {
                ImageIO.write(channel.channelIcon, "jpg", new File(directory + "/profile.jpg"));
            }
            if (!new File(directory + "banner.jpg").exists()) {
                ImageIO.write(channel.channelBanner, "jpg", new File(directory + "/banner.jpg"));
            }

            FileWriter writer = new FileWriter(directory + "/config.yml");
            yaml.dump(data, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return channel;

    }
    public ArrayList<String> searchForChannels(String channelName) throws IOException {
        ArrayList<String> channelURLs = new ArrayList<>();
        String url = "https://www.youtube.com/results?search_query=" + channelName;
        Document doc = Jsoup.connect(url).get();
        Elements sections = doc.select("a");
        sections.forEach(el -> {
            if (el.attributes().get("href").contains("channel")) {
                channelURLs.add("https://www.youtube.com" + el.attributes().get("href"));
            }
        });
        return channelURLs;
    }
}

class YTChannel {
    public String channelHandle, channelNick, channelID, channelUrl;
    public String channelDescription, subscriberCount;

    public BufferedImage channelIcon, channelBanner;

    private YTVideo[] videos;

    private static final String[] subPages = {"user", "channel", "c"};

    public YTChannel(String channelHandle, boolean exists) {
        ImageIO.scanForPlugins();
        if(exists) formatChannelData(channelHandle);

        else scrapeChannel(channelHandle);
    }

    public void scrapeChannel(String channelHandle){
        this.channelHandle = channelHandle;
        FuckingOpenGraph og = null;

        for (String subPage : subPages) {
            channelUrl = "https://www.youtube.com/" + subPage + "/" + channelHandle;
            try {
                og = new FuckingOpenGraph(channelUrl);
                System.out.println(subPage + " Worked, what kind of multi-billion dollar company uses different tags for the same thing?");
                break;
            } catch (IOException e) {
                System.out.println(subPage + " Didn't work");
            }
        }

        if (og == null) {
            System.out.println("Could not find channel");
            return;
        }

        channelUrl = og.get("og:url");

        channelNick = og.get("og:title");
        channelID = channelUrl.substring(channelUrl.lastIndexOf("/") + 1);
        channelDescription = og.get("og:description");

        try {
            channelIcon = ImageIO.read(new URL(og.get("og:image")));
        } catch (IOException e) {
            System.out.println("Could not find channel icon");
        }
    }

    public void formatChannelData(String channelNick){
        try{
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(System.getProperty("user.dir")
                    + "/Youtube Content/" + channelNick + "/ChannelInfo/config.yml");

            Map<String, Object> yamlMap = yaml.load(inputStream);

            channelUrl = (String) yamlMap.get("Channel URL");
            this.channelNick = channelNick;
            channelID = (String) yamlMap.get("Channel ID");
            channelHandle = (String) yamlMap.get("Channel Handle");
            subscriberCount = (String) yamlMap.get("Subscriber Count");
            channelDescription = (String) yamlMap.get("Channel Description");

            channelIcon = ImageIO.read(new File(System.getProperty("user.dir")
                    + "/Youtube Content/" + channelNick + "/ChannelInfo/profile.jpg"));

            channelBanner = ImageIO.read(new File(System.getProperty("user.dir")
                    + "/Youtube Content/" + channelNick + "/ChannelInfo/banner.jpg"));



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setChannelBanner(String bannerUrl) {
        try {
            channelBanner = ImageIO.read(new URL(bannerUrl));
            if (channelBanner == null) {
                ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();
                WebPReadParam readParam = new WebPReadParam();
                reader.setInput(new FileImageInputStream(new File(bannerUrl)));
                BufferedImage channelBanner = reader.read(0, readParam);
            }

            } catch(IOException e){
                throw new RuntimeException(e);
            }
    }

        public void SetSubscriberCount(String subscriberCount) {
        this.subscriberCount = subscriberCount;
    }
}

class YTVideo {
    private String videoID, videoTitle, videoDescription;
    private String videoURL;
    private int videoLength;
    private int viewCount, likeCount, dislikeCount, commentCount;
    private String uploadDate;

    private BufferedImage videoThumbnail;

    public YTVideo(String fileName) {

    }


}
