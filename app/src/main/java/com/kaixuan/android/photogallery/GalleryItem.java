package com.kaixuan.android.photogallery;

/**
 * Created by kaixuan on 2017/2/25.
 */

public class GalleryItem {
    private String mCaption;
    private String mUrl;
    private int mId;

    @Override
    public String toString() {
        return mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
