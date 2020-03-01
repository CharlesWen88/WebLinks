package sg.charleswen.weblinks.model;

import android.graphics.Bitmap;

public class WebLink {
    private String Url;
    private String Title;
    private String ImgUrl;
    private Bitmap bitmapValue;

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public Bitmap getBitmapValue() {
        return bitmapValue;
    }

    public void setBitmapValue(Bitmap bitmapValue) {
        this.bitmapValue = bitmapValue;
    }

}
