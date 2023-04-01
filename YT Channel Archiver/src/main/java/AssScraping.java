import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import java.time.Duration;
import java.text.NumberFormat;
import java.text.ParseException;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class AssScraping {


    public static List<YTVideo> playlistVideos(String playlistUrl) {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        // Create a new instance of ChromeOptions
        ChromeOptions options = new ChromeOptions();

        // Set the ChromeOptions to run headlessly
        options.setHeadless(false);

        // Create a new instance of the ChromeDriver with the ChromeOptions
        WebDriver driver = new ChromeDriver(options);

        driver.get(playlistUrl);

        List<WebElement> videoElements = null;
        List<YTVideo> videos = new LinkedList<>();

        try {
            WebElement lastElement = null;
            while (true) {
                // Find all the video elements on the page
                videoElements = driver.findElements(By.cssSelector("a.yt-simple-endpoint.style-scope.ytd-playlist-video-renderer"));

                // Check if the last video element on the page is the same as the previous iteration
                // If it is, we've reached the end of the playlist and can stop scrolling
                if (videoElements.get(videoElements.size() - 1).equals(lastElement)) {
                    break;
                }

                // Scroll down to the bottom of the page to load more elements
                lastElement = videoElements.get(videoElements.size() - 1);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", lastElement);
                Thread.sleep(1000);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Pattern videoIdPattern = Pattern.compile("\\?v=([^&]+)");

            // Loop through the video links and extract the video IDs
            assert videoElements != null;

            for (WebElement videoElement : videoElements) {
                String videoID = null;
                int videoViews = 0;
                BufferedImage videoThumbnail = null;
                String videoUrl = videoElement.getAttribute("href");
                Matcher matcher = videoIdPattern.matcher(videoUrl);
                if (matcher.find()) {
                    videoID = matcher.group(1);
                }
                try{
                    videoThumbnail = ImageIO.read(new URL("https://i.ytimg.com/vi/" + videoID + "/hqdefault.jpg"));
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                String videoTitle = videoElement.getAttribute("title");

                String sinceRelease = videoElement.findElement(By.xpath("//*[@id='video-info']/*[3]")).getText();

                int videoDuration = (int) Duration.parse("PT" +  videoElement.findElement(By.xpath("//span[@class='style-scope ytd-thumbnail-overlay-time-status-renderer']")).getText()
                                                .replace(":", "M") + "S").getSeconds();

                String unParsedViews = videoElement.findElement(By.xpath("//*[@id='video-info']/*[1]")).getText().replaceAll(" views", "");
                int multiplier = 1;
                if (unParsedViews.contains("K")) {
                    multiplier = 1000;
                    unParsedViews = unParsedViews.replace("K", "");
                } else if (unParsedViews.contains("M")) {
                    multiplier = 1000000;
                    unParsedViews = unParsedViews.replace("M", "");
                } else if(unParsedViews.contains("B")){
                    multiplier = 1000000000;
                    unParsedViews = unParsedViews.replace("B", "");
                }

                try {
                    videoViews = NumberFormat.getIntegerInstance().parse(unParsedViews).intValue() * multiplier;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                videos.add(new YTVideo(videoID, videoThumbnail, videoTitle, videoViews, videoDuration, sinceRelease));
            }

            driver.quit();
        }

        return videos;
    }


    public static List<String> returnDeleted(YTChannel channel){
        String playlistLink = "https://www.youtube.com/playlist?list=" + channel.channelID.charAt(0) + "U" + channel.channelID.substring(2);
        System.out.println(playlistLink);
        List<YTVideo> channelsVideos = playlistVideos(playlistLink);

        String path = System.getProperty("user.dir") + "/Youtube Content/" + channel.channelNick;
        List<String> deletedVideos = new ArrayList<>();
        try {
            List<String> downloadedIds = Files.readAllLines(Paths.get(path + "/FILE"))
                    .stream()
                    .map(line -> line.replaceFirst("youtube ", ""))
                    .collect(Collectors.toList());

            for (String videoId : downloadedIds) {
                boolean found = false;
                for (YTVideo video : channelsVideos) {
                    if (video.getVideoID().equals(videoId)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    deletedVideos.add(videoId);
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return deletedVideos;
    }

}