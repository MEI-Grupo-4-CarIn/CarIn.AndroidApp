package com.carin.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.HomeActivity

class ApprovalAdapter(
    private val approvals: MutableList<HomeActivity.Approval>,
    private val context: Context
) : RecyclerView.Adapter<ApprovalAdapter.ApprovalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApprovalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_approval, parent, false)
        return ApprovalViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApprovalViewHolder, position: Int) {
        val approval = approvals[position]
        holder.approveNameTxt.text = approval.fullname
        holder.approveEmailTxt.text = approval.role

        holder.buttonCheck.setOnClickListener {
            showPopupDialog()
        }

        holder.buttonCross.setOnClickListener {
            removeItem(position)
        }
    }

    override fun getItemCount(): Int {
        return approvals.size
    }

    private fun showPopupDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.account_approvals, null)
        val builder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
        val alertDialog = builder.create()

        dialogView.findViewById<ImageView>(R.id.closeIcon).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnYes).setOnClickListener {
            // Ação para o botão "Yes"
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnNo).setOnClickListener {
            // Ação para o botão "No"
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun removeItem(position: Int) {
        approvals.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, approvals.size)
        if (approvals.isEmpty() && context is HomeActivity) {
            (context as HomeActivity).checkIfListIsEmpty()
        }
    }

    class ApprovalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val approveNameTxt: TextView = itemView.findViewById(R.id.approveNameTxt)
        val approveEmailTxt: TextView = itemView.findViewById(R.id.approveEmailTxt)
        val buttonCheck: Button = itemView.findViewById(R.id.button_check)
        val buttonCross: Button = itemView.findViewById(R.id.button_cross)
    }
}
