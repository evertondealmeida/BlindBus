package tcc.blindbus.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tcc.blindbus.modelo.Linha;
import tcc.blindbus.modelo.Usuario;
import tcc.blindbus.modelo.Viagem;
import tcc.blindbus.persistencia.LinhaDAO;
import tcc.blindbus.persistencia.UsuarioDAO;

public abstract class MainActivity extends AppCompatActivity {
    protected UsuarioDAO daoUsuario;
    protected LinhaDAO daoLinha;
    protected Usuario usuario;
    protected Linha linha;
    protected static String idUsuarioGlobal;
    protected FirebaseDatabase firebaseDatabase;
    protected DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    protected void inicializarFirebase(Context context) {
        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
    protected boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

}
