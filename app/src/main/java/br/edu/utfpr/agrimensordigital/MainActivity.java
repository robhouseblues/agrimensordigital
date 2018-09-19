package br.edu.utfpr.agrimensordigital;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import br.edu.utfpr.agrimensordigital.model.Area;
import br.edu.utfpr.agrimensordigital.service.AreaService;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends AppCompatActivity {

    @ViewById
    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void init() {
        lista.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new AreaService(this).listar()));
    }

    @OptionsItem(R.id.mnuCadastrar)
    void clickCadastrar() {
        Intent it = new Intent(this, CadastroActivity_.class);
        startActivity(it);
    }

    @ItemClick(R.id.lista)
    void clickLista(Area area) {
        Intent it = new Intent(this, CadastroActivity_.class);
        it.putExtra("area", area);
        startActivity(it);
    }

}
