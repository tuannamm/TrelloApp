package eu.tutorials.trelloapp.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.tutorials.trelloapp.R
import eu.tutorials.trelloapp.models.Task

open class TaskListItemsAdapter(
    private val context: Context,
    private  var list: ArrayList<Task>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        var layoutParams =  ViewGroup.LayoutParams(
            (parent.width * 0.7).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
//        layoutParams.setMargins((15.toDP()).toPX(), 0, (40.toDP()).toPX(), 0)
        view.layoutParams = layoutParams
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder) {
            if(position == list.size - 1) {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.ll_task_item).visibility = View.GONE
            } else {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.findViewById<TextView>(R.id.ll_task_item).visibility = View.VISIBLE
            }
        }
    }

    private fun Int.toDP(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPX(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}

