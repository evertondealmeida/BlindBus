package tcc.blindbus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import tcc.blindbus.R;
import tcc.blindbus.modelo.Usuario;

public class ConfiguracaoActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);
        Button btConfigLinha = (Button) findViewById(R.id.btConfigLinha);
        Button btConfigViagem = (Button) findViewById(R.id.btConfigViagem);
        btConfigLinha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracaoActivity.this,
                        ListarActivity.class);
                startActivity(intent);
            }
        });
        btConfigViagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracaoActivity.this,
                        ViagemActivity.class);
                startActivity(intent);
            }
        });
    }
}
