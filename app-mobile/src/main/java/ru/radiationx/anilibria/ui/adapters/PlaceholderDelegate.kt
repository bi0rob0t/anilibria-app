package ru.radiationx.anilibria.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.databinding.ItemPlaceholderBinding
import ru.radiationx.anilibria.ui.common.adapters.AppAdapterDelegate
import ru.radiationx.shared.ktx.android.setCompatDrawable
import ru.radiationx.shared.ktx.android.setTintColorAttr

class PlaceholderDelegate :
    AppAdapterDelegate<PlaceholderListItem, ListItem, PlaceholderDelegate.ViewHolder>(
        R.layout.item_placeholder,
        { it is PlaceholderListItem },
        { ViewHolder(it) }
    ) {

    override fun bindData(item: PlaceholderListItem, holder: ViewHolder) =
        holder.bind(item.icRes, item.titleRes, item.descRes)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding by viewBinding<ItemPlaceholderBinding>()

        fun bind(icRes: Int, titleRes: Int, descRes: Int) {
            binding.itemPlaceholderIcon.setCompatDrawable(icRes)
            binding.itemPlaceholderIcon.setTintColorAttr(R.attr.colorOnSurface)
            binding.itemPlaceholderTitle.setText(titleRes)
            binding.itemPlaceholderDesc.setText(descRes)
        }
    }
}
