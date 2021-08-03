package com.example.salesapp.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesapp.R;
import com.example.salesapp.activity.CartActivity;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.model.Product;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.salesapp.activity.CartActivity.grandTotalplus;
import static com.example.salesapp.activity.CartActivity.proceedToBook;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    ArrayList<Product> cartModelArrayList;
    Context context;

    public CartAdapter(ArrayList<Product> cartModelArrayList, Context context) {
        this.context = context;
        this.cartModelArrayList = cartModelArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cart_adapter, parent, false);
        return new CartAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CartAdapter.ViewHolder holder, final int position) {
        DecimalFormat decim = new DecimalFormat("#,###.##");
        String price = decim.format(cartModelArrayList.get(position).getTotalCash());
        holder.productCartPrice.setText(price.replace(',', '.'));

        String priceSatuan = decim.format(cartModelArrayList.get(position).getPrice());
        holder.productCartSatuan.setText(priceSatuan.replace(',', '.'));

        holder.productCartCode.setText(cartModelArrayList.get(position).getName());
        holder.productCartQuantity.setText(String.valueOf(cartModelArrayList.get(position).getStok()));
        Picasso.get()
                .load(RetrofitBuilder.BASE_URL_IMAGES + cartModelArrayList.get(position).getImg())
                .into(holder.productCartImage);

        holder.deleteItem.setOnClickListener(v -> {
            if (cartModelArrayList.size() == 1) {
                cartModelArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartModelArrayList.size());
                grandTotalplus = 0;

                String priceTotal = decim.format(grandTotalplus);
                CartActivity.grandTotal.setText(priceTotal.replace(',', '.'));
                CartActivity.grandTotal.setText(String.valueOf(grandTotalplus));
            }
            if (cartModelArrayList.size() > 0) {
                cartModelArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartModelArrayList.size());
                grandTotalplus = 0;
                for (int i = 0; i < CartActivity.temparraylist.size(); i++) {
                    grandTotalplus = grandTotalplus + CartActivity.temparraylist.get(i).getTotalCash();
                }

                String priceTotal = decim.format(grandTotalplus);
                CartActivity.grandTotal.setText(priceTotal.replace(',', '.'));

                proceedToBook.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button));
                proceedToBook.setEnabled(true);
            } else {
                Toast.makeText(context, "Keranjang kosong!", Toast.LENGTH_SHORT).show();
                proceedToBook.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button));
                proceedToBook.setEnabled(false);
            }
        });

        holder.cartIncrement.setOnClickListener(v -> {
            grandTotalplus = 0;

            holder.cartDecrement.setEnabled(true);

            int cartUpdateCounter = (cartModelArrayList.get(position).getStok());
            holder.cartIncrement.setEnabled(true);
            cartUpdateCounter += 1;

            cartModelArrayList.get(position).setStok((cartUpdateCounter));
            int cash = (cartModelArrayList.get(position).getPrice()) * (cartModelArrayList.get(position).getStok());

            holder.productCartQuantity.setText(String.valueOf(cartModelArrayList.get(position).getStok()));

            cartModelArrayList.get(position).setTotalCash(cash);

            String priceTwo = decim.format(cash);
            holder.productCartPrice.setText(priceTwo.replace(',', '.'));

            for (int i = 0; i < CartActivity.temparraylist.size(); i++) {
                grandTotalplus = grandTotalplus + CartActivity.temparraylist.get(i).getTotalCash();
            }

            String priceTotal = decim.format(grandTotalplus);
            CartActivity.grandTotal.setText(priceTotal.replace(',', '.'));
        });

        holder.cartDecrement.setOnClickListener(v -> {
            grandTotalplus = 0;

            holder.cartIncrement.setEnabled(true);

            int cartUpdateCounter = (cartModelArrayList.get(position).getStok());

            if (cartUpdateCounter == 1) {
                holder.cartDecrement.setEnabled(false);
                Toast.makeText(context, "Quantity tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else {
                holder.cartDecrement.setEnabled(true);
                cartUpdateCounter -= 1;

                cartModelArrayList.get(position).setStok((cartUpdateCounter));
                int cash = (cartModelArrayList.get(position).getPrice()) * (cartModelArrayList.get(position).getStok());

                holder.productCartQuantity.setText(String.valueOf(cartModelArrayList.get(position).getStok()));

                cartModelArrayList.get(position).setTotalCash(cash);

                String priceTwoDecrement = decim.format(cash);
                holder.productCartPrice.setText(priceTwoDecrement.replace(',', '.'));

                for (int i = 0; i < CartActivity.temparraylist.size(); i++) {
                    grandTotalplus = grandTotalplus + CartActivity.temparraylist.get(i).getTotalCash();
                }

                String priceTotal = decim.format(grandTotalplus);
                CartActivity.grandTotal.setText(priceTotal.replace(',', '.'));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productCartImage, cartIncrement, cartDecrement, deleteItem;
        TextView productCartCode, productCartPrice, productCartQuantity, productCartSatuan;

        public ViewHolder(View itemView) {
            super(itemView);
            productCartImage = itemView.findViewById(R.id.image_view_product_cart);
            deleteItem = itemView.findViewById(R.id.image_view_trash_cart);
            productCartCode = itemView.findViewById(R.id.text_view_product_name_cart);
            productCartPrice = itemView.findViewById(R.id.text_view_product_total_cart);
            productCartQuantity = itemView.findViewById(R.id.text_view_product_qty_cart);
            productCartSatuan = itemView.findViewById(R.id.text_view_product_price_cart);
            cartDecrement = itemView.findViewById(R.id.image_view_minus_cart);
            cartIncrement = itemView.findViewById(R.id.image_view_plus_cart);
        }
    }
}