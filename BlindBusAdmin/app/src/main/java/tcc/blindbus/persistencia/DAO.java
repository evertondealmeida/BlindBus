package tcc.blindbus.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public abstract class DAO extends SQLiteOpenHelper {
    public static final String DATABASE ="bdblindbus";
    public static final int VERSION = 7;
    public DAO(Context context) {
        super(context, DATABASE,null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getUpgradeTableScript());
        db.execSQL(getCreateTableScript());
    }
    protected void inserir(ContentValues values) {
        getWritableDatabase().insert(getTableName(),null,values);
    }
    protected abstract String getTableName();
    protected abstract String getCreateTableScript();
    protected abstract String getUpgradeTableScript();
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(getUpgradeTableScript());
    }
}
