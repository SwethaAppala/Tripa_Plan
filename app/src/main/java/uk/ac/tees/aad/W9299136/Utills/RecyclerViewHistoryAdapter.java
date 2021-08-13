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
    List<Message> list;
    Context context;
    FirebaseAuth mAuth;
    FirebaseUser mUser;


    public RecyclerViewHistoryAdapter(List<Message> list, Context context) {
        this.list = list;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerViewHistoryAdapter.MyViewHolder holder, int position) {
        if (list.get(position).getUserID().equals(mUser.getUid())) {
            holder.message2.setVisibility(View.VISIBLE);
            holder.username2.setVisibility(View.VISIBLE);

            holder.message1.setVisibility(View.GONE);
            holder.username1.setVisibility(View.GONE);

            holder.message2.setText(list.get(position).getMessage());
            holder.username2.setText(list.get(position).getUsername());

        }else
        {
            holder.message2.setVisibility(View.GONE);
            holder.username2.setVisibility(View.GONE);

            holder.message1.setVisibility(View.VISIBLE);
            holder.username1.setVisibility(View.VISIBLE);


            holder.message1.setText(list.get(position).getMessage());
            holder.username1.setText(list.get(position).getUsername());

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username1, username2;
        TextView message1, message2;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            username1 = itemView.findViewById(R.id.username1);
            username2 = itemView.findViewById(R.id.username2);
            message1 = itemView.findViewById(R.id.message1);
            message2 = itemView.findViewById(R.id.message2);

        }
    }
}
