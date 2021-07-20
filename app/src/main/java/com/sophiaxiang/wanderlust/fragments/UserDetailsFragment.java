package com.sophiaxiang.wanderlust.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.ChatDetailsActivity;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentProfileBinding;
import com.sophiaxiang.wanderlust.databinding.FragmentUserDetailsBinding;
import com.sophiaxiang.wanderlust.models.Chat;
import com.sophiaxiang.wanderlust.models.User;
import com.sophiaxiang.wanderlust.models.Vacation;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsFragment extends Fragment {

    public static final String TAG = "UserDetailsFragment";
    private FragmentUserDetailsBinding binding;
    private DatabaseReference mDatabase;
    private DatabaseReference currentUserNodeReference;
    private DatabaseReference vacationDetailsReference;
    private User user;
    private Vacation vacation;
    private List<String> userImages;
    private String currentUserId;
    private int position = 0;
    private String chatId;

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_details, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("user");
        vacation = user.getVacation();

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUserNodeReference = mDatabase.child("users").child(user.getUserId());
        vacationDetailsReference = mDatabase.child("users").child(user.getUserId()).child("vacation");

        userImages = new ArrayList<>();
        populateImageList();

        setUpButtons();
        populateProfileViews();
        populateVacationViews();
        setProfileInfoListener();
        setVacationInfoListener();
    }

    private void setUpButtons() {
        binding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpNewChat();
                goChatDetails();
            }
        });

        binding.btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position>0)
                    position--;
                Glide.with(binding.ivPhoto.getContext())
                        .load(Uri.parse(userImages.get(position)))
                        .into(binding.ivPhoto);
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position<userImages.size()-1)
                    position++;
                Glide.with(binding.ivPhoto.getContext())
                        .load(Uri.parse(userImages.get(position)))
                        .into(binding.ivPhoto);
            }
        });
    }


    private void setVacationInfoListener() {
        ValueEventListener vacationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                vacation = dataSnapshot.getValue(Vacation.class);
                populateVacationViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "load Vacation:onCancelled", databaseError.toException());
            }
        };
        vacationDetailsReference.addValueEventListener(vacationListener);
    }


    private void setProfileInfoListener() {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);
                populateProfileViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "load User profile:onCancelled", databaseError.toException());
            }
        };
        currentUserNodeReference.addValueEventListener(userListener);
    }

    private void populateProfileViews() {
        binding.tvNameAge.setText(user.getName() + ", " + user.getAge());
        binding.tvBio.setText(user.getBio());
        binding.tvFrom.setText(user.getFrom());
        binding.tvAdventureLevel.setText(user.getAdventureLevel());
        populateImageList();
    }


    private void populateVacationViews() {
        if (!vacation.getDestination().equals("")) {
            binding.tvLocationDate.setText(vacation.getDestination() + "   |   " + vacation.getStartDate() + " - " + vacation.getEndDate());
        }
        binding.tvVacationNotes.setText(vacation.getNotes());
    }


    private void populateImageList() {
        userImages.clear();
        userImages.add(user.getImage1());
        userImages.add(user.getImage2());
        userImages.add(user.getImage3());
        populateImageView();
    }


    private void populateImageView() {
        if (user.getImage1() == null) {
            binding.ivPhoto.setImageResource(R.drawable.add_image);
        }
        else {
            position = 0;
            Glide.with(binding.ivPhoto.getContext())
                    .load(Uri.parse(userImages.get(position)))
                    .into(binding.ivPhoto);
        }
    }

    private void setUpNewChat() {
        if (currentUserId.compareTo(user.getUserId()) < 0)
            chatId = currentUserId + user.getUserId();
        else chatId =  user.getUserId() + currentUserId;

        Chat chat = new Chat(chatId, user.getName(), user.getUserId(), currentUserId, Long.MAX_VALUE);
        mDatabase.child("userChatLists").child(currentUserId).child(chatId).setValue(chat);
        mDatabase.child("userChatLists").child(user.getUserId()).child(chatId).setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                goChatDetails();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "update userChatLists onFailure", e);
            }
        });
    }


    private void goChatDetails() {
            Intent intent = new Intent(getContext(), ChatDetailsActivity.class);
            intent.putExtra("current user id", currentUserId);
            intent.putExtra("other user id", user.getUserId());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            getActivity().startActivity(intent); 
    }
}