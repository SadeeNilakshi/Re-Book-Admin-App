package lk.javainstitute.rebook_admin.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;
import java.util.HashMap;
import java.util.Map;

import lk.javainstitute.rebook_admin.Mail.SendMail;
import lk.javainstitute.rebook_admin.R;
import lk.javainstitute.rebook_admin.model.Product;


public class ManageBooksAdapter extends RecyclerView.Adapter<ManageBooksAdapter.ViewHolder> {
    private Context context;
    private List<Product> bookList;
    private FirebaseFirestore db;

    public ManageBooksAdapter(Context context, List<Product> bookList) {
        this.context = context;
        this.bookList = bookList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = bookList.get(position);

        holder.txtMProductName.setText(product.getTitle());
        holder.txtMAuthor.setText(product.getAuthor());
        holder.txtMPrice.setText("Rs. " + product.getPrice());
        holder.txtMCategory.setText(product.getCategory());

        Glide.with(context).load(product.getImageUrl()).into(holder.imgMProduct);

        if ("Removed".equals(product.getStatus())) {
            holder.btnBlock.setText("Removed");
            holder.btnBlock.setEnabled(false);
            holder.buttonApprove.setEnabled(true);
        } else if ("Approved".equals(product.getStatus())) {
            holder.buttonApprove.setText("Approved");
            holder.buttonApprove.setEnabled(false);
            holder.btnBlock.setEnabled(true);
        } else {
            holder.btnBlock.setText("Remove");
            holder.btnBlock.setEnabled(true);
            holder.buttonApprove.setText("Approve");
            holder.buttonApprove.setEnabled(true);
        }

        holder.btnBlock.setOnClickListener(v -> flagBook(product, holder.btnBlock, holder.buttonApprove));
        holder.buttonApprove.setOnClickListener(v -> approveBook(product, holder.buttonApprove, holder.btnBlock));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMProduct;
        TextView txtMProductName, txtMAuthor, txtMPrice, txtMCategory;
        Button btnBlock, buttonApprove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMProduct = itemView.findViewById(R.id.imgMbook);
            txtMProductName = itemView.findViewById(R.id.txtMBookName);
            txtMAuthor = itemView.findViewById(R.id.txtMbookAuthor);
            txtMPrice = itemView.findViewById(R.id.textMBookPrice);
            txtMCategory = itemView.findViewById(R.id.textCategory);
            btnBlock = itemView.findViewById(R.id.buttonBlock);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
        }
    }

    private void flagBook(Product product, Button btnBlock, Button btnApprove) {
        String documentId = product.getDocumentId();

        db.collection("product").document(documentId)
                .update("status", "Removed")
                .addOnSuccessListener(aVoid -> {
                    // Move to flagged_books collection
                    Map<String, Object> flaggedBook = new HashMap<>();
                    flaggedBook.put("productId", documentId);
                    flaggedBook.put("timestamp", System.currentTimeMillis());

                    db.collection("flagged_books").document(documentId)
                            .set(flaggedBook)
                            .addOnSuccessListener(aVoid1 -> {
                                btnBlock.setText("Removed");
                                btnBlock.setEnabled(false);
                                btnApprove.setEnabled(true);
                                Toast.makeText(context, "Book removed successfully", Toast.LENGTH_SHORT).show();
                                sendEmailToSeller(product, "removed");
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Failed to move book to flagged list", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to update book status", Toast.LENGTH_SHORT).show());
    }

    private void approveBook(Product product, Button btnApprove, Button btnBlock) {
        String documentId = product.getDocumentId();

        db.collection("product").document(documentId)
                .update("status", "Approved")
                .addOnSuccessListener(aVoid -> {
                    btnApprove.setText("Approved");
                    btnApprove.setEnabled(false);
                    btnBlock.setEnabled(true);
                    Toast.makeText(context, "Book approved successfully", Toast.LENGTH_SHORT).show();
                    sendEmailToSeller(product, "approved");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to approve book", Toast.LENGTH_SHORT).show());
    }

    private void sendEmailToSeller(Product product, String action) {
        String documentId = product.getDocumentId();

        db.collection("product").document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String sellerEmail = documentSnapshot.getString("email");
                        if (sellerEmail != null && !sellerEmail.isEmpty()) {
                            String subject, message;

                            if ("approved".equals(action)) {
                                subject = "Your book has been Approved!";
                                message = "Dear Seller,\n\nYour book '" + product.getTitle() +
                                        "' has been approved and is now available for buyers.\n\nBest Regards,\nReBook Admin";
                            } else {
                                subject = "Your book has been Removed!";
                                message = "Dear Seller,\n\nYour book '" + product.getTitle() +
                                        "' has been removed due to a policy violation or other reasons.\n\nBest Regards,\nReBook Admin";
                            }

                            try {
                                SendMail.sendEmail(context, sellerEmail, subject, message);
                            } catch (Exception e) {
                                Log.e("ManageBooksAdapter", "Error sending email: " + e.getMessage());
                                Toast.makeText(context, "Failed to send email", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("ManageBooksAdapter", "Seller email is null or empty");
                            Toast.makeText(context, "Seller email not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("ManageBooksAdapter", "Document does not exist");
                        Toast.makeText(context, "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ManageBooksAdapter", "Failed to fetch seller email: " + e.getMessage());
                    Toast.makeText(context, "Failed to fetch seller email", Toast.LENGTH_SHORT).show();
                });
    }
}
