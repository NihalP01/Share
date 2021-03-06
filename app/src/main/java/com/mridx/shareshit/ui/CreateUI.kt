package com.mridx.shareshit.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.mridx.shareshit.R
import com.mridx.shareshit.util.PermissionHandler
import com.mridx.shareshit.util.Utils
import com.mridx.shareshit.viewmodel.CreateUIViewModel
import kotlinx.android.synthetic.main.create_ui.*

class CreateUI : AppCompatActivity() {

    lateinit var viewModel: CreateUIViewModel
    lateinit var wifiManager: WifiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_ui)

        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        viewModel = ViewModelProvider(this).get(CreateUIViewModel::class.java)

        turnOnHotspot()

        viewModel.qr.observe(this, {
            showQr(it)
        })

        viewModel.connectionInfo.observe(this, {
            if (it.success) {
                Utils.CLIENT_IP = it.ip ?: "192.168.43.1"
                // TODO: 20/09/20 open files
                Log.d("kaku", "onCreate: ${it.ip}")
                Toast.makeText(this, "Connected to ${it.ip}", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun showQr(it: Bitmap?) {
        if (it == null) return
        Glide.with(this).load(it).into(qrView)
        createProgress.visibility = View.GONE
    }

    fun turnOnHotspot() {
        viewModel.turnOnHotspot(this, wifiManager)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PermissionHandler.SYSTEM_PERMISSION_REQ) {
            turnOnHotspot()
        } else if (requestCode == PermissionHandler.APP_SETTINGS_REQ) {
            turnOnHotspot()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionHandler.LOCATION_PERMISSION_REQ) {
            turnOnHotspot()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy()
    }

}
