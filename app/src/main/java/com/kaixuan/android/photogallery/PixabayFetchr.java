package com.kaixuan.android.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaixuan on 2017/2/25.
 */

public class PixabayFetchr {
    private static final String TAG = "PixabayFetchr";
    private static final String API_KEY = "4607639-7149ce7976e5c7f713ce53cdf";
    private static final String PER_PAGE = "30";
    private static final Uri ENDPOINT = Uri
            .parse("https://pixabay.com/api/")
            .buildUpon()
            .appendQueryParameter("key", API_KEY)
            .appendQueryParameter("image_type", "photo")
            .appendQueryParameter("per_page", PER_PAGE)
            .build();

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() +
                        " :with " + urlSpec);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getURlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> downloadGalleryItems(String url){
        List<GalleryItem> items = new ArrayList<>();

        try {
            String jsonString = getURlString(url);
            Log.i(TAG, url);
            Log.i(TAG, "Received JSON: " + jsonString);

            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch item", e);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }

        return items;
    }

    public List<GalleryItem> fetchRecentPhotos(String currentPage){
        String url = buildUrl(currentPage, null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String currentPage, String query){
        String url = buildUrl(currentPage, query);
        return downloadGalleryItems(url);
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonObject) throws JSONException {

        JSONArray hitsJsonArray = jsonObject.getJSONArray("hits");

        for (int i = 0; i < hitsJsonArray.length(); i++) {
            JSONObject photoJsonObject = hitsJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getInt("id"));
            item.setCaption(photoJsonObject.getString("tags"));
            item.setUrl(photoJsonObject.getString("webformatURL"));

            items.add(item);
        }
    }

    private String buildUrl(String  currentPage, String query){
        Uri.Builder builder = ENDPOINT.buildUpon()
                .appendQueryParameter("page", currentPage);
        if (query != null){
            builder.appendQueryParameter("q", query);
        }

        return builder.build().toString();
    }

}
