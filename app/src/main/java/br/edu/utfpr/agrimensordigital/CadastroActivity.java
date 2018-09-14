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

@EActivity(R.layout.activity_cadastro)
public class CadastroActivity extends AppCompatActivity implements LocationListener {

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

    private Double latitude;
    private Double longitude;
    private List<Ponto> pontos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    @Click(R.id.btnSalvarPonto)
    void clickSalvarPonto() {
        Ponto ponto = new Ponto();

        ponto.setLatitude(latitude);
        ponto.setLongitude(longitude);

        pontos.add(ponto);

        Toast.makeText(this, "Salvou o ponto.", Toast.LENGTH_LONG).show();


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

        for (Ponto p : pontos) {
            p.setArea(area);
        }

        Jpdroid dataBase = Jpdroid.getInstance();

        try {
            dataBase.persist(area);
            Toast.makeText(this, "Salvou a área.", Toast.LENGTH_LONG).show();
        } catch (JpdroidException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude() / 1000000;
        longitude = location.getLongitude() / 1000000;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
