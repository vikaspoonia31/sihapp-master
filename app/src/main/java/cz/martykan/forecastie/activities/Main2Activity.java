package cz.martykan.forecastie.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import cz.martykan.forecastie.R;
import cz.martykan.forecastie.act1;
import luongvo.com.gmapdirectionfinder.MapsActivity;

public class Main2Activity extends AppCompatActivity {
    Button b;

    public void goToActivity2 (View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void gotoActivity3( View view){
        Intent intent = new Intent(this, act1.class);
        startActivity(intent);
    }
    public void goToActivity4 (View view){
        Intent intent = new Intent(this, GraphActivity2.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
    }
}
