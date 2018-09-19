package br.edu.utfpr.agrimensordigital;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import br.com.rafael.jpdroid.core.Jpdroid;
import br.com.rafael.jpdroid.exceptions.JpdroidException;
import br.edu.utfpr.agrimensordigital.model.Area;
import br.edu.utfpr.agrimensordigital.model.Ponto;
import br.edu.utfpr.agrimensordigital.util.GPSUtil;

@EActivity(R.layout.activity_cadastro)
public class CadastroActivity extends AppCompatActivity {

    @ViewById
    TextView lblNome;

    @ViewById
    EditText edtNomeArea;

    @ViewById
    Button btnSalvarPonto;

    @ViewById
    Button btnSalvar;

    @ViewById
    Button btnFinalizar;

    @ViewById
    ImageView ivMap;

    @ViewById
    TextView lblArea;

    @ViewById
    TextView lblValorArea;

    @ViewById
    TextView lblPerimetro;

    @ViewById
    TextView lblValorPerimetro;

    private List<Ponto> pontos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        pontos = null;
    }

    @Click(R.id.btnSalvarPonto)
    void clickSalvarPonto() {
        Ponto ponto = new Ponto();

        if (pontos == null) {
            pontos = new ArrayList<>();
        }

        GPSUtil gpsUtil = new GPSUtil(this);

        ponto.setLatitude(gpsUtil.getLatitude());
        ponto.setLongitude(gpsUtil.getLongitude());

        pontos.add(ponto);

        Toast.makeText(this, "Ponto salvo.", Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.btnFinalizar)
    void clickFinalizar() {
        //ao finalizar ele realiza o processo da geração da foto, depois atualiza os componentes em tela.
        new ConexaoThread().start();
    }

    @Click(R.id.btnSalvar)
    void clickSalvar() {
        Area area = new Area();
        area.setNome(edtNomeArea.getText().toString());
        area.setPontos(pontos);
        area.setArea(Double.parseDouble(lblValorArea.getText().toString()));
        area.setPerimetro(Double.parseDouble(lblValorPerimetro.getText().toString()));
        Jpdroid dataBase = Jpdroid.getInstance();

        try {
            dataBase.persist(area);
            Toast.makeText(this, "Área salva.", Toast.LENGTH_LONG).show();
        } catch (JpdroidException e) {
            e.printStackTrace();
        }
    }

    class ConexaoThread extends  Thread {
        @Override
        public void run() {
            try {
                String pic = "http://maps.googleapis.com/maps/api/staticmap?zoom=15&size=400x400&path=" + concatPontos();
                //String pic = "http://maps.googleapis.com/maps/api/staticmap?zoom=15&size=400x400&path=" + concatPontosTeste();

                URL url = new URL(pic);
                URLConnection con = url.openConnection();

                InputStream in = con.getInputStream();

                final Bitmap bmp = BitmapFactory.decodeStream(in);

                CadastroActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivMap.setImageBitmap(bmp);
                        lblPerimetro.setVisibility(View.VISIBLE);
                        lblValorPerimetro.setVisibility(View.VISIBLE);
                        lblArea.setVisibility(View.VISIBLE);
                        lblValorArea.setVisibility(View.VISIBLE);
                        lblNome.setVisibility(View.VISIBLE);
                        edtNomeArea.setVisibility(View.VISIBLE);
                        ivMap.setVisibility(View.VISIBLE);
                        btnSalvarPonto.setVisibility(View.GONE);
                        btnSalvar.setVisibility(View.VISIBLE);
                        btnFinalizar.setVisibility(View.GONE);

                        List<LatLng> listaLatLng = new ArrayList<>();

                        for (Ponto p : pontos) {
                            LatLng latLng = new LatLng(p.getLatitude(), p.getLongitude());
                            listaLatLng.add(latLng);
                        }

                        //Pontos teste
//                        LatLng latLng = null;
//                        latLng = new  LatLng(40.737102, -73.990318);
//                        listaLatLng.add(latLng);
//                        latLng = new  LatLng(40.749825, -73.987963);
//                        listaLatLng.add(latLng);
//                        latLng = new  LatLng(40.752946, -73.987384);
//                        listaLatLng.add(latLng);
//                        latLng = new  LatLng(40.755823, -73.986397);
//                        listaLatLng.add(latLng);

                        lblValorPerimetro.setText(String.valueOf(SphericalUtil.computeLength(listaLatLng)));
                        lblValorArea.setText(String.valueOf(SphericalUtil.computeArea(listaLatLng)));
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    String concatPontos() {
        String str = "";
        for (Ponto p : pontos) {
            if (!str.equals((""))) {
                str += "|";
            }
            str += p.getLatitude().toString() + ",";
            str += p.getLongitude().toString();
        }
        return str;
    }

    String concatPontosTeste() {
        String str = "";
        List<LatLng> listaLatLng = new ArrayList<>();
        LatLng latLng = null;
        latLng = new  LatLng(40.737102, -73.990318);
        listaLatLng.add(latLng);
        latLng = new  LatLng(40.749825, -73.987963);
        listaLatLng.add(latLng);
        latLng = new  LatLng(40.752946, -73.987384);
        listaLatLng.add(latLng);
        latLng = new  LatLng(40.755823, -73.986397);
        listaLatLng.add(latLng);
        for (LatLng p : listaLatLng) {
            if (!str.equals((""))) {
                str += "|";
            }
            str += p.latitude + ",";
            str += p.longitude;
        }
        return str;
    }

}
