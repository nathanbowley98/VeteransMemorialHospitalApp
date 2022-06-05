package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

// FirebaseRecyclerAdapter is a class provided by
// FirebaseUI. it provides functions to bind, adapt and show
// database contents in a Recycler View
public class OfferAdapter extends FirebaseRecyclerAdapter<
        Offer, OfferAdapter.offersViewholder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public OfferAdapter(@NonNull FirebaseRecyclerOptions<Offer> options) {
        super(options);
    }

    @Override
    protected void
    onBindViewHolder(@NonNull offersViewholder holder,
                     int position, @NonNull Offer model) {

        holder.employerEmail.setText(model.getEmployerEmail());
        holder.description.setText(model.getDescription());
    }

    @NonNull
    @Override
    public offersViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_offer, parent, false);
        return new OfferAdapter.offersViewholder(view);
    }


    class offersViewholder extends RecyclerView.ViewHolder {
        TextView employerEmail;
        TextView description;
        Button dismissed;

        public offersViewholder(@NonNull View itemView) {
            super(itemView);
            dismissed = (Button) itemView.findViewById(R.id.dismissOffer);
            employerEmail = itemView.findViewById(R.id.offerEmployerEmail);
            description = itemView.findViewById(R.id.jobOfferDescription);

            dismissed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }


    }
}