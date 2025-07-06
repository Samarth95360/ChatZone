import React from 'react';
import ChatItem from '../ChatItem';
import { motion } from 'framer-motion';

const ChatListConversations = ({ conversationList, selectedUser, onSelectUser }) => {
    return (
        <div className="overflow-auto chat-scroll-container">
            {conversationList.map((chat) => (
                <motion.div
                    key={chat.chatRoomId}
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.3 }}
                >
                    <ChatItem
                        chat={chat}
                        isSelected={selectedUser?.chatRoomId === chat.chatRoomId}
                        onClick={() => onSelectUser(chat)}
                    />
                </motion.div>
            ))}
        </div>
    );
};

export default ChatListConversations;
