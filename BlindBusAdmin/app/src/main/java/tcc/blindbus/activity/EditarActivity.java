package tcc.blindbus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import tcc.blindbus.R;
import tcc.blindbus.persistencia.LinhaDAO;
import tcc.blindbus.modelo.Linha;

public class EditarActivity extends MainActivity {
    EditText editText_Descricao, editText_Codigo;
    Button btn_Poliform;
    Linha alterarLinha, linha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);
        linha = new Linha();
        daoLinha = new LinhaDAO(EditarActivity.this);
        Intent intent = getIntent();
        alterarLinha = (Linha) intent.getSerializableExtra("linha-escolhida");
        editText_Descricao = (EditText) findViewById(R.id.editText_Descricao);
        editText_Codigo = (EditText) findViewById(R.id.editText_Codigo);
        btn_Poliform = (Button) findViewById(R.id.btn_Poliform);
        if (alterarLinha !=null){
            btn_Poliform.setText("Modificar");
            editText_Descricao.setText(alterarLinha.getDescricao());
            editText_Codigo.setText(alterarLinha.getCodigo());
            linha.setId(alterarLinha.getId());
        }else{
            btn_Poliform.setText("Cadastrar");
        }
        btn_Poliform.setOnClickListener(new View.OnClickListener() {
            Intent intent2 = new Intent(EditarActivity.this, ListarActivity.class);
            @Override
            public void onClick(View v) {
                linha.setDescricao(editText_Descricao.getText().toString());
                linha.setCodigo(editText_Codigo.getText().toString());

                if(btn_Poliform.getText().toString().equals("Cadastrar")){
                    daoLinha.inserirLinha(linha);
                    daoLinha.close();
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent2);
                }else{
                    daoLinha.alterarLinha(linha);
                    daoLinha.close();
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent2);
                }
            }
        });

    }
}
