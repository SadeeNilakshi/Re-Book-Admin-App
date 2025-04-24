package lk.javainstitute.rebook_admin.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import lk.javainstitute.rebook_admin.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        loadCategoryCount();
        loadBookCounts();
        loadUserCount();

        return root;
    }

    private void loadCategoryCount() {
        db.collection("category")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        if (documents != null) {
                            int count = documents.size();
                            binding.textViewCatCount.setText(String.valueOf(count));
                        }
                    } else {
                        binding.textViewCatCount.setText("0");
                    }
                });
    }

    private void loadBookCounts() {
        db.collection("product")
                .whereEqualTo("status", "Approved")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int approvedCount = task.getResult().size();
                        binding.textViewBookCount.setText(String.valueOf(approvedCount));
                    } else {
                        binding.textViewBookCount.setText("0");
                    }
                });

        db.collection("product")
                .whereEqualTo("status", "Removed")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int removedCount = task.getResult().size();
                        binding.textViewFlgCount.setText(String.valueOf(removedCount));
                    } else {
                        binding.textViewFlgCount.setText("0");
                    }
                });
        db.collection("product")
                .whereEqualTo("status", "sold")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int soldCount = task.getResult().size();
                        binding.textViewSold.setText(String.valueOf(soldCount));
                    } else {
                        binding.textViewSold.setText("0");
                    }
                });
    }

    private void loadUserCount() {
        db.collection("user")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int userCount = task.getResult().size();
                        binding.textViewTotalUsers.setText(String.valueOf(userCount));
                    } else {
                        binding.textViewTotalUsers.setText("0");
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
