package com.example.android.newsapp;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.List;

import static android.content.ContentValues.TAG;

public class RssFeedListAdapter extends RecyclerView.Adapter<RssFeedListAdapter.FeedModelViewHolder> {

    private List<RssFeedModel> mRssFeedModels;
    private Context context;
    private AQuery aQuery;
    Toast mToast;

    public static class FeedModelViewHolder extends RecyclerView.ViewHolder
    {
        private View rssFeedView;
        public FeedModelViewHolder(View v)
        {
            super(v);
            rssFeedView = v;
        }
    }

    public RssFeedListAdapter(List<RssFeedModel> rssFeedModels, Context context)
    {
        mRssFeedModels = rssFeedModels;
        this.context=context;
    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rows, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        TextView title = (TextView) holder.rssFeedView.findViewById(R.id.postTitleLabel);
        final RssFeedModel rssFeedModel = mRssFeedModels.get(position);
        title.setText(rssFeedModel.title);
        String url = rssFeedModel.link;

       /* try
        {
            org.jsoup.Connection con2= Jsoup.connect(url);
            Document doc = con2.get();
            Element e1=doc.head().select("link[href~=.*\\.(ico|png)]").first(); // example type 1 & 2
    //        String imageUrl1=e1.attr("href");
            String imageUrl1=      "http://timesofindia.indiatimes.com/photo/52142819.cms";
            Element e2 = doc.head().select("meta[itemprop=image]").first(); //example type 3
            String imageUrl2=e2.attr("itemprop");
            ImageView thumb_ = (ImageView) holder.rssFeedView.findViewById(R.id.img_);
            Picasso.with(context).load(imageUrl1).into(thumb_);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Connection failure", e);
        }*/
        //ImageView thumb_ = (ImageView) holder.rssFeedView.findViewById(R.id.img_);
        aQuery = new AQuery(context);
        aQuery.id(R.id.img_).progress(R.id.img_).image("https://www.apple.com/ac/structured-data/images/knowledge_graph_logo.png?2017031708230",true,true);

        ((TextView) holder.rssFeedView.findViewById(R.id.postDescriptionLabel)).setText((rssFeedModel.pubDate).substring(0,16));
       title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                mToast.makeText(context,rssFeedModel.link ,Toast.LENGTH_LONG).show();
                String url = rssFeedModel.link;
                try {
                    Context context = view.getContext();
                    Intent intent = new Intent().parseUri(url, Intent.URI_INTENT_SCHEME);

                    if (intent != null) {

                        PackageManager packageManager = context.getPackageManager();
                        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

                        if (info != null) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            context.startActivity(browserIntent);
                        } else {
                            String fallbackUrl = intent.getStringExtra("www.google.com");
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
                    context.startActivity(browserIntent);
                        }
                    }
                } catch (URISyntaxException e) {
                    Log.e(TAG, "Can't resolve intent://", e);
                }
                /*Uri uri = Uri.parse(url) ;
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                context.startActivity(intent);*/
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return mRssFeedModels.size();
    }
}

