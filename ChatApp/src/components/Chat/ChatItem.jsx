import Avatar from 'boring-avatars';
import { FaRegClock } from 'react-icons/fa';
import './Chat.css';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
dayjs.extend(relativeTime);

const ChatItem = ({ chat, isSelected, onClick }) => {
    const formattedTime = chat?.lastMessage?.timeStamp
        ? dayjs(chat.lastMessage.timeStamp).format('hh:mm A')
        : '';

    return (
        <div
            className={`chat-item d-flex align-items-center p-2 rounded-3 ${isSelected ? 'bg-highlight text-white' : ''}`}
            onClick={onClick}
            style={{ cursor: 'pointer' }}
        >
            {/* Avatar (fixed size) */}
            <div style={{ flexShrink: 0 }}>
                <Avatar
                    size={45}
                    name={chat.roomName}
                    variant="beam"
                    colors={["#92A1C6", "#146A7C", "#F0AB3D", "#C271B4", "#C20D90"]}
                />
            </div>

            {/* Texts (flexible, truncated if long) */}
            <div className="d-flex flex-column justify-content-center ms-3 me-2 overflow-hidden" style={{ flex: 1 }}>
                <div
                    className="fw-semibold text-truncate"
                    title={chat.roomName}
                >
                    {chat.roomName}
                </div>
                <div
                    className="text-muted small text-truncate"
                    title={chat?.lastMessage?.text}
                >
                    {chat?.lastMessage?.text || ''}
                </div>
            </div>

            {/* Time (fixed) */}
            <div
                className="text-muted small text-end"
                style={{ flexShrink: 0, minWidth: '60px', whiteSpace: 'nowrap' }}
            >
                {formattedTime}
            </div>
        </div>
    );
};

export default ChatItem;
