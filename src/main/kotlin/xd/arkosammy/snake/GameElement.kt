package xd.arkosammy.snake

import com.googlecode.lanterna.TextColor

data class GameElement(val x: Int, val y: Int, val type: Type) {

    enum class Type(private val graphic: Char, private val color: TextColor) {
        APPLE('O', TextColor.ANSI.RED_BRIGHT),
        SNAKE_HEAD('S', TextColor.ANSI.GREEN_BRIGHT),
        SNAKE_BODY('V', TextColor.ANSI.GREEN),
        WALL('#', TextColor.ANSI.WHITE);

        fun getGraphic(): Char {
            return this.graphic
        }

        fun getColor(): TextColor {
            return this.color
        }
    }

}