package com.carin.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.carin.R

class InfoRouteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_route)

        val iconImageView = findViewById<ImageView>(R.id.iconImageView)
        iconImageView.setOnClickListener {
            startActivity(Intent(this, RouteActivity::class.java))
        }

        val optionsIcon = findViewById<ImageView>(R.id.optionsIcon)
        optionsIcon.setOnClickListener { view ->
            showOptionsMenu(view)
        }
    }

    private fun showOptionsMenu(view: View) {
        val popupMenu = PopupMenu(this, view as ImageView)
        popupMenu.inflate(R.menu.menu_route)

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


