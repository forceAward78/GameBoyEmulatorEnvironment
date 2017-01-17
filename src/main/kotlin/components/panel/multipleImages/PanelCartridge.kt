package components.panel.multipleImages

import components.panel.multipleImages.base.PanelMultipleImages
import src.configuration.Display
import java.awt.Graphics
import java.awt.Graphics2D

/**
 * Created by vicboma on 08/01/17.
 */
class PanelCartridge internal constructor(_back: String, val _front: String) : PanelMultipleImages(_back,_front) {

    companion object {
        fun create(_back: String,_front: String) = PanelCartridge(_back,_front)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponents(g)
        var g2 = g as Graphics2D

        g2.drawImage(list!![0], (this.width * 0.07).toInt() , (this.height * 0.07).toInt(), null)
        g2.drawImage(list!![1],(this.width * 0.332).toInt() , (this.height * 0.39).toInt(),   ((Display.WIDHT / 3) * 0.495).toInt(), ((Display.HEIGTH / 2) * 0.495).toInt(), null)

    }
}