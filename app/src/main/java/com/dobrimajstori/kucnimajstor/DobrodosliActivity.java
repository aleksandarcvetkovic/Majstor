package com.dobrimajstori.kucnimajstor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DobrodosliActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignIn;
    private Button buttonSignUp;



    //Userovi podaci
    private String ImeIPrezime;
    private String phoneNumber;

    //firebase
    private FirebaseAuth firebaseAuth;

    //Objekat korisnik
    Korisnik k;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dobrodosli);

        ///////////dugmici za signIn i signUp aktivitije
        buttonSignIn= findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DobrodosliActivity.this,LoginProba.class));
            }
        });

        buttonSignUp= findViewById(R.id.buttonSignup);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DobrodosliActivity.this,Register.class));
            }
        });
        //////////////

        firebaseAuth = FirebaseAuth.getInstance();




    }

    @Override
    public void onStart() {
        super.onStart();

       // firebaseAuth.addAuthStateListener(mAuthListener);

        Intent PokreniActivity=new Intent(DobrodosliActivity.this, IzaberiActivity.class);
        PokreniActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PokreniActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PokreniActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);


        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null)
            startActivity(PokreniActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



    }



    @Override
    public void onClick(View view) {

    }
}
