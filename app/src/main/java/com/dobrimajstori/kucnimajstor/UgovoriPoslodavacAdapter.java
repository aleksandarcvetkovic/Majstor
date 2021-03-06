package com.dobrimajstori.kucnimajstor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.foldingcell.FoldingCell;
import com.squareup.picasso.Picasso;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

import static com.facebook.FacebookSdk.getApplicationContext;

@SuppressWarnings({"WeakerAccess", "unused"})
public class  UgovoriPoslodavacAdapter extends ArrayAdapter<Ugovor>  {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;
    private Posao posao;
    private Korisnik poslodavac;
    private boolean ugovorProsao;
    private Calendar vremeUgovora;
    private Calendar trenutnoCalendar;

    public UgovoriPoslodavacAdapter(Context context, List<Ugovor> objects) {
        super(context, 0, objects);



    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // get item for selected view
        final Ugovor ugovor = getItem(position);
        // if ugovor_majstor_kartica is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        final ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.ugovor_poslodavac_kartica, parent, false);
            // binding view parts to view holder
            viewHolder.profilna=cell.findViewById(R.id.content_avatar);
            viewHolder.price = cell.findViewById(R.id.title_price);
            viewHolder.time = cell.findViewById(R.id.title_time_label);
            viewHolder.date = cell.findViewById(R.id.title_date_label);
            viewHolder.fromAddress = cell.findViewById(R.id.title_from_address);
            viewHolder.toAddress = cell.findViewById(R.id.title_to_address);
            //viewHolder.requestsCount = cell.findViewById(R.id.title_requests_count);
            viewHolder.pledgePrice = cell.findViewById(R.id.title_pledge);
            viewHolder.contentRequestBtn = cell.findViewById(R.id.content_request_btn);
            viewHolder.imePoslodavca=cell.findViewById(R.id.content_name_view);
            viewHolder.josBrDana=cell.findViewById(R.id.title_weight);
            //viewHolder.ulicaiBr=cell.findViewById(R.id.content_from_address_1);
            //viewHolder.gradDrzava=cell.findViewById(R.id.content_from_address_2);
            viewHolder.datumContent=cell.findViewById(R.id.content_delivery_time);
            viewHolder.vremeContent=cell.findViewById(R.id.content_deadline_time);
            viewHolder.poslodavacNalog=cell.findViewById(R.id.poslodavacNalog);
            //viewHolder.poslodavacAdresa=cell.findViewById(R.id.poslodavacAdresa);
            viewHolder.poslodavacDatumEvent=cell.findViewById(R.id.poslodavacDatumEvent);
            viewHolder.cenaContent=cell.findViewById(R.id.head_image_center_text);
            viewHolder.daniContent=cell.findViewById(R.id.head_image_right_text);
            //viewHolder.udaljenostContent=cell.findViewById(R.id.head_image_left_text);
            viewHolder.naslovPoslaContent=cell.findViewById(R.id.naslovPoslaContentUgovoriPoslodavac);
            viewHolder.opisPoslaContent=cell.findViewById(R.id.opisPoslaContent);
            viewHolder.zahtevRaskid=cell.findViewById(R.id.poslodavac_ugovori_zahtev_raskid);
            cell.setTag(viewHolder);
        } else {
            // for existing ugovor_majstor_kartica set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        if (null == ugovor)
            return cell;



        FirebaseDatabase.getInstance().getReference("Ugovori").child(ugovor.getIdugovora()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try{


                    Ugovor ugovor=dataSnapshot.getValue(Ugovor.class);

                    vremeUgovora=Calendar.getInstance();
                    vremeUgovora.set(ugovor.getDandolaska().getGodina(),ugovor.getDandolaska().getMesec(),ugovor.getDandolaska().getDan(),ugovor.getDandolaska().getSati(),ugovor.getDandolaska().getMinuti(),0);

                    trenutnoCalendar=Calendar.getInstance();

                    ugovorProsao=vremeUgovora.before(trenutnoCalendar);

                    if(ugovorProsao)
                    {
                        viewHolder.zahtevRaskid.setText("OCENI MAJSTORA");

                    }
                    else
                    if(ugovor.getRaskidMajstor())
                    {

                        viewHolder.zahtevRaskid.setText("RASKINI UGOVOR");

                    }
                    else
                    if(ugovor.getRaskidPoslodavac())
                    {

                        viewHolder.zahtevRaskid.setText("IZBRIŠI ZAHTEV");

                    }
                    else
                    {
                        viewHolder.zahtevRaskid.setText("ZAHTEV ZA RASKID");


                    }



                }
                catch (Exception e){

                    Toast.makeText(getContext(),"Ugovor obrisan!",Toast.LENGTH_SHORT).show();

                    Intent PokreniActivity=new Intent(getContext(), Pocetna.class);
                    PokreniActivity.putExtra("fragment","ugovoriposlodavac");
                    getContext().startActivity(PokreniActivity);

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



                viewHolder.zahtevRaskid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if(viewHolder.zahtevRaskid.getText().equals("OCENI MAJSTORA"))
                            {
                                //Oceni majstora...

                                OceniMajstora.prenosUgovora=ugovor;
                                getContext().startActivity(new Intent(getContext(),OceniMajstora.class));

                            }
                            else
                            if(viewHolder.zahtevRaskid.getText().equals("RASKINI UGOVOR"))
                            {

                                final PrettyDialog dialogfilter= new PrettyDialog(getContext());
                                dialogfilter
                                        .setTitle("Raskinite ugovor!")
                                        .setMessage("Majstor je već podneo zahtev za raskid ugovora, da li želite da raskinete ugovor?")
                                        .setIcon(R.drawable.pdlg_icon_info)
                                        .setIconTint(R.color.colorPrimary)
                                        .addButton(
                                                "RASKINI",     // button text
                                                R.color.pdlg_color_white,  // button text color
                                                R.color.colorPrimary,new PrettyDialogCallback() {  // button OnClick listener
                                                    @Override
                                                    public void onClick() {

                                                        //Raskida ugovor i brise ga iz baze

                                                        Pocetna.baza.getReference("SviPoslovi").child(ugovor.getPosao()).child("ugovoren").setValue(false);
                                                        Pocetna.baza.getReference("Korisnici").child(Pocetna.currentFirebaseUser.getUid()).child("PotpisaniUgovoriPoslodavac").child(ugovor.getIdugovora()).removeValue();
                                                        Pocetna.baza.getReference("Korisnici").child(ugovor.getMajstor()).child("PotpisaniUgovoriMajstor").child(ugovor.getIdugovora()).removeValue();
                                                        Pocetna.baza.getReference("Ugovori").child(ugovor.getIdugovora()).removeValue();
                                                        Toast.makeText(getApplicationContext(),"Ugovor uspešno raskinut!",Toast.LENGTH_LONG);


                                                        Intent PokreniActivity=new Intent(getContext(), Pocetna.class);
                                                        PokreniActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        PokreniActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        PokreniActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                        getContext().startActivity(PokreniActivity);

                                                        dialogfilter.dismiss();

                                                    }
                                                }  // button background color

                                        )
                                        .show();

                            }
                            else
                                if(viewHolder.zahtevRaskid.getText().equals("IZBRIŠI ZAHTEV"))
                                {
                                    final PrettyDialog dialogfilter = new PrettyDialog(getContext());
                                    dialogfilter
                                            .setTitle("Izbriši zahtev")
                                            .setMessage("Već ste podneli zahtev za raskid ali ga majstor još nije prihvatio, da li želite da povučete zahtev za raskid?")
                                            .setIcon(R.drawable.pdlg_icon_info)
                                            .setIconTint(R.color.colorPrimary)
                                            .addButton(
                                                    "IZBRIŠI ZAHTEV",     // button text
                                                    R.color.pdlg_color_white,  // button text color
                                                    R.color.colorPrimary, new PrettyDialogCallback() {  // button OnClick listener
                                                        @Override
                                                        public void onClick() {

                                                            Pocetna.baza.getReference("Ugovori").child(ugovor.getIdugovora()).child("raskidPoslodavac").setValue(false);
                                                            viewHolder.zahtevRaskid.setText("ZAHTEV ZA RASKID");
                                                            Toast.makeText(getContext(),"Zahtev za raskid uspešno izbrisan!",Toast.LENGTH_LONG);

                                                            dialogfilter.dismiss();
                                                        }
                                                    }  // button background color

                                            )
                                            .show();
                                }
                                else
                                    if(viewHolder.zahtevRaskid.getText().equals("ZAHTEV ZA RASKID")){

                                        final PrettyDialog dialogfilter = new PrettyDialog(getContext());
                                        dialogfilter
                                                .setTitle("Zahtev za raskid")
                                                .setMessage("Da li želite da pošaljete poslodavcu zahtev za raskid ugovora?")
                                                .setIcon(R.drawable.pdlg_icon_info)
                                                .setIconTint(R.color.colorPrimary)
                                                .addButton(
                                                        "POŠALJI ZAHTEV",     // button text
                                                        R.color.pdlg_color_white,  // button text color
                                                        R.color.colorPrimary, new PrettyDialogCallback() {  // button OnClick listener
                                                            @Override
                                                            public void onClick() {


                                                                Pocetna.baza.getReference("Ugovori").child(ugovor.getIdugovora()).child("raskidPoslodavac").setValue(true);
                                                                Toast.makeText(getContext(),"Zahtev za raskid uspešno poslat!",Toast.LENGTH_LONG);

                                                                viewHolder.zahtevRaskid.setText("IZBRIŠI ZAHTEV");

                                                                dialogfilter.dismiss();
                                                            }
                                                        }  // button background color

                                                )
                                                .show();

                                    }



                        }
                    });

