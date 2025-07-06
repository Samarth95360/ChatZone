import React, { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import ChatList from './ChatList/ChatList';
import ChatWindow from './ChatWindow';
import ChatListUsers from './ChatList/ChatListUsers';
import './Chat.css';
import { fetchUserConversations, fetchAllUsers, initiatePrivateConversation, addMembersToGroup } from '../../services/chatServices';
import UserProfileSidebar from './ChatList/UserProfileSidebar';
import AudioCallWindow from './AudioCallWindow';

const ChatMain = () => {
    const [selectedUser, setSelectedUser] = useState(null);
    const [allMessages, setAllMessages] = useState([]);
    const [stompConnected, setStompConnected] = useState(false);
    const [conversationList, setConversationList] = useState([]);
    const [showNewChat, setShowNewChat] = useState(false);
    const [showProfileSidebar, setShowProfileSidebar] = useState(false);
    const [availableUsers, setAvailableUsers] = useState([]);
    const [selectedUserIdsForGroup, setSelectedUserIdsForGroup] = useState([]);
    const [isAddingToGroup, setIsAddingToGroup] = useState(false);
    const [incomingCall, setIncomingCall] = useState(null);
    const expiry = new Date(Date.now() + 5 * 60 * 1000).toISOString();
    const [callPayload, setCallPayload] = useState(null);
    const stompClientRef = useRef(null);

    const token = localStorage.getItem('token');
    const currentUserId = localStorage.getItem('userId');

    useEffect(() => {
        if (!token) return;
        console.log("current user id :- ",currentUserId);
        fetchUserConversations().then(setConversationList).catch(console.error);
    }, [token]);

    useEffect(() => {
        if (!showNewChat) return;
        const controller = new AbortController();
        fetchAllUsers().then(setAvailableUsers).catch(console.error);
        return () => controller.abort(); // Optional if you want cleanup
    }, [showNewChat]);

    // useEffect(() => {
    //     if (!stompConnected || !currentUserId) return;

    //     const callInitiateSubscription = stompClientRef.current.subscribe(
    //         `/topic/audio-call.initiate.${currentUserId}`,
    //         (message) => {
    //             const callSignal = JSON.parse(message.body);
    //             console.log("Incoming call:", callSignal);
    //             setCallPayload({ ...callSignal, isIncoming: true });
    //             setIncomingCall(true);
    //         }
    //     );

    //     return () => callInitiateSubscription?.unsubscribe();
    // }, [stompConnected, currentUserId]);


    useEffect(() => {
        if (!stompConnected || !selectedUser?.chatRoomId) return;

        const subscription1 = stompClientRef.current.subscribe(
            `/topic/chat-room.${selectedUser.chatRoomId}`,
            (message) => {
                try {
                    const receivedMessage = JSON.parse(message.body);
                    console.log("Received message is :- ", receivedMessage);
                    setAllMessages((prev) => [...prev, receivedMessage]);
                } catch (err) {
                    console.error("Error parsing incoming message:", err);
                }
            }
        );

        const subscription2 = stompClientRef.current.subscribe(
            `/topic/chat-room/edited-messages.${selectedUser.chatRoomId}`,
            (message) => {
                try {
                    const edited = JSON.parse(message.body);
                    setAllMessages((prev) => prev.map(m => m.id === edited.id ? edited : m));
                } catch (err) {
                    console.error("Error parsing edited message:", err);
                }
            }
        );

        const subscription3 = stompClientRef.current.subscribe(
            `/topic/chat-room/delete-messages.${selectedUser.chatRoomId}`,
            (message) => {
                try {
                    const deletedMsg = JSON.parse(message.body);
                    setAllMessages((prev) => prev.map(m => m.id === deletedMsg.id ? deletedMsg : m));
                } catch (err) {
                    console.error("Error parsing edited message:", err);
                }
            }
        );

        const subscription4 = stompClientRef.current.subscribe(
            `/topic/conversations.updated.${selectedUser.chatRoomId}`,
            (message) => {
                const updatedConversation = JSON.parse(message.body);
                console.log("Updated message is :- ", updatedConversation);
                console.log("User is :- ", currentUserId);
                setConversationList(prevList => {
                    const exists = prevList.some(c => c.chatRoomId === updatedConversation.chatRoomId);
                    const updatedList = exists
                        ? prevList.map(c =>
                            c.chatRoomId === updatedConversation.chatRoomId
                                ? {
                                    ...c,
                                    roomName: updatedConversation.roomName ?? c.roomName,
                                    status: updatedConversation.status ?? c.status
                                }
                                : c
                        )
                        : [updatedConversation, ...prevList];

                    // ✅ Update selectedUser if necessary
                    if (selectedUser?.chatRoomId === updatedConversation.chatRoomId) {
                        setSelectedUser(prev => ({
                            ...prev,
                            roomName: updatedConversation.roomName ?? prev.roomName,
                            status: updatedConversation.status ?? prev.status
                        }));
                    }

                    return updatedList;
                });
            }
        )

        // ✅ Clean up both subscriptions on unmount or dependency change
        return () => [subscription1, subscription2, subscription3, subscription4].forEach(sub => sub?.unsubscribe());

    }, [stompConnected, selectedUser?.chatRoomId]);


    useEffect(() => {
        if (!token) return;

        const socket = new SockJS(`http://localhost:8050/ws-chat?token=${encodeURIComponent(token)}`);
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            debug: console.log,
            onConnect: () => {
                setStompConnected(true);
            },
            onStompError: console.error,
            onWebSocketClose: () => setStompConnected(false),
        });

        stompClientRef.current = client;
        client.activate();

        return () => {
            stompClientRef.current?.deactivate();
        };
    }, [token]);

    const handleUserSelect = async (user) => {
        const existing = conversationList.find(c => c.id === user.id || c.chatRoomId === user.chatRoomId);
        if (existing) {
            setSelectedUser(existing);
            setShowNewChat(false);
            return;
        }

        try {
            const convData = await initiatePrivateConversation(user.id);

            setConversationList(prevList => {
                const existsIndex = prevList.findIndex(c => c.chatRoomId === convData.chatRoomId);
                let updatedList;

                if (existsIndex !== -1) {
                    const updated = prevList[existsIndex];
                    updatedList = [updated, ...prevList.filter((_, idx) => idx !== existsIndex)];
                } else {
                    updatedList = [convData, ...prevList];
                }

                return updatedList;
            });

            setSelectedUser(convData);
            setShowNewChat(false);
        } catch (err) {
            console.error("Failed to initiate conversation:", err);
        }
    };

    const handleConversationSelect = (conversation) => {
        setSelectedUser(conversation);
        setShowNewChat(false);
    };

    const handleUserToggle = (userId) => {
        setSelectedUserIdsForGroup((prev) =>
            prev.includes(userId) ? prev.filter(id => id !== userId) : [...prev, userId]
        );
    };

    const handleAddMembersToGroup = async () => {
        try {
            const payload = {
                chatRoomId: selectedUser.chatRoomId,
                userIdsToAdd: selectedUserIdsForGroup
            };

            const updatedConversation = await addMembersToGroup(payload); // returns UserConversationData

            // Avoid adding duplicate conversation
            setConversationList(prevList => {
                const exists = prevList.some(c => c.chatRoomId === updatedConversation.chatRoomId);

                if (exists) {
                    // Move it to the top
                    return [
                        updatedConversation,
                        ...prevList.filter(c => c.chatRoomId !== updatedConversation.chatRoomId)
                    ];
                } else {
                    return [updatedConversation, ...prevList];
                }
            });

            setSelectedUser(updatedConversation); // Set the newly updated/created group as selected
            alert('Members added successfully!');
            setSelectedUserIdsForGroup([]);
            setIsAddingToGroup(false);

        } catch (err) {
            console.error('Failed to add members:', err);
        }
    };


    return (
        <div className="chat-app-theme">
            <div className="chat-main-container d-flex vh-100 overflow-hidden">
                <ChatList
                    showNewChat={showNewChat}
                    setShowNewChat={setShowNewChat}
                    conversationList={conversationList}
                    availableUsers={availableUsers}
                    selectedUser={selectedUser}
                    onSelectUser={showNewChat ? handleUserSelect : handleConversationSelect}
                    setShowProfileSidebar={setShowProfileSidebar}
                />
                <ChatWindow
                    selectedUser={selectedUser}
                    allMessages={allMessages}
                    setAllMessages={setAllMessages}
                    stompClientRef={stompClientRef}
                    currentUserId={currentUserId}
                    onAddMembersClick={() => {
                        setIsAddingToGroup(true);
                        fetchAllUsers().then(setAvailableUsers); // Refresh user list
                    }}
                />
                {isAddingToGroup && (
                    <div className="chat-sidebar d-flex flex-column" style={{ width: '300px', background: 'rgba(255, 255, 255, 0.05)', backdropFilter: 'blur(6px)', borderLeft: '1px solid rgba(255,255,255,0.1)' }}>
                        <div className="d-flex justify-content-between align-items-center px-3 py-2 border-bottom" style={{ borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
                            <h6 className="mb-0 text-primary">Add Members</h6>
                            <button className="btn btn-sm btn-outline-light" onClick={() => setIsAddingToGroup(false)}>✖</button>
                        </div>

                        <div className="flex-grow-1 overflow-auto p-2">
                            <ChatListUsers
                                users={availableUsers}
                                onUserToggle={handleUserToggle}
                                selectedUserIds={selectedUserIdsForGroup}
                            />
                        </div>

                        <div className="p-3 border-top" style={{ borderTop: '1px solid rgba(255,255,255,0.1)' }}>
                            <button
                                className="btn btn-primary w-100 rounded-pill"
                                onClick={handleAddMembersToGroup}
                                disabled={selectedUserIdsForGroup.length === 0}
                            >
                                ✅ Add to Group
                            </button>
                        </div>
                    </div>
                )}
                {showProfileSidebar && (
                    <UserProfileSidebar onClose={() => setShowProfileSidebar(false)} stompClientRef={stompClientRef} />
                )}
                {/* {incomingCall && (
                    <AudioCallWindow
                        selectedUser={selectedUser}
                        currentUserId={currentUserId}
                        stompClientRef={stompClientRef}
                        onClose={() => setIncomingCall(false)}
                        callPayload={callPayload}
                    />
                )} */}
            </div>
        </div>

    );
};

export default ChatMain;
