package eu.tutorials.trelloapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.tutorials.trelloapp.R
import eu.tutorials.trelloapp.activities.TaskListActivity
import eu.tutorials.trelloapp.models.Card
import eu.tutorials.trelloapp.models.SelectedMembers

open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>,

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            if(model.labelColor.isNotEmpty()){
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.VISIBLE
                holder.itemView.findViewById<View>(R.id.view_label_color).setBackgroundColor(Color.parseColor(model.labelColor))
            } else {
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.GONE
            }
            holder.itemView.findViewById<TextView>(R.id.tv_card_name).text = model.name

            if((context as TaskListActivity).mAssignedMemberDetailList.size >0) {
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
                val assignedMembers = context.mAssignedMemberDetailList

                for(i in context.mAssignedMemberDetailList.indices) {
                    for(j in model.assignedTo) {
                        if(context.mAssignedMemberDetailList[i].id == j) {
                            val selectedMembers = SelectedMembers(
                                                        context.mAssignedMemberDetailList[i].id,
                                                        context.mAssignedMemberDetailList[i].image
                                                    )
                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }

                if(selectedMembersList.size > 0){
                //if the only member in the list is the creator - do not show the recycler view
                    if(selectedMembersList.size == 1
                        && selectedMembersList[0].id == model.createdBy) {
                            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).visibility = View.GONE
                    } else { //add all the assigned members to the recycler view under the card name create and set the adapter
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).visibility = View.VISIBLE
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).layoutManager =
                            GridLayoutManager(context, 4)
                        val adapter = CardMemberListsAdapter(context, selectedMembersList, false)
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).adapter = adapter
                        adapter.setOnClickListener(
                            object : CardMemberListsAdapter.OnClickListener{
                                override fun onClick(position: Int, model: SelectedMembers) {
                                    if(onClickListener != null) {
                                        onClickListener!!.onClick(position)
                                    }
                                }
                        })
                    }
                }  else {
                    holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).visibility = View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