                    //Konvertuje datum

                    String myFormat = "dd.MM.20yy."; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);


                    final Calendar popuni =Calendar.getInstance();
                    popuni.set(ugovor.getDandolaska().getGodina(),ugovor.getDandolaska().getMesec(),ugovor.getDandolaska().getDan(),ugovor.getDandolaska().getSati(),ugovor.getDandolaska().getMinuti());
                    final String datum=sdf.format(popuni.getTime());


                    String vreme=String.format("%02d:%02d",  ugovor.getDandolaska().getSati(), ugovor.getDandolaska().getMinuti());


                    // bind data from selected element to view through view holder
                    viewHolder.price.setText(ugovor.getCena() +" RSD");
                    viewHolder.time.setText(vreme);

                    Calendar sad = Calendar.getInstance();

                    int danuNedelji=popuni.get(Calendar.DAY_OF_WEEK);
                    String danStr="";
                    if(danuNedelji==sad.get(Calendar.DAY_OF_WEEK))
                    {
                        danStr="Danas";
                        viewHolder.date.setTextColor(Color.RED);

                    }
                    else
                        switch (danuNedelji)
                        {
                            case 1:
                                danStr="Nedelja";
                                break;
                            case 2:
                                danStr="Ponedeljak";
                                break;
                            case 3:
                                danStr="Utorak";
                                break;
                            case 4:
                                danStr="Sreda";
                                break;
                            case 5:
                                danStr="Četvrtak";
                                break;
                            case 6:
                                danStr="Petak";
                                break;
                            case 7:
                                danStr="Subota";
                                break;
                        }

                    viewHolder.date.setText(danStr);
                    viewHolder.toAddress.setText(datum);

                    viewHolder.pledgePrice.setText(String.valueOf(ugovor.getCena()));
                    viewHolder.cenaContent.setText(String.valueOf(ugovor.getCena()));

                    viewHolder.datumContent.setText(datum);
                    viewHolder.vremeContent.setText(vreme);


                    String[] deloviAdrese=ugovor.getLokacija().split(",");



                    //viewHolder.ulicaiBr.setText(ugovor.getLokacija());


                    Location lokacijaPoslodavca=new Location("A");
                    lokacijaPoslodavca.setLatitude(ugovor.getLat());
                    lokacijaPoslodavca.setLongitude(ugovor.getLon());

                    DecimalFormat df = new DecimalFormat("#.#");

                    //viewHolder.requestsCount.setText((df.format(Pocetna.userLocation.distanceTo(lokacijaPoslodavca)/1000))+"Km");
                    //viewHolder.udaljenostContent.setText((df.format(Pocetna.userLocation.distanceTo(lokacijaPoslodavca)/1000))+"Km");




                    vremeUgovora=Calendar.getInstance();
                    vremeUgovora.set(ugovor.getDandolaska().getGodina(),ugovor.getDandolaska().getMesec(),ugovor.getDandolaska().getDan());

                    trenutnoCalendar=Calendar.getInstance();

                    long dani = (vremeUgovora.getTimeInMillis() - trenutnoCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000);
                    viewHolder.josBrDana.setText(String.valueOf(dani));
                    viewHolder.daniContent.setText(String.valueOf(dani));


                    //uzima sliku

                    DatabaseReference data=FirebaseDatabase.getInstance().getReference("Korisnici").child(ugovor.getMajstor()).child("UriSlike");

                    data.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists())
                            {
                                String uri=dataSnapshot.getValue(String.class);
                                Picasso.get().load(uri).into(viewHolder.profilna);
                            }
                            else
                            {
                                viewHolder.profilna.setImageResource(R.drawable.ic_korisnik);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                    //Uzima naziv posla

                    Pocetna.baza.getReference("SviPoslovi").child(ugovor.getPosao()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            posao = dataSnapshot.getValue(Posao.class);
                            viewHolder.fromAddress.setText(posao.getNaslovposla());
                            viewHolder.naslovPoslaContent.setText(posao.getNaslovposla());
                            viewHolder.opisPoslaContent.setText(posao.getOpis());


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Pocetna.baza.getReference("Korisnici").child(ugovor.getMajstor()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            poslodavac=dataSnapshot.getValue(Korisnik.class);
                            viewHolder.imePoslodavca.setText(poslodavac.getIme()+" "+poslodavac.getPrezime());


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    // set custom btn handler for list item from that item
                    if (ugovor.getRequestBtnClickListener() != null) {
                        viewHolder.contentRequestBtn.setOnClickListener(ugovor.getRequestBtnClickListener());
                    } else {
                        // (optionally) add "default" handler if no handler found in item
                        // viewHolder.contentRequestBtn.setOnClickListener(defaultRequestBtnClickListener);
                        viewHolder.contentRequestBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {



                                InfoUgovor.prenosUgovora=ugovor;

                                Intent i=new Intent(getContext(),InfoUgovor.class);
                                getContext().startActivity(i);

                            }
                        });
                    }

                    viewHolder.poslodavacNalog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MessageActivity.drugiId=ugovor.getMajstor();
                            Pocetna.prenosBida=new Bid();
                            Pocetna.prenosBida.setIdposla(ugovor.getPosao());

                            Intent i=new Intent(getContext(),MessageActivity.class);
                            getContext().startActivity(i);
                        }
                    });


                    viewHolder.poslodavacDatumEvent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            final Calendar popuni =Calendar.getInstance();
                            popuni.set(ugovor.getDandolaska().getGodina(),ugovor.getDandolaska().getMesec(),ugovor.getDandolaska().getDan(),ugovor.getDandolaska().getSati(),ugovor.getDandolaska().getMinuti());


                            Intent intent = new Intent(Intent.ACTION_EDIT);
                            intent.setType("vnd.android.cursor.item/event");
                            intent.putExtra("beginTime", popuni.getTimeInMillis());
                            intent.putExtra("allDay", false);
                            intent.putExtra("rrule", false);
                            intent.putExtra("title", "Posao: "+posao.getNaslovposla());
                            //LatLng latLng=new LatLng(ugovor.getLat(),ugovor.getLon());
                            intent.putExtra(CalendarContract.Events.EVENT_LOCATION,ugovor.getLokacija());
                            intent.putExtra("description",posao.getOpis());
                            getContext().startActivity(intent);

                        }
                    });

        return cell;

    }

    // simple methods for register ugovor_majstor_kartica state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    public View.OnClickListener getDefaultRequestBtnClickListener() {
        //Toast.makeText(getContext(),"Klik",Toast.LENGTH_LONG);
        return defaultRequestBtnClickListener;
    }

    public void setDefaultRequestBtnClickListener(View.OnClickListener defaultRequestBtnClickListener) {
        this.defaultRequestBtnClickListener = defaultRequestBtnClickListener;
    }


    // View lookup cache
    private static class ViewHolder {
        TextView price;
        TextView contentRequestBtn;
        TextView pledgePrice;
        TextView fromAddress;
        TextView toAddress;
        TextView requestsCount;
        TextView date;
        TextView time;
        ImageView profilna;
        TextView josBrDana;
        //TextView ulicaiBr;
        // TextView gradDrzava;
        TextView datumContent;
        TextView vremeContent;
        ImageView poslodavacNalog;
        //ImageView poslodavacAdresa;
        ImageView poslodavacDatumEvent;
        TextView cenaContent;
        TextView daniContent;
        TextView udaljenostContent;
        TextView naslovPoslaContent;
        TextView opisPoslaContent;
        TextView zahtevRaskid;

        //content

        TextView imePoslodavca;
    }

}
