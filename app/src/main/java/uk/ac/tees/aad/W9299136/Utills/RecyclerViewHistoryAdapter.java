package uk.ac.tees.aad.W9299136.Utills;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import uk.ac.tees.aad.W9299136.R;

public class RecyclerViewHistoryAdapter extends RecyclerView.Adapter<RecyclerViewHistoryAdapter.MyViewHolder> {
    List<LoginHistory> list;
    Context context;



    public RecyclerViewHistoryAdapter(List<LoginHistory> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerViewHistoryAdapter.MyViewHolder holder, int position) {
      holder.date.setText(list.get(position).getDate());
      holder.email.setText(list.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView email, date;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.email);
            date = itemView.findViewById(R.id.date);

        }
    }
}
