package com.example.unifor_gym.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.models.PopularClass

class PopularClassAdapter(private var popularClasses: List<PopularClass>) :
    RecyclerView.Adapter<PopularClassAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aulas_populares, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val popularClass = popularClasses[position]
        holder.tvPopularClassName.text = popularClass.name
        holder.progressPopularity.progress = popularClass.popularity
    }

    override fun getItemCount(): Int = popularClasses.size

    fun updateData(newData: List<PopularClass>) {
        popularClasses = newData
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPopularClassName: TextView = itemView.findViewById(R.id.tvPopularClassName)
        val progressPopularity: ProgressBar = itemView.findViewById(R.id.progressPopularity)
    }
}