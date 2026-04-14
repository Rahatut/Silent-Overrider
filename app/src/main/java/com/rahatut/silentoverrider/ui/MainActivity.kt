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
import com.rahatut.silentoverrider.service.RingService
import com.rahatut.silentoverrider.storage.TriggerSettingsStore
import com.rahatut.silentoverrider.storage.WhitelistStore

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var whitelistStore: WhitelistStore
    private lateinit var settingsStore: TriggerSettingsStore
    private lateinit var adapter: ArrayAdapter<String>
    private var selectedNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        whitelistStore = WhitelistStore(applicationContext)
        settingsStore = TriggerSettingsStore(applicationContext)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, whitelistStore.getAll().toMutableList())
        binding.whitelistList.adapter = adapter
        binding.keywordInput.setText(settingsStore.getKeyword())
        binding.durationInput.setText(settingsStore.getAlertDurationSeconds().toString())
        updateSelectedNumberLabel(null)

        ensureRuntimePermissions()

        binding.addButton.setOnClickListener {
            val raw = binding.phoneInput.text?.toString().orEmpty()
            val added = whitelistStore.add(raw)
            if (added) {
                reloadList()
                binding.phoneInput.text?.clear()
                toast("Number added")
            } else {
                toast("Invalid or duplicate number")
            }
        }

        binding.removeButton.setOnClickListener {
            val number = selectedNumber
            if (number.isNullOrBlank()) {
                toast("Select a number from the list first")
                return@setOnClickListener
            }

            if (whitelistStore.remove(number)) {
                reloadList()
                updateSelectedNumberLabel(null)
                toast("Number removed")
            } else {
                toast("Unable to remove number")
            }
        }

        binding.whitelistList.setOnItemClickListener { _, _, position, _ ->
            val selected = adapter.getItem(position).orEmpty()
            selectedNumber = selected
            updateSelectedNumberLabel(selected)
        }

        binding.whitelistList.setOnItemLongClickListener { _, _, position, _ ->
            val selected = adapter.getItem(position).orEmpty()
            selectedNumber = selected
            updateSelectedNumberLabel(selected)
            toast("Selected: $selected")
            true
        }

        binding.saveSettingsButton.setOnClickListener {
            val keyword = binding.keywordInput.text?.toString().orEmpty().trim()
            val durationRaw = binding.durationInput.text?.toString().orEmpty().trim()
            val duration = durationRaw.toIntOrNull()

            val keywordSaved = settingsStore.setKeyword(keyword)
            val durationSaved = duration?.let { settingsStore.setAlertDurationSeconds(it) } ?: false

            if (!keywordSaved) {
                toast("Keyword cannot be empty")
                return@setOnClickListener
            }
            if (!durationSaved) {
                toast("Duration must be 5-300 seconds")
                return@setOnClickListener
            }

            toast("Settings saved")
        }

        binding.testAlertButton.setOnClickListener {
            ContextCompat.startForegroundService(this, android.content.Intent(this, RingService::class.java))
            toast("Test alert started")
        }
    }

    private fun reloadList() {
        adapter.clear()
        adapter.addAll(whitelistStore.getAll())
        adapter.notifyDataSetChanged()
        if (selectedNumber != null && !whitelistStore.contains(selectedNumber.orEmpty())) {
            updateSelectedNumberLabel(null)
        }
    }

    private fun updateSelectedNumberLabel(number: String?) {
        selectedNumber = number
        val label = if (number.isNullOrBlank()) "None" else number
        binding.selectedNumberValue.text = label
    }

    private fun ensureRuntimePermissions() {
        val required = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            required += Manifest.permission.RECEIVE_SMS
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            required += Manifest.permission.READ_PHONE_STATE
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            required += Manifest.permission.READ_CALL_LOG
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
