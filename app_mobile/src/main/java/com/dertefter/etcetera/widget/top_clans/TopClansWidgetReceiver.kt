package com.dertefter.etcetera.widget.top_clans

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import com.dertefter.etcetera.EtCetera
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TopClansWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TopClansWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val repository = (context.applicationContext as EtCetera).appGraph.searchRepository

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(TopClansWidget::class.java)

                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs[TopClansWidget.IS_LOADING] = true
                    }
                }
                glanceAppWidget.updateAll(context)

                repository.updateTopClans()

                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs[TopClansWidget.IS_LOADING] = false
                    }
                }
                glanceAppWidget.updateAll(context)
            } finally {
                pendingResult?.finish()
            }
        }
    }
}
