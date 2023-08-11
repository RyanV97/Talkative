package dev.cryptcraft.talkative.client.gui.editor.widgets.evaluable

import dev.cryptcraft.talkative.api.conditional.Evaluable

interface EvaluableWidget {
    fun getOriginalEvaluable(): Evaluable?
    fun getModifiedEvaluable(): Evaluable?
}