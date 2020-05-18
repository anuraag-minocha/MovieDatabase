package com.android.test

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_search.view.*

class SearchAdapter(var list: ArrayList<Movie>, var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var mBookmarkListener: BookmarkListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_search, parent, false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as SearchViewHolder
        viewHolder.itemView.tvName.text = list[position].title
        viewHolder.itemView.tvYear.text = context.getString(R.string.year, list[position].year)
        Picasso.with(context).load(list[position].poster).into(viewHolder.itemView.imageView)
        if (list[position].isBookmarked)
            viewHolder.itemView.ivBookmark.setImageResource(android.R.drawable.star_big_on)
        else
            viewHolder.itemView.ivBookmark.setImageResource(android.R.drawable.star_big_off)
    }

    inner class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("id", list[adapterPosition].id)
                context.startActivity(intent)
            }

            itemView.ivBookmark.setOnClickListener {
                if (list[adapterPosition].isBookmarked) {
                    itemView.ivBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                    removeBookmark(list[adapterPosition])
                    list[adapterPosition].isBookmarked = false
                    notifyItemChanged(adapterPosition)
                } else {
                    itemView.ivBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                    addBookmark(list[adapterPosition])
                    list[adapterPosition].isBookmarked = true
                    notifyItemChanged(adapterPosition)
                }
                mBookmarkListener.onBookmarked()
            }
        }

        fun addBookmark(movie: Movie) {
            val preferences =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            val string = preferences.getString("bookmarks", "")
            val gson = Gson()
            if (string != "") {
                val bookmarks = gson.fromJson<MovieList>(string, MovieList::class.java)
                val list = bookmarks.movieList as ArrayList<Movie>
                list.add(movie)
                bookmarks.movieList = list
                preferences.edit().putString("bookmarks", gson.toJson(bookmarks)).apply()
            } else {
                val bookmarks = MovieList(listOf(), "")
                val list = arrayListOf<Movie>()
                list.add(movie)
                bookmarks.movieList = list
                preferences.edit().putString("bookmarks", gson.toJson(bookmarks)).apply()
            }

        }

        fun removeBookmark(movie: Movie) {
            val preferences =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            val string = preferences.getString("bookmarks", "")
            val gson = Gson()
            val bookmarks = gson.fromJson<MovieList>(string, MovieList::class.java)
            val list = bookmarks.movieList as ArrayList<Movie>
            bookmarks.movieList = list.filter { it.id != movie.id }
            preferences.edit().putString("bookmarks", gson.toJson(bookmarks)).apply()
        }

    }

    fun updateList(arrayList: ArrayList<Movie>) {
        val preferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val string = preferences.getString("bookmarks", "")
        if (string != "") {
            val gson = Gson()
            val bookmarks = gson.fromJson<MovieList>(string, MovieList::class.java)
            list.addAll(arrayList.map { item ->
                if (bookmarks.movieList.contains(item))
                    item.isBookmarked = true
                item
            })
        } else
            list.addAll(arrayList)
        notifyDataSetChanged()
    }

    fun clearList() {
        list.clear()
    }

    interface BookmarkListener {
        fun onBookmarked()
    }
}
