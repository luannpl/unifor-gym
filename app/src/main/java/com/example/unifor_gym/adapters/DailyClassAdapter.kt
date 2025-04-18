package com.example.unifor_gym.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.models.DailyClass
import com.example.unifor_gym.R


class DailyClassAdapter(private var classes: List<DailyClass>) :
    RecyclerView.Adapter<DailyClassAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aulas_diarias, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dailyClass = classes[position]
        holder.tvClassName.text = dailyClass.name
        holder.tvClassTime.text = dailyClass.timeRange
    }

    override fun getItemCount(): Int = classes.size

    fun updateData(newClasses: List<DailyClass>) {
        this.classes = newClasses
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvClassName: TextView = itemView.findViewById(R.id.tvClassName)
        val tvClassTime: TextView = itemView.findViewById(R.id.tvClassTime)
    }
}