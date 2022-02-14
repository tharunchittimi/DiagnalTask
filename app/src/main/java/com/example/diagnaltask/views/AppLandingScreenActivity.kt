package com.example.diagnaltask.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.diagnaltask.BR
import com.example.diagnaltask.R
import com.example.diagnaltask.databinding.ActivityAppLandingScreenBinding
import com.example.diagnaltask.viewmodels.AppLandingScreenViewModel

class AppLandingScreenActivity : AppCompatActivity() {

    private var mViewDataBinding: ActivityAppLandingScreenBinding? = null
    private var mViewModel: AppLandingScreenViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        mViewDataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_app_landing_screen)
        mViewDataBinding?.lifecycleOwner = this
        this.mViewModel = ViewModelProvider(this)[AppLandingScreenViewModel::class.java]
        mViewDataBinding?.setVariable(BR.appLandingScreenViewModel, mViewModel)
        mViewDataBinding?.executePendingBindings()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MoviesListActivity::class.java))
            finish()
        }, 2000)
    }
}