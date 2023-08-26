package eu.tutorials.kidsdrawingapp

import android.R
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.Nullable
import eu.tutorials.kidsdrawingapp.model.ListItemModel


class SettingsAdapter(private val mContext: Context, private val mData: List<ListItemModel>) :
    ArrayAdapter<ListItemModel?>(mContext, 0, mData) {
    override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {

        val convertView = LayoutInflater.from(mContext).inflate(eu.tutorials.kidsdrawingapp.R.layout.list_item_layout, parent, false)

        val item = mData[position]


        val imageView1 = convertView!!.findViewById<ImageView>(eu.tutorials.kidsdrawingapp.R.id.sss)
        val aSwitch = convertView.findViewById<Switch>(eu.tutorials.kidsdrawingapp.R.id.switch1)
        val title = convertView.findViewById<TextView>(eu.tutorials.kidsdrawingapp.R.id.minimize)
        val description = convertView.findViewById<TextView>(eu.tutorials.kidsdrawingapp.R.id.descript)

        imageView1.setImageResource(item.imageResId)
        imageView1.setImageResource(item.imageResId)
        title?.text = item.title
        description?.text = item.description

        val sharedPreferences = mContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        aSwitch.isChecked = sharedPreferences.getBoolean(item.title, item.isEnabled)

        aSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the updated switch state to SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putBoolean(item.title, isChecked)
            editor.apply()
        }
        return convertView
    }
}
