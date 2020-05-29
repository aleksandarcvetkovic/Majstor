package com.dobrimajstori.kucnimajstor;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ramotion.foldingcell.FoldingCell;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"WeakerAccess", "unused"})
public class MojiPosloviAdapter extends ArrayAdapter<Posao> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;
    private Posao posao;
    private Korisnik poslodavac;

    public MojiPosloviAdapter(Context context, List<Posao> objects) {
        super(context, 0, objects);
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // get item for selected view
        final Posao posao = getItem(position);
        // if ugovor_majstor_kartica is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        final ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.moji_poslovi_kartica, parent, false);
            // binding view parts to view holder
            viewHolder.price = cell.findViewById(R.id.title_price);
           // viewHolder.time = cell.findViewById(R.id.title_time_label);
            //viewHolder.date = cell.findViewById(R.id.title_date_label);
            viewHolder.fromAddress = cell.findViewById(R.id.title_from_address);
            viewHolder.toAddress = cell.findViewById(R.id.title_to_address);
            viewHolder.requestsCount = cell.findViewById(R.id.title_requests_count);
            viewHolder.pledgePrice = cell.findViewById(R.id.title_pledge);
            viewHolder.contentRequestBtn = cell.findViewById(R.id.content_request_btn);
            //viewHolder.imePoslodavca=cell.findViewById(R.id.content_name_view);
            viewHolder.josBrDana=cell.findViewById(R.id.title_weight);
            viewHolder.ulicaiBr=cell.findViewById(R.id.content_from_address_1);
            //viewHolder.gradDrzava=cell.findViewById(R.id.content_from_address_2);
            viewHolder.datumContent=cell.findViewById(R.id.content_delivery_time);
            viewHolder.vremeContent=cell.findViewById(R.id.content_deadline_time);
            viewHolder.poslodavacNalog=cell.findViewById(R.id.poslodavacNalog);
            viewHolder.poslodavacAdresa=cell.findViewById(R.id.poslodavacAdresa);
            viewHolder.poslodavacDatumEvent=cell.findViewById(R.id.poslodavacDatumEvent);
            viewHolder.cenaContent=cell.findViewById(R.id.head_image_center_text);
            viewHolder.daniContent=cell.findViewById(R.id.head_image_right_text);
            viewHolder.udaljenostContent=cell.findViewById(R.id.head_image_left_text);
            viewHolder.naslovPosla=cell.findViewById(R.id.naslovPoslaContent);
            viewHolder.opisPoslaContent=cell.findViewById(R.id.opisPoslaContent);
            //viewHolder.zahhteviContent=cell.findViewById(R.id.zahteviSviposloviContent);

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

        if (null == posao)
            return cell;




        //Konvertuje datum

        String myFormat = "dd.MM.20yy."; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);


        final Calendar popuni =Calendar.getInstance();
        popuni.set(posao.getDatumKreiranja().getGodina(),posao.getDatumKreiranja().getMesec(),posao.getDatumKreiranja().getDan(),posao.getDatumKreiranja().getSati(),posao.getDatumKreiranja().getMinuti());
        final String datum=sdf.format(popuni.getTime());


        String vreme=String.format("%02d:%02d",  posao.getDatumKreiranja().getSati(),posao.getDatumKreiranja().getMinuti());


        // bind data from selected element to view through view holder
        viewHolder.price.setText(posao.getBudzet() +" RSD");

        viewHolder.naslovPosla.setText(posao.getNaslovposla());
        viewHolder.opisPoslaContent.setText(posao.getOpis());




        viewHolder.toAddress.setText("Kreirano: "+datum);

        viewHolder.pledgePrice.setText(String.valueOf(posao.getBudzet()));
        viewHolder.cenaContent.setText(posao.getBudzet() +" RSD");

        viewHolder.datumContent.setText(datum);
        viewHolder.vremeContent.setText(vreme);

        //String[] deloviAdrese=ugovor.getLokacija().split(",");



        viewHolder.ulicaiBr.setText(posao.getLokacija());


        Location lokacijaPoslodavca=new Location("A");
        lokacijaPoslodavca.setLatitude(posao.getLat());
        lokacijaPoslodavca.setLongitude(posao.getLon());

        DecimalFormat df = new DecimalFormat("#.#");


        viewHolder.requestsCount.setText(String.valueOf(posao.getPregovara()));
        viewHolder.udaljenostContent.setText(String.valueOf(posao.getPregovara()));




        Calendar vremeUgovora=Calendar.getInstance();
        vremeUgovora.set(posao.getDatumKreiranja().getGodina(),posao.getDatumKreiranja().getMesec(),posao.getDatumKreiranja().getDan());

        Calendar trenutnoCalendar=Calendar.getInstance();

        long dani = (trenutnoCalendar.getTimeInMillis()-vremeUgovora.getTimeInMillis()) / (24 * 60 * 60 * 1000);
        viewHolder.josBrDana.setText(String.valueOf(dani));
        viewHolder.daniContent.setText(String.valueOf(dani));

        String listString = "";

        for (String s : posao.getKategorije())
        {

            if(listString.length()>0)
                listString +=", ";
            listString += s ;
        }


        viewHolder.ulicaiBr.setText(listString);

        viewHolder.fromAddress.setText(posao.getNaslovposla());

        viewHolder.contentRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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
        TextView josBrDana;
        TextView ulicaiBr;
        TextView datumContent;
        TextView vremeContent;
        ImageView poslodavacNalog;
        ImageView poslodavacAdresa;
        ImageView poslodavacDatumEvent;
        TextView cenaContent;
        TextView daniContent;
        TextView udaljenostContent;
        TextView naslovPosla;
        TextView opisPoslaContent;


        //content

        TextView imePoslodavca;
    }
}
