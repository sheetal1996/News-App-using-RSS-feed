package com.example.android.newsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private SwipeRefreshLayout mSwipeLayout;
    private List<RssFeedModel> mFeedModelList;
    private String mFeedTitle;
    private String mFeedLink;
    private String mFeedDescription;
    private InputStream inputStream;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RssFeedListAdapter mAdapter;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        context = getApplicationContext();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // mRecyclerView.setAdapter(new RssFeedListAdapter(null));
        Log.d(TAG, "Entered the main class");
        new FetchFeedTask().execute((Void) null);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new FetchFeedTask().execute((Void) null);
            }
        });
    }

    public List<RssFeedModel> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException
    {
        this.inputStream = inputStream;
        String title = null;
        String link = null;
        String description = null;
        String pubDate = null;
        boolean isItem = false;
        List<RssFeedModel> items = new ArrayList<>();
        try
        {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);
            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
            {
                int eventType = xmlPullParser.getEventType();
                String name = xmlPullParser.getName();
                if(name == null)
                    continue;
                if(eventType == XmlPullParser.END_TAG)
                {
                    if(name.equalsIgnoreCase("item"))
                    {
                        isItem = false;
                    }
                    continue;

                }
                if (eventType == XmlPullParser.START_TAG)
                {
                    if(name.equalsIgnoreCase("item"))
                    {
                        isItem = true;
                        continue;
                    }
                }
                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT)
                {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title"))
                {
                    title = result;
                }
                else if (name.equalsIgnoreCase("link"))
                {
                    link = result;
                }
                else if (name.equalsIgnoreCase("description"))
                {
                    description=result;
                }
                else if (name.equalsIgnoreCase("pubDate"))
                {
                    pubDate=result;
                }
                if (title != null && link != null && description != null && pubDate != null)
                {
                    if (isItem)
                    {
                        RssFeedModel item = new RssFeedModel(title, link, description, pubDate);
                        items.add(item);
                    }
                    else
                    {
                        mFeedTitle = title;
                        mFeedLink = link;
                        mFeedDescription = description;
                    }
                    title = null;
                    link = null;
                    description = null;
                    pubDate=null;
                    isItem = false;
                }
            }
            return items;
        }
        finally
        {
            inputStream.close();
        }
    }
    //--------------------------------------------------------------------------------//

    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean>
    {
        private String urlLink;

        @Override
        protected void onPreExecute()
        {
            mSwipeLayout.setRefreshing(true);
            urlLink = "http://timesofindia.indiatimes.com/rssfeeds/913168846.cms";
        }

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            if (TextUtils.isEmpty(urlLink))
                return false;
            try
            {
                if(!urlLink.startsWith("http://") && !urlLink.startsWith("https://"))
                    urlLink = "http://" + urlLink;
                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                return true;
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error", e);
            }
            catch (XmlPullParserException e)
            {
                Log.e(TAG, "Error", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success)
        {
            mSwipeLayout.setRefreshing(false);
            if (success)
            {
                Log.d(TAG, "Reached here");//Fill RecyclerView
                mAdapter = new RssFeedListAdapter(mFeedModelList, context);
                mRecyclerView.setAdapter(mAdapter);
            }
            /*else
            {
                Toast.makeText(MainActivity.this,
                        "Enter a valid Rss feed url",
                        Toast.LENGTH_LONG).show();
            }*/
        }
    }
}
