package smartscaledatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PigDao {

    @Insert
    void insert(PigTable pigTable);

    @Query("SELECT * FROM pig_table ORDER BY priority ASC")
    LiveData<List<PigTable>> getAllPigs();


    @Query("select * from pig_table where priority= :priority")
    PigTable getPig(int priority);

    @Query("SELECT COUNT(pigNumber) FROM pig_table")
    LiveData<Integer> getItemCount();


}