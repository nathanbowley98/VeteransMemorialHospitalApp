package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Colleague;
import com.example.myapplication.Job;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class viewColleagueAdapter extends RecyclerView.Adapter<viewColleagueAdapter.ColleaguesViewholder> {

    private static ArrayList<Colleague> options;

    public viewColleagueAdapter(ArrayList<Colleague> options) {
        this.options = options;
    }

    public static int getLength(){
        return options.size();
    }

    public ColleaguesViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_person_info, parent, false);
        ColleaguesViewholder colleaguesViewholder = new viewColleagueAdapter.ColleaguesViewholder(view);

        return colleaguesViewholder;
    }


    @Override
    public void onBindViewHolder(ColleaguesViewholder holder, int position) {
        Colleague colleague = options.get(position);
        options.add(colleague);

        holder.review_person_email.setText("Email: " + colleague.getEmail());
        holder.review_person_name.setText("Name: " + colleague.getName());
        Context context = holder.itemView.getContext();

        FirebaseDatabase firebaseDB;
        DatabaseReference firebaseDBRef;

        firebaseDB = FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL);
        firebaseDBRef = firebaseDB.getReference("users");

        firebaseDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot: snapshot.getChildren()){
                    User um = datasnapshot.getValue(User.class);

                    if ((um.getFirstName() + " " + um.getLastName()).equalsIgnoreCase(colleague.getName())){
                        holder.review_person_rating.setText("Rating: " + Double.toString(um.getAverage_rating()));
                        holder.review_person_numReviews.setText("Number of Reviews: " + Integer.toString(um.getNum_reviews()));
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.review_person_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAbsoluteAdapterPosition();
                Session.reviewing_user(options.get(pos).getEmail());
                Intent intent =  new Intent(context, reviewView.class);
                context.startActivity(intent);
            }
        });


    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return this.options.size();
    }

    public static class ColleaguesViewholder extends RecyclerView.ViewHolder {

        public TextView review_person_name;
        public TextView review_person_email;
        public TextView review_person_rating;
        public TextView review_person_numReviews;
        public Button review_person_button;

        public ColleaguesViewholder(@NotNull View itemView) {
            super(itemView);

            review_person_email = itemView.findViewById(R.id.review_person_email);
            review_person_name = itemView.findViewById(R.id.review_person_name);
            review_person_button = itemView.findViewById(R.id.review_person_button);
            review_person_rating = itemView.findViewById(R.id.review_person_rating);
            review_person_numReviews = itemView.findViewById(R.id.review_person_numReviews);
        }
    }

}