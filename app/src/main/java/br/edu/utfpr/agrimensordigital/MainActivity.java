package br.edu.utfpr.agrimensordigital;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import br.com.rafael.jpdroid.core.Jpdroid;
import br.edu.utfpr.agrimensordigital.model.Area;
import br.edu.utfpr.agrimensordigital.model.Ponto;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Jpdroid dataBase = Jpdroid.getInstance();
        dataBase.setContext(this);
        dataBase.addEntity(Ponto.class);
        dataBase.addEntity(Area.class);
        dataBase.open();

    }


    @Click(R.id.btnCadastrar)
    void clickSalvarPonto() {
        Intent it = new Intent(this, CadastroActivity_.class);
        startActivity(it);
    }

}
