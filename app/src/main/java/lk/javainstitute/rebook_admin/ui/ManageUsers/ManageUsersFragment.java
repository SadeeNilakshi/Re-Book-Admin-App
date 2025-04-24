package lk.javainstitute.rebook_admin.ui.ManageUsers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.javainstitute.rebook_admin.Adapter.ManageUserAdapter;
import lk.javainstitute.rebook_admin.Database.SQLiteHelper;
import lk.javainstitute.rebook_admin.R;
import lk.javainstitute.rebook_admin.model.User;

public class ManageUsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ManageUserAdapter adapter;
    private List<User> userList;
    private FirebaseFirestore db;
    private SQLiteHelper sqLiteHelper;
    private SearchView searchViewUser;

    public ManageUsersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_users, container, false);

        recyclerView = view.findViewById(R.id.manageUserRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchViewUser = view.findViewById(R.id.searchViewUser);

        userList = new ArrayList<>();

        // Initialize SQLiteHelper here
        sqLiteHelper = new SQLiteHelper(getContext());

        adapter = new ManageUserAdapter(getContext(), userList, sqLiteHelper);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadUsersFromFirestore();
        setupSearchFunctionality();

        return view;
    }

    private void loadUsersFromFirestore() {
        CollectionReference usersRef = db.collection("user");

        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    userList.add(user);
                }
                adapter.setOriginalList(new ArrayList<>(userList));
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void setupSearchFunctionality() {
        searchViewUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return false;
            }
        });
    }

    private void filterUsers(String query) {
        if (query.isEmpty()) {
            adapter.resetList();
        } else {
            List<User> filteredList = new ArrayList<>();
            for (User user : userList) {
                if (user.getEmail() != null && user.getEmail().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }
            adapter.updateList(filteredList);
        }
    }


}
