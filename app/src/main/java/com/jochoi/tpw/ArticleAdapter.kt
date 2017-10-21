package com.jochoi.tpw

import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by jo on 10/21/17.
 */
class ArticleAdapter(val mListItemClickListener: ListItemClickListener,
                     val articleData: List<Article>): RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    // this interface should be implemented by MainActivity
    // MainActivity will basically pass itself as context with the click listener
    interface ListItemClickListener {
        fun onListItemClick(article: Article)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ArticleViewHolder {
        // need to inflate the layout that we created
        val inflater = LayoutInflater.from(parent?.context)
        return ArticleViewHolder(inflater.inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ArticleViewHolder?, position: Int) {
        holder?.articleTitle?.text = articleData[position].title
        holder?.articleDescription?.text = articleData[position].description
    }

    override fun getItemCount(): Int {
        return articleData.size
    }

    // need to create a custom view holder to actually store the views
    // the inner keyword allows for the inner class to access variables from the outer class
    // In order to make sure all list items are clickable, need to extend class (View.OnClickListener)
    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        // you have to explicitly declare this list item as clickable
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            // this will be the main function that will be invoked to interact with MainActivity
            // without the "inner" modifier, we would not be able to access the fields of the
            // outer class
            // adapterPosition will actually invoke getAdapterPosition(), does boilerplate for us
            print("INSIDE THE ON CLICK IN ARTICLE ADAPTER")
            mListItemClickListener.onListItemClick(articleData[adapterPosition])
        }

        val articleTitle: TextView = itemView.findViewById(R.id.title) as TextView
        val articleDescription: TextView = itemView.findViewById(R.id.description) as TextView
    }
}