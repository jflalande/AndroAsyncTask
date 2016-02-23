package jf.andro.androasynctask;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class BitmapDownloaderTask 
    extends AsyncTask<String, Integer, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    private final WeakReference<TextView> textViewReference;

    public BitmapDownloaderTask(ImageView imageView, TextView textView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        textViewReference = new WeakReference<TextView>(textView);
    }

    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        // params comes from the execute() call: params[0] is the url.
        String url = params[0];

        publishProgress(new Integer(0));

        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        HttpGet getRequest = new HttpGet(url);

        try {
            Thread.sleep(2000); // To simulate a slow downloading rate
            HttpResponse response = client.execute(getRequest);
            publishProgress(new Integer(1));
            Thread.sleep(1000); // To simulate a slow downloading rate
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) { 
                Log.w("ImageDownloader", "Error " + statusCode 
                        + " while retrieving bitmap from " + url); 
                return null;
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent(); 
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    publishProgress(new Integer(3));
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();  
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message 
            // for IOException or IllegalStateException
            getRequest.abort();
            Log.w("ImageDownloader", 
              "Error while retrieving bitmap from " + url);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Integer step = values[0];
        if (textViewReference != null) {
            textViewReference.get().setText("Step: " + step.toString());
        }
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    protected void onPreExecute() {
        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageResource(R.drawable.interro);
            }
        }
    }
}
