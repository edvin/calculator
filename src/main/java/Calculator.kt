import Op.*
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage
import tornadofx.App
import tornadofx.View
import tornadofx.importStylesheet

class CalculatorApp : App() {
    override val primaryView = Calculator::class

    override fun start(stage: Stage) {
        importStylesheet("/style.css")
        stage.isResizable = false
        super.start(stage)
    }
}

class Calculator : View() {
    override val root: VBox by fxml()
    @FXML lateinit var display: Label

    init {
        title = "Calculator"

        root.lookupAll(".button").forEach { button ->
            button.setOnMouseClicked {
                op((button as Button).text)
            }
        }

        // Keyboard support
        root.addEventFilter(KeyEvent.KEY_TYPED) {
            op(it.character.toUpperCase().replace("\r", "="))
        }
    }

    var curried: Op = add(0)

    fun opAction(fn: Op) {
        curried = fn
        display.text = ""
    }

    val displayValue: Long
        get() = when (display.text) {
            "" -> 0
            else -> display.text.toLong()
        }

    private fun op(x: String): Unit {
        if (Regex("[0-9]").matches(x)) {
            display.text += x
        } else {
            when (x) {
                "+" -> opAction(add(displayValue))
                "-" -> opAction(sub(displayValue))
                "/" -> opAction(div(displayValue))
                "%" -> { opAction(add(displayValue /100)); op("=") }
                "x" -> opAction(mult(displayValue))
                "C" -> opAction(add(0))
                "+/-" -> { opAction(add(-1* displayValue)); op("=") }
                "=" -> display.text = curried.calc(displayValue).toString()
            }
        }
    }
}

sealed class Op(val x: Long) {
    abstract fun calc(y: Long): Long
    class add(x: Long) : Op(x) { override fun calc(y: Long) = x + y }
    class sub(x: Long) : Op(x) { override fun calc(y: Long) = x - y }
    class mult(x: Long) : Op(x) { override fun calc(y: Long) = x * y }
    class div(x: Long) : Op(x) { override fun calc(y: Long) = x / y }
}