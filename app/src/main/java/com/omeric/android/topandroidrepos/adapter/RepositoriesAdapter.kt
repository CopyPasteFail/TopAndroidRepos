package com.omeric.android.topandroidrepos.adapter

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.omeric.android.topandroidrepos.R
import com.omeric.android.topandroidrepos.data.model.RepositoryModel
import com.squareup.picasso.Picasso
import android.content.Intent
import com.omeric.android.topandroidrepos.activity.DetailsActivity


class RepositoriesAdapter(
    private val repositories: List<RepositoryModel>,
    private val rowLayout: Int,
    private val context: Context
) : RecyclerView.Adapter<RepositoriesAdapter.RepositoryViewHolder>()
{
    companion object
    {
        private val TAG = "gipsy:" + this::class.java.name
    }

    //A view holder inner class where we get reference to the views in the layout using their ID
    class RepositoryViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        internal var repositoryItemLayout: ConstraintLayout = view.findViewById(R.id.repository_item_layout)
        internal var repositoryTitle: TextView = view.findViewById(R.id.title_list_item)
        //            data = (TextView) view.findViewById(R.id.date);
        internal var repositoryDescription: TextView = view.findViewById(R.id.description_list_item)
        internal var stars: TextView = view.findViewById(R.id.stars_list_item)
        internal var forks: TextView = view.findViewById(R.id.forks_list_item)
        internal var avatarImage: ImageView = view.findViewById(R.id.avatar_image_list_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoriesAdapter.RepositoryViewHolder
    {
        Log.d(TAG,"onCreateViewHolder:")
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return RepositoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int)
    {
        Log.d(TAG,"onBindViewHolder:")
        val avatarUrl = repositories[position].owner?.avatarUrl
        Picasso
            .get()
            .load(avatarUrl)
            .placeholder(android.R.drawable.sym_def_app_icon)
            .error(android.R.drawable.sym_def_app_icon)
            .into(holder.avatarImage)
        holder.repositoryTitle.text = repositories[position].name
        holder.repositoryDescription.text = repositories[position].description
        holder.stars.text = repositories[position].stargazersCount.toString()
        holder.forks.text = repositories[position].forksCount.toString()

        holder.repositoryItemLayout.setOnClickListener {
            context.startActivity(Intent(context, DetailsActivity::class.java)
                .putExtra(DetailsActivity.INTENT_USERNAME_ID, repositories[position].owner?.login)
                .putExtra(DetailsActivity.INTENT_REPOSITORY_NAME_ID, repositories[position].name)
            )
        }
    }

    override fun getItemCount(): Int
    {
        return repositories.size
    }
}
