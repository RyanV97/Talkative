package dev.cryptcraft.talkative.client.gui.editor.widgets.evaluable

import dev.cryptcraft.talkative.api.Evaluable

interface EvaluableWidget {
    fun getOriginalEvaluable(): Evaluable?
    fun getModifiedEvaluable(): Evaluable?
}