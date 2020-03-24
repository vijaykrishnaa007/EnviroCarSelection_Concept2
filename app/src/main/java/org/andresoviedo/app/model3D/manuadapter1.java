package org.andresoviedo.app.model3D;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.andresoviedo.dddmodel2.R;

import java.util.ArrayList;
import java.util.List;

public class manuadapter1 extends RecyclerView.Adapter<manuadapter1.ExampleViewHolder>
{
    private static CheckBox lastChecked = null;
    private static int lastCheckedPos = 0;
    private List<vehicledetails> mExampleList;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView name,date;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
        }
    }

    public manuadapter1(List<vehicledetails> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle,
                parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        vehicledetails currentItem = mExampleList.get(position);
        holder.name.setText(currentItem.getLinks().get(0).getTitle());
        holder.date.setText(currentItem.getEngineCapacity()+" CC");
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public void filterList(ArrayList<vehicledetails> filteredList) {
        mExampleList = filteredList;
        notifyDataSetChanged();
    }
}
