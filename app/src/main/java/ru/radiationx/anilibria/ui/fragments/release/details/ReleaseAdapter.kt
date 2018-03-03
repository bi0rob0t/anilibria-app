package ru.radiationx.anilibria.ui.fragments.release.details

/* Created by radiationx on 18.11.17. */

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import ru.radiationx.anilibria.App
import ru.radiationx.anilibria.entity.app.release.ReleaseFull
import ru.radiationx.anilibria.entity.app.vital.VitalItem
import ru.radiationx.anilibria.ui.adapters.*
import ru.radiationx.anilibria.ui.adapters.global.CommentRouteDelegate
import ru.radiationx.anilibria.ui.adapters.other.DividerShadowItemDelegate
import ru.radiationx.anilibria.ui.adapters.release.detail.*
import java.util.*

class ReleaseAdapter(private var itemListener: ItemListener) : ListDelegationAdapter<MutableList<ListItem>>() {

    private val vitalItems = mutableListOf<VitalItem>()

    private var currentRelease: ReleaseFull? = null
    private var currentTabTag = ReleaseEpisodesHeadDelegate.TAG_ONLINE
    private var reverseEpisodes = App.injections.appPreferences.getEpisodesIsReverse()
    private val remindCloseListener = object : ReleaseRemindDelegate.Listener {
        override fun onClickClose(position: Int) {
            items.removeAt(position)
            items.removeAt(position)
            notifyItemRangeRemoved(position, 2)
            App.injections.appPreferences.setReleaseRemind(false)
        }
    }

    private val episodeHeadListener = object : ReleaseEpisodesHeadDelegate.Listener {
        override fun onSelect(tabTag: String, position: Int) {
            currentTabTag = tabTag
            currentRelease?.let {
                val startPos = items.indexOfFirst { it is ReleaseEpisodeListItem }
                items.removeAll { it is ReleaseEpisodeListItem }
                items.addAll(startPos, prepareEpisodeItems(it))
                notifyItemRangeChanged(startPos, items.size)
                return@let
            }
        }
    }

    init {
        items = mutableListOf()
        delegatesManager.run {
            addDelegate(ReleaseHeadDelegate(itemListener))
            addDelegate(ReleaseEpisodeDelegate(itemListener))
            addDelegate(ReleaseEpisodeControlDelegate(itemListener))
            addDelegate(ReleaseEpisodesHeadDelegate(episodeHeadListener))
            addDelegate(ReleaseDonateDelegate(itemListener))
            addDelegate(ReleaseRemindDelegate(remindCloseListener))
            addDelegate(ReleaseBlockedDelegate())
            addDelegate(CommentRouteDelegate())
            addDelegate(DividerShadowItemDelegate())
            addDelegate(VitalWebItemDelegate(true))
            addDelegate(VitalNativeItemDelegate(true))
        }
    }

    private val random = Random()

    private fun rand(from: Int, to: Int): Int {
        return random.nextInt(to - from) + from
    }

    fun setVitals(vitals: List<VitalItem>) {
        vitalItems.clear()
        vitalItems.addAll(vitals)
    }

    private fun getVitalListItem(item: VitalItem) = when (item.contentType) {
        VitalItem.ContentType.WEB -> VitalWebListItem(item)
        else -> VitalNativeListItem(item)
    }

    fun setRelease(release: ReleaseFull) {
        items.clear()
        currentRelease = release
        items.add(ReleaseHeadListItem(release))
        items.add(DividerShadowListItem())

        if (release.isBlocked) {
            items.add(ReleaseBlockedListItem(release))
            items.add(DividerShadowListItem())
        }

        if (!release.isBlocked && release.episodes.isNotEmpty()) {
            items.add(ReleaseDonateListItem())
            items.add(DividerShadowListItem())
        }

        if (vitalItems.isNotEmpty()) {
            val randomVital = if (vitalItems.size > 1) rand(0, vitalItems.size) else 0
            val listItem = getVitalListItem(vitalItems[randomVital])
            this.items.add(listItem)
            items.add(DividerShadowListItem())
        }

        if (!release.isBlocked && App.injections.appPreferences.getReleaseRemind()) {
            items.add(ReleaseRemindListItem())
            items.add(DividerShadowListItem())
        }

        if (release.episodes.isNotEmpty() || release.episodesSource.isNotEmpty()) {
            if (release.episodes.isNotEmpty()) {
                items.add(ReleaseEpisodeControlItem(release))
            }
            if (/*release.episodesSource.isNotEmpty() && */release.episodesSource.isNotEmpty()) {
                items.add(ReleaseEpisodesHeadListItem(currentTabTag))
            }
            items.addAll(prepareEpisodeItems(release))
            items.add(DividerShadowListItem())
        }

        items.add(CommentRouteListItem())
        items.add(DividerShadowListItem())

        notifyDataSetChanged()
    }

    private fun prepareEpisodeItems(release: ReleaseFull): List<ReleaseEpisodeListItem> {
        val newItems = when (currentTabTag) {
            ReleaseEpisodesHeadDelegate.TAG_ONLINE -> release.episodes.mapIndexed { index, episode ->
                ReleaseEpisodeListItem(episode, index % 2 == 0)
            }
            ReleaseEpisodesHeadDelegate.TAG_DOWNLOAD -> release.episodesSource.mapIndexed { index, episode ->
                ReleaseEpisodeListItem(episode, index % 2 == 0)
            }
            else -> emptyList()
        }.toMutableList()
        if (reverseEpisodes) {
            newItems.reverse()
        }
        return newItems
    }

    interface ItemListener :
            ReleaseHeadDelegate.Listener,
            ReleaseEpisodeDelegate.Listener,
            ReleaseDonateDelegate.Listener,
            ReleaseEpisodeControlDelegate.Listener

}
