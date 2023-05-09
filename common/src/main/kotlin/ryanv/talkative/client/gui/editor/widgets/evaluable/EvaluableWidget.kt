package ryanv.talkative.client.gui.editor.widgets.evaluable

import ryanv.talkative.api.Evaluable

interface EvaluableWidget {
    fun getOriginalEvaluable(): Evaluable?
    fun getModifiedEvaluable(): Evaluable?
}