package com.example.salesapp.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.salesapp.R;
import com.example.salesapp.activity.DetailBookActivity;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.model.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookAdapterViewHolder> {

    private List<Book> dataModelArrayList;
    private Context context;

    public BookAdapter(Context context, List<Book> dataModelArrayList) {
        this.context = context;
        this.dataModelArrayList = dataModelArrayList;
    }

    @NonNull
    @Override
    public BookAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.activity_book_adapter, viewGroup, false);
        return new BookAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapterViewHolder bookViewHolder, int i) {
        Book book = dataModelArrayList.get(i);

        Picasso.get().load(RetrofitBuilder.BASE_URL_IMAGES + book.getImage()).into(bookViewHolder.imgBook);
        bookViewHolder.titleBook.setText(book.getTitle());

        bookViewHolder.btnReadNow.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailBookActivity.class);
            intent.putExtra("title", book.getTitle());
            intent.putExtra("long_desc", book.getDescription());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (dataModelArrayList != null) ? dataModelArrayList.size() : 0;
    }

    public class BookAdapterViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgBook;
        private TextView titleBook;
        private Button btnReadNow;

        public BookAdapterViewHolder(View itemView) {
            super(itemView);

            imgBook = itemView.findViewById(R.id.image_view_book);
            titleBook = itemView.findViewById(R.id.text_view_title_book);
            btnReadNow = itemView.findViewById(R.id.button_book_read_now);
        }
    }
}