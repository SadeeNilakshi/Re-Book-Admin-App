package lk.javainstitute.rebook_admin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import lk.javainstitute.rebook_admin.Database.SQLiteHelper;
import lk.javainstitute.rebook_admin.R;
import lk.javainstitute.rebook_admin.model.User;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.UserViewHolder> {
    private Context context;
    private List<User> userList;

    private List<User> originalList;
    private SQLiteHelper dbHelper;

    public ManageUserAdapter(Context context, List<User> userList, SQLiteHelper dbHelper) {
        this.context = context;
        this.userList = userList;
        this.originalList = new ArrayList<>(userList);
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textUserName.setText(user.getFname() + " " + user.getLname());
        holder.textUserEmail.setText(user.getEmail());
        holder.textUserMobile.setText(user.getMobile());

        // Load profile picture from SQLite
        String profileImagePath = getProfileImageFromSQLite(user.getEmail());
        if (profileImagePath != null && !profileImagePath.isEmpty()) {
            Glide.with(context)
                    .load(profileImagePath)
                    .placeholder(R.drawable.account) // Show default image while loading
                    .error(R.drawable.account) // Show default image if loading fails
                    .into(holder.imageView3);
        } else {
            holder.imageView3.setImageResource(R.drawable.account); // Default image
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView3;
        TextView textUserName, textUserEmail, textUserMobile;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView3 = itemView.findViewById(R.id.imageView3);
            textUserName = itemView.findViewById(R.id.textUserName);
            textUserEmail = itemView.findViewById(R.id.textUserEmail);
            textUserMobile = itemView.findViewById(R.id.textUserMobile);

        }
    }

    @SuppressLint("Range")
    private String getProfileImageFromSQLite(String email) {
        String imagePath = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT profile_img FROM user WHERE email = ?", new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                imagePath = cursor.getString(cursor.getColumnIndex("profile_img"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return imagePath;
    }

    public void updateList(List<User> newList) {
        userList.clear();
        userList.addAll(newList);
        notifyDataSetChanged();
    }

    public void resetList() {
        userList.clear();
        userList.addAll(originalList);
        notifyDataSetChanged();
    }

    public void setOriginalList(List<User> newList) {
        originalList.clear();
        originalList.addAll(newList);
    }


}
