import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;


import org.yaml.snakeyaml.*;

import javax.imageio.ImageIO;

public class YoutubeScraper {
    public YTChannel phraseChannel(String channelHandle, String downloadType){
        YTChannel channel = new YTChannel(channelHandle, false);

        try {
            Document doc = Jsoup.connect(channel.channelUrl).get();

            channel.SetSubscriberCount(doc.select("subscriber-count").attr("aria-label")); //TODO: Fix this

            File directory = new File(System.getProperty("user.dir")
                           + "/Youtube Content/" + channel.channelNick);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            Map<String, Object> data = new HashMap<>();
            data.put("Channel Handle", channel.channelHandle);
            data.put("Download", downloadType);
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
            if (!new File(directory + "/profile.jpg").exists()) {
                try{
                    ImageIO.write(channel.channelIcon, "jpg", new File(directory + "/profile.jpg"));
                }
                catch (Exception e){
                    channel.channelIcon = ImageIO.read(new File(System.getProperty("user.dir") + "/src/main/resources/NoProfile.jpg"));
                    ImageIO.write(channel.channelIcon, "jpg", new File(directory + "/profile.jpg"));
                }

            }
            if (!new File(directory + "/banner.jpg").exists()) {
                try{
                    channel.setChannelBanner("https://www.banner.yt/" + channel.channelID + "/tv");
                    ImageIO.write(channel.channelBanner, "jpg", new File(directory + "/banner.jpg"));
                }catch (Exception e){
                    channel.channelBanner = ImageIO.read(new File(System.getProperty("user.dir") + "/src/main/resources/NoBanner.jpg"));
                    ImageIO.write(channel.channelBanner, "jpg", new File(directory + "/banner.jpg"));
                }

            }

            FileWriter writer = new FileWriter(directory + "/config.yml");
            // Add handle and nick to channels file to new line
            try {
                Files.write(Paths.get(System.getProperty("user.dir") + "/Youtube Content/channels.txt"), (channel.channelHandle + ";" + channel.channelNick).getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }

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
    public String downloadType;

    public BufferedImage channelIcon, channelBanner;

    private YTVideo[] videos;

    private static final String[] subPages = {"user/", "channel/", "c/","@"};

    public YTChannel(String channelHandle, boolean exists) {
        ImageIO.scanForPlugins();
        if(exists) {
            String nick = convertToNick(channelHandle);
            try {
                if (nick != null) {
                    formatChannelData(nick);
                } else {
                    System.out.println("Channel does not exist");
                    throw new IllegalArgumentException("Channel does not exist");
                }
            }
            catch (Exception e){
                System.out.println("Channel does not exist");
            }
        }

        else scrapeChannel(channelHandle);
    }

    public static String convertToNick(String channelHandle){
        try{
            List<String> lines = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/Youtube Content/channels.txt"));
            for(String line : lines){
                if(line.contains(channelHandle)){
                    return line.split(";")[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void scrapeChannel(String channelHandle){
        this.channelHandle = channelHandle;
        FuckingOpenGraph og = null;

        for (String subPage : subPages) {
            channelUrl = "https://www.youtube.com/" + subPage + channelHandle;
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
            downloadType = (String) yamlMap.get("Download");
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

    public void setChannelBanner(String bannerUrl){
        try {
            channelBanner = ImageIO.read(new URL(bannerUrl));
            } catch(IOException e){
                try {
                    channelBanner = ImageIO.read(new File(System.getProperty("user.dir")
                            + "/Youtube Content/banner.jpg"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
    }

        public void SetSubscriberCount(String subscriberCount) {
        this.subscriberCount = subscriberCount;
    }
}

class YTVideo {
    private String videoID, videoTitle, videoDescription;
    private String videoURL;
    private String sinceRelease;
    private int videoLength;
    private int viewCount, likeCount, dislikeCount, commentCount;
    private long uploadDate;

    private BufferedImage videoThumbnail;

    public YTVideo(String fileName) {

    }

    public YTVideo(String videoID, BufferedImage videoThumbnail, String videoTitle, int views, int duration, String sinceRelease){
        this.videoID = videoID;
        this.videoThumbnail = videoThumbnail;
        this.sinceRelease = sinceRelease;
        this.videoTitle = videoTitle;
        this.viewCount = views;
        this.videoLength = duration;
    }

    public String getVideoID() {
        return videoID;
    }




}
