package com.vexanium.vexgift.module.vexpoint.ui.card;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.bean.response.InviteCardResponse;

import java.util.ArrayList;
import java.util.List;

public class InviteCardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<InviteCardResponse.InviteCard> mData;
    private float mBaseElevation;

    public InviteCardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void setData(List<InviteCardResponse.InviteCard> data) {
        mData = data;
        mViews = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            mViews.add(null);
        }
        notifyDataSetChanged();
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.size() > 0 ? mViews.get(position) : null;
    }

    public InviteCardResponse.InviteCard getDataAt(int position) {
        return mData.size() > 0 ? mData.get(position) : null;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_invite_card, container, false);
        container.addView(view);

        final CardView cardView = view.findViewById(R.id.cardView);

        TextView h2TextView = cardView.findViewById(R.id.invite_card_h2);
        TextView h3TextView = cardView.findViewById(R.id.invite_card_h3);
        TextView h4TextView = cardView.findViewById(R.id.invite_card_h4);
        final ImageView imageView = cardView.findViewById(R.id.invite_card_image);

        if (mData != null && mData.size() > 0 && mData.size() > position) {
            InviteCardResponse.InviteCard inviteCard = mData.get(position);
            if (inviteCard != null) {
                h2TextView.setText(inviteCard.h2);
                h3TextView.setText(inviteCard.h3);
                h4TextView.setText(inviteCard.h4);

                String imageUrl = inviteCard.imageUrl;
                Glide.with(container.getContext()).asBitmap()
                        .apply(RequestOptions
                                .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                                .format(DecodeFormat.PREFER_ARGB_8888))
                        .load(imageUrl)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                                int imageWidth = resource.getWidth();
                                int imageHeight = resource.getHeight();

                                int picWidth = cardView.getWidth();
                                int picHeight = imageHeight * picWidth / imageWidth;
                                params.width = picWidth;
                                params.height = picHeight;
                                imageView.setLayoutParams(params);

                                return false;
                            }
                        })
                        .into(imageView);
            }
        }


        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

}

