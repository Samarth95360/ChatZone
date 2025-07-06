import React from 'react';
import ChatItem from '../ChatItem';
import { motion } from 'framer-motion';

const ChatListUsers = ({ users, onUserToggle, selectedUserIds = [], onSelectUser }) => {
    return (
        <div className="overflow-auto chat-scroll-container">
            {users.map((user) => {
                const isSelected = selectedUserIds?.includes?.(user.id) ?? false;

                const handleClick = (e) => {
                    e.stopPropagation();
                    if (onUserToggle) {
                        onUserToggle(user.id);
                    } else if (onSelectUser) {
                        onSelectUser(user);
                    }
                };

                return (
                    <motion.div
                        key={user.id}
                        initial={{ opacity: 0, y: 10 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.3 }}
                        onClick={handleClick}
                        style={{
                            border: isSelected ? '2px solid #0d6efd' : '1px solid transparent',
                            borderRadius: '10px',
                            marginBottom: '6px',
                            background: isSelected ? '#e9f2ff' : 'transparent',
                            padding: '2px',
                        }}
                    >
                        <ChatItem
                            chat={{
                                roomName: user.userName,
                                chatRoomId: user.id,
                                lastMessage: { text: user.userStatus },
                                timeStamp: null,
                            }}
                            isSelected={isSelected}
                            onClick={() => {}} // disabled
                        />
                    </motion.div>
                );
            })}
        </div>
    );
};

export default ChatListUsers;
