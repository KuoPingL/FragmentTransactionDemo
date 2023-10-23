package com.demo.fragmenttransaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.demo.fragmenttransaction.R
import com.demo.fragmenttransaction.databinding.ItemFragmentsBinding

class FragmentAdapter(private val _selectListener: SelectListener?): ListAdapter<FragmentAdapter.Data, FragmentAdapter.DataViewHolder>(
    DataDiffUtil()
) {

    companion object {
        const val BASE_UUID = "NONE"
        val baseData = Data("BOTTOM", -1, R.color.color_undecided)
    }

    fun clearSelectedData() {
        _selectedData = null
        _selectedPosition = RecyclerView.NO_POSITION
    }

    private var _selectedData: Data? = null
    private var _selectedPosition = RecyclerView.NO_POSITION

    override fun submitList(list: MutableList<Data>?) {
        list?.add(baseData)
        super.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemFragmentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)

        _selectedData?.let {
            holder.setSelected(data == it)
        } ?: holder.setSelected(false) // fix holder still remember it was selected

        holder.itemView.setOnClickListener {

            if (data == baseData) {
                if (itemCount == 1) _selectListener?.onSelect(baseData)
                return@setOnClickListener
            }

            // if a data was selected
            if (_selectedData != null) {
                if (_selectedData == data) {
                    // if this data was selected previously
                    // then simply reset _selectedData and _selectedPosition then refresh
                    notifyItemChanged(position)
                    clearSelectedData()
                } else {
                    // if there was a selected data, then update _selectedData, then update change to the previous position
                    _selectedData = data
                    notifyItemChanged(_selectedPosition)
                    // then update the new position, and again refresh the new position
                    _selectedPosition = holder.adapterPosition
                    notifyItemChanged(_selectedPosition)
                    // tell selectListener that a new data was selected
                    _selectListener?.onSelect(_selectedData)
                }
            } else {
                // if _selectedData == null, then simply update both _selectedData and _selectedPosition
                // and notifyChange, followed by _selectListener onSelect
                _selectedData = data
                _selectedPosition = holder.adapterPosition
                notifyItemChanged(_selectedPosition)
                _selectListener?.onSelect(_selectedData)
            }
        }
    }

    data class Data (val name: String,
                     var entry: Int,
                     var colorRes: Int = R.color.color_undecided
    )

    class DataViewHolder(private val _binding: ItemFragmentsBinding): ViewHolder(_binding.root) {
        fun bind(data: Data) {
            _binding.data = data
            _binding.isSelected = false
        }
        fun setSelected(selected: Boolean) {
            _binding.isSelected = selected
        }
    }

    class DataDiffUtil: DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean = oldItem == newItem
    }

    interface SelectListener {
        fun onSelect(data: Data?)
    }
}