package com.carin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.HomeActivity

class ApprovalAdapter(
    private val approvals: List<HomeActivity.Approval>
) :
    RecyclerView.Adapter<ApprovalAdapter.ApprovalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApprovalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_approval, parent, false)
        return ApprovalViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApprovalViewHolder, position: Int) {
        val approval = approvals[position]
        holder.textView2.text = "${approval.fullname}"
        holder.textView3.text = "${approval.role}"
    }

    override fun getItemCount(): Int {
        return approvals.size
    }

    class ApprovalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView2: TextView = itemView.findViewById(R.id.textView2)
        val textView3: TextView = itemView.findViewById(R.id.textView3)
    }

}
