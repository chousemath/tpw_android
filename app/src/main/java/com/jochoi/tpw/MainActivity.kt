package com.jochoi.tpw

import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Intent
import android.content.Loader
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<Article>>, ArticleAdapter.ListItemClickListener {
    private val logTag = MainActivity::class.java.simpleName

    override fun onListItemClick(article: Article) {
        // Intent.ACTION_VIEW is a placeholder for "android.intent.action.VIEW"
        Log.d(logTag, "downloadLink: " + article.downloadLink)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(article.downloadLink))
        try {
            startActivity(browserIntent)
        } catch (e: Exception) {
            Log.e(logTag, "Browser Intent Start Activity Failed: " + e)
        }
    }

    private val mArticleLoaderId = 1
    private val urlAllCards = "https://proto3d.herokuapp.com/cards"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // by default, the recycler view should be shown vertically
        rv_articles.layoutManager = LinearLayoutManager(this)
        // Basically whenever items are inserted, moved or removed the size (width and height) of
        // RecyclerView might change and in turn the size of any other view in view hierarchy might
        // change. This is particularly troublesome if items are added or removed frequently.
        // Avoid unnecessary layout passes by setting setHasFixedSize to true when you are adding or
        // removing items in the RecyclerView and that doesn't change it's height or the width.
        rv_articles.setHasFixedSize(true)
        runLoaders()
    }

    private fun runLoaders() {
        // need to check your network connection
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        // if connected to network, go ahead and initiate background fetch of data
        if (networkInfo != null && networkInfo.isConnected) {
            loaderManager.initLoader(mArticleLoaderId, null, this)
        } else {
            // friendly warning that network connection is not available
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Article>> {
        return object : AsyncTaskLoader<List<Article>>(this) {
            override fun onStartLoading() {
                // force this node to run in the background
                forceLoad()
            }

            override fun loadInBackground(): List<Article>? {
                // this is where we want to fetch and return the list of articles
                val uriAllCards: Uri = Uri.parse(urlAllCards)
                return QueryUtils.fetchArticleData(uriAllCards.toString())
            }
        }
    }

    override fun onLoaderReset(loader: Loader<List<Article>>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoadFinished(loader: Loader<List<Article>>?, data: List<Article>?) {
        // after data is finished being fetch, need to render it in the recycler view
        if (data != null && data.isNotEmpty()) {
            // bind the adapter we created to the recycler view
            // I believe that the reason you can pass in "this" as an instance of
            // ListItemClickListener is because MainActivity implements the
            // ArticleAdapter.ListItemClickListener interface
            rv_articles.adapter = ArticleAdapter(this, data)
        }
    }
}
