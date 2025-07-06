import React from 'react';
import { Dropdown } from 'react-bootstrap';
import { BsThreeDotsVertical } from 'react-icons/bs';
import './../../Chat/Chat.css';

const ChatListHeader = ({ showNewChat, setShowNewChat, onProfileClick }) => {
    return (
        <div className="d-flex justify-content-between align-items-center mb-3">
            <h5 className="mb-0 text-white">Chats</h5>

            <Dropdown align="end">
                <Dropdown.Toggle
                    variant="outline-light"
                    className="text-white p-0 border-0 bg-dark-subtle rounded-2"
                    id="chat-dropdown-toggle"
                >
                    <BsThreeDotsVertical size={20} />
                </Dropdown.Toggle>

                <Dropdown.Menu className="custom-dropdown-menu">
                    <Dropdown.Item onClick={() => setShowNewChat(!showNewChat)}>
                        {showNewChat ? '⬅ Back to Chats' : '➕ New Chat'}
                    </Dropdown.Item>
                    <Dropdown.Item onClick={onProfileClick}>
                        👤 Profile
                    </Dropdown.Item>
                </Dropdown.Menu>
            </Dropdown>
        </div>
    );
};

export default ChatListHeader;
