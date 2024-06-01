package xd.arkosammy.snake

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val game: Game = Game.getInstance()
            val executorServer: ExecutorService = Executors.newSingleThreadExecutor()
            executorServer.submit(SnakeInputHandler(game.getScreen()))
            game.startLoop()
            executorServer.shutdownNow()
        }
    }

}