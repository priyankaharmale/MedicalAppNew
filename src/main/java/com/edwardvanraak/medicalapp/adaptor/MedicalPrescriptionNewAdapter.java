package com.edwardvanraak.medicalapp.adaptor;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.edwardvanraak.medicalapp.R;

import java.util.ArrayList;

/**
 * Created by hnwebmarketing on 2/7/2018.
 */

public class MedicalPrescriptionNewAdapter extends RecyclerView.Adapter<MedicalPrescriptionNewAdapter.MyViewHolder> {

    private ArrayList<String> diseaseModels;
    Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_prescimag;

        public ProgressBar progress_item;

        public MyViewHolder(View view) {
            super(view);
            iv_prescimag = (ImageView) view.findViewById(R.id.iv_prescimag);
            progress_item = (ProgressBar) view.findViewById(R.id.progress_item);


        }
    }


    public MedicalPrescriptionNewAdapter(ArrayList<String> productModelArrayList, Context context) {
        this.diseaseModels = productModelArrayList;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adaptor_medical_prescrpimag, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final String diseaseModel = diseaseModels.get(position);

        try {
            Glide.with(context)
                    .load(diseaseModel)
                    .centerCrop()
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.progress_item.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.progress_item.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.iv_prescimag);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }

        holder.iv_prescimag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showimage(diseaseModel);
            }
        });
    }


    @Override
    public int getItemCount() {
        return diseaseModels.size();
    }

    public void showimage(final String image) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_imagedialog);

        dialog.show();
        ImageView declineButton = (ImageView) dialog.findViewById(R.id.iv_cancle);
        ImageView iv_imagelagr = (ImageView) dialog.findViewById(R.id.iv_image);
        final ProgressBar progress_item = (ProgressBar) dialog.findViewById(R.id.progress_item);

        try {
            Glide.with(context)
                    .load(image)
                    .centerCrop()
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progress_item.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progress_item.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(iv_imagelagr);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}

