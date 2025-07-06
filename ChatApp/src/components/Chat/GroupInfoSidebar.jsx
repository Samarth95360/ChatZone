import React, { useEffect, useState } from 'react';
import Avatar from 'boring-avatars';
import {
    FiX, FiEdit2, FiSave, FiLogOut, FiTrash2
} from 'react-icons/fi';
import {
    fetchUserProfilesInGroup,
    exitGroup,
    updateGroupInfo
} from '../../services/chatServices';
import './GroupInfoSidebar.css';

const GroupInfoSidebar = ({ selectedUser, onClose, stompClientRef }) => {
    const [members, setMembers] = useState([]);

    const [groupName, setGroupName] = useState(selectedUser.roomName);
    const [groupDesc, setGroupDesc] = useState(selectedUser.status || '');

    const [originalGroupName, setOriginalGroupName] = useState(selectedUser.roomName);
    const [originalGroupDesc, setOriginalGroupDesc] = useState(selectedUser.status || '');

    const [editing, setEditing] = useState(false);

    useEffect(() => {
        if (!selectedUser) return;

        // Update states when new chat is selected
        const initialName = selectedUser.roomName || '';
        const initialDesc = selectedUser.status || '';

        setGroupName(initialName);
        setGroupDesc(initialDesc);

        setOriginalGroupName(initialName);
        setOriginalGroupDesc(initialDesc);

        setEditing(false);

        if (selectedUser.roomType === 'Group') {
            fetchUserProfilesInGroup(selectedUser.chatRoomId).then(setMembers);
        } else {
            setMembers([]);
        }
    }, [selectedUser]);


    const handleExitGroup = async () => {
        const confirmed = window.confirm('Are you sure you want to exit the group?');
        if (confirmed) {
            await exitGroup(selectedUser.chatRoomId);
            window.location.reload();
        }
    };

    const handleUpdateGroup = async () => {
        const payload = {
            chatRoomId: selectedUser.chatRoomId,
            roomName: groupName,
            roomStatus: groupDesc,
        };

        try {
            const response = await updateGroupInfo(payload);
            if (response.status === 200 && response.data !== null) {
                stompClientRef.current.publish({
                    destination: '/app/update-profile',
                    body: JSON.stringify(response.data)
                });
                setOriginalGroupName(groupName);
                setOriginalGroupDesc(groupDesc);
                setEditing(false);
            }
        } catch (error) {
            console.error('Failed to update group:', error);
            alert('Failed to update group. Reverting changes.');
            setGroupName(originalGroupName);
            setGroupDesc(originalGroupDesc);
        }
    };


    const handleDeleteChat = () => {
        const confirmed = window.confirm('Are you sure you want to delete this chat?');
        if (confirmed) {
            // TODO: Implement chat deletion logic
            alert('Chat deleted successfully!');
        }
    };

    const isGroup = selectedUser.roomType === 'Group';
    const isDouble = selectedUser.roomType === 'Double';
    const isSingle = selectedUser.roomType === 'Single';

    return (
        <div className="group-info-sidebar p-3 shadow-lg">
            {/* Header */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h5 className="text-white fw-semibold">
                    {isGroup ? 'Group Info' : 'User Info'}
                </h5>
                <FiX size={24} onClick={onClose} className="text-white cursor-pointer" />
            </div>

            {/* Avatar + Name + Status */}
            <div className="text-center mb-4">
                <Avatar
                    size={80}
                    name={groupName}
                    variant="beam"
                    colors={["#92A1C6", "#146A7C", "#F0AB3D", "#C271B4", "#C20D90"]}
                />
                {isGroup && editing ? (
                    <div className="mt-3">
                        <input
                            className="form-control mb-2"
                            value={groupName}
                            onChange={(e) => setGroupName(e.target.value)}
                            placeholder="Group Name"
                        />
                        <textarea
                            className="form-control"
                            value={groupDesc}
                            onChange={(e) => setGroupDesc(e.target.value)}
                            placeholder="Group Description"
                            rows={2}
                        />
                        <button className="btn btn-sm btn-success mt-2 w-100 d-flex align-items-center justify-content-center gap-2" onClick={handleUpdateGroup}>
                            <FiSave /> Save Changes
                        </button>
                    </div>
                ) : (
                    <div className="mt-3">
                        <h6 className="text-light fw-semibold mb-1">{groupName}</h6>
                        <p className="text-muted small mb-2">{groupDesc || 'No description'}</p>
                        {isGroup && (
                            <button className="btn btn-sm btn-outline-light d-flex align-items-center gap-2 mx-auto" onClick={() => setEditing(true)}>
                                <FiEdit2 /> Edit Group
                            </button>
                        )}
                    </div>
                )}
            </div>

            <hr className="border-secondary" />

            {/* Group Members (only for Group type) */}
            {isGroup && (
                <>
                    <h6 className="text-white mb-3">Members ({members.length})</h6>
                    <div className="member-scroll-container">
                        {members.map(member => (
                            <div key={member.userId} className="d-flex align-items-center gap-3 mb-3">
                                <Avatar size={36} name={member.userName} variant="beam" />
                                <div>
                                    <div className="fw-semibold text-light">{member.userName}</div>
                                    <small className="text-muted">{member.userStatus}</small>
                                </div>
                            </div>
                        ))}
                    </div>

                    <button className="btn btn-outline-danger mt-4 w-100 d-flex align-items-center justify-content-center gap-2" onClick={handleExitGroup}>
                        <FiLogOut /> Exit Group
                    </button>
                    <button className="btn btn-outline-danger mt-4 w-100 d-flex align-items-center justify-content-center gap-2" onClick={handleDeleteChat}>
                        <FiTrash2 /> Delete Chat
                    </button>
                </>
            )}

            {/* Delete Chat (for Single or Double) */}
            {(isSingle || isDouble) && (
                <button className="btn btn-outline-danger mt-4 w-100 d-flex align-items-center justify-content-center gap-2" onClick={handleDeleteChat}>
                    <FiTrash2 /> Delete Chat
                </button>
            )}
        </div>
    );
};

export default GroupInfoSidebar;
