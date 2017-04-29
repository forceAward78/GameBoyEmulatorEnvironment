package app.components.panel.grid

import main.kotlin.utils.image.BufferedImageMemoryFromComponent
import main.kotlin.utils.image.createBufferedImage
import main.kotlin.utils.image.scale
import main.kotlin.utils.listGames.ListGames
import java.awt.Color
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.security.SecureRandom
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Semaphore
import javax.swing.*

/**
 * Created by vicboma on 13/04/17.
 */
object CacheGrid {

    val W = 240
    val H = 200

    val semaphore = Semaphore(10,true)
    val random = SecureRandom()
    var state = CacheState.STOP
    var limit = 0
    //val queue = ConcurrentLinkedQueue<CompletableFuture<Map<String, Serializable>>>()

    fun createRefImage(listGames: ListGames, classLoader: ClassLoader, bufferedDefault : BufferedImage, jTable:JTable) {//: CompletableFuture<Queue<CompletableFuture<Map<String, Serializable>>>> {
        limit = listGames.rowNames?.size!!
        state = CacheState.LOADING
        val imageDefault = ImageIcon().scale(bufferedDefault, classLoader.getResource("cover/_gbNotFound.png").file.toString())

        Thread.sleep(4000)
        try {
            val rows = listGames.rowNames?.size
            var cols = jTable.model.columnCount
            for (row in 0..rows!!) {
                for (col in 0..cols - 1) {

                    val index = (row * cols) + col
                    if( index > rows)
                        break

//                    queue.add(
                            CompletableFuture.supplyAsync() {

                                try {
                                    semaphore.acquire()

                                    val bufferImage = ImageIcon().createBufferedImage(W, H, BufferedImage.TYPE_INT_ARGB)
                                    val nameRom = listGames.rowNames!![(row * cols) + col][1].toString()
                                    val nameImage = nameRom.toLowerCase().split(".")[0].toString().plus(".png")
                                    val resource = classLoader.getResource("cover/$nameImage")
                                    val image = when (resource) {
                                        null -> imageDefault
                                        else -> ImageIcon().scale(bufferImage, resource.file.toString())
                                    }

                                    println(StringBuffer("($row * $cols) + $col = ${(row * cols) + col}").append(" $nameRom - $nameImage ").toString())

                                    //map["imageIcon"] =
                                    val imageIcon = ImageIcon(
                                            BufferedImageMemoryFromComponent.invoke(
                                                    JPanel().apply {
                                                        size = Dimension(bufferImage.width, bufferImage.height)
                                                        isOpaque = false
                                                        setBackground(Color(0, 0, 0))

                                                        layout = boxLayout(this).apply {
                                                            setBackground(Color(0, 0, 0))
                                                            isOpaque = false
                                                        }

                                                        add(jLabelFactory(" "))
                                                        add(jLabelFactory(ImageIcon(image)))
                                                        add(jLabelFactory(" "))
                                                        add(jLabelFactory(nameRom))
                                                    }
                                            )
                                    )

                            //        println(StringBuffer("END ($row * $cols) + $col = ${(row * cols) + col}").append(" $nameRom - $nameImage ").toString())

                                   // if (resource == null) {
                                    Thread.sleep((random.nextInt(700)+1).toLong())
                                    jTable.setValueAt(/*map["imageIcon"]*/imageIcon, row, col)
                                   // (jTable.model as AbstractTableModel).fireTableCellUpdated(row, col)
                                    //}
                                   // map
                                }
                                finally{
                                    semaphore.release()
                                }

                            }
                   // )
                }
            }
        } catch(e: Exception) {
            println(e.message)
            e.stackTrace
        } finally {
       //     println("****** FIN LOAD MODEL *******")
            state = CacheState.FINISH
          //  return CompletableFuture.completedFuture(queue)
        }
    }

    fun showImageIconAsync(jtable:JTable) {

    /*    var size = limit
        while(size > 0){
            size --
            val completable = queue.poll()
            completable.thenApplyAsync {
                val row = it["row"] as Int
                val col = it["column"] as Int
                val imageIcon = it["imageIcon"] as ImageIcon
                jtable.setValueAt(imageIcon, row, col)
            }
        }
        println("Empty Queue! state: ${CacheGrid.state} ")*/
    }

    private fun boxLayout(container : Container) = BoxLayout(container, BoxLayout.Y_AXIS)

    private fun jLabelFactory(name : String ) = JLabel(name).apply {
                    setBackground(Color(0, 0, 0))
                    isOpaque = false
                    setAlignmentX(Component.CENTER_ALIGNMENT)
                }

    private fun jLabelFactory(imageIcon : ImageIcon ) = JLabel(imageIcon).apply {
                    setBackground(Color(0, 0, 0))
                    isOpaque = false
                    setAlignmentX(Component.CENTER_ALIGNMENT)
                }

}

enum class CacheState {
    STOP, LOADING, FINISH
}