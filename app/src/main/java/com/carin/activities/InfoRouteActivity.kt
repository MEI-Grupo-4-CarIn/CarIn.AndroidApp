package com.carin.activities

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.carin.R
import com.carin.domain.enums.Role
import com.carin.utils.AuthUtils

class InfoRouteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_route)

        val userAuths = AuthUtils.getUserAuth(this)

        userAuths?.let {
            adjustUIBasedOnRole(it.role)
        }

        val iconImageView = findViewById<ImageView>(R.id.iconImageView)
        iconImageView.setOnClickListener {
            startActivity(Intent(this, RoutesListActivity::class.java))
        }

        val textViewSeeMore = findViewById<TextView>(R.id.textViewSeeMore)
        textViewSeeMore.setOnClickListener {
            startActivity(Intent(this, InfoVehicleActivity::class.java))
        }

        val textViewSeeMoreDriver = findViewById<TextView>(R.id.textViewSeeMoreDriver)
        textViewSeeMoreDriver.setOnClickListener {
            startActivity(Intent(this, InfoUserActivity::class.java))
        }

        val optionsIcon = findViewById<ImageView>(R.id.optionsIcon)
        optionsIcon.setOnClickListener { view ->

            val popupMenu = PopupMenu(ContextThemeWrapper(this, R.style.PopupMenu), view)

            val editItem = popupMenu.menu.add(Menu.NONE, R.id.edit, Menu.NONE, "Edit")
            val deleteItem = popupMenu.menu.add(Menu.NONE, R.id.delete, Menu.NONE, "Delete")

            val editIcon = ContextCompat.getDrawable(this, R.drawable.ic_edit)
            editIcon?.setBounds(0, 0, editIcon.intrinsicWidth, editIcon.intrinsicHeight)
            val editIconSpan = editIcon?.let { ImageSpan(it, ImageSpan.ALIGN_BOTTOM) }
            val editItemTitle = SpannableString(" ${getString(R.string.edit)}")
            editItemTitle.setSpan(editIconSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            editItem.title = editItemTitle

            val deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)
            deleteIcon?.setBounds(0, 0, deleteIcon.intrinsicWidth, deleteIcon.intrinsicHeight)
            val logoutIconSpan = deleteIcon?.let { ImageSpan(it, ImageSpan.ALIGN_BOTTOM) }
            val logoutItemTitle = SpannableString(" ${getString(R.string.delete)}")
            logoutItemTitle.setSpan(logoutIconSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            deleteItem.title = logoutItemTitle

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        startActivity(Intent(this, EditRouteActivity::class.java))
                        true
                    }
                    R.id.delete -> {
                        showDeleteConfirmationDialog()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun adjustUIBasedOnRole(role: Role) {
        when (role) {
            Role.Admin -> showAdminComponents()
            Role.Manager -> showManagerComponents()
            Role.Driver -> showDriverComponents()
        }
    }

    private fun showAdminComponents() {

    }
    private fun showManagerComponents() {

    }

    private fun showDriverComponents() {
        findViewById<View>(R.id.optionsIcon).visibility = View.GONE

    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.delete_confirmation, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        btnYes.setOnClickListener {

            dialog.dismiss()
        }

        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
    }
}


