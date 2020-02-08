package ru.radiationx.anilibria.ui.common.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate
import java.lang.Exception

abstract class AppAdapterDelegate<I : T, T, VH : androidx.recyclerview.widget.RecyclerView.ViewHolder>(
        protected val layoutRes: Int? = null,
        protected val viewChecker: ((T) -> Boolean)? = null,
        protected val holderCreator: ((View) -> VH)? = null
) : AbsListItemAdapterDelegate<I, T, VH>() {

    open fun bindData(item: I, holder: VH) {}

    open fun applyPayloads(item: I, payloads: MutableList<Any>, holder: VH) {}

    // Использовать только если нужна кастомная логика или viewChecker == null
    override fun isForViewType(item: T, items: MutableList<T>, position: Int): Boolean {
        return viewChecker!!.invoke(item)
    }

    // Использовать только если нужна кастомная логика
    override fun onBindViewHolder(item: I, viewHolder: VH, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            applyPayloads(item, payloads, viewHolder)
        } else {
            bindData(item, viewHolder)
        }
    }

    // Использовать только если нужна кастомная логика или layoutRes или holderCreator == null
    override fun onCreateViewHolder(parent: ViewGroup): VH {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes!!, parent, false)
        return holderCreator!!.invoke(view)
    }
}