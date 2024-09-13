
package eu.on.screen

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import eu.on.screen.ViewModel.HideDrawViewModel
import yuku.ambilwarna.AmbilWarnaDialog


class ColorPickerActivity : AppCompatActivity() {
    private lateinit var viewModel: HideDrawViewModel
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        viewModel = HideDrawViewModel.getInstance()
        var getSetColor: Int = sharedPreferences!!.getInt("setColor", -1)

        fun callTosendColor(color: Int){
        val intent = Intent("action.PickColor").apply {
            putExtra("pickColor", color)
            sendBroadcast(this)
        }
        }
       AmbilWarnaDialog(this, getSetColor,
                object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onCancel(dialog: AmbilWarnaDialog) {
                        val intent = Intent("action.hideDraw")
                        intent.putExtra("hideDraw", viewModel.hideDraw.value)
                        sendBroadcast(intent)
                        finish()
                    }

                    override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                        callTosendColor(color)
                        println(color)
                        val intent = Intent("action.hideDraw")
                        intent.putExtra("hideDraw", viewModel.hideDraw.value)
                        sendBroadcast(intent)
                        finish()
                    }
                }).show()
    }

}
