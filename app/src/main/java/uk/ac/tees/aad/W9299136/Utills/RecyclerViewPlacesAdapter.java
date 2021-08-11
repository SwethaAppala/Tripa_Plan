package uk.ac.tees.aad.W9299136.Utills;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import uk.ac.tees.aad.W9299136.R;

public class RecyclerViewPlacesAdapter extends RecyclerView.Adapter<RecyclerViewPlacesAdapter.MyViewHolder> {
    List<NearByPlace>list;
    Context context;

    public RecyclerViewPlacesAdapter(List<NearByPlace> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_near_by_place,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerViewPlacesAdapter.MyViewHolder holder, int position) {
        holder.Icon.setImageResource(list.get(position).getIcon());
        holder.Title.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView Icon;
        TextView Title;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            Icon=itemView.findViewById(R.id.icon);
            Title=itemView.findViewById(R.id.title);

        }
    }
}
