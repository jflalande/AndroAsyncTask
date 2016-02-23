package jf.andro.androasynctask;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView img = (ImageView) findViewById(R.id.img);
        TextView txt = (TextView) findViewById(R.id.textView1);
        BitmapDownloaderTask task = new BitmapDownloaderTask(img, txt);
        task.execute("http://www.univ-orleans.fr/lifo/Members/" + 
        "Jean-Francois.Lalande/enseignement/android/images/androids.png");
    }
}
