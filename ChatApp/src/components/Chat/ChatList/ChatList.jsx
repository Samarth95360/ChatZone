import React from 'react';
import ChatListHeader from './ChatListHeader';
import ChatListConversations from './ChatListConversations';
import ChatListUsers from './ChatListUsers';

const ChatList = ({
    showNewChat,
    setShowNewChat,
    conversationList,
    availableUsers,
    selectedUser,
    onSelectUser,
    setShowProfileSidebar
}) => {
    return (
        <div className="chat-list-container p-3">
            <ChatListHeader
                showNewChat={showNewChat}
                setShowNewChat={setShowNewChat}
                onProfileClick={() => setShowProfileSidebar(true)}
            />
            {showNewChat ? (
                <ChatListUsers users={availableUsers} onSelectUser={onSelectUser} />
            ) : (
                <ChatListConversations
                    conversationList={conversationList}
                    selectedUser={selectedUser}
                    onSelectUser={onSelectUser}
                />
            )}
        </div>
    );
};

export default ChatList;
