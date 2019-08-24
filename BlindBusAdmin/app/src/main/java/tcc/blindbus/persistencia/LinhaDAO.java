package tcc.blindbus.persistencia;
import android.content.ContentValues;
import android.database.Cursor;
import android.content.Context;
import java.util.ArrayList;

import tcc.blindbus.modelo.Linha;

public class LinhaDAO extends DAO {

    public LinhaDAO(Context context){
        super(context);
    }

    @Override
    protected String getTableName() {
        return "linha";
    }

    @Override
    protected String getCreateTableScript() {
        return "CREATE TABLE linha(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "codigo TEXT NOT NULL," +
                "descricao TEXT NOT NULL);";
    }

    @Override
    protected String getUpgradeTableScript() {
        return "DROP TABLE IF EXISTS linha";
    }

    public void inserirLinha(Linha linha){
        ContentValues values = new ContentValues();
        values.put("codigo",linha.getCodigo().toUpperCase());
        values.put("descricao",linha.getDescricao().toUpperCase());
        inserir(values);
    }

    public void alterarLinha(Linha linha){
        ContentValues values = new ContentValues();
        values.put("codigo",linha.getCodigo().toUpperCase());
        values.put("descricao",linha.getDescricao().toUpperCase());
        String [] args = {linha.getId()};
        getWritableDatabase().update(getTableName(),values,"id=?",args);
    }

    public ArrayList<Linha> getLista(){
        String [] columns ={"id","codigo","descricao"};
        Cursor cursor = getWritableDatabase().query(getTableName(),columns,null,null,null,null,"Codigo ASC",null);
        ArrayList<Linha> linhas = new ArrayList<Linha>();
        while (cursor.moveToNext()){
            Linha linha = new Linha();
            linha.setId(cursor.getString(0));
            linha.setCodigo(cursor.getString(1));
            linha.setDescricao(cursor.getString(2));
            linhas.add(linha);
        }
        return linhas;
    }
}
