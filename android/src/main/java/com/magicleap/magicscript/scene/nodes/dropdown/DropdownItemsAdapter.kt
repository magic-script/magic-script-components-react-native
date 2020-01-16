package com.magicleap.magicscript.scene.nodes.dropdown

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes

class DropdownItemsAdapter(
    context: Context,
    @LayoutRes private val layoutRes: Int,
    items: List<UiDropdownListItemNode>
) : ArrayAdapter<UiDropdownListItemNode>(context, layoutRes, items) {

    // in pixels
    var textSizePx = 12

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutRes, null)
        view as TextView

        getItem(position)?.let { item ->
            view.text = item.label
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx.toFloat())
        }

        return view
    }

}