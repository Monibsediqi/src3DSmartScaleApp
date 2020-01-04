package smartscaledatabase;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "pig_table")
public class PigTable {


    @PrimaryKey(autoGenerate = true)
    private int id =0;

    @ColumnInfo(name = "priority")
    @NonNull
    private int priority ;

    @ColumnInfo(name = "pig_weight")
    @NonNull
    private double weight;

    @ColumnInfo(name = "pigNumber")
    @NonNull
    private int pigNumber;

    private String dateAndTime;


    public PigTable(int pigNumber, double weight, int priority, String dateAndTime) {
        this.pigNumber = pigNumber;
        this.weight = weight;
        this.priority = priority;
        this.dateAndTime = dateAndTime;
    }
    public String getDateAndTime(){
        return this.dateAndTime;
    }
    public int getPriority() { return this.priority; }

    public double getWeight() { return this.weight; }

    public int getPigNumber() { return this.pigNumber; }

    public int getId(){return this.id;}
    public void  setId(int id) {this.id = id;}
}
