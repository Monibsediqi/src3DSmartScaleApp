package smartscaledatabase;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

class PigRepository {

    private PigDatabase pigDatabase;
    private PigDao pigDao;
    private LiveData<List<PigTable>> pigInfoRecycle;

    PigRepository(Application application) {
        pigDatabase = PigDatabase.getInstance(application);
        pigDao = pigDatabase.pigDao();
        pigInfoRecycle = pigDao.getAllPigs();
    }

    LiveData<List<PigTable>> getAllPigs() {
        return pigInfoRecycle;
    }

    PigTable getPig(int priority) {
        return pigDatabase.pigDao().getPig(priority);
    }
    public LiveData<Integer> getItemCount (){return pigDao.getItemCount();}


    void insert(PigTable pigTable) {
        new InsertPigTableAsyncTask(pigDao).execute(pigTable);
    }


    private static class InsertPigTableAsyncTask extends AsyncTask<PigTable, Void, Void> {
        private PigDao pigDao;

        private InsertPigTableAsyncTask(PigDao pigDao) {
            this.pigDao = pigDao;
        }

        @Override
        protected Void doInBackground(PigTable... pigTables) {
            pigDao.insert(pigTables[0]);
            return null;
        }

    }
}
