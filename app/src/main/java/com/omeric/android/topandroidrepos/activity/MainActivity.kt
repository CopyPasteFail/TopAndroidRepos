package com.omeric.android.topandroidrepos.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import com.omeric.android.topandroidrepos.data.remote.GitHubApiService
import io.reactivex.disposables.Disposable
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.omeric.android.topandroidrepos.R
import com.omeric.android.topandroidrepos.adapter.RepositoriesAdapter
import com.omeric.android.topandroidrepos.data.model.SearchRepositoriesModel


class MainActivity : AppCompatActivity()
{
    companion object
    {
        private val TAG = "gipsy:" + this::class.java.name
        const val BASE_URL = "https://api.github.com/"
    }

    private var compositeDisposable: CompositeDisposable? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var mProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, ":onCreate")

        // Trailing slash is needed
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        recyclerView = findViewById(R.id.recycler_view_main_activity)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mProgressBar = findViewById(R.id.progressbar_main_activity)

        // create an instance of the GitHubApiService
        val gitHubApiService = retrofit.create(GitHubApiService::class.java)

        //https://api.github.com/search/repositories?q=topic:android&sort=stars&order=desc&per_page=5
        gitHubApiService.getRepositoriesFromSearch(mapOf("q" to "topic:android", "sort" to "stars"
            , "order" to "desc", "per_page" to "10"))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<SearchRepositoriesModel>
            {
                override fun onSubscribe(disposable: Disposable)
                {
                    Log.d(TAG, "repositoriesSingle::onSubscribe")
                    add(disposable)
                    showProgressBar()
                }

                override fun onSuccess(repositories : SearchRepositoriesModel)
                {
                    // data is ready and we can update the UI
                    Log.d(TAG, "repositoriesSingle::onSuccess:")
                    Log.d(TAG, "repositoriesSingle::onSuccess: Number of repositories received:  ${repositories.totalCount}")
                    recyclerView.adapter = RepositoriesAdapter(repositories.items!!, R.layout.list_item_movie, applicationContext)
                    hideProgressBar()
                }

                override fun onError(e: Throwable)
                {
                    // oops, we best show some error message
                    Log.e(TAG, "repositoriesSingle::onError: $e")
                    Toast.makeText(this@MainActivity, "Error connecting to GitHub", Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                }
            })
    }

    override fun onDestroy()
    {
        Log.d(TAG, ":onDestroy")
        dispose()
        super.onDestroy()
    }

    /*
     * Create a CompositeDisposable object which acts as a container for disposables
     * (think Recycle Bin) and add our Disposable to it
     */
    fun add(disposable: Disposable)
    {
        Log.d(TAG, ":add")
        if (compositeDisposable == null)
        {
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable?.add(disposable)
    }

    private fun dispose()
    {
        Log.d(TAG, ":dispose")
        if (compositeDisposable?.isDisposed == false)
        {
            /*
             * The subscription can be disposed with a simple method call,
             * thus preventing those nasty situations when, for example,
             * rotating the device in the middle of a running background task causes a memory leak
             */
            compositeDisposable?.dispose()
            compositeDisposable = null
        }
    }

    private fun hideProgressBar()
    {
        Log.d(TAG, "::hideProgressBar:")
        mProgressBar.visibility = View.INVISIBLE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showProgressBar()
    {
        Log.d(TAG, "::hideProgressBar:")
        mProgressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }
}
