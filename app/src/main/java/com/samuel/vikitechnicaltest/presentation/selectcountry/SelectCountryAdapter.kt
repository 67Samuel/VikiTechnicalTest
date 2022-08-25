package com.samuel.vikitechnicaltest.presentation.selectcountry

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.samuel.vikitechnicaltest.R
import com.samuel.vikitechnicaltest.business.domain.models.Country
import com.samuel.vikitechnicaltest.databinding.FragmentSelectCountryBinding
import com.samuel.vikitechnicaltest.databinding.SelectCountryListItemBinding
import de.hdodenhof.circleimageview.CircleImageView

/**
 * - List of objects are kept in the AsyncListDiffer differ, referenced by differ.currentList
 */

class SelectCountryAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Country>() {

        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.currencyCode == newItem.currencyCode
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.rate == newItem.rate
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return SelectCountryViewHolder(
            SelectCountryListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SelectCountryViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // for submitting new list items to the current list
    fun submitList(list: List<Country>) {
        differ.submitList(list)
    }

    class SelectCountryViewHolder
    constructor(
        private val binding: SelectCountryListItemBinding,
        private val interaction: Interaction?,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Country) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            binding.countryName.text = item.name
            binding.countryCurrencyCode.text = item.currencyCode
            binding.countryImage.setImageResource(item.imageId)
        }
    }

    // interface for detecting clicks
    // usage: pass in an object that extends Interaction when initializing the adapter
    interface Interaction {
        fun onItemSelected(position: Int, item: Country)
    }
}