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

import com.google.firebase.auth.FirebaseAuth;

import release.saosalvador.com.administradorbiblioteca.R;

public class TransitionActivity extends AppCompatActivity {
    int cont;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        firebaseAuth.signInWithEmailAndPassword("rafaelfranca2013@hotmail.com","32612421");
        if (ContextCompat.checkSelfPermission(TransitionActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(TransitionActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            checkAllPermissions();
        } else {
            loadMain();
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    public void checkAllPermissions() {
        cont = 0;
        for (String permission : PERMISSIONS) {
            cont++;
            int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

            if (ActivityCompat.checkSelfPermission
                    (TransitionActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(TransitionActivity.this,PERMISSIONS , PERMISSION_GRANTED);
            }else{
                ActivityCompat.requestPermissions(TransitionActivity.this, PERMISSIONS, PERMISSION_GRANTED);
            }
            if (cont == PERMISSIONS.length) loadMain();
        }
    }
    private void loadMain(){
        boolean t = true;
        while (t) {
            if (ContextCompat.checkSelfPermission(TransitionActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED||
                    ContextCompat.checkSelfPermission(TransitionActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                        Intent i = new Intent(TransitionActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            }
        }
    }
}
