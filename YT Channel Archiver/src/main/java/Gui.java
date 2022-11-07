import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


public class Gui extends JFrame {
    private ArrayList<YTChannel> archivedChannels = new ArrayList<>();
    public void main(String[] args) {
        super.setTitle("Youtube Downloader");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 800);
        this.setVisible(true);
    }
    public void downloadChannel(String channelURL) {
        YTChannel channel = new YTChannel(channelURL, false);
        Youtubedl.downloadAll(channel);
    }

    public void loadChannels() {
        File directory = new File(System.getProperty("user.dir") + "/Youtube Content");
        for(File channelFolder : Objects.requireNonNull(directory.listFiles(File::isDirectory))) {
            archivedChannels.add(new YTChannel(channelFolder.getName(), true));
        }
    }

    public ArrayList<YTChannel> getArchivedChannels() {
        return archivedChannels;
    }
}
