import React, { useState } from 'react';
import classNames from 'classnames';
import moment from 'moment';
import { FiEdit2, FiTrash2 } from 'react-icons/fi';

const ChatMessage = ({ message, onEdit, onDelete }) => {
    const [hovered, setHovered] = useState(false);
    const isSentByUser = message.sentByMe;

    // console.log("Individual Message is :- ",message);
    // console.log("Sender's id :- ",message.sendBy);
    // console.log("isItSender",message.sendBy === currentUserId);

    const renderContent = () => {
        if (message.deleted) {
            return (
                <div className="fst-italic text-muted">
                    Message was deleted by the sender
                </div>
            );
        }

        return (
            <>
                {message.text}
                {message.edited && (
                    <span className="ms-2 text-muted" style={{ fontSize: '0.7rem' }}>
                        (edited)
                    </span>
                )}
            </>
        );
    };

    return (
        <div
            className={`d-flex mb-2 px-2 ${isSentByUser ? 'justify-content-end' : 'justify-content-start'}`}
            style={{ animation: 'fadeIn 0.3s ease-in' }}
        >
            <div
                className="d-flex align-items-start"
                style={{ position: 'relative', maxWidth: '75%' }}
                onMouseEnter={() => setHovered(true)}
                onMouseLeave={() => setHovered(false)}
            >
                {/* Message Bubble */}
                <div className="d-flex flex-column align-items-end message-bubble-wrapper">
                    <div
                        className={`p-3 rounded-4 ${isSentByUser ? 'bg-primary text-white' : 'bg-light text-dark border'} ${message.deleted ? 'text-muted fst-italic' : ''}`}
                        style={{
                            wordWrap: 'break-word',
                            whiteSpace: 'pre-wrap',
                            lineHeight: '1.4',
                            fontSize: '0.95rem',
                            boxShadow: '0 2px 6px rgba(0,0,0,0.1)',
                            paddingRight: '1rem',
                        }}
                    >
                        {renderContent()}
                    </div>
                    <div
                        className="text-muted mt-1"
                        style={{
                            fontSize: '0.75rem',
                            textAlign: 'right',
                            width: '100%',
                        }}
                    >
                        {moment(message.dateTime).format('hh:mm A')}
                    </div>
                </div>

                {/* Action Icons */}
                {isSentByUser && !message.deleted && (
                    <div
                        className="message-actions-vertical ms-2"
                        style={{ display: hovered ? 'flex' : 'none', flexDirection: 'column' }}
                    >
                        <FiEdit2
                            className="text-secondary cursor-pointer mb-2"
                            onClick={() => onEdit && onEdit(message)}
                        />
                        <FiTrash2
                            className="text-danger cursor-pointer"
                            onClick={() => onDelete && onDelete(message)}
                        />
                    </div>
                )}
            </div>
        </div>

    );
};

export default ChatMessage;
