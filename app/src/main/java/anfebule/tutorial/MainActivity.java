package anfebule.tutorial;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Actividad principal en donde se muestra el splash por un tiempo determinado
 * Created by Andres on 20/06/2016.
 */
public class MainActivity extends AppCompatActivity {

    //Variable que guarda la cantidad de tiempo que se mostrará el splash
    private final int splashDisplayLength = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hander para mantener la pantalla por un tiempo especifico
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashIntent = new Intent(MainActivity.this, Categorias.class);
                MainActivity.this.startActivity(splashIntent);
                MainActivity.this.finish();
            }
        }, splashDisplayLength);

    }
}
