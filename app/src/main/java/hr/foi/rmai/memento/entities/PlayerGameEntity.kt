package hr.foi.rmai.memento.entities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import hr.foi.rmai.memento.R
import kotlin.math.max
import kotlin.math.min

class PlayerGameEntity(context: Context, width: Int, height: Int):
                        GameEntity(context, width, height)
{
    lateinit var bitmap: Bitmap
    public var boosting: Boolean = false

    private val MAX_SPEED = 15
    private val MIN_SPEED = 1
    private val GRAVITY = -10

    private val maxShieldStrength = 30
    public var shieldStrength = maxShieldStrength

    init {
        bitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.ship),
            sizeX, sizeY, false
        )

        maxY = height - bitmap.height * 2
    }

    override public fun update() {
        if (boosting) {
            speed += 3
        } else {
            speed -= 5
        }

        speed = min(speed, MAX_SPEED)
        speed = max(speed, MIN_SPEED)

        y -= (speed + GRAVITY)

        if (y < minY)
        {
            y = minY
        }

        if (y > maxY)
        {
            y = maxY
        }

        super.update()
    }

    public fun reduceShieldStrength() {
        shieldStrength--
    }

    public fun resetShieldStrength() {
        shieldStrength = maxShieldStrength
    }
}