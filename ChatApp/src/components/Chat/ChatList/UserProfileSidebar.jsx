import React, { useEffect, useState } from 'react';
import Avatar from 'boring-avatars';
import { FiX, FiSave, FiEdit2 } from 'react-icons/fi';
import { getUserProfiles, updateUserProfile } from '../../../services/chatServices';
import './UserProfileSidebar.css';

const UserProfileSidebar = ({ onClose, stompClientRef }) => {
    const [userProfile, setUserProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [editField, setEditField] = useState(null);
    const [tempValues, setTempValues] = useState({});

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const profile = await getUserProfiles();
                setUserProfile(profile);
                setTempValues({
                    status: profile.status || '',
                    phone: profile.phoneNo || ''
                });
            } catch (err) {
                console.error("Failed to load profile", err);
                setError('Failed to load profile');
            } finally {
                setLoading(false);
            }
        };
        fetchProfile();
    }, []);

    const handleFieldChange = (field, value) => {
        setTempValues(prev => ({ ...prev, [field]: value }));
    };

    const handleFieldSave = async (field) => {
        try {
            const payload = {
                userId: userProfile.userId,
                status: tempValues.status,
                phoneNo: tempValues.phone
            };

            const response = await updateUserProfile(payload);
            const updatedData = response?.data;

            if (response.status === 200 && updatedData) {
                setUserProfile(prev => ({
                    ...prev,
                    status: tempValues.status,
                    phoneNo: tempValues.phone
                }));
                setEditField(null);

                // STOMP publish
                if (stompClientRef?.current?.connected) {
                    stompClientRef.current.publish({
                        destination: '/app/update-profile',
                        body: JSON.stringify(updatedData)
                    });
                    console.log("After stomp and response :- ",updatedData);
                }
            }
        } catch (err) {
            console.error('Failed to update profile:', err);
            alert('Failed to update');
        }
    };

    if (loading) return <div className="text-white p-3">Loading profile...</div>;
    if (error) return <div className="text-danger p-3">{error}</div>;
    if (!userProfile) return <div className="text-white p-3">No profile data found</div>;

    return (
        <div className="group-info-sidebar p-3 shadow-lg">
            {/* Header */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h5 className="text-white fw-semibold">Your Profile</h5>
                <FiX size={24} onClick={onClose} className="text-white cursor-pointer" />
            </div>

            {/* Avatar */}
            <div className="text-center mb-4">
                <Avatar
                    size={80}
                    name={userProfile.userName}
                    variant="beam"
                    colors={["#92A1C6", "#146A7C", "#F0AB3D", "#C271B4", "#C20D90"]}
                />
            </div>

            {/* Static Name Field */}
            <div className="px-2 text-light small mb-3">
                <label className="text-uppercase text-muted small">Name</label>
                <div className="field-value text-truncate">{userProfile.userName || 'N/A'}</div>
            </div>

            {/* Editable Fields */}
            <div className="px-2 text-light small d-flex flex-column gap-3">
                {[{ label: 'Status', field: 'status' }, { label: 'Phone', field: 'phone' }].map(({ label, field }) => (
                    <div key={field}>
                        <label className="text-uppercase text-muted small">{label}</label>
                        <div className="d-flex align-items-center">
                            {editField === field ? (
                                <>
                                    {field === 'status' ? (
                                        <textarea
                                            className="form-control me-2"
                                            rows={2}
                                            value={tempValues[field]}
                                            onChange={(e) => handleFieldChange(field, e.target.value)}
                                        />
                                    ) : (
                                        <input
                                            className="form-control me-2"
                                            type="text"
                                            value={tempValues[field]}
                                            onChange={(e) => handleFieldChange(field, e.target.value)}
                                        />
                                    )}
                                    <button
                                        className="btn btn-sm btn-success"
                                        onClick={() => handleFieldSave(field)}
                                    >
                                        <FiSave />
                                    </button>
                                </>
                            ) : (
                                <div className="d-flex align-items-center justify-content-between w-100">
                                    <div className="field-value text-truncate me-2">
                                        {tempValues[field] || 'N/A'}
                                    </div>
                                    <button
                                        className="btn btn-sm btn-outline-light"
                                        onClick={() => setEditField(field)}
                                    >
                                        <FiEdit2 />
                                    </button>
                                </div>
                            )}
                        </div>
                    </div>
                ))}

                {/* Join date */}
                <div className="text-muted mt-3 border-top pt-2" style={{ fontSize: '0.75rem' }}>
                    <strong>Joined:</strong> {new Date(userProfile.creationTimeStamp).toLocaleString()}
                </div>
            </div>
        </div>
    );
};

export default UserProfileSidebar;
