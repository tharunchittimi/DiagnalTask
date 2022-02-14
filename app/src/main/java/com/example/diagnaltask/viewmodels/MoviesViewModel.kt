package com.example.diagnaltask.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.diagnaltask.MyApplication
import com.example.diagnaltask.R
import com.example.diagnaltask.models.ContentListingResponse
import com.example.diagnaltask.util.JSONResourceReader


class MoviesViewModel : ViewModel() {

    private var totalCount = MutableLiveData<ContentListingResponse>()
    private var moviesList =
        MutableLiveData<ArrayList<ContentListingResponse.Page.ContentItems.Content?>>()
    private var isSearchOpened = MutableLiveData<Boolean?>()
    private var noDataMessage = MutableLiveData<Boolean?>()

    init {
        addMoviesToModel(1)
    }

    fun getMoviesList(): LiveData<ArrayList<ContentListingResponse.Page.ContentItems.Content?>> {
        return moviesList
    }

    fun setSearchOpened(isOpened: Boolean?) {
        this.isSearchOpened.value = isOpened
    }

    fun getSearchOpened(): LiveData<Boolean?> {
        return isSearchOpened
    }

    fun setNoDataMessage(showError: Boolean?) {
        this.noDataMessage.value = showError
    }

    fun getNoDataMessage():LiveData<Boolean?> {
        return noDataMessage
    }

    fun getMoviesTotalCount(): LiveData<ContentListingResponse> {
        return totalCount
    }

    fun addMoviesToModel(offset: Int) {
        val list = ArrayList<ContentListingResponse.Page.ContentItems.Content?>()
        val jsonReader = when (offset) {
            in 1..19 -> {
                JSONResourceReader(
                    MyApplication.getApplicationContext().resources,
                    R.raw.content_listing_page1
                )
            }
            in 20..39 -> {
                JSONResourceReader(
                    MyApplication.getApplicationContext().resources,
                    R.raw.content_listing_page2
                )
            }
            else -> {
                JSONResourceReader(
                    MyApplication.getApplicationContext().resources,
                    R.raw.content_listing_page3
                )
            }
        }
        val model = jsonReader.constructUsingGson(ContentListingResponse::class.java)
        totalCount.value = model
        moviesList.value?.let {
            list.addAll(it)
        }
        model?.page?.contentItems?.content?.let { list.addAll(it) }
        moviesList.value = list
    }

}