package athato.ghummakd.jigayasa.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import athato.ghummakd.jigayasa.presentation.navigation.AgjNavGraph
import athato.ghummakd.jigayasa.presentation.theme.AgjTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgjTheme {
                AgjNavGraph()
            }
        }
    }
}
