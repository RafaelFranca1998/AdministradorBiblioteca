package release.saosalvador.com.administradorbiblioteca.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import release.saosalvador.com.administradorbiblioteca.R;

public class TransicaoActivity extends AppCompatActivity {
    int cont;

    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transicao);
        if (ContextCompat.checkSelfPermission(TransicaoActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(TransicaoActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            checkAllPermissions();
        } else {
            telaPrincipal();
        }
    }
    
    @TargetApi(Build.VERSION_CODES.M)
    public void checkAllPermissions() {
        cont = 0;
        for (String permission : PERMISSIONS) {
            cont++;
            int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

            if (ActivityCompat.checkSelfPermission
                    (TransicaoActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(TransicaoActivity.this,PERMISSIONS , PERMISSION_GRANTED);
            }else{
                ActivityCompat.requestPermissions(TransicaoActivity.this, PERMISSIONS, PERMISSION_GRANTED);
            }
            if (cont == PERMISSIONS.length) telaPrincipal();
        }
    }
    private void telaPrincipal(){
        boolean t = true;
        while (t) {
            if (ContextCompat.checkSelfPermission(TransicaoActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED||
                    ContextCompat.checkSelfPermission(TransicaoActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

            } else {
                t = false;
                int SPLASH_TIME_OUT = 3000;
                new Handler().postDelayed(new Runnable() {
                    /*
                     * Exibindo splash com um timer.
                     */
                    @Override
                    public void run() {
                        Intent i = new Intent(TransicaoActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            }
        }
    }
}
