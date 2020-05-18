package com.android.test

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_bookmark.view.*
import kotlinx.android.synthetic.main.list_item_search.view.imageView

class BookmarkAdapter(var list: ArrayList<Movie>, var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var mBookmarkRemoveListener: BookmarkRemoveListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_bookmark, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as BookmarkViewHolder
        viewHolder.itemView.textView.text = list[position].title
        Picasso.with(context).load(list[position].poster).into(viewHolder.itemView.imageView)
    }

    inner class BookmarkViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("id", list[adapterPosition].id)
                context.startActivity(intent)
            }

            itemView.ivRemove.setOnClickListener {
                removeBookmark(list[adapterPosition])
                mBookmarkRemoveListener.onBookmarkRemoved(list[adapterPosition])
                list.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
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
        clearList()
        list.addAll(arrayList)
        notifyDataSetChanged()
    }

    fun clearList() {
        list.clear()
    }

    interface BookmarkRemoveListener {
        fun onBookmarkRemoved(movie: Movie)
    }
}