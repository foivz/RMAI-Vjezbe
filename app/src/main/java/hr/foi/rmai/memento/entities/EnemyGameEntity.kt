package hr.foi.rmai.memento.entities

import android.content.Context
import java.util.Random

class EnemyGameEntity(context: Context, width: Int, height: Int, title: String):
    GameEntity(context, width, height) {

    val generator = Random()
    var enemyTitle = ""

    init {
        speed = generator.nextInt(10)

        x = width
        y = generator.nextInt(maxY)

        enemyTitle = title
    }

    override fun update() {
        x -= speed
        x -= playerSpeed

        if (x < minX) {
            speed = generator.nextInt(10)
            x = maxX
            y = generator.nextInt(maxY)
        }

        super.update()
    }
}