public class Control {

    public static void main(String[] args) {
        Gui gui = new Gui();
        Youtubedl.main();
        gui.loadChannels();
        System.out.println(gui.getArchivedChannels().get(0).channelNick);




    }
}