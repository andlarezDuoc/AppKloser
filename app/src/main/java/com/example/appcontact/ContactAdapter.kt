package com.example.appcontact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcontact.models.User

class ContactAdapter(
    private val users: List<User>,
    private val onItemClick: (User) -> Unit,
    private val onItemLongClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit,
    private val onSendClick: (User) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    var selectedPosition = RecyclerView.NO_POSITION

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: androidx.cardview.widget.CardView = view as androidx.cardview.widget.CardView
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val btnDeleteItem: android.widget.ImageButton = view.findViewById(R.id.btnDeleteItem)
        val btnSendItem: android.widget.ImageButton = view.findViewById(R.id.btnSendItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.tvName.text = user.name ?: "Desconocido"
        holder.tvEmail.text = user.email ?: "Sin correo"

        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(android.graphics.Color.DKGRAY)
            holder.btnDeleteItem.visibility = View.VISIBLE
        } else {
            holder.cardView.setCardBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"))
            holder.btnDeleteItem.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onItemClick(user)
        }

        holder.itemView.setOnLongClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onItemLongClick(user)
            true
        }
        
        holder.btnDeleteItem.setOnClickListener {
            onDeleteClick(user)
        }
        
        holder.btnSendItem.setOnClickListener {
            onSendClick(user)
        }
    }

    override fun getItemCount() = users.size

    fun clearSelection() {
        val previousPosition = selectedPosition
        selectedPosition = RecyclerView.NO_POSITION
        notifyItemChanged(previousPosition)
    }
}
