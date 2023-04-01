import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Control {

    public static void main(String[] args) {
        //System.out.println(AssScraping.returnDeleted(new YTChannel("yh5aitec", true)));
        Gui gui = new Gui();
        Youtubedl.main(gui);
        gui.loadChannels();



        Youtubedl.updateChannels();
        /*
        List<String> channels = new LinkedList<>();
        channels.add("UCDKJdFer1phQI95UinPZehw");
        channels.add("UCCAfRoTJrKPbSrh_Eg3i4vg");
        channels.add("PyrocynicalVEVO");

        Youtubedl.downloadList(channels);

         */

        //System.out.println(Youtubedl.findDuplicates("yh5aitec"));


    }
}