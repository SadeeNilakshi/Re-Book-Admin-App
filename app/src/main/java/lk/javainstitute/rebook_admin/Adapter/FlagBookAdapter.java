package lk.javainstitute.rebook_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import lk.javainstitute.rebook_admin.R;
import lk.javainstitute.rebook_admin.model.Product;

public class FlagBookAdapter extends RecyclerView.Adapter<FlagBookAdapter.ViewHolder> {
    private Context context;
    private List<Product> bookList;
    private FirebaseFirestore db;

    public FlagBookAdapter(Context context, List<Product> bookList) {
        this.context = context;
        this.bookList = bookList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.flagged_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = bookList.get(position);

        holder.txtFlagName.setText(product.getTitle());
        holder.txtFlagAuthor.setText(product.getAuthor());
        holder.txtFlagPrice.setText("Rs. " + product.getPrice());
        holder.txtFlagCategory.setText(product.getCategory());

        Glide.with(context).load(product.getImageUrl()).into(holder.imgFlag);

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFlag;
        TextView txtFlagName, txtFlagAuthor, txtFlagPrice, txtFlagCategory;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFlag = itemView.findViewById(R.id.imageViewFlag);
            txtFlagName = itemView.findViewById(R.id.textFlagTitle);
            txtFlagAuthor = itemView.findViewById(R.id.textFlagAuthor);
            txtFlagPrice = itemView.findViewById(R.id.textFlagPrice);
            txtFlagCategory = itemView.findViewById(R.id.textFlagCategory);
        }
    }

}
