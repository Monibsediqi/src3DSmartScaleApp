package smartscaledatabase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class PigViewModel extends AndroidViewModel {
    private PigRepository pigRepository;
    private LiveData<List<PigTable>> pigInfoRecycle;




    public PigViewModel(@NonNull Application application) {
        super(application);

        pigRepository = new PigRepository(application);
        pigInfoRecycle = pigRepository.getAllPigs();
    }


    public void insert(PigTable pigTable){pigRepository.insert(pigTable); }
    public LiveData<List<PigTable>> getAllPigs(){ return pigInfoRecycle; }
    public PigTable getPig(int priority) {
        return pigRepository.getPig(priority);
    }
    public LiveData<Integer> getItemCuount(){return pigRepository.getItemCount();}



}
