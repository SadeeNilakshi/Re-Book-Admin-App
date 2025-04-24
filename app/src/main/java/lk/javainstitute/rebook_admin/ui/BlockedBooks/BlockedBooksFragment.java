package lk.javainstitute.rebook_admin.ui.BlockedBooks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.javainstitute.rebook_admin.Adapter.FlagBookAdapter;
import lk.javainstitute.rebook_admin.R;
import lk.javainstitute.rebook_admin.model.Product;

public class BlockedBooksFragment extends Fragment {

    private RecyclerView recyclerView;
    private FlagBookAdapter adapter;
    private List<Product> bookList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blocked_books, container, false);

        recyclerView = view.findViewById(R.id.recyclerFlaggedContent);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookList = new ArrayList<>();
        adapter = new FlagBookAdapter(requireContext(), bookList);
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        fetchBooks();
        return view;
    }

    private void fetchBooks() {
        db.collection("product")
                .whereEqualTo("status", "Removed")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (value != null) {
                            bookList.clear();
                            for (QueryDocumentSnapshot document : value) {
                                Product book = document.toObject(Product.class);
                                book.setDocumentId(document.getId());
                                bookList.add(book);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
