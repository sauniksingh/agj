package athato.ghummakd.jigayasa.widget

import android.app.Application
import athato.ghummakd.jigayasa.di.ServiceLocator

class AgjApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Eagerly build repository so the widget's seed runs on first launch.
        ServiceLocator.repository(this)
    }
}
