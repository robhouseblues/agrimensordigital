package br.edu.utfpr.agrimensordigital;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

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

        Toast.makeText(this, "Ponto salvo.", Toast.LENGTH_LONG).show();
    }

    @Click(R.id.btnFinalizar)
    void clickFinalizar() {
        lblNome.setVisibility(View.VISIBLE);
        edtNomeArea.setVisibility(View.VISIBLE);
        btnSalvarPonto.setVisibility(View.GONE);
        btnSalvar.setVisibility(View.VISIBLE);
        btnFinalizar.setVisibility(View.GONE);
    }

    @Click(R.id.btnSalvar)
    void clickSalvar() {
        Area area = new Area();
        area.setNome(edtNomeArea.getText().toString());
        area.setPontos(pontos);

        List<LatLng> listaLatLng = new ArrayList<>();

        for (Ponto p : pontos) {
            LatLng latLng = new LatLng(p.getLatitude(), p.getLongitude());
            listaLatLng.add(latLng);
        }

        area.setArea(SphericalUtil.computeArea(listaLatLng));

        area.setPerimetro(SphericalUtil.computeLength(listaLatLng));

//        for (Ponto p : pontos) {
//            p.setArea(area);
//        }

        Jpdroid dataBase = Jpdroid.getInstance();

        try {
            dataBase.persist(area);
            Toast.makeText(this, "Área salva.", Toast.LENGTH_LONG).show();
        } catch (JpdroidException e) {
            e.printStackTrace();
        }
    }

}
