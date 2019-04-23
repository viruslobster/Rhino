package com.example.michael.rhino

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.michael.rhino.api.RhinoApi
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

private const val REQUEST_ACCOUNT_PICKER = 1000
private const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
private const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
private const val PREF_ACCOUNT_NAME = "accountName"

class LoadingActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private val SCOPES = listOf(SheetsScopes.SPREADSHEETS)
    private lateinit var credential: GoogleAccountCredential


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        credential = GoogleAccountCredential.usingOAuth2(applicationContext, SCOPES)
            .setBackOff(ExponentialBackOff())

        loadCredentialsAndServices()
    }

    private fun loadCredentialsAndServices() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices()
        } else if (credential.selectedAccountName == null) {
            chooseAccount()
        } else if (!isDeviceOnline()) {
            Log.e("TAG", "Device not online!")
        } else {
            Log.d("TAG", "actually make request here")
            // start activity
            RhinoApi.setCredentials(credential)
            val intent = Intent(this, ExercisePickerActivity::class.java)
            startActivity(intent)
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private fun chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            val accountName = getPreferences(Context.MODE_PRIVATE)
                .getString("accountName", null);
            if (accountName != null) {
                credential.selectedAccountName = accountName
                loadCredentialsAndServices()
            } else {
                startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)
            }
        } else {
            EasyPermissions.requestPermissions(
                this,"This app needs access to your Google account", REQUEST_PERMISSION_GET_ACCOUNTS,
                Manifest.permission.GET_ACCOUNTS
            )
        }

    }

    private fun isDeviceOnline(): Boolean {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo

        return networkInfo?.isConnected ?: false
    }

    private fun isGooglePlayServicesAvailable(): Boolean =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS


    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val code = apiAvailability.isGooglePlayServicesAvailable(this)
        if (apiAvailability.isUserResolvableError(code)) {
            showGooglePlayServicesAvailableilityErrorDialog(code)
        }
    }

    private fun showGooglePlayServicesAvailableilityErrorDialog(connectionStatusCode: Int) {
        Log.d("TAG", "showGoogle blah blah blah")
        val apiAvailability = GoogleApiAvailability.getInstance()
        apiAvailability.getErrorDialog(this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES)
            .show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> {
                if (resultCode != Activity.RESULT_OK) {
                    Log.e("TAG", "Please install google play services")
                } else {
                    loadCredentialsAndServices()
                }
            }

            REQUEST_ACCOUNT_PICKER -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                    val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    if (accountName != null) {
                        val settings = getPreferences(Context.MODE_PRIVATE)
                        val editor = settings.edit()
                        editor.putString(PREF_ACCOUNT_NAME, accountName)
                        editor.apply()
                        credential.selectedAccountName = accountName
                        loadCredentialsAndServices()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }
}
