package cz.martykan.forecastie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cz.martykan.forecastie.activities.SplashActivity;

public class act1 extends AppCompatActivity {
    Button b;
    public void goToActivity2 (View view){
        Intent intent = new Intent (this, SplashActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act1);
    }
}
