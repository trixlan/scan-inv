package com.gercha.scan_inv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.gercha.scan_inv.clases.Bien

class TagAdapter(private val context: Context, private val dataSource: ArrayList<Bien>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = dataSource.size
    override fun getItem(position: Int): Any = dataSource[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = convertView ?: inflater.inflate(R.layout.item_tag, parent, false)
        val item = getItem(position) as Bien

        // Referenciar las vistas del XML item_tag
        val tvEpc = rowView.findViewById<TextView>(R.id.tv_epc)
        val tvNoInv = rowView.findViewById<TextView>(R.id.tv_no_inv)
        val tvResguardante = rowView.findViewById<TextView>(R.id.tv_resguardante)
        val tvDescipcion = rowView.findViewById<TextView>(R.id.tv_descripcion)

        // Asignar los valores
        tvEpc.text = "${item.epc}"
        tvNoInv.text = "Inv: ${item.noInventario}"
        tvResguardante.text = "Resg: ${item.resguardante}"
        tvDescipcion.text = "Desc: ${item.descripcion}"

        return rowView
    }
}