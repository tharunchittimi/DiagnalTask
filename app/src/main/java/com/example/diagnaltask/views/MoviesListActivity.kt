package com.example.diagnaltask.views

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diagnaltask.BR
import com.example.diagnaltask.R
import com.example.diagnaltask.adapter.MoviesListAdapter
import com.example.diagnaltask.databinding.ActivityMoviesListBinding
import com.example.diagnaltask.models.ContentListingResponse
import com.example.diagnaltask.util.GridSpacingItemDecoration
import com.example.diagnaltask.util.Utils
import com.example.diagnaltask.viewmodels.MoviesViewModel


class MoviesListActivity : AppCompatActivity() {

    private var mViewDataBinding: ActivityMoviesListBinding? = null
    private var mViewModel: MoviesViewModel? = null
    private var moviesListAdapter: MoviesListAdapter? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var currentResponse: ContentListingResponse? = null
    private var gridLayoutItemDecoration: GridSpacingItemDecoration? = null
    private var showKeyboardDelayed = false
    private var isSearchOpenOption: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onConfigurationCheck(resources.configuration)
        initBinding()
        setUpRecyclerView()
        addObservers()
        initSearchView()
        setUpToolBar()
    }

    private fun initBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_movies_list)
        mViewDataBinding?.lifecycleOwner = this
        this.mViewModel = ViewModelProvider(this)[MoviesViewModel::class.java]
        mViewDataBinding?.setVariable(BR.moviesViewModel, mViewModel)
        mViewDataBinding?.executePendingBindings()
    }

    private fun setUpRecyclerView() {
        moviesListAdapter = MoviesListAdapter()
        mViewDataBinding?.rvMoviesList?.let {
            it.layoutManager = gridLayoutManager
            it.adapter = moviesListAdapter
            it.addItemDecoration(gridLayoutItemDecoration as RecyclerView.ItemDecoration)
            it.itemAnimator?.setChangeDuration(0)
        }
        moviesListAdapter?.setOnItemCommunicatorListener(object : MoviesListAdapter.Communicator {
            override fun showNoDataMessage(isDataNotAvailable: Boolean) {
                mViewModel?.setNoDataMessage(isDataNotAvailable)
            }
        })
        addScrollListener()
    }

    private fun onConfigurationCheck(newConfig: Configuration) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = getGridLayoutManager(3)
            gridLayoutItemDecoration = getGridItemDecoration(3)
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = getGridLayoutManager(7)
            gridLayoutItemDecoration = getGridItemDecoration(7)
        }
    }

    private fun getGridLayoutManager(spanCount: Int): GridLayoutManager {
        return GridLayoutManager(this, spanCount)
    }

    private fun getGridItemDecoration(spanCount: Int): GridSpacingItemDecoration {
        val spacing = 50
        val includeEdge = true
        return GridSpacingItemDecoration(
            spanCount,
            spacing,
            includeEdge
        )
    }

    private fun addObservers() {
        mViewModel?.getMoviesList()?.observe(this, {
            it?.let { data -> moviesListAdapter?.addList(data) }
        })
        mViewModel?.getMoviesTotalCount()?.observe(this, {
            mViewDataBinding?.toolBarMoviesList?.txtToolbarHeading?.text = it?.page?.title
            currentResponse = it
        })
        mViewModel?.getSearchOpened()?.observe(this, {
            if (it == true) {
                mViewDataBinding?.toolbarSearchView?.rootLayoutSearchView?.visibility = View.VISIBLE
                showSearchFocusOnFirst()
            } else {
                mViewDataBinding?.toolbarSearchView?.rootLayoutSearchView?.visibility = View.GONE
                Utils.hideKeyboard(this)
            }
        })
        mViewModel?.getNoDataMessage()?.observe(this, {
            if (it == true) {
                Toast.makeText(this@MoviesListActivity, "No Data Available", Toast.LENGTH_LONG)
                    .show()
            } else {
                return@observe
            }
        })
    }

    private fun setUpToolBar() {
        mViewDataBinding?.toolBarMoviesList?.txtToolbarBack?.setOnClickListener {
            onBackPressed()
        }
        mViewDataBinding?.toolBarMoviesList?.txtToolBarRight?.setOnClickListener {
            isSearchOpenOption = true
            mViewModel?.setSearchOpened(isSearchOpenOption)
        }
        mViewDataBinding?.toolbarSearchView?.ivClear?.setOnClickListener {
            val searchData =
                mViewDataBinding?.toolbarSearchView?.editQuery?.text?.toString()?.trim()
            if (searchData?.length ?: 0 <= 0) {
                isSearchOpenOption = false
                mViewModel?.setSearchOpened(isSearchOpenOption)
            } else {
                mViewDataBinding?.toolbarSearchView?.editQuery?.text = null
                Utils.hideKeyboard(this)
            }
        }
    }

    private fun showSearchFocusOnFirst() {
        mViewDataBinding?.toolbarSearchView?.editQuery?.requestFocus()
        showKeyboardDelayed = true
        maybeShowKeyboard()
    }

    private fun maybeShowKeyboard() {
        if (hasWindowFocus() && showKeyboardDelayed) {
            if (mViewDataBinding?.toolbarSearchView?.editQuery?.isFocused == true) {
                mViewDataBinding?.toolbarSearchView?.editQuery?.let {
                    it.post {
                        val imm =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
                    }
                }
            }
            showKeyboardDelayed = false
        }
    }

    private fun addScrollListener() {
        mViewDataBinding?.rvMoviesList?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if ((currentResponse?.page?.totalContentItems ?: 0 > (moviesListAdapter?.itemCount
                        ?: 0))
                ) {
                    if (gridLayoutManager?.findLastCompletelyVisibleItemPosition() == ((moviesListAdapter?.itemCount
                            ?: 1) - 1)
                    ) {
                        mViewDataBinding?.toolbarSearchView?.editQuery?.text?.toString()?.trim()
                            .let {
                                if (it?.length ?: 0 > 0) {
                                    return@let
                                } else {
                                    mViewModel?.addMoviesToModel(moviesListAdapter?.itemCount ?: 0)
                                }
                            }
                    }
                }
            }
        })
    }

    private fun initSearchView() {
        mViewDataBinding?.toolbarSearchView?.editQuery?.addTextChangedListener(object :
            TextWatcher {
            private val handler = Handler(Looper.getMainLooper())
            private var runnable: Runnable? = null
            override fun afterTextChanged(newText: Editable?) {
                runnable?.let {
                    handler.removeCallbacks(it)
                }
                runnable = Runnable {
                    if (newText?.trim()?.length ?: 0 <= 2) {
                        moviesListAdapter?.showSearchedList("")
                    } else {
                        if (newText?.length ?: 0 >= 3) {
                            moviesListAdapter?.showSearchedList(newText.toString())
                        }
                    }
                }
                handler.postDelayed(runnable!!, 400L)
            }

            override fun beforeTextChanged(
                newText: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(newText: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
    }

}