package smartscaledatabase;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "pig_table")
public class PigTable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "priority")
    @NonNull
    private int priority;

    @ColumnInfo(name = "pig_weight")
    @NonNull
    private double weight;

    @ColumnInfo(name = "pigName")
    @NonNull
    private String pigName;

    private int dateAndTime;


    public PigTable(String pigName, double weight, int priority, int dateAndTime) {
        this.pigName = pigName;
        this.weight = weight;
        this.priority = priority;
        this.dateAndTime = dateAndTime;
    }
    public int getDateAndTime(){
        return this.dateAndTime;
    }
    public int getPriority() { return this.priority; }

    public double getWeight() { return this.weight; }

    public String getPigName() { return this.pigName; }

    public void setId(int id) { this.id = id; }
    public int getId(){return this.id; }
}
