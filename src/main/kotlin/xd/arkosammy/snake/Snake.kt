package xd.arkosammy.snake

class Snake(private var pos: Array<Int>, private var direction: Direction, private var next: Snake?, private val prev: Snake?) {

    fun addTail() {
        if(this.next == null) {
            this.next = Snake(this.direction.getOpposite().addPosition(this.pos), this.direction, null, this)
            return
        }
        this.next!!.addTail()
    }

    fun setDirection(direction: Direction) {
        this.direction = direction
    }

    fun getDirection(): Direction = this.direction


    fun getSnakeNodes(snakeNodes: MutableList<GameElement>) : MutableList<GameElement> {
        snakeNodes.add(GameElement(this.pos[0], this.pos[1], GameElement.Type.SNAKE_HEAD))
        if(this.next != null) {
            return this.next!!.snakeNodesAfterHead(snakeNodes)
        }
        return snakeNodes
    }

    private fun snakeNodesAfterHead(snakeNodes: MutableList<GameElement>) : MutableList<GameElement> {
        snakeNodes.add(GameElement(this.pos[0], this.pos[1], GameElement.Type.SNAKE_BODY))
        if(this.next != null) {
            return this.next!!.snakeNodesAfterHead(snakeNodes)
        }
        return snakeNodes
    }

    fun updatePositions() {
        this.pos = this.direction.addPosition(this.pos)
        this.wrapPositionIfNeeded()
        if(this.next != null) {
            this.next!!.updatePositions()
        }
    }

    private fun wrapPositionIfNeeded() {
        val maxX: Int = GameScreen.getInstance().getTerminalScreen().terminalSize.columns - 1
        val maxY: Int = GameScreen.getInstance().getTerminalScreen().terminalSize.rows - 1
        var x: Int = this.pos[0]
        var y: Int = this.pos[1]
        if(x < 0) {
            x = maxX
        } else if (x >= maxX) {
            x = 0
        }
        if(y < 0) {
            y = maxY - 1
        } else if (y >= maxY) {
            y = 0
        }
        this.pos = arrayOf(x, y)
    }

    fun updateDirections() {
        val tail: Snake = this.getTail()
        tail.updateDirection()
    }

    private fun updateDirection() {
        val prev: Snake = this.prev ?: return
        this.setDirection(prev.getDirection())
        prev.updateDirection()
    }

    private fun getTail() : Snake = this.next?.getTail() ?: this


    fun checkCollision(game: Game) : CollisionType {
        val pos: Array<Int> = this.pos
        val gameElementAtPos: GameElement = game.getScreen().getElementAtIgnoringSnakeHead(pos[0], pos[1]) ?: return CollisionType.NONE
        return when(gameElementAtPos.type) {
            GameElement.Type.APPLE -> CollisionType.APPLE
            GameElement.Type.WALL, GameElement.Type.SNAKE_BODY -> CollisionType.WALL
            GameElement.Type.SNAKE_HEAD -> CollisionType.NONE
        }
    }

    enum class Direction(x: Int, y: Int) {
        UP(0, 1),
        RIGHT(1, 0),
        DOWN(0, -1),
        LEFT(-1, 0);

        private val vec: Array<Int> = arrayOf(x, y)

        fun addPosition(pos: Array<Int>): Array<Int> {
            return arrayOf(pos[0] + vec[0], pos[1] + vec[1])
        }

        fun getOpposite(): Direction {
            return when (this) {
                UP -> DOWN
                RIGHT -> LEFT
                DOWN -> UP
                LEFT -> RIGHT
            }
        }

    }

    enum class CollisionType {
        NONE,
        APPLE,
        WALL
    }

}