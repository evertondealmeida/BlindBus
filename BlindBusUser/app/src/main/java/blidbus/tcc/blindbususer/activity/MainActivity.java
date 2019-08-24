package blidbus.tcc.blindbususer.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import java.util.ArrayList;
import java.util.Collection;
import blidbus.tcc.blindbususer.R;
import blidbus.tcc.blindbususer.modelo.Viagem;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, BeaconConsumer,RangeNotifier {
    protected final String TAG = MainActivity.this.getClass().getSimpleName();
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final long DEFAULT_SCAN_PERIOD_MS = 1000;
    private static final String ALL_BEACONS_REGION = "AllBeaconsRegion";
    String instanceBeacon = "";
    BeaconManager mBeaconManager;// Para interagir com beacons de uma entidade
    String verificaRepeticaoMensagem = "";
    private Region mRegion; // Representa os critérios dos campos com os quais procurar beacons
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Viagem viagem = new Viagem();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getStartButton().setOnClickListener(this);
        getStopButton().setOnClickListener(this);
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        //Corrigir um protocolo de beacon, Eddystone neste caso
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        ArrayList<Identifier> identifiers = new ArrayList<>();
        mRegion = new Region(ALL_BEACONS_REGION, identifiers);
        inicializarFirebase(MainActivity.this);
    }
    //Botoes procurar e parar
    @Override
    public void onClick(View view) {
        if (view.equals(findViewById(R.id.btProcurar))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Se as permissões de localização ainda não tiverem sido concedidas, solicite-as
                if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    askForLocationPermissions();
                } else { // Permissão de localização concedida
                    prepareDetection();
                }
            } else { // Versão de Android < 6
                prepareDetection();
            }
        } else if (view.equals(findViewById(R.id.btParar))) {
            stopDetectingBeacons();
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // Desativar bluetooth
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
        }
    }
    //Ativar localização e bluetooth para começar a detectar balizas
    private void prepareDetection() {
        if (!isLocationEnabled()) {
            askToTurnOnLocation();
        } else { // Localização ativada, vamos checar o bluetooth
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                alert(getString(R.string.nao_suporta_bluetooth_msg));
            } else if (mBluetoothAdapter.isEnabled()) {
                startDetectingBeacons();
            } else {
                // Pedir ao usuario que ative o bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }
    //Ativar Bluetooth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            // Usuário ativou o bluetooth
            if (resultCode == RESULT_OK) {
                startDetectingBeacons();
            } else if (resultCode == RESULT_CANCELED) { // Usuário se recusa a ativar o bluetooth
                alert(getString(R.string.bluetooth_desligado_msg));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //Comece a detectar as balizas, escondendo ou exibindo os botões correspondentes
    private void startDetectingBeacons() {
        // Definir um período de verificação
        mBeaconManager.setForegroundScanPeriod(DEFAULT_SCAN_PERIOD_MS);
        // Link para o serviço de beacons. Obter um retorno de chamada quando estiver pronto para ser usado
        mBeaconManager.bind(this);
        // Desativar o botão Iniciar
        getStartButton().setEnabled(false);
        getStartButton().setAlpha(.5f);
        // Ativar o botão Parar
        getStopButton().setEnabled(true);
        getStopButton().setAlpha(1);
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            // Comece a procurar por beacons que correspondam ao objeto Last Region, incluindo
            // atualiza a distância estimada
            mBeaconManager.startRangingBeaconsInRegion(mRegion);
            alert(getString(R.string.ligando_busca_de_beacons));
        } catch (RemoteException e) {
            Log.d(TAG, "Ocorreu um erro ao começar a procurar ônibus " + e.getMessage());
        }
        mBeaconManager.addRangeNotifier(this);
    }

    public void lerViagem() {
       databaseReference.child("Viagem/"+instanceBeacon).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viagem = new Viagem();
                Viagem viagemRecebida = dataSnapshot.getValue(Viagem.class);
                if(viagemRecebida!=null) {
                    viagem.setInstanceBeacon(viagemRecebida.getInstanceBeacon());
                    viagem.setNameSpace(viagemRecebida.getNameSpace());
                    viagem.setLinha(viagemRecebida.getLinha());
                    viagem.setSentido(viagemRecebida.getSentido());
                    viagem.setInformacoesUsuario(viagemRecebida.getInformacoesUsuario());
                    viagem.setClienteEncontrado(viagemRecebida.getClienteEncontrado());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void atualizaViagem(Viagem viagem) {
        viagem.setClienteEncontrado("SIM");
        databaseReference.child("Viagem/"+viagem.getInstanceBeacon()).setValue(null);
        databaseReference.child("Viagem").child(viagem.getInstanceBeacon()).setValue(viagem);
    }
    //O método chamou cada DEFAULT_SCAN_PERIOD_MS segundos com os beacons detectados durante esse período
     /* System.out.println("informações 2: "+beacon.toString());
            System.out.println("informações 5: "+beacon.getDistance());
            System.out.println("informações namespace: "+beacon.getId1());
            System.out.println("informações instance: "+beacon.getId2().toString());
            System.out.println("informações 9: "+beacon.getRunningAverageRssi());
            System.out.println("informações 10: "+beacon.getTxPower());
            System.out.println("informações 11: "+beacon.getServiceUuid());*/
    @SuppressLint("StringFormatInvalid")
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        for (Beacon beacon : beacons) {
            instanceBeacon = beacon.getId2().toString();
            lerViagem();
            if(instanceBeacon.equals(viagem.getInstanceBeacon())){
                atualizaViagem(viagem);
                if(verificaRepeticaoMensagem.equals(viagem.getLinha()+" "+viagem.getSentido())){
                    try {
                        new Thread().sleep(10000);
                        alert(viagem.getLinha()+" "+viagem.getSentido());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    verificaRepeticaoMensagem = viagem.getLinha()+" "+viagem.getSentido();
                    alert(viagem.getLinha()+" "+viagem.getSentido());
                }
                viagem = new Viagem();
            }
        }
    }

    private void stopDetectingBeacons() {
        try {
            mBeaconManager.stopMonitoringBeaconsInRegion(mRegion);
            alert(getString(R.string.desligando_busca_de_beacons));
        } catch (RemoteException e) {
            Log.d(TAG, "Ocorreu um erro ao parar de procurar ônibus " + e.getMessage());
        }
        mBeaconManager.removeAllRangeNotifiers();
        // Desvincular o serviço de beacons
        mBeaconManager.unbind(this);
        // Ativar o botão Iniciar
        getStartButton().setEnabled(true);
        getStartButton().setAlpha(1);
        // Desativar botão de parar
        getStopButton().setEnabled(false);
        getStopButton().setAlpha(.5f);
    }


    //Verifique a permissão de localização para Android> = M
    private void askForLocationPermissions() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.localizacao_negada);
        builder.setMessage(R.string.conceda_acesso_da_localizacao);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onDismiss(DialogInterface dialog) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepareDetection();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.funcionalidade_limitada);
                    builder.setMessage(getString(R.string.localizacao_nao_concedida) +
                            getString(R.string.nao_procurar_beacons));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }

    //Verifique se o local está ativado
    //@return true se o local estiver ativado, false de outra forma
    private boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean networkLocationEnabled = false;
        boolean gpsLocationEnabled = false;
        try {
            networkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            gpsLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception ex) {
            Log.d(TAG, "Exceção ao obter informações de localização");
        }
        return networkLocationEnabled || gpsLocationEnabled;
    }


    //Abrir as configurações de localização para que o usuário possa ativar os serviços de localização
    private void askToTurnOnLocation() {
        // Notificar al usuario
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.localizacao_desativada);
        dialog.setPositiveButton(R.string.configuracao_de_localizacao, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });
        dialog.show();
    }

    private Button getStartButton() {
        return (Button) findViewById(R.id.btProcurar);
    }

    private Button getStopButton() {
        return (Button) findViewById(R.id.btParar);
    }

     //Mostrar mensaje
     //@param message mensagem para ensinar
    private void alert(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.removeAllRangeNotifiers();
        mBeaconManager.unbind(this);
    }

    protected void inicializarFirebase(Context context) {
        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}
