package com.strimm.application.ui.adapters

import android.app.Activity
import android.graphics.drawable.PictureDrawable
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.strimm.application.R
import com.strimm.application.databinding.CategoryItemBinding
import com.strimm.application.model.CategoriesItem
import com.strimm.application.ui.interfaces.OnCategoryItemClick
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class CategoriesAdapter(
    var activity: Activity,
    var onCategoryItemClick: OnCategoryItemClick,
    private var categories: ArrayList<CategoriesItem>
) : RecyclerView.Adapter<CategoriesAdapter.CategoriesHolder>() {

    var selectedItem: Int = 0

    inner class CategoriesHolder(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(categories: CategoriesItem, position: Int) {
            binding.apply {

                if (selectedItem == position) {

                    cardCat.setBackgroundResource(R.drawable.button_bg)

                } else {

                    cardCat.setBackgroundResource(R.drawable.button_bg)

                }

                if (categories.svg.isEmpty()) {
                    categoryImg.visibility = View.GONE
                } else {
                    categoryImg.visibility = View.VISIBLE
                    DownloadImageTask(categoryImg).execute(categories.svg)
                }

                categoryNameTxt.text = categories.name

                itemView.setOnClickListener {
                    onCategoryItemClick.categoryItemClick(categories, position)
                    selectedItem = position
                    notifyDataSetChanged()
                }

            }

        }


    }

    private inner class DownloadImageTask(private val imageView: ImageView) :
        AsyncTask<String, Void, SVG?>() {
        override fun doInBackground(vararg params: String?): SVG? {
            try {
                val url = URL(params[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream: InputStream = connection.inputStream
                return SVG.getFromInputStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: SVGParseException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(svg: SVG?) {
            if (svg != null) {
                val drawable = PictureDrawable(svg.renderToPicture())
                imageView.setImageDrawable(drawable)
            }
        }
    }

    fun selectedItemPos(position: Int) {
        selectedItem = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesHolder =
        CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .run { CategoriesHolder(this) }

    override fun onBindViewHolder(holder: CategoriesHolder, position: Int) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount(): Int = categories.size
}