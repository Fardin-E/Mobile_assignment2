package com.example.mobile_assignment2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private Context context;
    private List<AddressModel> addressList;
    private DatabaseHelper dbHelper;

    public AddressAdapter(Context context, List<AddressModel> addressList) {
        this.context = context;
        this.addressList = addressList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        AddressModel address = addressList.get(position);

        // Set the address in the address TextView
        holder.textViewAddress.setText(address.getAddress());

        // Set the latitude in the latitude TextView
        holder.textViewLatitude.setText(String.valueOf(address.getLatitude()));

        // Set the longitude in the longitude TextView
        holder.textViewLongitude.setText(String.valueOf(address.getLongitude()));

        holder.itemView.setOnLongClickListener(v -> {
            // Use holder.getAdapterPosition() to retrieve the position
            int adapterPosition = holder.getAdapterPosition();

            // Handle long click here, e.g., show a dialog to confirm deletion
            showDialogForDeletion(adapterPosition);
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAddress; // View for displaying the address
        TextView textViewLatitude; // View for displaying the latitude
        TextView textViewLongitude; // View for displaying the longitude

        public AddressViewHolder(View itemView) {
            super(itemView);

            // Initialize views by finding them in the itemView
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewLatitude = itemView.findViewById(R.id.textViewLatitude);
            textViewLongitude = itemView.findViewById(R.id.textViewLongitude);
        }
    }

    public void updateData(List<AddressModel> addressList) {
        this.addressList = addressList;
        notifyDataSetChanged();
    }

    private void showDialogForDeletion(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Do you want to delete this entry?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove the item from the list and database
                deleteItem(position);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void deleteItem(int position) {
        // Remove the item from the list
        addressList.remove(position);
        notifyItemRemoved(position);

        // Delete the item from the database using dbHelper.deleteLocation(...)
        AddressModel addressModel = addressList.get(position);
        dbHelper.deleteLocation(addressModel.getAddress());

        // Notify that the data set has changed
        notifyItemRangeChanged(position, addressList.size());
    }

    public void deleteAllEntries() {
        dbHelper.deleteAllDatabaseEntries();
        addressList.clear(); // Clear the list of addresses
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }
}
