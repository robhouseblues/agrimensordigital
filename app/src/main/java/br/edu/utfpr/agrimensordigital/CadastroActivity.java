package br.edu.utfpr.agrimensordigital;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.agrimensordigital.model.Area;
import br.edu.utfpr.agrimensordigital.model.Ponto;
import br.edu.utfpr.agrimensordigital.service.AreaService;
import br.edu.utfpr.agrimensordigital.util.GPSUtil;

@EActivity(R.layout.activity_cadastro)
public class CadastroActivity extends AppCompatActivity {

    @ViewById
    LinearLayout nomeArea;

    @ViewById
    LinearLayout dadosArea;

    @ViewById
    EditText edtNome;

    @ViewById
    EditText edtArea;

    @ViewById
    EditText edtPerimetro;

    @ViewById
    Button btnSalvarPonto;

    @ViewById
    Button btnFinalizar;

    @ViewById
    Button btnSalvarArea;

    @ViewById
    ImageView imgMapa;

    @Extra
    Area area;

    private List<Ponto> pontos;

    private DecimalFormat df = new DecimalFormat("###,###.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.cadastro_de_area));
    }

    @AfterViews
    void init() {
        if (area == null) {
            ativarCadastroPontos();
        } else {
            ativarExibicaoArea();
            exibirArea();
        }
    }

    @Click(R.id.btnSalvarPonto)
    void clickSalvarPonto() {
        if (pontos == null) {
            pontos = new ArrayList<>();
        }

        Ponto ponto = new Ponto();

        GPSUtil gpsUtil = new GPSUtil(this);

        ponto.setLatitude(gpsUtil.getLatitude());
        ponto.setLongitude(gpsUtil.getLongitude());

        if (ponto.getLatitude() != null || ponto.getLongitude() != null) {
            pontos.add(ponto);
            Toast.makeText(this, "Ponto salvo com sucesso.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Não foi possível obter a localização.", Toast.LENGTH_LONG).show();
        }
    }

    @Click(R.id.btnFinalizar)
    void clickFinalizar() {
        ativarCadastroArea();

        calcularPerimetro();
        calcularArea();

        //ao finalizar ele realiza o processo da geração da foto, depois atualiza os componentes em tela.
        new ConexaoThread().start();
    }

    @Click(R.id.btnSalvarArea)
    void clickSalvar() {
        Area area = new Area();

        area.setNome(edtNome.getText().toString());
        area.setPerimetro(Double.parseDouble(edtPerimetro.getText().toString()));
        area.setArea(Double.parseDouble(edtArea.getText().toString()));
        area.setPontos(pontos);

        //Transformar bitmap em bytes para salvar como string
        if (imgMapa.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) imgMapa.getDrawable()).getBitmap();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            byte[] byteArray = os.toByteArray();

            area.setImagem(Base64.encodeToString(byteArray, 0));
        }

        AreaService areaService = new AreaService(this);

        try {
            areaService.incluir(area);

            Toast.makeText(this, "Área salva com sucesso.", Toast.LENGTH_LONG).show();

            limparCampos();

            ativarCadastroPontos();
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível salvar a área.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    class ConexaoThread extends Thread {
        @Override
        public void run() {
            try {
//                String pic = "http://maps.googleapis.com/maps/api/staticmap?zoom=15&size=400x400&path=" + concatPontos();
                String pic = "http://maps.googleapis.com/maps/api/staticmap?zoom=15&size=400x400&path=" + concatPontosTeste();

                URL url = new URL(pic);
                URLConnection con = url.openConnection();

                InputStream in = con.getInputStream();

                final Bitmap bmp = BitmapFactory.decodeStream(in);

                CadastroActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgMapa.setImageBitmap(bmp);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
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
        latLng = new LatLng(40.737102, -73.990318);
        listaLatLng.add(latLng);
        latLng = new LatLng(40.749825, -73.987963);
        listaLatLng.add(latLng);
        latLng = new LatLng(40.752946, -73.987384);
        listaLatLng.add(latLng);
        latLng = new LatLng(40.755823, -73.986397);
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

    private void ativarCadastroPontos() {
        nomeArea.setVisibility(View.GONE);
        dadosArea.setVisibility(View.GONE);
        imgMapa.setVisibility(View.GONE);
        btnSalvarPonto.setVisibility(View.VISIBLE);
        btnFinalizar.setVisibility(View.VISIBLE);
        btnSalvarArea.setVisibility(View.GONE);
    }

    private void ativarCadastroArea() {
        edtNome.setEnabled(true);
        nomeArea.setVisibility(View.VISIBLE);
        dadosArea.setVisibility(View.VISIBLE);
        imgMapa.setVisibility(View.VISIBLE);
        btnSalvarPonto.setVisibility(View.GONE);
        btnFinalizar.setVisibility(View.GONE);
        btnSalvarArea.setVisibility(View.VISIBLE);
    }

    private void ativarExibicaoArea() {
        edtNome.setEnabled(false);
        dadosArea.setVisibility(View.VISIBLE);
        imgMapa.setVisibility(View.VISIBLE);
        btnSalvarPonto.setVisibility(View.GONE);
        btnFinalizar.setVisibility(View.GONE);
        btnSalvarArea.setVisibility(View.GONE);
    }

    private void exibirArea() {
        edtNome.setText(area.getNome());
        edtPerimetro.setText(String.valueOf(area.getPerimetro()));
        edtArea.setText(String.valueOf(area.getArea()));

        if (area.getImagem() != null) {
            byte[] byteArray = Base64.decode(area.getImagem(), 0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgMapa.setImageBitmap(bitmap);
        }
    }

    private void limparCampos() {
        edtNome.setText("");
        edtPerimetro.setText("");
        edtArea.setText("");
    }

    private List<LatLng> converterPontos() {
        List<LatLng> lista = new ArrayList<>();

//        Pontos teste
//        LatLng latLng = null;
//        latLng = new LatLng(40.737102, -73.990318);
//        lista.add(latLng);
//        latLng = new LatLng(40.749825, -73.987963);
//        lista.add(latLng);
//        latLng = new LatLng(40.752946, -73.987384);
//        lista.add(latLng);
//        latLng = new LatLng(40.755823, -73.986397);
//        lista.add(latLng);

        for (Ponto p : pontos) {
            LatLng latLng = new LatLng(p.getLatitude(), p.getLongitude());
            lista.add(latLng);
        }

        return lista;
    }

    private void calcularPerimetro() {
        edtPerimetro.setText(String.valueOf(df.format(SphericalUtil.computeLength(converterPontos()))));
    }

    private void calcularArea() {
        edtArea.setText(String.valueOf(df.format(SphericalUtil.computeArea(converterPontos()))));
    }

}
