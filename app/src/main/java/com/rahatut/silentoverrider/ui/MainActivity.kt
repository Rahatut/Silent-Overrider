package com.rahatut.silentoverrider.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rahatut.silentoverrider.databinding.ActivityMainBinding
import com.rahatut.silentoverrider.storage.WhitelistStore

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var whitelistStore: WhitelistStore
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        whitelistStore = WhitelistStore(applicationContext)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, whitelistStore.getAll().toMutableList())
        binding.whitelistList.adapter = adapter

        ensureRuntimePermissions()

        binding.addButton.setOnClickListener {
            val raw = binding.phoneInput.text?.toString().orEmpty()
            val added = whitelistStore.add(raw)
            if (added) {
                reloadList()
                binding.phoneInput.text?.clear()
            } else {
                toast("Invalid or duplicate number")
            }
        }

        binding.whitelistList.setOnItemLongClickListener { _, _, position, _ ->
            val selected = adapter.getItem(position).orEmpty()
            whitelistStore.remove(selected)
            reloadList()
            true
        }
    }

    private fun reloadList() {
        adapter.clear()
        adapter.addAll(whitelistStore.getAll())
        adapter.notifyDataSetChanged()
    }

    private fun ensureRuntimePermissions() {
        val required = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            required += Manifest.permission.RECEIVE_SMS
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            required += Manifest.permission.POST_NOTIFICATIONS
        }

        if (required.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, required.toTypedArray(), REQUEST_PERMISSIONS_CODE)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_PERMISSIONS_CODE = 100
    }
}
