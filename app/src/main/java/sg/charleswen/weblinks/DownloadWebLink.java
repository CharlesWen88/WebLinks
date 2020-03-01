package sg.charleswen.weblinks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import sg.charleswen.weblinks.model.WebLink;

public class DownloadWebLink extends AsyncTask<List<String>, Void, List<WebLink>> {
    public interface AsyncResponse {
        void processFinish(List<WebLink> output);
    }

    public AsyncResponse delegate = null;

    public DownloadWebLink(AsyncResponse delegate){
        this.delegate = delegate;
    }

    protected List<WebLink> doInBackground(List<String>... urls) {
        List<WebLink> webLinks = new ArrayList<>();
        for (String url : urls[0]) {
            try {
                Document doc = Jsoup.connect(url).get();
                String title = doc.title(); // Parses the HTML document

                Elements img = doc.getElementsByTag("img");
                String src = "";
                for (Element e : img)
                {
                    String url1 = e.absUrl("src");
                    int index = url1.lastIndexOf(".");
                    if(index>1)
                    {
                        if(url1.substring(index+1).toLowerCase().matches("jpg|png|jpeg|bmp|gif|webp"))
                        {
                            src = url1;
                            break;
                        }
                    }
                }

                Bitmap bitmap = null;
                try {
                    InputStream in = new java.net.URL(src).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

                WebLink webLink = new WebLink();
                webLink.setUrl(url);
                webLink.setTitle(title);
                webLink.setImgUrl(src);
                webLink.setBitmapValue(bitmap);

                webLinks.add(webLink);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return webLinks;
    }

    protected void onPostExecute(List<WebLink> webLinks)
    {
        delegate.processFinish(webLinks);
    }

}
