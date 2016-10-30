package pl.edu.pwr.artificaleyeapp;

/**
 * Created by nieop on 29.04.2016.
 */

    import android.content.ClipData;
    import android.content.ContentValues;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.content.Context;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.Cursor;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.TextView;

    import java.util.LinkedList;
    import java.util.List;

    public class ParametersDB extends SQLiteOpenHelper
    {
        //Statement statement;
        public ParametersDB(Context context)
        {
            super(context, "Parameters.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "create table Parameters(" +
                            "id integer primary key autoincrement," +
                            "name varchar," +
                            "width int," +
                            "height int," +
                            "lowThreshold int," +
                            "highThreshold int);" +
                            "");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}

        public void addMode(Parameters param)
        {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", param.getName());
            values.put("width", param.getWidth());
            values.put("height", param.getHeight());
            values.put("lowThreshold", param.getLowThreshold());
            values.put("highThreshold", param.getHighThreshold());
            db.insertOrThrow("Parameters", null, values);
        }

        public void deleteMode(String idToDelete)
        {
            String[] args = {idToDelete};
            SQLiteDatabase db = getWritableDatabase();
            db.delete("Parameters", "id=?" , args);
        }

        public void deleteAllModes()
        {
            SQLiteDatabase db = getWritableDatabase();
            db.delete("Parameters", null, null);
        }

        public List<Parameters> selectParameters()
        {
            List<Parameters> parameters = new LinkedList<>();

            String[] columns = {"id", "name","width","height","lowThreshold","highThreshold"};
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query("Parameters", columns, null, null, null, null, null, null);
            int width, height, lowThreshold, highThreshold;
            String name;
            while(cursor.moveToNext())
            {
                name = cursor.getString(1);
                width = cursor.getInt(2);
                height = cursor.getInt(3);
                lowThreshold = cursor.getInt(4);
                highThreshold = cursor.getInt(5);
                parameters.add(new Parameters(name, width, height, lowThreshold, highThreshold));
            }

            return parameters;

        }

        public Parameters findById(int toFindId)
        {
            Cursor cursor;
            String[] columns = {"id", "name", "width", "height", "threshold", "refreshTime"};
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query("Parameters", columns, null, null, null, null, null);
            int id;
            while (cursor.moveToNext())
            {
                id = cursor.getInt(0);
                if (id == toFindId)
                {
                    return new Parameters(cursor.getInt(1),cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6));
                }
            }
            return new Parameters();
        }
    }

