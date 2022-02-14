package com.example.diagnaltask.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diagnaltask.R
import com.example.diagnaltask.databinding.InflateMovieItemBinding
import com.example.diagnaltask.models.ContentListingResponse
import com.example.diagnaltask.util.Utils
import com.example.diagnaltask.widgets.CustomTextView


class MoviesListAdapter : RecyclerView.Adapter<MoviesListAdapter.MoviesListViewHolder>() {

    private var list: ArrayList<ContentListingResponse.Page.ContentItems.Content?> = ArrayList()
    private var mainList: ArrayList<ContentListingResponse.Page.ContentItems.Content?> =
        arrayListOf()
    private var communicator: Communicator? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesListViewHolder {
        val inflateMovieItemBinding: InflateMovieItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.inflate_movie_item, parent, false
        )
        return MoviesListViewHolder(inflateMovieItemBinding)
    }

    override fun onBindViewHolder(holder: MoviesListViewHolder, position: Int) {
        holder.onBind(list[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MoviesListViewHolder(private var inflateMovieItemBinding: InflateMovieItemBinding) :
        RecyclerView.ViewHolder(inflateMovieItemBinding.root) {
        fun onBind(contentListingResponse: ContentListingResponse.Page.ContentItems.Content?) {
            inflateMovieItemBinding.imgMoviePoster.let {
                Glide.with(it.context)
                    .load(Utils.getMoviesPictures(contentListingResponse?.posterImage))
                    .placeholder(R.drawable.placeholder_for_missing_posters)
                    .into(it)
            }
            highlightText(
                searchText = searchedName,
                name = contentListingResponse?.name ?: "",
                textView = inflateMovieItemBinding.movieTitle
            )
        }
    }

    private fun highlightText(
        searchText: String,
        name: String,
        textView: CustomTextView
    ) {
        if (searchText.isNotEmpty() && name.contains(searchText, true)) {
            val sb = SpannableStringBuilder(name)
            var index: Int = name.lowercase().indexOf(searchText.lowercase())
            while (index >= 0 && index < name.length) {
                val fcs = ForegroundColorSpan(Color.RED)
                sb.setSpan(
                    fcs,
                    index,
                    index + searchText.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                index = name.indexOf(searchText, index + 1)
            }
            textView.text = sb
        } else {
            textView.text = name
        }
    }

    fun addList(moviesList: ArrayList<ContentListingResponse.Page.ContentItems.Content?>) {
        clearList()
        addData(moviesList)
        this.mainList.addAll(moviesList)
        notifyDataSetChanged()
    }

    private fun addData(list: ArrayList<ContentListingResponse.Page.ContentItems.Content?>) {
        this.list.clear()
        for (myItem in list) {
            this.list.add(myItem)
        }
        notifyDataSetChanged()
    }

    var searchedName = ""
    fun showSearchedList(newText: String?) {
        val searchedList: ArrayList<ContentListingResponse.Page.ContentItems.Content?> = ArrayList()
        if (newText?.isEmpty() == true) {
            searchedName = ""
            searchedList.addAll(mainList)
            communicator?.showNoDataMessage(mainList.size <= 0)
        } else {
            searchedName = newText.toString().trim()
            searchedList.clear()
            for (item in mainList) {
                item?.let {
                    if (it.name?.startsWith(searchedName, true) == true) {
                        searchedList.add(item)
                    }
                }
            }
            communicator?.showNoDataMessage(searchedList.size <= 0)
        }
        addData(searchedList)
    }

    private fun clearList() {
        this.list.clear()
        this.mainList.clear()
        notifyDataSetChanged()
    }

    fun setOnItemCommunicatorListener(communicator: Communicator) {
        this.communicator = communicator
    }

    interface Communicator {
        fun showNoDataMessage(isDataNotAvailable: Boolean)
    }

}