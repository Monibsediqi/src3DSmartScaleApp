package smartscaledatabase;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;


@Database(entities = {PigTable.class}, version = 1, exportSchema = false)
abstract class PigDatabase extends RoomDatabase {

    private static PigDatabase instance;
    abstract PigDao pigDao();


    static synchronized PigDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            PigDatabase.class, "pig_database").allowMainThreadQueries().addCallback(roomCallBack).addMigrations(FROM_1_TO_2).build();
                }
        return instance;
    }


    private static final Migration FROM_1_TO_2 = new Migration(1 ,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };


    private static Callback roomCallBack = new Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };


    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private PigDao pigDao;
        private PopulateDbAsyncTask(PigDatabase db) {
            pigDao = db.pigDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
//            pigDao.insert(new PigTable("Pig 001", 90, 0, "212311"));
//            pigDao.insert(new PigTable("Pig 002", 95, 1, "123122"));
//            pigDao.insert(new PigTable("Pig 003", 90, 2, "123123"));

            return null;
        }
    }
}
