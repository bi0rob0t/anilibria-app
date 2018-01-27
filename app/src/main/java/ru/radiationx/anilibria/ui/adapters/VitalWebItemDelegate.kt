package ru.radiationx.anilibria.ui.adapters

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_vital_web.view.*
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.entity.app.vital.VitalItem
import android.webkit.WebViewClient
import ru.radiationx.anilibria.utils.Utils


/**
 * Created by radiationx on 13.01.18.
 */
class VitalWebItemDelegate(val inDetail: Boolean = false) : AdapterDelegate<MutableList<ListItem>>() {


    override fun isForViewType(items: MutableList<ListItem>, position: Int): Boolean = items[position] is VitalWebListItem

    override fun onBindViewHolder(items: MutableList<ListItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        val item = items[position] as VitalWebListItem
        (holder as ViewHolder).bind(item.item)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_vital_web, parent, false)
    )

    private inner class ViewHolder(val holderView: View) : RecyclerView.ViewHolder(holderView) {

        lateinit var currentItem: VitalItem

        init {
            holderView.run {
                if (inDetail) {
                    holderView.item_card.cardElevation = 0f
                }
                vitalWebView.settings.apply {
                    //useWideViewPort = true
                    //loadWithOverviewMode = true
                    setRenderPriority(WebSettings.RenderPriority.LOW)
                }
                vitalWebView.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        progressSwitcher.displayedChild = 0
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        progressSwitcher.displayedChild = 1
                    }

                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        Utils.externalLink(url.toString())
                        return true
                    }
                }
            }
        }

        fun bind(item: VitalItem) {
            if (!::currentItem.isInitialized || currentItem != item) {
                currentItem = item
                holderView.run {
                    vitalWebView.easyLoadData("https://www.anilibria.tv/", item.contentText)
                }
            }
        }
    }
}