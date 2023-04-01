import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


public class Gui extends JFrame {
    private ArrayList<YTChannel> archivedChannels = new ArrayList<>();
    private JProgressBar progressBar = new JProgressBar(0,100);
    private JLabel speed = new JLabel("00.00Mb/s", SwingConstants.CENTER);
    private JPanel innerPanel = new JPanel();

    public Gui() {
        super("Youtube Downloader");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 800);

        this.setLayout(new FlowLayout());
        JPanel FixedPanel = new JPanel(new GridBagLayout());
        FixedPanel.setPreferredSize(this.getSize());


        innerPanel.setPreferredSize(new Dimension(500,400));

        JLabel label = new JLabel("Enter Channel Handle", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        innerPanel.add(label);

        JPanel entryPanel = new JPanel();

        JTextField textField = new JTextField();
        textField.setHorizontalAlignment(JLabel.CENTER);
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        textField.setPreferredSize(new Dimension(200, 50));
        entryPanel.add(textField);

        JButton button = new JButton("Download");
        JComboBox<String> downloadType = new JComboBox<>();
        //Centre the text in the downloadType JComboBox
        downloadType.setRenderer(new DefaultListCellRenderer() {
            @Override
            public void paint(Graphics g) {
                setHorizontalAlignment(CENTER);
                super.paint(g);
            }
        });

        downloadType.addItem("ALL");
        downloadType.addItem("CUSTOM");
        downloadType.addItem("DATE RANGE");
        button.setPreferredSize(new Dimension(100, 50));
        downloadType.setPreferredSize(new Dimension(100, 50));

        button.addActionListener(e -> {
            String channelHandle = textField.getText();
            String type = (String) downloadType.getSelectedItem();
            downloadChannel(channelHandle, type);
        });

        //ProcessPanel
        JPanel processPanel = new JPanel();
        processPanel.setPreferredSize(new Dimension(400,80));

        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(400, 50));

        processPanel.add(progressBar);
        entryPanel.add(downloadType);
        entryPanel.add(button);

        innerPanel.add(entryPanel);
        innerPanel.add(processPanel);


        speed.setFont(new Font("Arial", Font.PLAIN, 20));
        innerPanel.add(speed);


        FixedPanel.add(innerPanel);

        this.add(FixedPanel);
        this.setVisible(true);
        this.setResizable(false);
        progressBar.setValue(0);


    }
    public void downloadChannel(String channelHandle, String downloadType) {
        YTChannel channel = new YoutubeScraper().phraseChannel(channelHandle, downloadType);
        if(Objects.equals(downloadType, "ALL")) Youtubedl.downloadAll(channel);
        archivedChannels.add(channel);

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

    public void updateProgressBar(float progress, float speed, String unit, String title) {
        progressBar.setValue((int) progress);
        this.speed.setText(speed + "Mb/s");
        innerPanel.update(innerPanel.getGraphics());
    }
}