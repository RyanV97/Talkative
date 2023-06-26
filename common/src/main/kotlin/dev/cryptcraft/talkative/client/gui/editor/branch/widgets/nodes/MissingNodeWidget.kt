package dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes

import net.minecraft.network.chat.Component
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchNodeEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes.NodeWidget
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget

class MissingNodeWidget(x: Int, y: Int, val parentWidget: NodeWidget?, val parentScreen: BranchNodeEditorScreen) : NestedWidget(x, y, 200, 40, Component.empty()) {
}