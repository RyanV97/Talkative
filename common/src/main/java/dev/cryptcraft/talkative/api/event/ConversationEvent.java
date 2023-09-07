package dev.cryptcraft.talkative.api.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import dev.cryptcraft.talkative.server.conversations.Conversation;

public interface ConversationEvent {
    /**
     * @see Start#start(Conversation) 
     */
    Event<Start> START = EventFactory.createEventResult();
    /**
     * @see Progress#progress(Conversation, int, int) 
     */
    Event<Progress> PROGRESS = EventFactory.createLoop();
    /**
     * @see Response#response(Conversation, int) 
     */
    Event<Response> RESPONSE = EventFactory.createLoop();
    /**
     * @see End#end(Conversation) 
     */
    Event<End> END = EventFactory.createLoop();

    interface Start {
        /**
         * Invoked when a player is about to start a Conversation with an Actor.
         * 
         * @param conversation The Conversation that is about to start
         * @return A {@link EventResult} determining if the Conversation should start or be cancelled.
         */
        EventResult start(Conversation conversation);
    }
    
    interface Progress {
        /**
         * Invoked when a player is about to progress further into a Conversation.
         *
         * @param conversation The Conversation
         * @param currentNodeId The NodeID for the {@link dev.cryptcraft.talkative.api.tree.node.NodeBase} the player is currently on (before progressing)
         * @param nextNodeId The NodeID for the {@link dev.cryptcraft.talkative.api.tree.node.NodeBase} the player is about to progress to
         */
        void progress(Conversation conversation, int currentNodeId, int nextNodeId);
    }
    
    interface Response {
        /**
         * Invoked when a player has responded in a Conversation.
         *
         * @param conversation The Conversation
         * @param responseNodeId The NodeID for the {@link dev.cryptcraft.talkative.api.tree.node.ResponseNode} that the player chose
         */
        void response(Conversation conversation, int responseNodeId);
    }
    
    interface End {
        /**
         * Invoked when a player is about to finish a Conversation with an Actor.
         *
         * @param conversation The Conversation that is about to start
         * @param nodeId The NodeID for the Node the player was on when exiting
         */
        void end(Conversation conversation, int nodeId);
    }
}
