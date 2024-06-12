package com.carin.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.domain.enums.Role
import com.carin.domain.models.UserModel
import com.carin.utils.Resource
import com.carin.viewmodels.InfoUserViewModel

class ApprovalAdapter(
    private var approvals: List<UserModel>,
    private val context: Context,
    private val infoUserViewModel: InfoUserViewModel
) : RecyclerView.Adapter<ApprovalAdapter.ApprovalViewHolder>() {

    private var selectedRole: Role = Role.Driver

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApprovalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_approval, parent, false)
        return ApprovalViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApprovalViewHolder, position: Int) {
        holder.bind(approvals[position])
    }

    override fun getItemCount() = approvals.size

    fun updateUsers(newUsers: List<UserModel>) {
        approvals = newUsers
        notifyDataSetChanged()
    }

    private fun showPopupDialog(userModel: UserModel) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.account_approvals, null)
        val builder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
        val alertDialog = builder.create()

        val userRoleStrings = Role.entries.map {
            context.getString(context.resources.getIdentifier(it.stringKey, "string", context.packageName))
        }

        val spinner: Spinner = dialogView.findViewById(R.id.spinnerOptionsRole)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, userRoleStrings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected string
                val selectedString = parent.getItemAtPosition(position).toString()

                // Find the corresponding Role enum value
                selectedRole = Role.entries.find {
                    context.getString(context.resources.getIdentifier(it.stringKey, "string", context.packageName)) == selectedString
                }!!
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        val initialStatus = selectedRole
        val initialPosition = userRoleStrings.indexOf(
            context.getString(context.resources.getIdentifier(initialStatus.stringKey, "string", context.packageName))
        )
        if (initialPosition >= 0) {
            spinner.setSelection(initialPosition)
        }

        infoUserViewModel.userApprovalState.observe(context as LifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    val successMessage = context.getString(R.string.user_approved_success)
                    Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()

                    infoUserViewModel.loadUsersForApproval()
                    alertDialog.dismiss()
                }
                is Resource.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialogView.findViewById<TextView>(R.id.email).text = userModel.email
        dialogView.findViewById<ImageView>(R.id.closeIcon).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnYes).setOnClickListener {
            infoUserViewModel.approveUser(userModel.id, selectedRole)
        }

        dialogView.findViewById<Button>(R.id.btnNo).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    inner class ApprovalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val approveNameTxt: TextView = itemView.findViewById(R.id.approveNameTxt)
        val approveEmailTxt: TextView = itemView.findViewById(R.id.approveEmailTxt)
        val buttonCheck: Button = itemView.findViewById(R.id.button_check)

        fun bind(user: UserModel) {
            approveNameTxt.text = "${user.firstName} ${user.lastName}"
            approveEmailTxt.text = user.email

            buttonCheck.setOnClickListener {
                showPopupDialog(user)
            }
        }
    }
}
