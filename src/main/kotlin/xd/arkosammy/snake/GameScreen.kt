package xd.arkosammy.snake

import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.TextGraphics
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger
import java.awt.Font
import java.nio.charset.Charset

class GameScreen {

    companion object {
        private var instance: GameScreen? = null
        private const val FRAME_DELAY: Int = 50
        fun getInstance() : GameScreen = instance ?: GameScreen().also { newInstance -> instance = newInstance}
    }

    private val terminalScreen: Screen
    private val gameElements: MutableList<GameElement> = ArrayList()

    init {
        val terminal: Terminal = DefaultTerminalFactory(System.out, System.`in`, Charset.defaultCharset())
            .setTerminalEmulatorFrameAutoCloseTrigger(TerminalEmulatorAutoCloseTrigger.CloseOnEscape)
            .setTerminalEmulatorTitle("Snake")
            .setTerminalEmulatorFontConfiguration(AWTTerminalFontConfiguration.newInstance(Font("Monospaced", Font.BOLD, 20)))
            .setPreferTerminalEmulator(true)
            .createTerminal()

        terminal.setForegroundColor(TextColor.ANSI.WHITE)
        terminal.setBackgroundColor(TextColor.ANSI.BLACK)
        terminal.setCursorVisible(false)
        this.terminalScreen = TerminalScreen(terminal)
        this.terminalScreen.startScreen()
    }


    fun getTerminalScreen() : Screen = this.terminalScreen

    fun getElementAtIgnoringSnakeHead(x: Int, y: Int) : GameElement? {
        for(gameElement: GameElement in this.gameElements) {
            if(gameElement.x == x && gameElement.y == y && gameElement.type != GameElement.Type.SNAKE_HEAD) {
                return gameElement
            }
        }
        return null
    }

    fun clearElements() = this.gameElements.clear()

    fun submitElement(e: GameElement) {
        if(!this.gameElements.contains(e)) {
            this.gameElements.add(e)
        }
    }

    fun submitAllElements(e: List<GameElement>) {
        for(element: GameElement in e) {
            if(!this.gameElements.contains(element)) {
                this.gameElements.add(element)
            }
        }
    }

    fun refreshDisplay() {
        this.terminalScreen.doResizeIfNecessary()
        val scoreText: TextGraphics = this.terminalScreen.newTextGraphics()
        scoreText.putString(0, 0, "Score: ${Game.getInstance().getScore()}")
        for(i in (this.terminalScreen.terminalSize.rows -1) downTo 0) {
            for(j in 0 until this.terminalScreen.terminalSize.columns) {
                this.terminalScreen.setCharacter(j, i + 1, TextCharacter(' '))
            }
        }
        for(e: GameElement in this.gameElements) {
            val character: TextCharacter = TextCharacter(e.type.getGraphic()).withForegroundColor(e.type.getColor())
            this.terminalScreen.setCharacter(e.x, e.y + 1, character)
        }

    }

    fun display() {
        this.terminalScreen.refresh()
        Thread.sleep(FRAME_DELAY.toLong())
    }

}