package com.jochoi.tpw

import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Loader
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<Article>> {

    private val mArticleLoaderId = 1
    private val urlAllCards = "https://proto3d.herokuapp.com/cards"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
