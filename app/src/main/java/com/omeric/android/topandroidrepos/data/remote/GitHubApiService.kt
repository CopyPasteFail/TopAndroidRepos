package com.omeric.android.topandroidrepos.data.remote

import com.omeric.android.topandroidrepos.data.model.RepositoryModel
import com.omeric.android.topandroidrepos.data.model.SearchRepositoriesModel
import io.reactivex.Single
import retrofit2.http.*


/**
 * This interface defines methods used by Retrofit to communicate with a given API.
 * Each interface method, can be thought of as a RESTful method.
 */
interface GitHubApiService
{
    //example: https://api.github.com/repos/BracketCove/SpaceNotes
    @GET("repos/{user}/{repoName}")
    fun getUserSpecificRepository(
        @Path("user") user: String,
        @Path("repoName") repoName: String
    ): Single<RepositoryModel>

    //example: https://api.github.com/repos/BracketCove/SpaceNotes
    @GET("users/{user}/repos")
    fun getUserRepositories(
        @Path("user") user: String
    ): Single<List<RepositoryModel>>

    //example: https://api.github.com/search/repositories?q=topic:android&sort=stars&order=desc
    @GET("search/repositories")
    fun getRepositoriesFromSearch(
        @QueryMap filters : Map<String, String>
    ): Single<SearchRepositoriesModel>
}