package smartscaledatabase;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import c0m.WildCom.Project.R;

public class PigAdapter extends RecyclerView.Adapter <PigAdapter.PigHolder>{

    private static final String TAG = "Pig Adapter";
    private List<PigTable> pigTables = new ArrayList<>();
    private OnItemListener mOnItemListener;

    public PigAdapter(OnItemListener onItemListener){
        this.mOnItemListener = onItemListener;
    }

    @NonNull
    @Override
    public PigHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pig_item, parent, false);
        return new PigHolder(itemView, mOnItemListener);
    }
    @Override
    public void onBindViewHolder(@NonNull PigHolder holder, int position) {
       try {
           PigTable currentPig = pigTables.get(position);
           holder.pigName.setText("Pig " + (currentPig.getPigNumber()));
           String string = new DecimalFormat("##.#").format(currentPig.getWeight());
           String stringr = new StringBuilder().append(string).append(" KG").toString();
           holder.weight.setText(stringr);
           holder.dateAndTime.setText(String.valueOf(currentPig.getDateAndTime()));
       }
       catch (NullPointerException e){
           Log.e(TAG, "onBindViewHolder: NullPointer" + e.getMessage());
       }

    }
    //Here how many pigs do we want to display in our recycler view
    @Override
    public int getItemCount() {
        return pigTables.size();
    }

    public void setPigTables(List<PigTable> pigTables){
        this.pigTables = pigTables;
        notifyDataSetChanged();                         //Generally not recommended to use
    }

    class PigHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;

        private TextView pigName, weight, dateAndTime;

        public PigHolder(View itemView, OnItemListener onItemListener){
            super(itemView);
            pigName = itemView.findViewById(R.id.text_view_pig_name);
            weight = itemView.findViewById(R.id.text_view_weight);
            dateAndTime = itemView.findViewById(R.id.text_view_date_1);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "OnClick" + getAdapterPosition());
            onItemListener.onItemClick(getAdapterPosition());
        }
    }
    public interface OnItemListener{
        void onItemClick(int position);
    }

}
