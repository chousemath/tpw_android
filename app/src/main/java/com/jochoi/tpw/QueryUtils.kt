package com.jochoi.tpw

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * Created by jo on 10/20/17.
 * The `object` keyword basically makes this a singleton class
 */
object QueryUtils {

    private val logTag = QueryUtils::class.java.simpleName

    fun fetchArticleData(requestUrl: String): List<Article>? {
        val url = createUrl(requestUrl)
        var jsonResponse: String? = null
        try {
            jsonResponse = getHttpUrl(url)
        } catch (e: IOException) {
            Log.e(logTag, "Error communicating with server", e)
        }

        return extractFromJson(jsonResponse)
    }

    // a ? implies that there is a chance that null object will be returned
    private fun createUrl(requestUrl: String): URL? {
        // in Kotlin, if you want to assign a null value, ? is mandatory
        var url: URL? = null;
        try {
            url = URL(requestUrl)
        } catch (e: MalformedURLException) {
            Log.e(logTag, "Problem building URL", e)
        }
        return url
    }

    @Throws(IOException::class)
    private fun getHttpUrl(url: URL?): String? {
        // the ? below ensures that if url is null, everything after will not be executed
        // if url is null, the code below throws an exception ("Unsafe" cast operator)
        val urlConnection = url?.openConnection() as HttpURLConnection?
        try {
            // again, in case urlConnection is null, we don't want a null pointer exception
            // the ? prevents that exception from being thrown
            if (urlConnection?.responseCode == HttpURLConnection.HTTP_OK) {
                // we will retrieve all data from server as an input stream
                val inputStream = urlConnection.inputStream
                val scanner = Scanner(inputStream)
                // use a regex to designate the beginnning of input boundary
                // this is a way of tokenizing the input stream into something readable
                scanner.useDelimiter("\\A")
                if (scanner.hasNext()) {
                    // what is returned should be the json data
                    return scanner.next()
                }
            } else {
                Log.e(logTag, "Url connection error: " + urlConnection?.responseCode)
            }
        } finally {
            // make sure you disconnect to prevent memory leaks
            urlConnection?.disconnect()
        }
        // null is returned if we are unable to connect to the server
        return null
    }

    private fun extractFromJson(jsonResponse: String?): List<Article>? {
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return null
        }

        val articles = mutableListOf<Article>()
        try {
            val jsonBody = JSONObject(jsonResponse)
            val articleArray = jsonBody.getJSONArray("articles")
            for (i in 0..articleArray.length() - 1) {
                val currArticle = articleArray.getJSONObject(i)
                val title = if (currArticle.has("title")) currArticle.getString("title") else ""
                val description = if (currArticle.has("description")) currArticle.getString("description") else ""
                val titleKorean = if (currArticle.has("title_korean")) currArticle.getString("title_korean") else ""
                val descriptionKorean = if (currArticle.has("description_korean")) currArticle.getString("description_korean") else ""
                // ensure that there is some kind of default URL for downloadLink attribute
                // not ensuring this will cause an exception at startActivity(browerIntent)
                val downloadLink = if (currArticle.has("download_link") && currArticle.getString("download_link").isNotEmpty()) {
                    currArticle.getString("download_link")
                } else "https://www.thingiverse.com/"
                val cardTags: JSONArray = if (currArticle.has("card_tags")) currArticle.getJSONArray("card_tags") else JSONArray()
                articles.add(Article(title, description, titleKorean, descriptionKorean, downloadLink, cardTags))
            }
        } catch (e: JSONException) {
            Log.e(logTag, "Error parsing JSON articles:", e)
        }

        return articles
    }
}