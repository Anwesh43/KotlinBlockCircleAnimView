package ui.anwesome.com.kotlinblockcircleaimview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.blockcircleanimview.BlockCircleAnimView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BlockCircleAnimView.create(this)
    }
}
