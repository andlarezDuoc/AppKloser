package com.example.appcontact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcontact.models.User
import com.example.appcontact.viewmodels.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var rvContacts: RecyclerView
    private lateinit var fabCamera: FloatingActionButton
    private var photoUri: Uri? = null
    private var tempImageUri: Uri? = null

    private lateinit var viewModel: MainViewModel


    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri = tempImageUri
            showCustomToast("Foto tomada. Ahora presiona Enviar.")
        } else {
            showCustomToast("No se tomó la foto")
        }
    }

    private var selectedUser: User? = null
    private var adapter: ContactAdapter? = null

    private lateinit var cvCustomToast: androidx.cardview.widget.CardView
    private lateinit var tvCustomToast: android.widget.TextView
    private val hideToastHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private val hideToastRunnable = Runnable {
        cvCustomToast.visibility = android.view.View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        rvContacts = findViewById(R.id.rvContacts)
        rvContacts.layoutManager = LinearLayoutManager(this)

        fabCamera = findViewById(R.id.fabCamera)
        fabCamera.isEnabled = false
        fabCamera.alpha = 0.5f

        cvCustomToast = findViewById(R.id.cvCustomToast)
        tvCustomToast = findViewById(R.id.tvCustomToast)
        
        fabCamera.setOnClickListener {
            takePhoto()
        }

        findViewById<android.widget.Button>(R.id.btnHistory).setOnClickListener { showHistory() }
        findViewById<android.widget.Button>(R.id.btnLogout).setOnClickListener { logout() }
        findViewById<android.widget.Button>(R.id.btnAdd).setOnClickListener { showAddUserDialog() }

        setupObservers()
        viewModel.loadUsers()
    }

    private fun showCustomToast(message: String) {
        tvCustomToast.text = message
        cvCustomToast.visibility = android.view.View.VISIBLE
        cvCustomToast.bringToFront()

        hideToastHandler.removeCallbacks(hideToastRunnable)
        hideToastHandler.postDelayed(hideToastRunnable, 2000)
    }

    private fun setupObservers() {
        viewModel.users.observe(this) { users ->
            android.util.Log.d("MVVM_DEBUG", "Users received: ${users.size}")
            
            adapter = ContactAdapter(users, 
                onItemClick = { user ->
                    selectUser(user)
                },
                onItemLongClick = { user ->
                    selectUser(user)
                },
                onDeleteClick = { user ->
                    confirmDeleteUser(user)
                },
                onSendClick = { user ->
                    addToHistory(user)
                }
            )
            rvContacts.adapter = adapter
        }

        viewModel.errorMessage.observe(this) { message ->
            showCustomToast(message)
        }

        viewModel.operationStatus.observe(this) { message ->
            showCustomToast(message)
            if (message == "Contacto eliminado") {
                selectedUser = null
                fabCamera.isEnabled = false
                fabCamera.alpha = 0.5f
                adapter?.clearSelection()
            }
        }
    }

    private fun showAddUserDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_user, null)
        val etName = dialogView.findViewById<android.widget.EditText>(R.id.etName)
        val etEmail = dialogView.findViewById<android.widget.EditText>(R.id.etEmail)
        val btnCancel = dialogView.findViewById<android.widget.Button>(R.id.btnCancel)
        val btnAddUser = dialogView.findViewById<android.widget.Button>(R.id.btnAddUser)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnAddUser.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty()) {
                if (isValidEmail(email)) {
                    viewModel.addUser(name, email)
                    dialog.dismiss()
                } else {
                    showCustomToast("Email inválido")
                }
            } else {
                showCustomToast("Campos vacíos")
            }
        }

        dialog.show()
    }

    private fun confirmDeleteUser(user: User) {
        if (user.id == null) {
            showCustomToast("Error: Usuario sin ID")
            return
        }
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Eliminar a ${user.name}?")
            .setPositiveButton("Sí") { _, _ ->
                viewModel.deleteUser(user)
            }
            .setNegativeButton("No", null)
            .show()
    }


    private fun selectUser(user: User) {
        selectedUser = user
        fabCamera.isEnabled = true
        fabCamera.alpha = 1.0f
    }

    private fun takePhoto() {
        try {
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile("IMG_${System.currentTimeMillis()}_", ".jpg", storageDir)
            tempImageUri = FileProvider.getUriForFile(this, "com.example.appcontact.fileprovider", file)
            takePictureLauncher.launch(tempImageUri)
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("CAMERA_DEBUG", "Error taking photo: ${e.message}", e)
            showCustomToast("Error: ${e.message}")
        }
    }

    private fun addToHistory(user: User) {
        if (photoUri == null) {
            showCustomToast("Debe tomar una foto antes de enviar mensaje")
            return
        }
        val prefs = getSharedPreferences("AppHistory", MODE_PRIVATE)
        val currentHistory = prefs.getStringSet("history", mutableSetOf()) ?: mutableSetOf()
        
        val timestamp = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        val newEntry = "Mensaje a: ${user.name} - $timestamp"
        
        currentHistory.add(newEntry)
        
        prefs.edit().putStringSet("history", currentHistory).apply()
        
        showCustomToast("Foto enviada con exito al historial")
    }

    private fun showHistory() {
        val prefs = getSharedPreferences("AppHistory", MODE_PRIVATE)
        val historySet = prefs.getStringSet("history", setOf()) ?: setOf()
        
        val historyList = historySet.toList().sortedDescending()
        
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Historial de Mensajes")

        if (historyList.isEmpty()) {
            builder.setMessage("No hay historial")
        } else {
            builder.setItems(historyList.toTypedArray(), null)
        }
        
        builder.setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
        
        if (historyList.isNotEmpty()) {
            builder.setNeutralButton("Borrar Todo") { dialog, _ ->
                prefs.edit().clear().apply()
                showCustomToast("Historial borrado")
            }
        }
        
        builder.show()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun logout() {
        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply()
        
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
