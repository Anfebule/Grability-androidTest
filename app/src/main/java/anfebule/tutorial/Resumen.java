package anfebule.tutorial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

/**
 * Actividad que recibe como parmetro la informacin de una aplicacin especifica y la muestra
 * a modo de resumen.
 * Created by Andres on 22/06/2016.
 */
public class Resumen extends AppCompatActivity {

    ProgressDialog prgDialog;

    String app;
    String image;
    String summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen);

        //Se obtiene la info de parametros del intent anterior
        Intent intent = getIntent();
        app = intent.getStringExtra("app");
        image = intent.getStringExtra("image");
        summary = intent.getStringExtra("summary");


        final ImageView iv = (ImageView) findViewById(R.id.imageView2);
        TextView tv = (TextView) findViewById(R.id.textView);
        TextView tv1 = (TextView) findViewById(R.id.textView2);

        //Se obtiene la imagen de la URL y se carga al contenedor
        new AsyncTask<Void, Void, Void>() {

            private Bitmap bmp;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    InputStream in = new URL(image).openStream();
                    bmp = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (bmp != null)
                    iv.setImageBitmap(bmp);
            }
        }.execute();

        tv.setText(app);
        tv1.setText(summary);
    }
}
