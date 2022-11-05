import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FuckingOpenGraph {
    private Map<String,String> metaTags = new HashMap<>();

    public FuckingOpenGraph(String url) throws IOException {
        Document doc = null;
        doc = Jsoup.connect(url).get();
        Elements sections = doc.select("meta");
        sections.forEach(el -> metaTags.put(el.attributes().iterator().next().getValue(), el.attributes().get("content")));

    }

    public String get(String tag){
        return metaTags.get(tag);
    }
}
