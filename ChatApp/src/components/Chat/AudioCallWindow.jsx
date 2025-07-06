import React, { useRef, useEffect, useState } from 'react';
import Avatar from 'boring-avatars';
import { FiPhoneOff, FiPhoneIncoming, FiMic, FiMicOff, FiPhoneCall } from 'react-icons/fi';
import './Chat.css';

const AudioCallWindow = ({ selectedUser, currentUserId, stompClientRef, onClose, callPayload }) => {
    const [callStatus, setCallStatus] = useState(callPayload?.isIncoming ? 'Incoming Call...' : 'Calling...');
    const [isMuted, setIsMuted] = useState(false);
    const [inCall, setInCall] = useState(!callPayload?.isIncoming); // If outgoing, go directly to call
    const localAudioRef = useRef();
    const remoteAudioRef = useRef();

    useEffect(() => {
        if (!callPayload?.isIncoming) {
            setTimeout(() => setCallStatus('In Call'), 1000);
        }
    }, []);

    const toggleMute = () => {
        const stream = localAudioRef.current?.srcObject;
        if (stream) {
            stream.getAudioTracks()[0].enabled = !isMuted;
        }
        setIsMuted(!isMuted);
    };

    const handleAccept = () => {
        setInCall(true);
        setCallStatus('In Call');

        // TODO: Add WebRTC offer/answer logic for accepting call
        // e.g. trigger STOMP event to notify sender you're accepting

        if (stompClientRef?.current && callPayload?.senderId) {
            stompClientRef.current.publish({
                destination: "/app/audio-call-accept",
                body: JSON.stringify({
                    ...callPayload,
                    receiverId: currentUserId,
                    responseStatus: 'ACCEPTED'
                })
            });
        }
    };

    const handleReject = () => {
        setCallStatus('Call Rejected');
        onClose();

        // Optional: notify backend
        if (stompClientRef?.current && callPayload?.senderId) {
            stompClientRef.current.publish({
                destination: "/app/audio-call-reject",
                body: JSON.stringify({
                    ...callPayload,
                    receiverId: currentUserId,
                    responseStatus: 'REJECTED'
                })
            });
        }
    };

    const endCall = () => {
        setCallStatus('Call Ended');
        onClose();

        // Optional: notify others call has ended
    };

    return (
        <div
            className="audio-call-window p-4 d-flex flex-column align-items-center justify-content-center text-white position-fixed top-0 start-0 w-100 h-100"
            style={{ backgroundColor: 'rgba(0,0,0,0.85)', zIndex: 1050 }}
        >
            <Avatar
                size={80}
                name={callPayload?.receiverName || selectedUser?.roomName}
                variant="beam"
                colors={["#92A1C6", "#146A7C", "#F0AB3D", "#C271B4", "#C20D90"]}
            />
            <h4 className="mt-3">{callPayload?.receiverName || selectedUser?.roomName}</h4>
            <p className="text-muted mb-2">{callPayload?.callType || 'Audio Call'}</p>
            <p className="text-info small">{callStatus}</p>

            {callPayload?.receiverUsersId && (
                <div className="text-center mt-2">
                    <p className="fw-bold">Participants:</p>
                    <ul className="list-unstyled small">
                        {callPayload.receiverUsersId.map((id, index) => (
                            <li key={index}>{id === currentUserId ? 'You' : id}</li>
                        ))}
                    </ul>
                </div>
            )}

            <div className="d-flex gap-4 mt-4">
                {!inCall ? (
                    <>
                        <button className="btn btn-success rounded-circle" title="Accept Call" onClick={handleAccept}>
                            <FiPhoneCall size={24} />
                        </button>
                        <button className="btn btn-danger rounded-circle" title="Reject Call" onClick={handleReject}>
                            <FiPhoneOff size={24} />
                        </button>
                    </>
                ) : (
                    <>
                        <button className="btn btn-outline-light rounded-circle" title="Mute" onClick={toggleMute}>
                            {isMuted ? <FiMicOff size={24} /> : <FiMic size={24} />}
                        </button>
                        <button className="btn btn-danger rounded-circle" title="End Call" onClick={endCall}>
                            <FiPhoneOff size={24} />
                        </button>
                    </>
                )}
            </div>

            <audio ref={localAudioRef} autoPlay muted />
            <audio ref={remoteAudioRef} autoPlay />
        </div>
    );
};

export default AudioCallWindow;
