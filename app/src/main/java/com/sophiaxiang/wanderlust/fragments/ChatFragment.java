package com.sophiaxiang.wanderlust.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.adapters.ChatAdapter;
import com.sophiaxiang.wanderlust.databinding.FragmentChatBinding;
import com.sophiaxiang.wanderlust.models.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    public static final String TAG = "ChatFragment";
    private FragmentChatBinding binding;
    private ChatAdapter adapter;
    private List<Chat> allChats;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allChats = new ArrayList<>();
        adapter = new ChatAdapter(getContext(), allChats);
    }
}