package tcc.blindbus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

import tcc.blindbus.R;
import tcc.blindbus.persistencia.LinhaDAO;
import tcc.blindbus.modelo.Linha;

public class ListarActivity extends MainActivity {
    ListView lista;
    ArrayList<Linha> listview_Linha;
    Linha linha;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);
        Button btCadastrar = (Button) findViewById(R.id.btCadastrar);
        Button btVoltar = (Button) findViewById(R.id.btVoltar);
        btCadastrar.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListarActivity.this, EditarActivity.class);
                startActivity(intent);
            }
        });

        lista = (ListView) findViewById(R.id.listview_Linha);
        registerForContextMenu(lista);
        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListarActivity.this,
                        ConfiguracaoActivity.class);
                startActivity(intent);
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

                Linha linhaEscolhida = (Linha) adapter.getItemAtPosition(position);
                Intent i = new Intent(ListarActivity.this, EditarActivity.class);
                i.putExtra("linha-escolhida", linhaEscolhida);
                startActivity(i);
            }
        });

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
                linha = (Linha) adapter.getItemAtPosition(position);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarLinha();
    }
    public void carregarLinha(){
        daoLinha = new LinhaDAO(ListarActivity.this);
        listview_Linha = daoLinha.getLista();
        daoLinha.close();
        if (listview_Linha != null){
            adapter = new ArrayAdapter<Linha>(ListarActivity.this,
                    android.R.layout.simple_list_item_1, listview_Linha);
            lista.setAdapter(adapter);
        }
    }
}
