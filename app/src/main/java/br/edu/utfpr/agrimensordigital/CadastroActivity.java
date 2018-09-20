package br.edu.utfpr.agrimensordigital;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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

    @ViewById
    Spinner spnZoom;

    @ViewById
    Spinner spnTipoMapa;

    @ViewById
    LinearLayout salvarZoom;

    @ViewById
    LinearLayout salvarTipoMapa;

    @Extra
    Area area;

    private int tipoMapa;

    private int zoom;

    private List<Ponto> pontos;

    private static final String PREF_NAME = "pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.cadastro_de_area));
        tipoMapa = getTipoMapa();
        zoom = getZoom();
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

    @ItemSelect(R.id.spnZoom)
    public void spnZoomItemSelected(boolean selected, int position) {
        zoom = position;
        setZoom(position);
    }

    @ItemSelect(R.id.spnTipoMapa)
    public void spnTipoMapaItemSelected(boolean selected, int position) {
        tipoMapa = position;
        setZoom(position);
    }

    class ConexaoThread extends Thread {
        @Override
        public void run() {
            try {
                //Trata o tipo de mapa conforme valor do spinner;
                String tMap = "";
                if (tipoMapa == 0) {//GoogleMap.MAP_TYPE_HYBRID
                    tMap += "&maptype=hybrid&";
                } else if (tipoMapa == 1) {//GoogleMap.MAP_TYPE_SATTELITE
                    tMap += "&maptype=satellite&";
                } else if (tipoMapa == 2) {//GoogleMap.MAP_TYPE_TERRAIN
                    tMap += "&maptype=terrain&";
                }
                String tZoom = "";
                if (zoom == 0) {
                    tZoom = "zoom=20";
                } else if (zoom == 1) {
                    tZoom = "zoom=15";
                } else if (zoom == 2) {
                    tZoom = "zoom=10";
                } else if (zoom == 3) {
                    tZoom = "zoom=5";
                }
                String pic = "http://maps.googleapis.com/maps/api/staticmap?" + tZoom + tMap + "&size=600x600" + concatPontos();
                //String pic = "http://maps.googleapis.com/maps/api/staticmap?" + tZoom + tMap + "&size=600x600" + concatPontosTeste();

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
        Integer i = 0;
        for (Ponto p : pontos) {
            i++;
            //if (str.equals((""))) {
            str += "&markers=color:blue%7Clabel:" + i + "%7C";
            //}
            //if (str.equals((""))) {
            //str += ",";
            //}
            str += p.getLatitude().toString();
            str += "," + p.getLongitude().toString();
        }
        return str;
    }

    //7C62.107733,-145.5419367Ccolor:green%
    String concatPontosTeste() {
        String str = "";
        Integer i = 0;
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
            i++;
            //if (str.equals((""))) {
            str += "&markers=color:blue%7Clabel:" + i + "%7C";
            //}
            //if (str.equals((""))) {
            //str += ",";
            //}
            str += p.latitude;
            str += "," + p.longitude;
        }

        return str;
    }

    private void ativarCadastroPontos() {
        salvarZoom.setVisibility(View.GONE);
        salvarTipoMapa.setVisibility(View.GONE);
        nomeArea.setVisibility(View.GONE);
        dadosArea.setVisibility(View.GONE);
        imgMapa.setVisibility(View.GONE);
        btnSalvarPonto.setVisibility(View.VISIBLE);
        btnFinalizar.setVisibility(View.VISIBLE);
        btnSalvarArea.setVisibility(View.GONE);
        spnZoom.setSelection(zoom);
        spnTipoMapa.setSelection(tipoMapa);
    }

    private void ativarCadastroArea() {
        salvarZoom.setVisibility(View.VISIBLE);
        salvarTipoMapa.setVisibility(View.VISIBLE);
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
        edtPerimetro.setText(String.valueOf(SphericalUtil.computeLength(converterPontos())));
    }

    private void calcularArea() {
        edtArea.setText(String.valueOf(SphericalUtil.computeArea(converterPontos())));
    }

    public int getZoom() {
        SharedPreferences setting = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int valor = setting.getInt("zoom", 0);
        return valor;
    }

    private void setZoom(int valor) {
        SharedPreferences setting = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putInt("zoom", valor);
        editor.commit();
    }

    public int getTipoMapa() {
        SharedPreferences setting = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int valor = setting.getInt("tipo", 0);
        return valor;
    }

    private void setTipoMapa(int valor) {
        SharedPreferences setting = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putInt("tipo", valor);
        editor.commit();
    }

}
