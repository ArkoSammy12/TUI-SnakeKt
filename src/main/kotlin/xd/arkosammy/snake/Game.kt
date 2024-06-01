package xd.arkosammy.snake

import com.googlecode.lanterna.graphics.TextGraphics
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.screen.Screen
import java.io.IOException
import java.util.Random

class Game {

    companion object {
        private var game: Game? = null
        fun getInstance() : Game {
            return game?: Game().also { newInstance -> game = newInstance }
        }

        fun checkInput(gameScreen: GameScreen) {
            val screen: Screen = gameScreen.getTerminalScreen()
            val keyStroke: KeyStroke = screen.pollInput() ?: return

            val moveDirection: Snake.Direction? = when (keyStroke.character) {
                'w' -> Snake.Direction.DOWN
                'a' -> Snake.Direction.LEFT
                's' -> Snake.Direction.UP
                'd' -> Snake.Direction.RIGHT
                else -> null
            }

            if(moveDirection == null) {
                return
            }

            try {
                getInstance().onSnakeAttemptMove(moveDirection)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

        }
    }

    private val gameScreen: GameScreen = GameScreen.getInstance()
    private val snakeHead: Snake
    private var apple: GameElement
    private val random: Random = Random()
    private var score: Int = 0

    init {

        val initX: Int = this.random.nextInt(0, this.gameScreen.getTerminalScreen().terminalSize.columns - 1)
        val initY: Int = this.random.nextInt(0, this.gameScreen.getTerminalScreen().terminalSize.rows - 1)

        this.snakeHead = Snake(arrayOf(initX, initY), Snake.Direction.RIGHT, null, null)
        val appleX: Int = this.random.nextInt(0, this.gameScreen.getTerminalScreen().terminalSize.columns - 1)
        val appleY: Int = this.random.nextInt(0, this.gameScreen.getTerminalScreen().terminalSize.rows - 1)
        this.apple = GameElement(appleX, appleY, GameElement.Type.APPLE)

    }

    fun getScore() : Int = this.score

    fun getScreen() : GameScreen = this.gameScreen

    fun startLoop() {

        while(true) {
            this.gameScreen.clearElements()
            this.gameScreen.submitElement(apple)
            snakeHead.updatePositions()
            snakeHead.updateDirections()
            val snakeNodes: MutableList<GameElement> = snakeHead.getSnakeNodes(mutableListOf())
            this.gameScreen.submitAllElements(snakeNodes)
            val collisionType: Snake.CollisionType = snakeHead.checkCollision(this)
            when (collisionType) {
                Snake.CollisionType.NONE -> {}
                Snake.CollisionType.APPLE -> onAppleEaten()
                Snake.CollisionType.WALL -> {
                    this.onGameLost()
                    break
                }
            }

            this.gameScreen.refreshDisplay()
            this.gameScreen.display()

        }

    }

    private fun onSnakeAttemptMove(moveDirection: Snake.Direction) {
        if(this.snakeHead.getDirection().getOpposite() == moveDirection) {
            return
        }
        this.snakeHead.setDirection(moveDirection)
    }

    private fun onAppleEaten() {
        this.score++
        this.snakeHead.addTail()
        var appleX: Int
        var appleY: Int
        do {
            appleX = this.random.nextInt(0, this.gameScreen.getTerminalScreen().terminalSize.columns - 1)
            appleY = this.random.nextInt(0, this.gameScreen.getTerminalScreen().terminalSize.rows - 1)
        } while (isSnakeOccupying(appleX, appleY))
        this.apple = GameElement(appleX, appleY, GameElement.Type.APPLE)
    }

    private fun isSnakeOccupying(x: Int, y: Int): Boolean {
        for (gameElement: GameElement in this.snakeHead.getSnakeNodes(mutableListOf())) {
            if (gameElement.x == x && gameElement.y == y) {
                return true
            }
        }
        return false
    }

    private fun onGameLost() {
        this.gameScreen.getTerminalScreen().clear()
        val lostText: TextGraphics = this.gameScreen.getTerminalScreen().newTextGraphics()
        lostText.putString(0, 0, "You lost! Score: ${this.score}")
        this.gameScreen.getTerminalScreen().refresh()
    }

}

