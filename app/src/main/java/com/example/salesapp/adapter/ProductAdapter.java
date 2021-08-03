package com.example.salesapp.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesapp.R;
import com.example.salesapp.activity.NavigationActivity;
import com.example.salesapp.api.RetrofitBuilder;
import com.example.salesapp.fragment.HomeFragment;
import com.example.salesapp.model.DetailTransactions;
import com.example.salesapp.model.Product;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public static ArrayList<Product> dataList = new ArrayList<>();
    private Context context;
    public static Product cartModel;
    private HomeCallBack homeCallBack;

    public ProductAdapter(ArrayList<Product> dataList, Context context, HomeCallBack homeCallBack) {
        this.dataList = dataList;
        this.context = context;
        this.homeCallBack = homeCallBack;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_product_adapter, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Product product = dataList.get(position);
        Picasso.get()
                .load(RetrofitBuilder.BASE_URL_IMAGES + product.getImg())
                .into(holder.ivProduct);
        holder.tvProductName.setText(product.getName());

        DecimalFormat decim = new DecimalFormat ("#,###.##");
        String price = decim.format(product.getPrice());
        holder.tvProductPrice.setText(price.replace(',', '.'));

        holder.itemView.setOnClickListener(view -> {
            final AlertDialog dialogBuilder = new AlertDialog.Builder(view.getContext()).create();
            LayoutInflater inflaterPopUp = ((Activity)view.getContext()).getLayoutInflater();
            View dialogView = inflaterPopUp.inflate(R.layout.custom_dialog, null);

            ImageView ivDetailImage = dialogView.findViewById(R.id.image_view_detail_photo);
            ImageView ivPlus = dialogView.findViewById(R.id.image_view_plus);
            ImageView ivMinus = dialogView.findViewById(R.id.image_view_minus);
            TextView tvDetailName = dialogView.findViewById(R.id.text_view_detail_product_name);
            TextView tvDetailPrice = dialogView.findViewById(R.id.text_view_detail_product_price);
            TextView tvQty = dialogView.findViewById(R.id.text_view_qty);
            Button btnAddToCart = dialogView.findViewById(R.id.button_to_cart);

            // Display data for popup
            Picasso.get()
                    .load(RetrofitBuilder.BASE_URL_IMAGES + product.getImg())
                    .into(ivDetailImage);
            tvDetailName.setText(product.getName());
            String priceInDialog = decim.format(product.getPrice());
            tvDetailPrice.setText(priceInDialog.replace(',', '.'));

            tvQty.setText(String.valueOf(0));
            final int[] cartCounter = {0};

            ivPlus.setOnClickListener(v1 -> {
                ivMinus.setEnabled(true);
                cartCounter[0] += 1;
                tvQty.setText(String.valueOf(cartCounter[0]));
            });

            ivMinus.setOnClickListener(v2 -> {
                if (cartCounter[0] == 0) {
                    Toast.makeText(context, "Tidak dapat menambahkan kurang dari 0!", Toast.LENGTH_SHORT).show();
                } else {
                    cartCounter[0] -= 1;
                    tvQty.setText(String.valueOf(cartCounter[0]));
                }
            });

            btnAddToCart.setOnClickListener(v3 -> {
                cartModel = new Product();
                if (tvQty.getText().toString().equals("0")) {
                    Toast.makeText(context, "Pesanan Anda kosong!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Lihat keranjang Anda!", Toast.LENGTH_SHORT).show();
                    cartModel.setId(dataList.get(position).getId());
                    cartModel.setName(dataList.get(position).getName());
                    cartModel.setPrice(dataList.get(position).getPrice());
                    cartModel.setImg(dataList.get(position).getImg());
                    cartModel.setTotalCash(cartCounter[0] * dataList.get(position).getPrice());
                    dataList.add(cartModel);

                    HomeFragment.cart_count = 0;
                    for (int i = 0; i < dataList.size(); i++) {
                        for (int j = i + 1; j < dataList.size(); j++) {
                            if (dataList.get(i).getImg().equals(dataList.get(j).getImg())) {
                                dataList.get(i).setImg(RetrofitBuilder.BASE_URL_IMAGES + dataList.get(j).getImg());
                                dataList.get(i).setId(dataList.get(j).getId());
                                dataList.get(i).setTotalCash(dataList.get(j).getTotalCash());
                                dataList.remove(j);
                                j--;
                            }
                        }
                    }

                    HomeFragment.cart_count = dataList.size();
                    homeCallBack.updateCartCount(context);
                    dialogBuilder.dismiss();
                }
            });

            dialogBuilder.setView(dialogView);
            dialogBuilder.show();
        });
    }

    public interface CallBackUs {
        void addCartItemView();
    }

    public interface HomeCallBack {
        void updateCartCount(Context context);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivProduct;
        private TextView tvProductName, tvProductPrice;

        public ProductViewHolder(View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.image_view_product);
            tvProductName = itemView.findViewById(R.id.text_view_product_name);
            tvProductPrice = itemView.findViewById(R.id.text_view_product_price);
        }
    }
}