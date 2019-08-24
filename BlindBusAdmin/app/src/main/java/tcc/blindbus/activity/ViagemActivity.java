package tcc.blindbus.activity;

import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

import tcc.blindbus.R;
import tcc.blindbus.modelo.Usuario;
import tcc.blindbus.modelo.Viagem;
import tcc.blindbus.persistencia.LinhaDAO;
import tcc.blindbus.modelo.Linha;
import tcc.blindbus.persistencia.UsuarioDAO;

import android.widget.Switch;
import android.widget.Toast;

public class ViagemActivity extends MainActivity implements AdapterView.OnItemClickListener{
    ArrayAdapter adapter;
    ArrayList<Linha> listview_Viagem;
    ListView lista;

    Viagem viagemSelecionada = new Viagem();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viagem);
        Button btCadastrar = (Button) findViewById(R.id.btCadastrar);
        Button btVoltar = (Button) findViewById(R.id.btVoltar);
        daoUsuario = new UsuarioDAO(this); //Novo usuario
        inicializarFirebase(ViagemActivity.this);
        usuario =  daoUsuario.carregaUsuario(idUsuarioGlobal);
        btCadastrar.setOnClickListener(new android.view.View.OnClickListener() {

            public void onClick(View v) {
                if(viagemSelecionada.getLinha()!=null) {
                    Intent intent = new Intent(ViagemActivity.this,
                            TransmitirSinalActivity.class);
                    Switch switchSentidoCentro = (Switch) findViewById(R.id.switchSentido);
                    if (switchSentidoCentro.isChecked()) {
                        viagemSelecionada.setSentido("CENTRO");
                    } else {
                        viagemSelecionada.setSentido("BAIRRO");
                    }
                    viagemSelecionada.setInformacoesUsuario(usuario.getNumeroInscricao() +
                            " " + usuario.getNome());
                    Viagem viagem = new Viagem();
                    viagem.setInstanceBeacon("0x000005000453");
                    viagem.setNameSpace("0x699ebc80e1f311e39a0f");
                    viagem.setLinha(viagemSelecionada.getLinha());
                    viagem.setSentido(viagemSelecionada.getSentido());
                    viagem.setInformacoesUsuario(viagemSelecionada.getInformacoesUsuario());
                    viagem.setClienteEncontrado("NAO");
                    databaseReference.child("Viagem/0x000005000453").setValue(null);
                    databaseReference.child("Viagem").child(viagem.getInstanceBeacon()).setValue(viagem);
                    startActivity(intent);
                }else{
                    alert(getString(R.string.viagem_nao_selecionada));
                }
            }
        });
        lista = (ListView) findViewById(R.id.listview_Viagem);
        lista.setOnItemClickListener(this); // Clique no item
        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                view.setSelected(true);
                Linha linhaEscolhida = (Linha) adapter.getItemAtPosition(position);
                viagemSelecionada.setLinha(linhaEscolhida.getCodigo()+" "+linhaEscolhida.getDescricao());
                alert("LINHA "+linhaEscolhida.getCodigo()+"-"+linhaEscolhida.getDescricao()+" SELECIONADA" );
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
    private void alert(String s){
        Toast.makeText(ViagemActivity.this,s,Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        carregarLinha();
    }
    public void carregarLinha(){
        daoLinha = new LinhaDAO(this);
        listview_Viagem = daoLinha.getLista();
        daoLinha.close();

        if (listview_Viagem != null){
            adapter = new ArrayAdapter<Linha>(ViagemActivity.this, android.R.layout.simple_list_item_1, listview_Viagem);
            lista.setAdapter(adapter);
        }
        //  finish();

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
