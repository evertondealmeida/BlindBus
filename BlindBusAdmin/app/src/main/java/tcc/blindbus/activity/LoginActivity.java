package tcc.blindbus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import tcc.blindbus.R;
import tcc.blindbus.modelo.Linha;
import tcc.blindbus.modelo.Usuario;
import tcc.blindbus.persistencia.LinhaDAO;
import tcc.blindbus.persistencia.UsuarioDAO;
public class LoginActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        daoLinha = new LinhaDAO(this); //Nova Linha
        daoLinha.onCreate(daoLinha.getWritableDatabase());
        inicializarFirebase(LoginActivity.this);
        daoUsuario = new UsuarioDAO(this); //Novo usuario
        daoUsuario.onCreate(daoUsuario.getWritableDatabase()); //Cria as tabelas SQLite
        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    usuario = objSnapshot.getValue(Usuario.class);
                    daoUsuario.inserirUsuario(new Usuario(usuario.getNome(),usuario.getLogin(),
                            usuario.getSenha(),usuario.getNumeroInscricao()));
                    usuario = new Usuario();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        databaseReference.child("Linha").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    linha = objSnapshot.getValue(Linha.class);
                    daoLinha.inserirLinha(new Linha(linha.getCodigo(),linha.getDescricao()));
                    linha = new Linha();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Button btLogin = (Button) findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tLogin = (TextView) findViewById(R.id.tLogin);
                TextView tSenha = (TextView) findViewById(R.id.tSenha);
                String login = tLogin.getText().toString();
                String senha = tSenha.getText().toString();
                if(verificaConexao()) {
                    if (login.equals("")) {
                        alert(getString(R.string.usuario_invalido));
                    } else if (senha.equals("")) {
                        alert(getString(R.string.senha_invalida));
                    } else {
                        usuario = daoUsuario.validaUsuario(login, senha);
                        if (usuario != null) {
                            Intent intent = new Intent(LoginActivity.this,
                                    ConfiguracaoActivity.class);
                            idUsuarioGlobal = usuario.getId();
                            startActivity(intent);
                        } else {
                            alert(getString(R.string.login_senha_invalidos));
                        }
                    }
                }else{
                    alert(getString(R.string.nao_ha_internet));

                }
            }
        });
    }
    private void alert(String s){
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    //Função para finalizar aplicação no voltar
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent i=new Intent(Intent.ACTION_MAIN);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }



}
