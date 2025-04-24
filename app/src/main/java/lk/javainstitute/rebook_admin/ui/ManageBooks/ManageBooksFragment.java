package lk.javainstitute.rebook_admin.ui.ManageBooks;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import lk.javainstitute.rebook_admin.Adapter.ManageBooksAdapter;
import lk.javainstitute.rebook_admin.R;
import lk.javainstitute.rebook_admin.model.Product;

public class ManageBooksFragment extends Fragment {

    private RecyclerView recyclerView;
    private ManageBooksAdapter adapter;
    private List<Product> bookList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_books, container, false);

        recyclerView = view.findViewById(R.id.recyclerBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookList = new ArrayList<>();
        adapter = new ManageBooksAdapter(getContext(), bookList);
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        fetchBooks();
        return view;
    }

    private void fetchBooks() {
        CollectionReference booksRef = db.collection("product");
        booksRef.whereEqualTo("status", "Pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product book = document.toObject(Product.class);
                            book.setDocumentId(document.getId());
                            bookList.add(book);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error loading books", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
