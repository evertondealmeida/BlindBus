package tcc.blindbus.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import tcc.blindbus.R;
import tcc.blindbus.modelo.Viagem;

public class TransmitirSinalActivity extends MainActivity {
    private Animation animation;
    private Button btPiscar;
    private boolean travaFuncao = false;
    private ConexaoInternet conexaoInternet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmitir_sinal);
        Button btVoltar = (Button) findViewById(R.id.btVoltar);
        Button btParar = (Button) findViewById(R.id.btParar);
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        conexaoInternet = new ConexaoInternet();
        registerReceiver(conexaoInternet, intentFilter);
        inicializarFirebase(TransmitirSinalActivity.this);
        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                travaFuncao = true;
                finish();
            }
        });
        btParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(animation!=null) {
                    try {
                        new Thread().sleep(30000);
                        btPiscar.clearAnimation();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        lerViagem();


    }
    public void lerViagem() {
        lerConexao();
        databaseReference.child("Viagem/0x000005000453").addValueEventListener(new
            ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Viagem viagemRecebida = dataSnapshot.getValue(Viagem.class);
                if(viagemRecebida!=null && !travaFuncao) {
                    if(viagemRecebida.getClienteEncontrado().equals("SIM")) {
                        alert("Deficiente identificado");
                        atualizaViagem(viagemRecebida);
                        playSound();
                        piscar();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void atualizaViagem(Viagem viagem) {
        viagem.setClienteEncontrado("NAO");
       // databaseReference.child("Viagem/0x000005000453").setValue(null);
        databaseReference.child("Viagem").child("0x000005000453").setValue(viagem);
    }
    private void alert(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    private void playSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.deficienteidentificado);
        mediaPlayer.start();
    }
    private void piscar(){
        animation = new AlphaAnimation(1, 0); // Altera alpha de visível a invisível
        animation.setDuration(500); // duração - meio segundo
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); // Repetir infinitamente
        animation.setRepeatMode(Animation.REVERSE); //Inverte a animação no final para que o botão vá desaparecendo
        btPiscar = (Button) findViewById(R.id.btPiscar);
        btPiscar.startAnimation(animation);

    }
    private void lerConexao() {
        conexaoInternet.addOnMudarEstadoConexao(new ConexaoInternet.IOnMudarEstadoConexao() {
            @Override
            public void onMudar(ConexaoInternet.TipoConexao tipoConexao) {
                if (tipoConexao == ConexaoInternet.TipoConexao.TIPO_NAO_CONECTADO) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TransmitirSinalActivity.this);
                    builder.setTitle("Sem conexão");
                    builder.setMessage(getString(R.string.nao_ha_internet));
                    builder.setCancelable(true);
                    builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }
            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent i=new Intent(Intent.ACTION_MAIN);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }
}
