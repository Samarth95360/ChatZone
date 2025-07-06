// src/services/chatService.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8000/user';
const CHAT_BASE_URL = 'http://localhost:8050/user';
const CHAT_REST_BASE_URL = 'http://localhost:8050/chat';
const token = localStorage.getItem('token');

//fetch all the conv of a user
export async function fetchUserConversations() {

  try {
    const response = await axios.get(`${CHAT_BASE_URL}/profile`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response.data;

  } catch (error) {
    console.error("Failed to fetch user conversations:", error);
    throw new Error("Failed to fetch user conversations");
  }
}

export const initiatePrivateConversation = async (receiverId) => {

  const response = await axios.post(
    `${CHAT_BASE_URL}/initiate-private-conv`,
    {
      receiverId: receiverId,
      roomType: 'Double'
    },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  return response.data;
};


// Fetch all List of registered user
export const fetchAllUsers = async () => {
  const response = await axios.get(`${CHAT_BASE_URL}/list`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const fetchMessages = async (convId) => {
  const response = await axios.post(
    `${CHAT_REST_BASE_URL}/messages`,
    {
      convId: convId
    },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  console.log("Fetched Messages are :- ", response.data);

  return response.data;
};

export const addMembersToGroup = async (payload) => {

  const response = await axios.post(
    `${CHAT_BASE_URL}/add-members`,
    payload,
    {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    }
  );

  console.log("Add Members Payload:", payload);
  console.log("Server Response:", response.data);

  return response.data;
};

//create user profile in one go
export const createUserProfiles = async () => {
  const response = await axios.get(`${CHAT_BASE_URL}/create-user-profile`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const fetchUserProfilesInGroup = async (payload) => {
  console.log("data of fetchUserProfilesInGroup :- ", payload);

  const response = await axios.post(
    `${CHAT_REST_BASE_URL}/group-member-profiles`,
    payload,
    {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    }
  );

  console.log("user profile data is :- ", response.data);
  return response.data;

}

export const exitGroup = async (data) => {
  console.log("data of exitGroup :- ", data);
}

export const updateGroupInfo = async (payload) => {
  console.log("data of fetchUserProfilesInGroup :- ", payload);
  const response = await axios.post(
    `${CHAT_REST_BASE_URL}/update-room-info`,
    payload,
    {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    }
  );

  console.log("Response from updateGroupInfo is :- ", response);
  return response;

}

// Get user Profile
export const getUserProfiles = async () => {
  const response = await axios.get(`${CHAT_BASE_URL}/user-profile`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  console.log("profile response is :- ", response.data);
  return response.data;
};

// Update user Profile
export const updateUserProfile = async (payload) => {

  console.log("Update payload is :- ", payload);

  const response = await axios.post(`${CHAT_BASE_URL}/update-user-profile`,
    payload,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  console.log("updated profile response is :- ", response);
  return response;
};

// Not required
export const updateMessage = async (message) => {
  try {
    const response = await axios.put(
      `${CHAT_REST_BASE_URL}/edit-message`,
      message,
      {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      }
    );
    console.log("Update message response is:", response.data);
    return response.data;
  } catch (error) {
    console.error('Failed to update message:', error);
    throw error;
  }
};

// Not required
export const deleteMessage = async (message) => {
  try {
    const response = await axios.post(
      `${CHAT_REST_BASE_URL}/delete-message`,
      message,
      {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      }
    );
    console.log('Delete message response is:', response.data);
    return response.data;
  } catch (error) {
    console.error('Failed to delete message:', error);
    throw error;
  }
};


