package com.lollipop.qin1sptools.utils

import android.view.View
import android.widget.TextView
import com.lollipop.qin1sptools.R
import javax.microedition.lcdui.Command

/**
 * @author lollipop
 * @date 2021/8/17 23:29
 */
class CommandOptionBarDelegate(
    private val optionBar: () -> View?,
    private val leftButton: () -> TextView?,
    private val centerButton: () -> TextView?,
    private val rightButton: () -> TextView?,
) {

    companion object {
        private val LEFT_COMMAND_SEQUENCE = intArrayOf(
            Command.OK,
            Command.ITEM,
            Command.SCREEN,
            Command.STOP,
            Command.HELP,
            Command.EXIT,
            Command.CANCEL,
            Command.BACK,
        )

        private val CENTER_COMMAND_SEQUENCE = intArrayOf(
            Command.HELP,
            Command.ITEM,
            Command.SCREEN,
            Command.OK,
            Command.CANCEL,
            Command.EXIT,
            Command.STOP,
            Command.BACK,
        )

        private val RIGHT_COMMAND_SEQUENCE = intArrayOf(
            Command.BACK,
            Command.CANCEL,
            Command.HELP,
            Command.EXIT,
            Command.STOP,
            Command.SCREEN,
            Command.ITEM,
            Command.OK,
        )
    }

    private var leftOptionCommands = ArrayList<Command>()
    private var centerOptionCommand: Command? = null
    private var rightOptionCommand: Command? = null

    fun updateFeatureBar(commands: Array<Command>?) {
        leftOptionCommands.clear()
        centerOptionCommand = null
        rightOptionCommand = null
        if (commands == null || commands.isEmpty()) {
            optionBar()?.visibleOrGone(false)
            return
        }

        val overflowCommand = commands.size > 3

        commands.forEach { command ->
            val commandType = command.commandType
            val rightIndex = findIndex(commandType, RIGHT_COMMAND_SEQUENCE, rightOptionCommand)
            val centerIndex = findIndex(commandType, CENTER_COMMAND_SEQUENCE, centerOptionCommand)
            val leftIndex = findIndex(commandType, LEFT_COMMAND_SEQUENCE, null)

            if (overflowCommand) {
                if (rightIndex >= 0 && centerIndex >= 0) {
                    if (rightIndex > centerIndex) {
                        rightOptionCommand = command
                    } else {
                        centerOptionCommand = command
                    }
                } else if (rightIndex >= 0) {
                    rightOptionCommand = command
                } else if (centerIndex >= 0) {
                    centerOptionCommand = command
                } else {
                    leftOptionCommands.add(command)
                }
            } else {
                when (maxIndex(leftIndex, centerIndex, rightIndex)) {
                    0 -> {
                        leftOptionCommands.add(command)
                    }
                    1 -> {
                        centerOptionCommand = command
                    }
                    2 -> {
                        rightOptionCommand = command
                    }
                }
            }
        }

        updateCommandName()
    }

    private fun updateCommandName() {
        optionBar()?.visibleOrGone(true)
        when {
            leftOptionCommands.isEmpty() -> {
                leftButton()?.text = ""
            }
            leftOptionCommands.size == 1 -> {
                leftButton()?.text = leftOptionCommands[0].label
            }
            else -> {
                leftButton()?.setText(R.string.menu)
            }
        }
        centerButton()?.text = centerOptionCommand?.label ?: ""
        rightButton()?.text = rightOptionCommand?.label ?: ""
    }

    private fun maxIndex(vararg values: Int): Int {
        var maxValue = Int.MIN_VALUE
        var maxIndex = -1
        for (index in values.indices) {
            val value = values[index]
            if (value > maxValue) {
                maxValue = value
                maxIndex = index
            }
        }
        return maxIndex
    }

    private fun findIndex(type: Int, sequence: IntArray, value: Command?): Int {
        if (value != null) {
            return -1
        }
        return sequence.indexOf(type)
    }

}