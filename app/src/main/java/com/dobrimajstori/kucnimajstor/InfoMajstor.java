package com.dobrimajstori.kucnimajstor;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoMajstor extends AppCompatActivity{



    static Korisnik majstor;

        //Userovi podaci

        private TextView imeiPrezime;




        private TextView poslovi;
        private TextView ocena;
        private TextView mail;
        private TextView brojtel;

        private CircleImageView sliketina;

        private InfoMajstorKomentariAdapter adapter;
        private ArrayList<Komentar> listaKomentara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_majstor);


        imeiPrezime= findViewById(R.id.imeiprezime);
        poslovi= findViewById(R.id.poslovi);
        ocena= findViewById(R.id.ocena);
        mail= findViewById(R.id.mail);
        brojtel= findViewById(R.id.telefon);
        sliketina= findViewById(R.id.profilnaslika);

        ucitajUri();



        imeiPrezime.setText(majstor.getIme()+" "+ majstor.getPrezime());
        poslovi.setText(Integer.toString(majstor.getBrojposlova()));
       // ocena.setText(Double.toString(majstor.getProsecnaocena()));
        ocena.setText(String.format("%.1f", majstor.getProsecnaocena()));
        mail.setText(majstor.getMail());
        brojtel.setText(majstor.getBrtel());

        listaKomentara=new ArrayList<Komentar>();
        adapter=new InfoMajstorKomentariAdapter(listaKomentara);

        if(majstor.getKomentariPoslodavca()!=null)
            listaKomentara.addAll(majstor.getKomentariPoslodavca());


        RecyclerView komentariRecycler= findViewById(R.id.listaKomentara);
        komentariRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        komentariRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        brojtel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + brojtel.getText()));
                startActivity(intent);
            }
        });

}
    private void ucitajUri()
    {
        DatabaseReference uri=FirebaseDatabase.getInstance().getReference("Korisnici").child(majstor.getId()).child("UriSlike");

        uri.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String s = dataSnapshot.getValue(String.class);
                    Picasso.get().load(s).into(sliketina);
                }
                else
                {
                   sliketina.setImageResource(R.drawable.ic_korisnik);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
