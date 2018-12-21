package com.rygelouv.networkcalldslsample

import android.content.Context
import androidx.lifecycle.Observer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: RepoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = RepoListAdapter(this, R.layout.repo_item, ArrayList())
        repoList.adapter = adapter

        Repository.getRepos("kotlin").observe(this, Observer {
            when (it?.status) {
                Resource.LOADING -> {
                    Log.d("MainActivity", "--> Loading...")
                    loadingWrapper.visibility = View.VISIBLE
                    listWrapper.visibility = View.GONE
                }
                Resource.SUCCESS -> {
                    Log.d("MainActivity", "--> Success! | loaded ${it.data?.size ?: 0} records.")

                    loadingWrapper.visibility = View.GONE
                    listWrapper.visibility = View.VISIBLE
                    adapter.replace(it.data ?: ArrayList())
                }
                Resource.ERROR -> {
                    Log.d("MainActivity", "--> Error!")
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    open class RepoListAdapter(context: Context, resource: Int, list: ArrayList<Repo>) : ArrayAdapter<Repo>(context, resource, list) {
        var resource: Int
        var list: ArrayList<Repo>
        var vi: LayoutInflater

        init {
            this.resource = resource
            this.list = list
            this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var holder: ViewHolder
            var retView: View

            if(convertView == null){
                retView = vi.inflate(resource, null)
                holder = ViewHolder()
                holder.repoName = retView.findViewById(R.id.repoName) as TextView?
                retView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
                retView = convertView
            }

            val item = list.get(position)

            holder.repoName?.text = "${item.full_name} -> ${item.git_url}"

            return retView
        }

        fun replace(data: List<Repo>) {
            list.clear();
            list.addAll(data);
            notifyDataSetChanged();
        }

        internal class ViewHolder {
            var repoName: TextView? = null
        }
    }
}
