package com.example.simplelauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var appGrid: RecyclerView
    private val appList = mutableListOf<AppInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appGrid = findViewById(R.id.app_grid)
        appGrid.layoutManager = GridLayoutManager(this, 4)

        loadApps()
        appGrid.adapter = AppAdapter(appList)
    }

    private fun loadApps() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = pm.queryIntentActivities(intent, 0)
        for (resolveInfo in apps) {
            val label = resolveInfo.loadLabel(pm).toString()
            val icon = resolveInfo.loadIcon(pm)
            val packageName = resolveInfo.activityInfo.packageName
            appList.add(AppInfo(label, icon, packageName))
        }
        appList.sortBy { it.label }
    }
}

data class AppInfo(val label: String, val icon: android.graphics.drawable.Drawable, val packageName: String)

class AppAdapter(private val apps: List<AppInfo>) : RecyclerView.Adapter<AppAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.app_icon)
        val label: TextView = view.findViewById(R.id.app_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.icon.setImageDrawable(app.icon)
        holder.label.text = app.label
        holder.itemView.setOnClickListener {
            val intent = holder.itemView.context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = apps.size
}