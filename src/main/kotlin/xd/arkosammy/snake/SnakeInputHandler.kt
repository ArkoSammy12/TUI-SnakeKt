package xd.arkosammy.snake

import java.io.IOException

class SnakeInputHandler(private val gameScreen: GameScreen) : Runnable {

    override fun run() {
        while(true) {
            try {
                Game.checkInput(this.gameScreen)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

}