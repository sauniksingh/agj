package athato.ghummakd.jigayasa.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import athato.ghummakd.jigayasa.widget.views.TripWidgetProvider

object TripWidgetUpdater {
    fun requestUpdate(context: Context) {
        val mgr = AppWidgetManager.getInstance(context)
        val ids = mgr.getAppWidgetIds(ComponentName(context, TripWidgetProvider::class.java))
        if (ids.isEmpty()) return
        val intent = Intent(context, TripWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        context.sendBroadcast(intent)
    }
}
