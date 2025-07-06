import React, { useEffect, useRef, useState } from 'react';
import ChatMessage from './ChatMessage';
import { FiSend } from 'react-icons/fi';
import Avatar from 'boring-avatars'; // ✅ Import avatar
import { fetchMessages, updateMessage, deleteMessage } from '../../services/chatServices';
import './Chat.css';
import { HiUserAdd } from 'react-icons/hi';
import { MdCall, MdVideoCall } from 'react-icons/md';
import GroupInfoSidebar from './GroupInfoSidebar';
import AudioCallWindow from './AudioCallWindow';

const ChatWindow = ({ selectedUser, allMessages, setAllMessages, stompClientRef, currentUserId, onAddMembersClick }) => {
    const [messageInput, setMessageInput] = useState('');
    const [editingMsg, setEditingMsg] = useState(null);
    const [showGroupInfo, setShowGroupInfo] = useState(false);
    const [showAudioCall, setShowAudioCall] = useState(false);
    const [callPayload, setCallPayload] = useState(null);
    const expiry = new Date(Date.now() + 5 * 60 * 1000).toISOString();

    const messagesEndRef = useRef();

    useEffect(() => {
        if (!selectedUser || !currentUserId) return;
        console.log("selected user is :- ",selectedUser);
        fetchMessages(selectedUser.chatRoomId).then(setAllMessages);
    }, [selectedUser]);

    const handleSend = async () => {
        if (!messageInput.trim() || !selectedUser) return;
        const now = new Date().toISOString();

        if (editingMsg) {
            const updated = {
                ...editingMsg,
                text: messageInput.trim(),
                edited: true,
                dateTime: now
            };
            try {
                // console.log("Updated message is :- ",updated);
                // const resp = await updateMessage(updated);
                // setAllMessages(prev => prev.map(m => m.id === resp.id ? resp : m));
                stompClientRef.current.publish({
                    destination: '/app/edit-message',
                    body: JSON.stringify(updated)
                });
            } catch (e) {
                console.error('Edit failed:', e);
            }
            setEditingMsg(null);
        } else {
            const newMsg = {
                senderId: currentUserId,
                chatRoomId: selectedUser?.chatRoomId,
                text: messageInput.trim(),
                dateTime: now
            };
            console.log("Message is :- ", newMsg);
            stompClientRef.current.publish({
                destination: '/app/chat-message',
                body: JSON.stringify(newMsg)
            });
            // setAllMessages(prev => [...prev, newMsg]);
        }

        setMessageInput('');
    };

    const startEdit = (msg) => {
        setEditingMsg(msg);
        setMessageInput(msg.text);
    };

    const onDelete = async (msg) => {
        try {
            // const deletedMsg = await deleteMessage(msg);
            // setAllMessages(prev => prev.map(m => m.id === deletedMsg.id ? deletedMsg : m));
            stompClientRef.current.publish({
                destination: '/app/delete-message',
                body: JSON.stringify(msg)
            });
        } catch (e) {
            console.error('Delete failed:', e);
        }
    };

    // const filtered = allMessages.filter(m =>
    //     (m.senderId === currentUserId && m.receiverId === selectedUser.id) ||
    //     (m.senderId === selectedUser.id && m.receiverId === currentUserId)
    // );

    const filtered = allMessages;

    // console.log("filtered messages are :- ", filtered);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [filtered]);

    if (!selectedUser) {
        return (
            <div className="chat-window-container d-flex justify-content-center align-items-center text-muted fw-light fs-5">
                Select a user to start messaging 💬
            </div>
        );
    }

    // const handleStartCall = (callType) => {
    //     if (!selectedUser || !stompClientRef.current) return;

    //     const callPayload = {
    //         chatRoomId: selectedUser.chatRoomId,
    //         senderId: currentUserId,
    //         receiverUsersId: selectedUser.conversationUsersId,
    //         receiverName: selectedUser.roomName,
    //         callType: callType,
    //         startingTime: new Date().toISOString(),
    //         expiryTime: expiry,
    //         callHealthStatus: "Stable"
    //     };

    //     console.log("Audio call sending payload is :- ", callPayload);

    //     selectedUser.conversationUsersId
    //         ?.filter((id) => id !== currentUserId) // don’t notify self
    //         .forEach((userId) => {
    //             stompClientRef.current.publish({
    //                 destination: `/app/audio-call-initiate`, // maps to @MessageMapping in backend
    //                 body: JSON.stringify({ ...callPayload, receiverId: userId })
    //             });
    //         });

    //     setCallPayload(callPayload);
    //     setShowAudioCall(true);
    // };


    return (
        <div className="chat-window-container d-flex flex-column flex-grow-1 p-3">
            {/* ✅ Chat header with avatar */}
            <div className="chat-header d-flex justify-content-between align-items-center gap-3 px-3 py-2 mb-3 rounded shadow-sm" style={{ background: 'rgba(255, 255, 255, 0.05)', backdropFilter: 'blur(6px)', borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
                <div className="d-flex align-items-center gap-3">
                    <Avatar
                        size={40}
                        name={selectedUser.roomName}
                        variant="beam"
                        colors={["#92A1C6", "#146A7C", "#F0AB3D", "#C271B4", "#C20D90"]}
                    />
                    <span
                        className="fw-semibold fs-5 text-primary cursor-pointer"
                        onClick={() => setShowGroupInfo(true)}
                    >
                        {selectedUser.roomName}
                    </span>
                </div>
                {selectedUser.roomType !== "Single" && (
                    <div className="d-flex align-items-center gap-2">
                        <button className="icon-btn" title="Add Members" onClick={onAddMembersClick}>
                            <HiUserAdd size={24} />
                        </button>
                        <button
                            className="icon-btn text-success"
                            title="Start Audio Call"
                            // onClick={() => handleStartCall("Audio Call")}
                        >
                            <MdCall size={24} />
                        </button>
                        <button className="icon-btn text-danger" title="Start Video Call">
                            <MdVideoCall size={24} />
                        </button>
                    </div>
                )}
            </div>

            {/* Chat messages */}
            <div className="chat-messages flex-grow-1 overflow-auto mb-2 px-2">
                {filtered.map((msg, i) => (
                    <ChatMessage
                        key={i}
                        message={{ ...msg, sentByMe: msg.sendBy === currentUserId }}
                        onEdit={startEdit}
                        onDelete={onDelete}
                    />
                ))}
                <div ref={messagesEndRef} />
            </div>

            {/* Chat input */}
            <div className="chat-input-container d-flex align-items-center p-2 border-top">
                <input
                    type="text"
                    className="form-control me-2 rounded-pill"
                    placeholder={editingMsg ? "Editing message..." : "Type your message"}
                    value={messageInput}
                    onChange={e => setMessageInput(e.target.value)}
                    onKeyDown={e => e.key === 'Enter' && handleSend()}
                />
                <button className="btn btn-primary rounded-circle" onClick={handleSend}>
                    <FiSend size={20} />
                </button>
            </div>
            {showGroupInfo && (
                <GroupInfoSidebar
                    selectedUser={selectedUser}
                    onClose={() => setShowGroupInfo(false)}
                    stompClientRef={stompClientRef}
                />
            )}
            {/* {showAudioCall && (
                <AudioCallWindow
                    selectedUser={selectedUser}
                    currentUserId={currentUserId}
                    stompClientRef={stompClientRef}
                    onClose={() => setShowAudioCall(false)}
                    callPayload={callPayload}
                />
            )} */}

        </div>
    );
};

export default ChatWindow;
