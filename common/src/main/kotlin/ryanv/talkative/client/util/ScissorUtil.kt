package ryanv.talkative.client.util

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11
import java.util.*

//Original Code provided by DenimRed - https://github.com/DenimRed/
object ScissorUtil {
    private val STACK: Deque<ScissorBox> = ArrayDeque()
    fun start(x: Int, y: Int, width: Int, height: Int) {
        push()
        ScissorBox.fromScreenSpace(x, y, width, height).clampInside(STACK.peek()).apply()
    }

    fun stop() {
        RenderSystem.disableScissor()
        pop()
    }

    fun push() {
        if (GL11.glIsEnabled(GL11.GL_SCISSOR_TEST)) {
            val raw = IntArray(4)
            GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, raw)
            STACK.push(ScissorBox(raw[0], raw[1], raw[2], raw[3]))
            RenderSystem.disableScissor()
        }
    }

    fun pop() {
        if (!STACK.isEmpty()) {
            STACK.pop().apply()
        }
    }

    private class ScissorBox constructor(val left: Int, val bottom: Int, val width: Int, val height: Int) {
        fun apply() {
            if (this != INVALID) {
                RenderSystem.enableScissor(left, bottom, width, height)
            }
        }

        private fun minX(): Int {
            return left
        }

        private fun maxX(): Int {
            return left + width
        }

        private fun minY(): Int {
            return bottom
        }

        private fun maxY(): Int {
            return bottom + height
        }

        fun clampInside(other: ScissorBox?): ScissorBox {
            if (other != null) {
                val minX = Math.max(minX(), other.minX())
                val maxX = Math.min(maxX(), other.maxX())
                val minY = Math.max(minY(), other.minY())
                val maxY = Math.min(maxY(), other.maxY())
                return if (maxX > minX && maxY > minY) {
                    ScissorBox(minX, minY, maxX - minX, maxY - minY)
                } else INVALID
            }
            return this
        }

        companion object {
            private val INVALID = ScissorBox(0, 0, 0, 0)
            fun fromScreenSpace(x: Int, y: Int, width: Int, height: Int): ScissorBox {
                val window = Minecraft.getInstance().window
                val scale = window.guiScale
                return ScissorBox(
                    (x * scale).toInt(),
                    ((window.guiScaledHeight - y - height) * scale).toInt(),
                    (width * scale).toInt(),
                    (height * scale).toInt()
                )
            }
        }
    }
}